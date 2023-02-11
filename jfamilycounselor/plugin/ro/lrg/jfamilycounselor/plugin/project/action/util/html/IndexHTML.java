package ro.lrg.jfamilycounselor.plugin.project.action.util.html;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public record IndexHTML(String projectName, List<HTMLPackage> packages) implements HTMLRendable {
    
    private static Logger logger = jFCLogger.getJavaLogger();

    private static String indexTemplate;

    static {
	try {
	    var classLoader = IndexHTML.class.getClassLoader();
	    indexTemplate = new String(classLoader.getResourceAsStream("index.html").readAllBytes(), StandardCharsets.UTF_8);
	} catch (Exception e) {
	    logger.severe("Could not load propper resources");
	    System.exit(0);
	}
    }

    public String htmlRaw() {
	var packagesHtml = packages.stream()
		.sorted(Comparator.comparing(HTMLPackage::packageName))
		.map(p -> p.html())
		.filter(o -> o.isPresent())
		.map(o -> o.get())
		.collect(Collectors.joining("\n"));

	return indexTemplate
		.replace("{PACKAGE_ENTRIES}", packagesHtml)
		.replace("{PROJECT_NAME}", projectName);
    }

}