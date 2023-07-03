package ro.lrg.jfamilycounselor.hftcview;

import static ro.lrg.jfamilycounselor.util.stringify.Stringify.stringify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.hftcview.html.HFTCView;
import ro.lrg.jfamilycounselor.report.ExportReportJob;
import ro.lrg.jfamilycounselor.report.RelevantTypesJob;
import ro.lrg.jfamilycounselor.util.Constants;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * @author Bogdan316
 */
public class ExportHFTCViewJob extends Job {

	private static Cache<IType, ITypeHierarchy> hierarchyCache = MonitoredUnboundedCache.getHighConsumingCache();

	private static final double APERTURE_COVERAGE_THRESHOLD = 0.5;

	private static Logger logger = jFCLogger.getLogger();

	private final IJavaProject iJavaProject;
	private final EstimationType estimation;

	public ExportHFTCViewJob(EstimationType estimation, IJavaProject iJavaProject) {
		super(iJavaProject.getElementName() + " - " + estimation + " - HFTC View Export");
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

		var reportsFolder = outputFolder.append("hftc-views");
		reportsFolder = reportsFolder.append(String.format("%s-%s-hftc-view-%s", iJavaProject.getElementName(),
				estimation.toString(), timestamp.toString()));
		reportsFolder.toFile().mkdirs();

		return reportsFolder;
	}

