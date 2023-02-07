package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public record State(
	Stack<AssignemntsPair> assignmentsPairs,
	Set<Pair<IType, IType>> inconclusive,
	Set<Pair<IType, IType>> resolved) {

    public static State empty() {
	return new State(new Stack<>(), new HashSet<>(), new HashSet<>());
    }

}