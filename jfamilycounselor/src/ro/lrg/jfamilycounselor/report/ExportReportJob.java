package ro.lrg.jfamilycounselor.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.report.csv.CsvCapability;
import ro.lrg.jfamilycounselor.report.html.ReportIndex;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
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
		super(iJavaProject.getElementName() + " - " + estimation + " - Report Export");
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
		var relevantTypesJob = new RelevantTypesJob(iJavaProject, estimation);
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
			// Compute Export Data
			// ------------------------------------------------------------------------------------
			var exportTypesMap = new ConcurrentHashMap<String, Queue<AnalysedPairEntry>>();

			var exportCorrelationsMap = new ConcurrentHashMap<String, String>();

			relevantTypes.parallelStream().forEach(t -> {
				var metaType = Factory.getInstance().createMType(t);

				var apertureCoverages = new ConcurrentLinkedQueue<Double>();

				exportTypesMap.putIfAbsent(t.getFullyQualifiedName(), new ConcurrentLinkedQueue<>());

				var start = Instant.now();

				if (estimation.equals(EstimationType.TYPE_PARAMETERS_BASED)) {
					var typeParametersPairs = metaType.relevantTypeParametersPairs().getElements();
					typeParametersPairs.parallelStream().forEach(tp -> {

						var startTP = Instant.now();

						var possibleTypes = tp.possibleTypes().getElements();

						var usedTypes = tp.usedTypes().getElements();

						var apertureCoverageTP = (1.0 * usedTypes.size()) / possibleTypes.size();

						var endTP = Instant.now();

						var durationTP = Duration.between(startTP, endTP);

						apertureCoverages.add(apertureCoverageTP);

						var exportAnalysedPairs = exportTypesMap.get(t.getFullyQualifiedName());

						if(apertureCoverageTP < 1.0) {
							usedTypes.forEach(u -> exportCorrelationsMap
									.putIfAbsent("" + u.getUnderlyingObject().hashCode(), u.toString()));

							exportAnalysedPairs.add(new AnalysedPairEntry(tp.toString(),
									DurationFormatter.format(durationTP), apertureCoverageTP,
									usedTypes.stream().map(u -> "" + u.getUnderlyingObject().hashCode()).toList()));
						} else {
							exportAnalysedPairs.add(new AnalysedPairEntry(tp.toString(),
									DurationFormatter.format(durationTP), apertureCoverageTP,
									List.of()));
						}

					});

				} else {
					var referencesPairs = metaType.relevantReferencesPairs().getElements();
					referencesPairs.parallelStream().forEach(rp -> {

						var startRP = Instant.now();

						var possibleTypes = rp.possibleTypes().getElements();

						var usedTypes = usedTypes(rp);

						var apertureCoverageRP = (1.0 * usedTypes.size()) / possibleTypes.size();

						var endRP = Instant.now();

						var durationRP = Duration.between(startRP, endRP);

						apertureCoverages.add(apertureCoverageRP);

						var exportAnalysedPairs = exportTypesMap.get(t.getFullyQualifiedName());
						
						if(apertureCoverageRP < 1.0) {
							usedTypes.forEach(u -> exportCorrelationsMap
									.putIfAbsent("" + u.getUnderlyingObject().hashCode(), u.toString()));

							exportAnalysedPairs.add(new AnalysedPairEntry(rp.toString(),
									DurationFormatter.format(durationRP), apertureCoverageRP,
									usedTypes.stream().map(u -> "" + u.getUnderlyingObject().hashCode()).toList()));
						} else {
							exportAnalysedPairs.add(new AnalysedPairEntry(rp.toString(),
									DurationFormatter.format(durationRP), apertureCoverageRP,
									List.of()));
						}
	
					});
				}

				var end = Instant.now();

				var ac = apertureCoverages.stream().min(Double::compareTo).orElseGet(() -> 0.);
				var duration = Duration.between(start, end);

				logger.info(t.getFullyQualifiedName() + ": " + ac + " in: " + DurationFormatter.format(duration));

				try {
					csvFileWriter.write(CsvCapability
							.joinAsCSVRow(List.of(t.getFullyQualifiedName(), ac.toString(), DurationFormatter.format(duration))));
				} catch (IOException e) {
					logger.warning("IOException encountered: " + e.getMessage());
				}

				subMonitor.split(1);

			});

			csvFlushThread.interrupt();
			csvFileWriter.close();

			
			// ------------------------------------------------------------------------------------
			// Write Files
			// ------------------------------------------------------------------------------------
			try {
				var indexHtml = outputDirPath.append("html").append("index.html").toFile();
				writeStringToFile(indexHtml, ReportIndex.html(estimation + ": " + iJavaProject.getElementName()));
				writeJSFiles(outputDirPath, exportTypesMap, exportCorrelationsMap);
			} catch (JsonProcessingException e) {
				logger.warning("JsonProcessingException encountered: " + e.getMessage());
				return Status.error("JsonProcessingException during serialization of diagram relations");
			}

			// ------------------------------------------------------------------------------------
			// Refresh
			// ------------------------------------------------------------------------------------
			try {
				iJavaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				logger.warning("CoreException encountered: " + e.getMessage());
			}

			var exportEndTime = Instant.now();
			logger.info(estimation + " export report job for " + iJavaProject.getElementName() + " took: "
					+ DurationFormatter.format(Duration.between(exportStartTime, exportEndTime)));

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
			yield rp.assignmentsBasedUsedTypes().getElements();
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

		var reportsFolder = wsRoot.getFolder(iJavaProject.getPath().append("jFamilyCounselor").append("reports"))
				.getLocation();

		var outputDirName = String.format("%s-%s-report-%s", iJavaProject.getElementName(), estimation.toString(),
				timestamp.toString());

		var outputDirPath = reportsFolder.append(outputDirName);

		outputDirPath.toFile().mkdir();
		outputDirPath.append("html").toFile().mkdirs();

		return Optional.of(outputDirPath);
	}

	private void writeJSFiles(IPath outputDirPath, Map<String, Queue<AnalysedPairEntry>> exportedTypesMap,
			Map<String, String> exportCorrelationsMap) throws IOException, JsonProcessingException {
		var typesJS = outputDirPath.append("html").append("types.js").toFile();
		var correlationsJS = outputDirPath.append("html").append("correlations.js").toFile();

		var objectMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
		
		var sortedExportedTypesMap = new HashMap<String, List<AnalysedPairEntry>>();
		exportedTypesMap.forEach((k, v) -> {
			var sorted = v.stream().sorted(Comparator.comparing(AnalysedPairEntry::apertureCoverage)).toList();
			sortedExportedTypesMap.put(k, sorted);
		});

		var typesOutputJS = String.format("var types = %s;", objectMapper.writeValueAsString(sortedExportedTypesMap));
		var correlationsOutputJS = String.format("var correlations = %s;",
				objectMapper.writeValueAsString(exportCorrelationsMap));

		writeStringToFile(typesJS, typesOutputJS);
		writeStringToFile(correlationsJS, correlationsOutputJS);
	}

	private void writeStringToFile(File outputFile, String outputString) throws IOException {
		outputFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getAbsolutePath()));
		writer.write(outputString);
		writer.flush();
		writer.close();
	}

	public boolean belongsTo(Object family) {
		return FAMILY.equals(family);
	}
}