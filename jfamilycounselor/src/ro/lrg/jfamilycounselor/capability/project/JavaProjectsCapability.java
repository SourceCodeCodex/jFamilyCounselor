package ro.lrg.jfamilycounselor.capability.project;

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
 * Capability that determines the opened Java projects in the workspace. It also
 * caches the result, which is eventually invalidated whenever there is a change
 * in the workspace (for performance reasons).
 * 
 * @author rosualinpetru
 *
 */
public class JavaProjectsCapability {
    private JavaProjectsCapability() {
    }

    private static final Logger logger = jFCLogger.getLogger();

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
