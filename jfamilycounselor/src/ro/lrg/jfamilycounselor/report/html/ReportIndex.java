package ro.lrg.jfamilycounselor.report.html;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class ReportIndex {

	private static Logger logger = jFCLogger.getLogger();

	private static String indexTemplate;

	static {
		try {
			var classLoader = ReportIndex.class.getClassLoader();
			indexTemplate = new String(classLoader.getResourceAsStream("report-index.html").readAllBytes(),
					StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.severe("Could not load propper resources in jFamilyCounselor");
			Platform.getLog(ReportIndex.class).error("Could not load propper resources in jFamilyCounselor");
		}
	}

	public static String html(String title) {
		return indexTemplate.replace("{TITLE}", title);
	}

}