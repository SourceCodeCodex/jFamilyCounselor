package ro.lrg.jfamilycounselor.report.html;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import ro.lrg.jfamilycounselor.util.duration.DurationFormatter;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public record HTMLReferencesPair(String referencesPair, Double apertureCoverage, Duration duration,
		List<String> usedTypes) implements HTMLRendable {

	private static Logger logger = jFCLogger.getLogger();

	private static String referencesPairEntryTemplate;
	private static String typesPairEntryTemplate;

	static {
		try {
			var classLoader = HTMLPackage.class.getClassLoader();
			referencesPairEntryTemplate = new String(
					classLoader.getResourceAsStream("references-pair-entry.html").readAllBytes(),
					StandardCharsets.UTF_8);
			typesPairEntryTemplate = new String(classLoader.getResourceAsStream("types-pair-entry.html").readAllBytes(),
					StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.severe("Could not load propper resources");
			System.exit(0);
		}
	}

	public String htmlRaw() {
		var randomUUID = UUID.randomUUID();

		var usedTypesEntries = usedTypes.stream().map(p -> {
			return typesPairEntryTemplate.replace("{TYPES_PAIR}", p);
		}).collect(Collectors.joining("\n"));

		var html = referencesPairEntryTemplate.replace("{USED_TYPES}", usedTypesEntries)
				.replace("{RANDOM_ID}", randomUUID.toString()).replace("{REFERENCES_PAIR}", referencesPair)
				.replace("{APERTURE_COVERAGE}", apertureCoverage.toString())
				.replace("{DURATION}", DurationFormatter.format(duration));

		return html;
	}

}
