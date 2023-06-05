package ro.lrg.jfamilycounselor.capability.specific.coverage.assignment;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.generic.type.DistinctConcreteConeProductCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignment.handler.ParameterParameterHandler;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignment.handler.ThisParameterHandler;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Capability that computes the used types using the assignments-based
 * estimation for a pair of references.
 * 
 * It is important to also pass the references types since they will be the
 * first mostConcreteRecordedType values.
 * 
 * @author rosualinpetru
 *
 */
public class AssignmentUsedTypesCapability {
    private AssignmentUsedTypesCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    private static final int MAX_DEPTH = 4;

    private static final double INCONCLUSIVE_THRESHOLD = 1. / 3.;

    public static Optional<List<Pair<IType, IType>>> usedTypes(Pair<IJavaElement, IJavaElement> referencesPair, Pair<IType, IType> referencesTypes) {
	var state = State.empty();

	var initialPair = AssignemntsPair.initialAssignmentsPair(referencesPair, referencesTypes);

	state.assignmentsPairs().push(initialPair);

	while (!state.assignmentsPairs().isEmpty()) {
	    var assignemntsPair = state.assignmentsPairs().pop();

	    if (assignemntsPair.depth() > MAX_DEPTH) {
		markInvalid(assignemntsPair, state);
		continue;
	    }

	    dispatchToHandlers(assignemntsPair, state);
	}

	var totalAssignmentsPaths = state.resolved().size() + state.inconclusive().size();

	if (state.inconclusive().size() * 1.0 / totalAssignmentsPaths >= INCONCLUSIVE_THRESHOLD)
	    return Optional.empty();

	var distinctConcreteConeProduct = DistinctConcreteConeProductCapability.product(referencesTypes._1, referencesTypes._2);

	return Optional.of(state.resolved().stream().distinct().filter(pair -> distinctConcreteConeProduct.map(p -> p.contains(pair)).orElse(true)).toList());

    }

    private static void dispatchToHandlers(AssignemntsPair assignemntsPair, State state) {
	if (assignemntsPair._1.writingElement().isEmpty() || assignemntsPair._2.writingElement().isEmpty()) {
	    markInvalid(assignemntsPair, state);
	    return;
	}

	var we1 = assignemntsPair._1.writingElement().get();
	var we2 = assignemntsPair._2.writingElement().get();

	if (we1 instanceof IMethod || we1 instanceof IField || we2 instanceof IMethod || we2 instanceof IField) {
	    markInvalid(assignemntsPair, state);
	    return;
	}

	if (we1 instanceof ILocalVariable && we2 instanceof ILocalVariable) {
	    var newAssignmentsPairs = ParameterParameterHandler.handle(assignemntsPair);
	    newAssignmentsPairs.forEach(state.assignmentsPairs()::push);
	    return;
	}

	if (we1 instanceof IType && we2 instanceof ILocalVariable) {
	    var newAssignmentsPairs = ThisParameterHandler.handle(assignemntsPair);
	    newAssignmentsPairs.forEach(state.assignmentsPairs()::push);
	    return;
	}

	if (we1 instanceof ILocalVariable && we2 instanceof IType) {
	    var newAssignmentsPairs = ThisParameterHandler.handle(assignemntsPair.swap());
	    newAssignmentsPairs.stream().map(AssignemntsPair::swap).forEach(state.assignmentsPairs()::push);
	    return;
	}

	if (we1 instanceof IType t1 && we2 instanceof IType t2) {
	    state.resolved().add(Pair.of(t1, t2));
	    return;
	}

	logger.warning("Assignments pair was not handled: " + assignemntsPair);

    }

    private static void markInvalid(AssignemntsPair assignemntsPair, State stacks) {
	stacks.inconclusive().add(Pair.of(assignemntsPair._1.mostConcreteRecordedType(), assignemntsPair._2.mostConcreteRecordedType()));
    }

}
