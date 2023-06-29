package ro.lrg.jfamilycounselor.diagram;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class ChordDiagram {
	private static final Logger logger = jFCLogger.getLogger();
	private final URL diagramHtmlUrl;
	
	public ChordDiagram(File htmlFilePath) throws MalformedURLException {
		diagramHtmlUrl = Paths.get(htmlFilePath.getAbsolutePath()).toUri().toURL();
	}

	public Optional<java.nio.file.Path> getDiagramHtmlPath() {
		var bundle = Platform.getBundle("jfamilycounselor");
		var sourceFolderUrl = FileLocator.find(bundle, new Path("resources"), null);

		try {
			var sourceFolderPath = FileLocator.toFileURL(sourceFolderUrl).getPath();
			var htmlPath = Paths.get(sourceFolderPath, "chord-diagram.html");
			return Optional.of(htmlPath);
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
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
