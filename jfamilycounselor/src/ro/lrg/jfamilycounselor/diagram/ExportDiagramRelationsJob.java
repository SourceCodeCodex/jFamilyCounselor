package ro.lrg.jfamilycounselor.diagram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.report.RelevantTypesJob;
import ro.lrg.jfamilycounselor.util.Constants;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;
import ro.lrg.xcore.metametamodel.Group;

public class ExportDiagramRelationsJob extends Job {

	public static final String FAMILY = "jFamilyCounselorExportReport";
	private static final double APERTURE_COVERAGE_THRESHOLD = 0.5;

	public static final ISchedulingRule MUTEX = new ISchedulingRule() {
		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}
	};

	private static Logger logger = jFCLogger.getLogger();

	private final IJavaProject iJavaProject;
	private final EstimationType estimation;

	public ExportDiagramRelationsJob(EstimationType estimation, IJavaProject iJavaProject) {
		super("Exporting " + estimation + " diagram...");
		this.iJavaProject = iJavaProject;
		this.estimation = estimation;
	}

	private void writeStringToFile(File outputFile, String outputString) throws IOException {
		outputFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getAbsolutePath()));
		writer.write(outputString);
		writer.flush();
		writer.close();
	}

	private IPath createExportFolder() throws JavaModelException {
		var wsRoot = iJavaProject.getProject().getWorkspace().getRoot();

		IPath outputFolder;

		outputFolder = wsRoot.getFolder(iJavaProject.getPath().append("jFamilyCounselor")).getLocation();

		var formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		var timestamp = formatter.format(new Date());

		var reportsFolder = outputFolder.append("jFamilyCounselor-diagrams");
		reportsFolder = reportsFolder.append(String.format("%s-%s-diagram-%s", iJavaProject.getElementName(),
				estimation.toString(), timestamp.toString()));
		reportsFolder.toFile().mkdirs();

		return reportsFolder;
	}

	private Pair<String, Pair<IType, IType>> mapRelevantTypeToMPair(IType relevantType, MTypesPair mPair) {
		Pair<?, ?> pair = mPair.getUnderlyingObject();
		var p1IType = ((IType) pair._1);
		var p2IType = ((IType) pair._2);
		var projectName = relevantType.getJavaProject().getElementName() + "/";

		if ((p1IType.getJavaProject().getElementName() + "/" + p1IType.toString())
				.compareTo(p2IType.getJavaProject().getElementName() + "/" + p2IType.toString()) > 0)
			return new Pair<>(projectName + relevantType.getFullyQualifiedName(), new Pair<>(p2IType, p1IType));

		return new Pair<>(projectName + relevantType.getFullyQualifiedName(), new Pair<>(p1IType, p2IType));
	}

	protected IStatus run(IProgressMonitor monitor) {
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

		var start = Instant.now();

		Map<Pair<IType, IType>, Set<String>> pairToClients = relevantTypes.parallelStream().flatMap(t -> Factory
				.getInstance().createMType(t).relevantReferencesPairs().getElements().stream().flatMap(r -> {
					var aperture = r.aperture();
					var usedTypes = Optional.ofNullable(switch (estimation) {
					case NAME_BASED: {
						yield r.nameBasedUsedTypes();
					}
					case NAME_BASED_LEVENSHTEIN: {
						yield r.nameBasedLevenshteinUsedTypes();
					}
					case ASSIGNMENTS_BASED: {
						yield r.assignemntsBasedUsedTypes();
					}
					case CASTS_BASED: {
						yield r.castsBasedUsedTypes();
					}
					default:
						yield null;
					});

					if (usedTypes.isEmpty()) {
						return Stream.empty();
					}

					var apertureCoverage = usedTypes.get().getElements().size() / aperture;
					if (apertureCoverage <= APERTURE_COVERAGE_THRESHOLD) {
						return usedTypes.get().getElements().stream().map(p -> mapRelevantTypeToMPair(t, p));
					}

					return Stream.empty();
				}).distinct())
				.collect(Collectors.groupingBy(p -> p._2, Collectors.mapping(p -> p._1, Collectors.toSet())));

		var clientsPairs = pairToClients.entrySet().parallelStream().collect(Collectors.toMap(
				e -> e.getKey()._1.getJavaProject().getElementName() + "/" + e.getKey()._1.getFullyQualifiedName()
						+ e.getKey()._2.getJavaProject().getElementName() + "/" + e.getKey()._2.getFullyQualifiedName(),
				e -> e.getValue()));

		Map<String, ITypeHierarchy> typeHierarchiesMap = new ConcurrentHashMap<>();
		Map<String, String> leavesMap = new ConcurrentHashMap<>();
		var correlatedTypes = pairToClients.keySet().stream().flatMap(p -> Stream.of(p._1, p._2)).distinct().toList();

		correlatedTypes.parallelStream().forEach(t -> {
			try {
				ITypeHierarchy typeHierarchy = t.newTypeHierarchy(null);
				String projectName = t.getJavaProject().getElementName() + "/";

				// check that the type is at the base of the hierarchy
				if (typeHierarchy.getSubclasses(t).length != 0) {
					return;
				}

				leavesMap.putIfAbsent(projectName + t.getFullyQualifiedName(), projectName + t.getFullyQualifiedName());

				var superClasses = typeHierarchy.getAllSuperclasses(t);
				// contains only Object
				// TODO: can length be 0 ?
				if (superClasses.length == 1) {
					typeHierarchiesMap.putIfAbsent(projectName + t.getFullyQualifiedName(), typeHierarchy);
					return;
				}

				var rootClass = superClasses[superClasses.length - 2];

				// if the root class is from a java library take the highest class from the
				// project
				if (rootClass.isBinary()) {
					var implementedClasses = Stream.of(superClasses).takeWhile(c -> !c.isBinary())
							.collect(Collectors.toList());
					if (implementedClasses.size() == 0) {
						typeHierarchiesMap.putIfAbsent(projectName + t.getFullyQualifiedName(), typeHierarchy);
						return;
					}
					rootClass = implementedClasses.get(implementedClasses.size() - 1);
				}

				var rootClassFQN = projectName + rootClass.getFullyQualifiedName();
				if (!typeHierarchiesMap.containsKey(rootClassFQN)) {
					typeHierarchiesMap.put(rootClassFQN, rootClass.newTypeHierarchy(null));
				}

			} catch (JavaModelException e) {
				return;
			}
		});

		var hierarchiesList = new ConcurrentLinkedQueue<ParentLink>();
		hierarchiesList.add(new ParentLink("", Constants.OBJECT_FQN));
		typeHierarchiesMap.entrySet().parallelStream().forEach(h -> {
			var rootClassHierarchy = h.getValue();

			// pair of parent and child IType
			var q = new ArrayDeque<Pair<String, IType>>();
			q.add(new Pair<String, IType>(Constants.OBJECT_FQN, rootClassHierarchy.getType()));

			while (q.size() != 0) {
				var pair = q.remove();
				var newParent = pair._2.getJavaProject().getElementName() + "/" + pair._2.getFullyQualifiedName();
				hierarchiesList.add(new ParentLink(pair._1, newParent));

				q.addAll(Stream.of(rootClassHierarchy.getSubclasses(pair._2)).map(c -> new Pair<>(newParent, c))
						.collect(Collectors.toList()));
			}
		});

		typeHierarchiesMap.values().stream().forEach(h -> {
			var superClasses = h.getAllSuperclasses(h.getType());
			if (superClasses.length == 1) {
				// TODO: something else
				return;
			}
			var rootClass = superClasses[superClasses.length - 2];

			ITypeHierarchy rootHierachy = null;
			try {
				rootHierachy = rootClass.newTypeHierarchy(null);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			var q = new ArrayDeque<IType>();
			q.add(rootClass);
			while (q.size() != 0) {
				var hierarchy = q.remove();
				q.addAll(List.of(rootHierachy.getSubclasses(hierarchy)));
			}
		});

		var pairsFQNs = pairToClients.entrySet().parallelStream().flatMap(entry -> {
			var p1FQN = entry.getKey()._1.getJavaProject().getElementName() + "/"
					+ entry.getKey()._1.getFullyQualifiedName();
			var p2FQN = entry.getKey()._2.getJavaProject().getElementName() + "/"
					+ entry.getKey()._2.getFullyQualifiedName();
			if (leavesMap.containsKey(p1FQN) && leavesMap.containsKey(p2FQN)) {
				return Stream.of(new Pair<>(p1FQN + "|" + p2FQN, entry.getValue().size()));
			}
			return Stream.empty();
		}).collect(Collectors.toMap(p -> p._1, p -> p._2));

		var objectMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();

		IPath reportsFolder;
		try {
			reportsFolder = createExportFolder();
			var diagramOutputFile = reportsFolder.append("chord-diagram.html").toFile();
			var pairsJsonFile = reportsFolder.append("pairs.js").toFile();
			var hierarchiesJsonFile = reportsFolder.append("hierarchies.js").toFile();
			var clientsPairsJsonFile = reportsFolder.append("clients.js").toFile();

			var pairsJson = objectMapper.writeValueAsString(pairsFQNs);
			var leavesDataOutput = String.format("var pairs = %s;", pairsJson);

			var hierarchiesJson = objectMapper.writeValueAsString(hierarchiesList.stream().collect(Collectors.toSet()));
			var hierarchiesDataOutput = String.format("var hierarchies = %s;", hierarchiesJson);

			var clientsPairsJson = objectMapper.writeValueAsString(clientsPairs);
			var clientsPairsDataOutput = String.format("var clients = %s;", clientsPairsJson);

			writeStringToFile(pairsJsonFile, leavesDataOutput);
			writeStringToFile(hierarchiesJsonFile, hierarchiesDataOutput);
			writeStringToFile(clientsPairsJsonFile, clientsPairsDataOutput);

			try {
				var chordDiagram = new ChordDiagram(diagramOutputFile);
				var diagramHtmlPath = chordDiagram.getDiagramHtmlPath();
				if (diagramHtmlPath.isEmpty())
					return Status.error("The diagram html source could not be found");
				writeStringToFile(diagramOutputFile,
						Files.readString(diagramHtmlPath.get()).replace("|diagram-title|", estimation + " Diagram"));

				chordDiagram.startBrowser();
			} catch (MalformedURLException e) {
				logger.warning("MalformedURLException encountered: " + e.getMessage());
				return Status.error("MalformedURLException during creation of diagram html URL");
			}
		} catch (JavaModelException e) {
			logger.warning("JavaModelException encountered: " + e.getMessage());
			return Status.error("JavaModelException during creation of output folders");
		} catch (JsonProcessingException e) {
			logger.warning("JsonProcessingException encountered: " + e.getMessage());
			return Status.error("JsonProcessingException during serialization of diagram relations");
		} catch (IOException e) {
			logger.warning("IOException encountered: " + e.getMessage());
			return Status.error("IOException during writing json to the output file");
		}

		var end = Instant.now();

		logger.info("JSON " + estimation + " export report job for " + iJavaProject.getElementName() + " took: "
				+ Duration.between(start, end).getSeconds() + " seconds");

		return Status.OK_STATUS;
	}

	public boolean belongsTo(Object family) {
		return FAMILY.equals(family);
	}
}