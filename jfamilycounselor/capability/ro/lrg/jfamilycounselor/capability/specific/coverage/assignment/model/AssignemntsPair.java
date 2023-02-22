package ro.lrg.jfamilycounselor.capability.specific.coverage.assignment.model;

import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class AssignemntsPair extends Pair<Assignment, Assignment> {

    private int depth = 0;

    private boolean passedCombination = true;

    public AssignemntsPair(Assignment _1, Assignment _2) {
	super(_1, _2);
    }

    public static AssignemntsPair initialAssignmentsPair(Pair<IJavaElement, IJavaElement> referencesPair, Pair<IType, IType> referencesTypes) {
	var pair = new AssignemntsPair(new Assignment(referencesPair._1, Optional.of(referencesPair._1), referencesTypes._1), new Assignment(referencesPair._2, Optional.of(referencesPair._2), referencesTypes._2));
	pair.passedCombination = false;
	return pair;
    }

    public void setDepth(int depth) {
	this.depth = depth;
    }

    public int depth() {
	return depth;
    }
    
    public AssignemntsPair swap() {
	var newPair = new AssignemntsPair(_2, _1);
	newPair.depth = depth;
	newPair.passedCombination = passedCombination;
        return  newPair;
    }

    public boolean passedCombination() {
	return passedCombination;
    }
    
    public void setPassedCombination(boolean passedCombination) {
	this.passedCombination = passedCombination;
    }

}
