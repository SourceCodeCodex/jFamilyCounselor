package ro.lrg.jfamilycounselor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jfamilycounselor.metamodel.entity.MProject;
import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.capability.project.JavaProjectsCapability;
import ro.lrg.jfamilycounselor.test.util.TestUtil;
import ro.lrg.xcore.metametamodel.Group;

public class TestWineBarExample {

    private MProject currentProject;

    private MType findClass(String fullyQualifiedName) {
	for (MType aClass : currentProject.relevantTypesByReferences().getElements()) {
	    if (aClass.getUnderlyingObject().getFullyQualifiedName().equals(fullyQualifiedName)) {
		return aClass;
	    }
	}
	return null;
    }

    @BeforeEach
    public void loadProject() {
	TestUtil.importProject("WineBar", "WineBar.zip");
	currentProject = jfamilycounselor.metamodel.factory.Factory.getInstance().createMProject(TestUtil.getProject("WineBar").get());
	JavaProjectsCapability.reloadProjects();
    }

    @AfterEach
    public void deleteProject() {
	TestUtil.deleteProject("WineBar");
    }

    @Test
    public void testTwoSetters() {
	String className = "ro.lrg.testdata.winebar1.WaiterTray";
	MType theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	Group<MReferencesPair> refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.5),
		theClass.assignmentsBasedApertureCoverage());
	className = "ro.lrg.testdata.winebar1.WineBar";
	theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.25),
		theClass.assignmentsBasedApertureCoverage());
    }

    @Test
    public void testOneSetter() {
	String className = "ro.lrg.testdata.winebar2.WaiterTray";
	MType theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	Group<MReferencesPair> refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.5),
		theClass.assignmentsBasedApertureCoverage());
	className = "ro.lrg.testdata.winebar2.WineBar";
	theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.25),
		theClass.assignmentsBasedApertureCoverage());
    }

    @Test
    public void testSolveAtConcreteLeafType4LocalVariable() {
	String className = "ro.lrg.testdata.winebar3.WaiterTray";
	MType theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	Group<MReferencesPair> refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.5),
		theClass.assignmentsBasedApertureCoverage());
	className = "ro.lrg.testdata.winebar3.WineBar";
	theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.5),
		theClass.assignmentsBasedApertureCoverage());
    }

    @Test
    public void testSolveAtConcreteLeafType4MethodReturnedType() {
	String className = "ro.lrg.testdata.winebar4.WaiterTray";
	MType theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	Group<MReferencesPair> refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.5),
		theClass.assignmentsBasedApertureCoverage());
	className = "ro.lrg.testdata.winebar4.WineBar";
	theClass = findClass(className);
	assertNotNull("Class " + className + " not found in project " + currentProject.toString(), theClass);
	refPairs = theClass.relevantReferencesPairs();
	assertEquals("Incorrect number of reference pairs for class " + className, 1, refPairs.getElements().size());
	assertEquals("Incorrect aperture value for " + refPairs.getElements().get(0), 4, refPairs.getElements().get(0).aperture().intValue());
	assertEquals("Incorrect assignmend-based aperture coverage computation for " + className,
		Double.valueOf(0.5),
		theClass.assignmentsBasedApertureCoverage());
    }

}