	private Pair<String, Pair<IType, IType>> sortTypeEntries(IType relevantType, Pair<IType, IType> pair) {
		var s1 = stringify(pair._1);
		var s2 = stringify(pair._2);

		var s = stringify(relevantType);

		if (s1.compareTo(s2) > 0)
			return Pair.of(s, Pair.of(pair._2, pair._1));

		return Pair.of(s, Pair.of(pair._1, pair._2));
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

		var subMonitor1 = SubMonitor.convert(monitor, "(1/3) Computing Aperture Coverages", relevantTypes.size());

		var pairToClients = relevantTypes.parallelStream().flatMap(t -> {
			var mType = Factory.getInstance().createMType(t);

			var correlations = switch (estimation) {
			case TYPE_PARAMETERS_BASED: {
				yield mType.relevantTypeParametersPairs().getElements().stream().flatMap(r -> {
					var aperture = r.aperture();
					var usedTypes = r.usedTypes();

					var apertureCoverage = (1.0 * usedTypes.getElements().size()) / aperture;

					logger.info(r + ": " + apertureCoverage);

					if (apertureCoverage <= APERTURE_COVERAGE_THRESHOLD)
						return usedTypes.getElements().stream().map(p -> sortTypeEntries(t, p.getUnderlyingObject()));

					return Stream.empty();
				}).distinct();
			}
			default: {
				yield mType.relevantReferencesPairs().getElements().stream().flatMap(r -> {
					var aperture = r.aperture();
					var usedTypes = Optional.ofNullable(switch (estimation) {
					case NAME_BASED: {
						yield r.nameBasedUsedTypes();
					}
					case NAME_BASED_LEVENSHTEIN: {
						yield r.nameBasedLevenshteinUsedTypes();
					}
					case ASSIGNMENTS_BASED: {
						yield r.assignmentsBasedUsedTypes();
					}
					case CASTS_BASED: {
						yield r.castsBasedUsedTypes();
					}
					default:
						yield null;
					});

					if (usedTypes.isEmpty())
						return Stream.empty();

					var apertureCoverage = (1.0 * usedTypes.get().getElements().size()) / aperture;

					logger.info(r + ": " + apertureCoverage);

					if (apertureCoverage <= APERTURE_COVERAGE_THRESHOLD)
						return usedTypes.get().getElements().stream()
								.map(p -> sortTypeEntries(t, p.getUnderlyingObject()));

					return Stream.empty();
				}).distinct();
			}
			};

			subMonitor1.split(1);

			return correlations;
		}).collect(Collectors.groupingBy(p -> p._2, Collectors.mapping(p -> p._1, Collectors.toSet())));

		var correlatedTypes = pairToClients.keySet().stream().flatMap(p -> Stream.of(p._1, p._2)).distinct().toList();

		var subMonitor2 = SubMonitor.convert(monitor, "(2/3) Computing Type Hierarchies", correlatedTypes.size());

		var typeHierarchiesMap = new ConcurrentHashMap<String, ITypeHierarchy>();
		var leaves = new ConcurrentLinkedQueue<IType>();

		correlatedTypes.parallelStream().forEach(t -> {
			try {
				var typeHierarchyOpt = hierarchyCache.get(t);

				var typeHierarchy = typeHierarchyOpt.isPresent() ? typeHierarchyOpt.get() : t.newTypeHierarchy(null);

				if (typeHierarchyOpt.isEmpty())
					hierarchyCache.put(t, typeHierarchy);

				var s = stringify(t);

				logger.info("Computing hierarchy for " + s);

				// check that the type is at the base of the hierarchy
				if (typeHierarchy.getSubclasses(t).length != 0) {
					subMonitor2.split(1);
					return;
				}

				leaves.add(t);

				var superClasses = typeHierarchy.getAllSuperclasses(t);

				// contains only Object
				if (superClasses.length <= 1) {
					typeHierarchiesMap.putIfAbsent(s, typeHierarchy);
					subMonitor2.split(1);
					return;
				}

				var rootClass = superClasses[superClasses.length - 2];

				// if the root class is from a java library take the highest class from the
				// project
				if (rootClass.isBinary()) {
					var implementedClasses = Stream.of(superClasses).takeWhile(c -> !c.isBinary())
							.collect(Collectors.toList());
					if (implementedClasses.size() == 0) {
						typeHierarchiesMap.putIfAbsent(s, typeHierarchy);
						subMonitor2.split(1);
						return;
					}
					rootClass = implementedClasses.get(implementedClasses.size() - 1);
				}

				var rootClassFQN = stringify(rootClass);
				if (!typeHierarchiesMap.containsKey(rootClassFQN)) {
					typeHierarchiesMap.put(rootClassFQN, rootClass.newTypeHierarchy(null));
				}

			} catch (JavaModelException e) {
				subMonitor2.split(1);
				return;
			}
		});

		var subMonitor3 = SubMonitor.convert(monitor, "(3/3) Rebuilding Type Hierarchies Trees",
				typeHierarchiesMap.size());

		var hierarchiesList = new ConcurrentLinkedQueue<ParentLink>();

		hierarchiesList.add(new ParentLink("", Constants.OBJECT_FQN));

		typeHierarchiesMap.entrySet().parallelStream().forEach(h -> {
			var rootClassHierarchy = h.getValue();

			// pair of parent and child IType
			var q = new ArrayDeque<Pair<String, IType>>();
			q.add(Pair.of(Constants.OBJECT_FQN, rootClassHierarchy.getType()));

			while (q.size() != 0) {
				var pair = q.remove();
				var newParent = stringify(pair._2);
				hierarchiesList.add(new ParentLink(pair._1, newParent));

				q.addAll(Stream.of(rootClassHierarchy.getSubclasses(pair._2)).map(c -> Pair.of(newParent, c))
						.collect(Collectors.toList()));

			}

			subMonitor3.split(1);
		});
		
		var clientsMap = pairToClients.entrySet().parallelStream()
				.filter(e ->  leaves.contains(e.getKey()._1) && leaves.contains(e.getKey()._2))
				.collect(Collectors.toMap(e -> stringify(e.getKey()._1) + "|" + stringify(e.getKey()._2), e -> e.getValue()));
		
		try {

			var objectMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
			var reportsFolder = createExportFolder();
			
			var diagramOutputFile = reportsFolder.append("index.html").toFile();
			var hierarchiesJsonFile = reportsFolder.append("hierarchies.js").toFile();
			var clientsPairsJsonFile = reportsFolder.append("clients.js").toFile();

			var clientsMapOutputJSON = String.format("var clients = %s;", objectMapper.writeValueAsString(clientsMap));

			var hierarchiesJson = objectMapper
					.writeValueAsString(hierarchiesList.stream().collect(Collectors.toCollection(TreeSet::new)));
			var hierarchiesDataOutput = String.format("var hierarchies = %s;", hierarchiesJson);

			writeStringToFile(hierarchiesJsonFile, hierarchiesDataOutput);
			writeStringToFile(clientsPairsJsonFile, clientsMapOutputJSON);

			try {
				var hftcView = new HFTCView(diagramOutputFile);
				writeStringToFile(diagramOutputFile,
						hftcView.getHtml(iJavaProject.getElementName() + " - " + estimation + " - HFTC View"));
				hftcView.startBrowser();
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

		try {
			iJavaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			logger.warning("CoreException encountered: " + e.getMessage());
		}

		logger.info("JSON " + estimation + " HFTC view export job for " + iJavaProject.getElementName() + " took: "
				+ Duration.between(start, end).getSeconds() + " seconds");

		return Status.OK_STATUS;
	}

	public boolean belongsTo(Object family) {
		return ExportReportJob.FAMILY.equals(family);
	}
}