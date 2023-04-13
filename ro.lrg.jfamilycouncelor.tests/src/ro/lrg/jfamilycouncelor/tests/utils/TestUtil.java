package ro.lrg.jfamilycouncelor.tests.utils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;

public class TestUtil {
	
	public static void importProject(String projectName, String fileName) {

		try {
			URL url = Platform.getBundle("ro.lrg.jfamilycouncelor.tests").getEntry("/");
			url = FileLocator.resolve(url);
			String path = url.getPath() + "testdata/";
			ZipFile theFile = new ZipFile(new File(path + fileName));
			ZipFileStructureProvider zp = new ZipFileStructureProvider(theFile);

			IWorkspaceRoot workSpaceRoot =  ResourcesPlugin.getWorkspace().getRoot();
			IProject project = workSpaceRoot.getProject(projectName);
			project.create(null);
			project.open(null);

			IPath container = workSpaceRoot.getProject(projectName).getFullPath();
			ImportOperation importOp = new ImportOperation(container, zp.getRoot(), zp, new IOverwriteQuery() {
				public String queryOverwrite(String pathString) {
					return IOverwriteQuery.ALL;
				}
			});

			importOp.setCreateContainerStructure(true);
			importOp.setOverwriteResources(true);
			importOp.run(null);
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,null);
			} catch(InterruptedException e) {}
			theFile.close();		

		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());			
		}
	}

	public static void deleteProject(String projectName) {
		try {
			IWorkspaceRoot workSpaceRoot =  ResourcesPlugin.getWorkspace().getRoot();
			IProject project = workSpaceRoot.getProject(projectName);
			project.close(null);
			project.delete(true, null);
		} catch (CoreException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Optional<IJavaProject> getProject(String projectName) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IJavaModel javaModel = JavaCore.create(workspaceRoot);
		IJavaProject theProject = javaModel.getJavaProject(projectName);

		try {
			Arrays.toString(theProject.getAllPackageFragmentRoots());
		} catch (JavaModelException e) {
			throw new Error(e);
		}
		return Optional.ofNullable(theProject);

	}
}
