package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class AssignemntsPair extends Pair<Assignment, Assignment> {

    private int depth = 0;

    private boolean isInitial = false;

    public AssignemntsPair(Assignment _1, Assignment _2) {
	super(_1, _2);
    }
    
    public static AssignemntsPair initial(Assignment _1, Assignment _2) {
	var pair = new AssignemntsPair(_1, _2);
	pair.isInitial = true;
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
	newPair.isInitial = isInitial;
	newPair.depth = depth;
	return newPair;
    }

    public boolean isInitial() {
	return isInitial;
    }
}
