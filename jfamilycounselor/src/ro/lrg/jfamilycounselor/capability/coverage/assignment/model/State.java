package ro.lrg.jfamilycounselor.capability.coverage.assignment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * 
 * Throughout the assignments-based approach, we record the following:
 * 
 * - assignmentsPairs = pairs of assignments that needs to be derived
 * 
 * - inconclusive = if the derivation of a pair leads to an expression that
 * cannot be derived / the approach does not handle that kind of expression, we
 * record the pair of mostConcreteRecordedTypes
 * 
 * - resolved = pairs of types that were successfully resolved through
 * derivation
 * 
 * @author rosualinpetru
 *
 */
public record State(
	Stack<AssignemntsPair> assignmentsPairs,
	List<Pair<IType, IType>> inconclusive,
	List<Pair<IType, IType>> resolved) {

    public static State empty() {
	return new State(new Stack<>(), new ArrayList<>(), new ArrayList<>());
    }

    public void markInvalid(AssignemntsPair assignemntsPair) {
	inconclusive.add(Pair.of(assignemntsPair._1.mostConcreteRecordedType(), assignemntsPair._2.mostConcreteRecordedType()));
    }

}