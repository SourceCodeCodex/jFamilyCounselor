package ro.lrg.jfamilycounselor.report.html;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import ro.lrg.jfamilycounselor.util.duration.DurationFormatter;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public record HTMLType(String projectName, String typeFQN, Double apertureCoverage, Duration duration, List<HTMLReferencesPair> referencesPairs) implements HTMLRendable {

    private static Logger logger = jFCLogger.getLogger();

    private static String typeTemplate;

    static {
	try {
	    var classLoader = HTMLPackage.class.getClassLoader();
	    typeTemplate = new String(classLoader.getResourceAsStream("type.html").readAllBytes(), StandardCharsets.UTF_8);
	} catch (Exception e) {
	    logger.severe("Could not load propper resources");
	    System.exit(0);
	}
    }

    public String htmlRaw() {
	var referencesPairsHTML = referencesPairs.stream()
		.map(p -> p.html())
		.filter(o -> o.isPresent())
		.map(o -> o.get())
		.collect(Collectors.joining("\n"));

	var html = typeTemplate
		.replace("{REFERENCES_PAIRS}", referencesPairsHTML)
		.replace("{ANALYSIS_TIME}", DurationFormatter.format(duration))
		.replace("{APERTURE_COVERAGE_AGGREGATED}", apertureCoverage.toString())
		.replace("{TYPE_FQN}", typeFQN)
		.replace("{PROJECT_NAME}", projectName);

	return html;
    }

}
