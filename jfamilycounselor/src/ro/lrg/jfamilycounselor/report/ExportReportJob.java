package ro.lrg.jfamilycounselor.report;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.report.csv.CsvCapability;
import ro.lrg.jfamilycounselor.report.html.HTMLIndex;
import ro.lrg.jfamilycounselor.report.html.HTMLPackage;
import ro.lrg.jfamilycounselor.report.html.HTMLReferencesPair;
import ro.lrg.jfamilycounselor.report.html.HTMLType;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.duration.DurationFormatter;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * The job is intended to export data as results are being computed.
 * 
 * @author rosualinpetru
 *
 */
public class ExportReportJob extends Job {

    public static final String FAMILY = "jFamilyCounselorExportReport";

    private static Logger logger = jFCLogger.getLogger();

    private final IJavaProject iJavaProject;
    private final EstimationType estimation;

    public ExportReportJob(EstimationType estimation, IJavaProject iJavaProject) {
	super(estimation + "(" + iJavaProject.getElementName() + ") exporting report...");
	this.iJavaProject = iJavaProject;
	this.estimation = estimation;
    }

    protected IStatus run(IProgressMonitor monitor) {
	var exportStartTime = Instant.now();

	var outputDirPathOpt = createOutputDirectory();
	if (outputDirPathOpt.isEmpty())
	    Status.error("Error while creating the output direcotry");

	var outputDirPath = outputDirPathOpt.get();

	// ------------------------------------------------------------------------------------
	// Compute Relevant Types
	// ------------------------------------------------------------------------------------
	var relevantTypesJob = new RelevantTypesJob(iJavaProject);
	relevantTypesJob.setPriority(Job.LONG);
	relevantTypesJob.setSystem(false);
	relevantTypesJob.setUser(true);
	relevantTypesJob.schedule();

	try {
	    relevantTypesJob.join();
	} catch (InterruptedException e) {
	    logger.warning("Thread interrupted: ExportReportJob");
	    return Status.CANCEL_STATUS;
	}

	var relevantTypes = relevantTypesJob.relevantTypes();
	var workload = relevantTypes.size();
	var subMonitor = SubMonitor.convert(monitor, workload);

	var packages = relevantTypes.stream().collect(Collectors.groupingBy(t -> t.getPackageFragment()));

	// ------------------------------------------------------------------------------------
	// CSV - Flushing Thread
	// ------------------------------------------------------------------------------------
	var csvFile = outputDirPath.append(iJavaProject.getElementName() + ".csv").toFile();
	try {
	    csvFile.createNewFile();
	} catch (IOException e) {
	    logger.warning("IOException encountered: " + e.getMessage());
	    return Status.error("IOException while creating the csv file");
	}

	try (var csvFileWriter = new FileWriter(csvFile)) {

	    var headers = List.of("Class", "Aperture Coverage", "Duration");
	    csvFileWriter.write(CsvCapability.joinAsCSVRow(headers));

	    var csvFlushThread = new Thread(() -> {
		while (!Thread.interrupted()) {
		    try {
			Thread.sleep(5000);
			csvFileWriter.flush();
		    } catch (IOException | InterruptedException e) {
			break;
		    }
		}

	    });

	    csvFlushThread.start();

	    // ------------------------------------------------------------------------------------
	    // Create Initial HTML Report Structure
	    // ------------------------------------------------------------------------------------
	    try {
		createHTMLReportStructure(outputDirPath, packages);
	    } catch (IOException e) {
		logger.warning("IOException encountered: " + e.getMessage());
		return Status.error("IOException while creating the HTML report structure");
	    }

	    // ------------------------------------------------------------------------------------
	    // Compute and Export Data
	    // ------------------------------------------------------------------------------------
	    relevantTypes.parallelStream()
		    .forEach(t -> {
			var metaType = Factory.getInstance().createMType(t);

			var referencesPairs = metaType.referencesPairs().getElements();

			var apertureCoverages = new ConcurrentLinkedQueue<Double>();

			var referencesPairHTML = new ConcurrentLinkedQueue<Pair<Integer, HTMLReferencesPair>>();

			var conter = new AtomicInteger(0);

			var start = Instant.now();
			referencesPairs
				.stream()
				.map(rp -> Pair.of(conter.getAndIncrement(), rp))
				.toList()
				.parallelStream()
				.forEach(zippedWithIndex -> {
				    var index = zippedWithIndex._1;
				    var rp = zippedWithIndex._2;

				    var startRP = Instant.now();

				    var possibleTypes = rp.possibleTypes().getElements();

				    var usedTypes = usedTypes(rp);

				    var apertureCoverageRP = (1.0 * usedTypes.size()) / possibleTypes.size();

				    var endRP = Instant.now();

				    var durationRP = Duration.between(startRP, endRP);

				    var html = new HTMLReferencesPair(rp.toString(), apertureCoverageRP, durationRP, usedTypes.stream().map(p -> p.toString()).toList());

				    referencesPairHTML.add(Pair.of(index, html));

				    apertureCoverages.add(apertureCoverageRP);
				});

			var end = Instant.now();

			var ac = apertureCoverages.stream().filter(d -> d != 0).min(Double::compareTo).orElseGet(() -> 0.);
			var duration = Duration.between(start, end);

			logger.info(t.getFullyQualifiedName() + ": " + ac + " in: " + DurationFormatter.format(duration));

			var sortedHtmlRefPairs = referencesPairHTML.stream().sorted(Comparator.<Pair<Integer, HTMLReferencesPair>, Integer>comparing(p -> p._1)).map(p -> p._2).toList();

			var htmlRenderer = new HTMLType(iJavaProject.getElementName(), t.getFullyQualifiedName(), ac, duration, sortedHtmlRefPairs);

			try {
			    csvFileWriter.write(CsvCapability.joinAsCSVRow(List.of(t.getFullyQualifiedName(), ac.toString(), DurationFormatter.format(duration))));
			    writeTypeHTMLFile(outputDirPath, t, htmlRenderer);
			} catch (IOException e) {
			    logger.warning("IOException encountered: " + e.getMessage());
			}

			subMonitor.split(1);

		    });

	    csvFlushThread.interrupt();

	    csvFileWriter.close();

	    try {
		iJavaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	    } catch (CoreException e) {
		logger.warning("CoreException encountered: " + e.getMessage());
	    }

	    var exportEndTime = Instant.now();
	    logger.info(estimation + " export report job for " + iJavaProject.getElementName() + " took: " + DurationFormatter.format(Duration.between(exportStartTime, exportEndTime)));

	    return Status.OK_STATUS;
	} catch (IOException e) {
	    logger.warning("IOException encountered: " + e.getMessage());
	    return Status.error("IOException during the analysis");
	}
    }

