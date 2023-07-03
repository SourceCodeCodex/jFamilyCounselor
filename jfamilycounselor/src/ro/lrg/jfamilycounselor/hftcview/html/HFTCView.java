package ro.lrg.jfamilycounselor.hftcview.html;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import ro.lrg.jfamilycounselor.report.html.ReportIndex;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * @author Bogdan316
 */
public class HFTCView {
	private static final Logger logger = jFCLogger.getLogger();
	private static String hftcTemplate;

	static {
		try {
			var classLoader = HFTCView.class.getClassLoader();
			hftcTemplate = new String(classLoader.getResourceAsStream("hftc-view-index.html").readAllBytes(),
					StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.severe("Could not load propper resources in jFamilyCounselor");
			Platform.getLog(ReportIndex.class).error("Could not load propper resources in jFamilyCounselor");
		}
	}

	private final URL diagramHtmlUrl;

	public HFTCView(File htmlFilePath) throws MalformedURLException {
		diagramHtmlUrl = Paths.get(htmlFilePath.getAbsolutePath()).toUri().toURL();
	}

	public String getHtml(String viewTitle) {
		return hftcTemplate.replace("{TITLE}", viewTitle);
	}

	public void startBrowser() {
		try {
			var support = PlatformUI.getWorkbench().getBrowserSupport();
			var browser = support.createBrowser(IWorkbenchBrowserSupport.AS_VIEW, null, null, null);

			var diagramUrl = diagramHtmlUrl;
			browser.openURL(diagramUrl);
		} catch (PartInitException e) {
			logger.warning("PartInitException encountered: " + e.getMessage());
		}
	}
}
