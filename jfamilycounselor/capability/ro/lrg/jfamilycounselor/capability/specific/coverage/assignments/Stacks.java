package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments;

import java.util.Stack;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public record Stacks(
	Stack<AssignemntsPair> assignmentsPairs,
	Stack<Pair<IType, IType>> inconclusive,
	Stack<Pair<IType, IType>> resolved) {

    public static Stacks empty() {
	return new Stacks(new Stack<>(), new Stack<>(), new Stack<>());
    }

}