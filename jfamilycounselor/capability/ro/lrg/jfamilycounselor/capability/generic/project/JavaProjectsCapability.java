package ro.lrg.jfamilycounselor.capability.generic.project;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * This is a special capability in the sense that it changes its behavior based
 * on the changes of the workspace. Therefore, the reload method is called
 * whenever a close/open operation is performed on any project.
 * 
 * @author rosualinpetru
 *
 */
public class JavaProjectsCapability {
    private JavaProjectsCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    private static List<IJavaProject> javaProjects = javaProjects();

    public static List<IJavaProject> getJavaProjects() {
	return javaProjects;
    }

    public static void reloadProjects() {
	javaProjects = javaProjects();
    }

    private static List<IJavaProject> javaProjects() {
	List<IJavaProject> projectList = new LinkedList<IJavaProject>();
	IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	IProject[] projects = workspaceRoot.getProjects();
	for (int i = 0; i < projects.length; i++) {
	    IProject project = projects[i];
	    try {
		if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
		    projectList.add(JavaCore.create(project));
		}
	    } catch (CoreException e) {
		logger.warning("CoreException encountered for: " + project.getName() + ". Message: " + e.getMessage());
	    }
	}
	return projectList;
    }

}