    private List<MTypesPair> usedTypes(MReferencesPair rp) {
	return switch (estimation) {
	case NAME_BASED: {
	    yield rp.nameBasedUsedTypes().getElements();
	}
	case NAME_BASED_LEVENSHTEIN: {
	    yield rp.nameBasedLevenshteinUsedTypes().getElements();
	}
	case ASSIGNMENTS_BASED: {
	    yield rp.assignemntsBasedUsedTypes().getElements();
	}
	case CASTS_BASED: {
	    yield rp.castsBasedUsedTypes().getElements();
	}
	default:
	    throw new IllegalArgumentException("Unknown estimation: " + estimation);
	};
    }

    private Optional<IPath> createOutputDirectory() {
	var formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
	var timestamp = formatter.format(new Date());

	var wsRoot = iJavaProject.getProject().getWorkspace().getRoot();

	var reportsFolder = wsRoot.getFolder(iJavaProject.getPath().append("jFamilyCounselor")).getLocation();

	var outputDirName = String.format("%s-%s-%s", iJavaProject.getElementName(), estimation.toString(), timestamp.toString());

	var outputDirPath = reportsFolder.append(outputDirName);

	var outputDir = outputDirPath.toFile();
	outputDir.mkdirs();

	return Optional.of(outputDirPath);
    }

    private void createHTMLReportStructure(IPath outputDirPath, Map<IPackageFragment, List<IType>> packages) throws IOException {
	var htmlFile = outputDirPath.append("index.html").toFile();
	var content = new HTMLIndex(iJavaProject.getElementName(),
		packages.entrySet().stream()
			.map(p -> new HTMLPackage(p.getKey().getElementName(), p.getValue().stream()
				.map(t -> t.getElementName())
				.toList()))
			.toList())
		.html();

	if (content.isPresent()) {
	    htmlFile.createNewFile();
	    var fileWriter = new FileWriter(htmlFile);
	    fileWriter.write(content.get());
	    fileWriter.close();
	}

	packages.keySet().stream().forEach(p -> {
	    var dirPath = outputDirPath.append(p.getElementName());
	    dirPath.toFile().mkdirs();
	});
    }

    private void writeTypeHTMLFile(IPath outputDirPath, IType iType, HTMLType renderer) throws IOException {
	var htmlFile = outputDirPath.append(iType.getPackageFragment().getElementName()).append(iType.getElementName() + ".html").toFile();
	var content = renderer.html();
	if (content.isPresent()) {
	    htmlFile.createNewFile();
	    var fileWriter = new FileWriter(htmlFile);
	    fileWriter.write(content.get());
	    fileWriter.close();
	}
    }

    public boolean belongsTo(Object family) {
	return FAMILY.equals(family);
    }
}
