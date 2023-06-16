package ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * 
 * Throughout the assignments-based approach, we record the following:
 * 
 * @param assignmentsPairs pairs of assignments that need to be derived
 * 
 * @param inconclusive     if the derivation of a pair leads to an expression
 *                         that cannot be derived / the approach does not handle
 *                         that kind of expression, we record the pair of
 *                         lowestRecordedType
 * 
 * @param resolved         pairs of types that were successfully resolved
 *                         through derivation (concrete subtypes of the
 *                         reference's type)
 * 
 * @author rosualinpetru
 *
 */
public record State(
	Stack<AssignemntsPair> assignmentsPairs,
	List<InconclusiveTypesPair> inconclusive,
	List<Pair<IType, IType>> resolved) {

    public static State empty() {
	return new State(new Stack<>(), new ArrayList<>(), new ArrayList<>());
    }

    public void markInvalid(AssignemntsPair assignemntsPair) {
	inconclusive.add(new InconclusiveTypesPair(Pair.of(assignemntsPair._1.lowestRecordedType(), assignemntsPair._2.lowestRecordedType()), true));
    }

}