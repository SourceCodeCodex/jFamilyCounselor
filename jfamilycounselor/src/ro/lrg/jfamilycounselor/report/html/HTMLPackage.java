package ro.lrg.jfamilycounselor.report.html;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public record HTMLPackage(String packageName, List<String> types) implements HTMLRendable {

	private static Logger logger = jFCLogger.getLogger();

	private static String packageEntryTemplate;
	private static String typeEntryTemplate;

	static {
		try {
			var classLoader = HTMLPackage.class.getClassLoader();
			packageEntryTemplate = new String(classLoader.getResourceAsStream("package-entry.html").readAllBytes(),
					StandardCharsets.UTF_8);
			typeEntryTemplate = new String(classLoader.getResourceAsStream("type-entry.html").readAllBytes(),
					StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.severe("Could not load propper resources");
			System.exit(0);
		}
	}

	public String htmlRaw() {
		var randomUUID = UUID.randomUUID();

		var typeEntries = types.stream().map(t -> {
			return typeEntryTemplate.replace("{TYPE_SIMPLE_NAME}", t);
		}).collect(Collectors.joining("\n"));

		var html = packageEntryTemplate.replace("{TYPES}", typeEntries).replace("{RANDOM_ID}", randomUUID.toString())
				.replace("{PACKAGE_NAME}", packageName);

		return html;
	}

}
