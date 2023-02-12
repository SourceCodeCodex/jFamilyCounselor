package ro.lrg.jfamilycounselor.plugin.project.action;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.plugin.project.action.util.csv.CsvUtil;
import ro.lrg.jfamilycounselor.plugin.project.action.util.html.HTMLPackage;
import ro.lrg.jfamilycounselor.plugin.project.action.util.html.HTMLReferencesPair;
import ro.lrg.jfamilycounselor.plugin.project.action.util.html.HTMLType;
import ro.lrg.jfamilycounselor.plugin.project.action.util.html.IndexHTML;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
import ro.lrg.jfamilycounselor.util.duration.DurationFormatter;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class ExportReportJob extends Job {

    public static final String FAMILY = "jFamilyCounselorExportReport";

    public static final ISchedulingRule MUTEX = new ISchedulingRule() {
	public boolean contains(ISchedulingRule rule) {
	    return rule == this;
	}

	public boolean isConflicting(ISchedulingRule rule) {
	    return rule == this;
	}
    };

    private static Logger logger = jFCLogger.getJavaLogger();

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

	// compute relevant types
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

	// pack with packages
	var packages = relevantTypes.stream().collect(Collectors.groupingBy(t -> t.getPackageFragment()));

	// csv
	var csvFile = outputDirPath.append(iJavaProject.getElementName() + ".csv").toFile();
	try {
	    csvFile.createNewFile();
	} catch (IOException e) {
	    logger.warning("IOException encountered: " + e.getMessage());
	    return Status.error("IOException while creating the csv file");
	}

	try (var csvFileWriter = new FileWriter(csvFile)) {

	    var headers = List.of("Class", "Aperture Coverage", "Duration");
	    csvFileWriter.write(CsvUtil.convertToCsv(headers));

	    @SuppressWarnings("preview")
	    var flushThread = Thread.startVirtualThread(() -> {
		while (!Thread.interrupted()) {
		    try {
			Thread.sleep(Duration.ofSeconds(5));
			csvFileWriter.flush();
		    } catch (IOException | InterruptedException e) {
			break;
		    }
		}

	    });

	    // create HTML report structure
	    try {
		createHTMLReportStructure(outputDirPath, packages);
	    } catch (IOException e) {
		logger.warning("IOException encountered: " + e.getMessage());
		return Status.error("IOException while creating the HTML report structure");
	    }

	    // start computation

	    relevantTypes.parallelStream()
		    .forEach(t -> {
			var metaType = Factory.getInstance().createMType(t);

			var referencesPairs = metaType.referencesPairs().getElements();

			var apertureCoverages = new ConcurrentLinkedQueue<Double>();

			var referencesPairHTML = new ConcurrentLinkedQueue<HTMLReferencesPair>();

			var start = Instant.now();
			referencesPairs.parallelStream()
				.forEach(rp -> {
				    var startRP = Instant.now();

				    var possibleTypes = rp.possibleTypes().getElements();
				    List<MTypesPair> usedTypes;
				    if (estimation == EstimationType.NAME_BASED)
					usedTypes = rp.nameUsedTypes().getElements();
				    else
					usedTypes = rp.assignemntsUsedTypes().getElements();

				    var apertureCoverageRP = (1.0 * usedTypes.size()) / possibleTypes.size();

				    var endRP = Instant.now();

				    var durationRP = Duration.between(startRP, endRP);

				    referencesPairHTML.add(new HTMLReferencesPair(rp.toString(), apertureCoverageRP, durationRP, usedTypes.stream().map(p -> p.toString()).toList()));

				    apertureCoverages.add(apertureCoverageRP);
				});

			var end = Instant.now();

			var ac = apertureCoverages.stream().filter(d -> d != 0).min(Double::compareTo).orElseGet(() -> 0.);
			var duration = Duration.between(start, end);

			logger.info(t.getFullyQualifiedName() + ": " + ac + " in: " + DurationFormatter.format(duration));

			var htmlRenderer = new HTMLType(iJavaProject.getElementName(), t.getFullyQualifiedName(), ac, duration, referencesPairHTML.stream().toList());

			try {
			    csvFileWriter.write(CsvUtil.convertToCsv(List.of(t.getFullyQualifiedName(), ac.toString(), DurationFormatter.format(duration))));
			    writeTypeHTMLFile(outputDirPath, t, htmlRenderer);
			} catch (IOException e) {
			    logger.warning("IOException encountered: " + e.getMessage());
			}

			subMonitor.split(1);

		    });

	    flushThread.interrupt();

	    csvFileWriter.close();

	    var exportEndTime = Instant.now();
	    logger.info(estimation + " export report job for " + iJavaProject.getElementName() + " took: " + DurationFormatter.format(Duration.between(exportStartTime, exportEndTime)));

	    return Status.OK_STATUS;
	} catch (IOException e) {
	    logger.warning("IOException encountered: " + e.getMessage());
	    return Status.error("IOException during the analysis");
	}
    }

    private Optional<IPath> createOutputDirectory() {
	var formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
	var timestamp = formatter.format(new Date());

	var wsRoot = iJavaProject.getProject().getWorkspace().getRoot();

	IPath projectTargetFolder;
	try {
	    projectTargetFolder = wsRoot.getFolder(iJavaProject.getOutputLocation()).getLocation();
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Optional.empty();
	}

	var reportsFolder = projectTargetFolder.append("jFamilyCounselor");

	var outputDirName = String.format("%s-%s-%s", iJavaProject.getElementName(), estimation.toString(), timestamp.toString());

	var outputDirPath = reportsFolder.append(outputDirName);

	var outputDir = outputDirPath.toFile();
	outputDir.mkdirs();

	return Optional.of(outputDirPath);
    }

    private void createHTMLReportStructure(IPath outputDirPath, Map<IPackageFragment, List<IType>> packages) throws IOException {
	var htmlFile = outputDirPath.append("index.html").toFile();
	var content = new IndexHTML(iJavaProject.getElementName(),
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
