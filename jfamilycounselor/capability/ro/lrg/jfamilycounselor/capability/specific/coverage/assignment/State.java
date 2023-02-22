package ro.lrg.jfamilycounselor.capability.specific.coverage.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.specific.coverage.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public record State(
	Stack<AssignemntsPair> assignmentsPairs,
	List<Pair<IType, IType>> inconclusive,
	List<Pair<IType, IType>> resolved) {

    public static State empty() {
	return new State(new Stack<>(), new ArrayList<>(), new ArrayList<>());
    }

}