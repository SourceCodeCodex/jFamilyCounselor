package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.IPDerivationCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.PPDerivationCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.expression.ExpressionDerivationCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.Assignment;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.list.CommonOperations;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class AssignmentsUsedTypesCapability {
    private AssignmentsUsedTypesCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    private static final int MAX_DEPTH = 3;

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

	    if (assignemntsPair._1.writingElement().isEmpty() || assignemntsPair._2.writingElement().isEmpty()) {
		markInvalid(assignemntsPair, state);
		continue;
	    }

	    var we1 = assignemntsPair._1.writingElement().get();
	    var we2 = assignemntsPair._2.writingElement().get();

	    if (we1 instanceof IMethod || we1 instanceof IField || we2 instanceof IMethod || we2 instanceof IField) {
		markInvalid(assignemntsPair, state);
		continue;
	    }

	    if (we1 instanceof ILocalVariable param1 && we2 instanceof ILocalVariable param2) {
		handlePP(param1, param2, assignemntsPair, state);
		continue;
	    }

	    if (we1 instanceof ILocalVariable param1 && we2 instanceof IType t2) {
		handlePT(param1, t2, assignemntsPair, state);
		continue;
	    }

	    if (we1 instanceof IType t1 && we2 instanceof ILocalVariable param2) {
		handleTP(t1, param2, assignemntsPair, state);
		continue;
	    }

	    if (we1 instanceof IType t1 && we2 instanceof IType t2) {
		state.resolved().add(new Pair<>(t1, t2));
		continue;
	    }

	    logger.warning("Assignments pair was not handled: " + assignemntsPair);
	}

	return Optional.of(List.copyOf(state.resolved()));

    }

    // **************************************************************************************************************
    // Invoker-Parameter Handling
    // **************************************************************************************************************
    private static void handleTP(IType t1, ILocalVariable param2, AssignemntsPair assignemntsPair, State stacks) {
	if (!param2.isParameter())
	    logger.severe(param2.getTypeSignature() + " was not a parameter. This cannot happen!");

	var initialExpressions = IPDerivationCapability.derive(param2);

	initialExpressions.forEach(pairF -> {
	    var pair = pairF.get();

	    var intraDerivation = ExpressionDerivationCapability.derive(pair._2);

	    if (assignemntsPair.passedCombination() || pair._1.isEmpty()) {
		intraDerivation.stream().forEach(dragons -> {
		    var newAssignmentsPair = new AssignemntsPair(
			    assignemntsPair._1,
			    new Assignment(assignemntsPair._2.reference(), dragons._1.map(j -> (IJavaElement) j), dragons._2.orElse(assignemntsPair._2.mostConcreteRecordedType())));

		    newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);

		    if (pair._1.isEmpty())
			newAssignmentsPair.setPassedCombination(false);

		    stacks.assignmentsPairs().push(newAssignmentsPair);
		});
	    } else {
		var invoker = pair._1.get();

		var intraDerivationType = ExpressionDerivationCapability.derive(invoker);

		var product = CommonOperations.cartesianProduct(intraDerivationType, intraDerivation);

		product.stream().forEach(dragons -> {
		    var r1 = dragons._1;
		    var r2 = dragons._2;

		    var newAssignmentsPair = new AssignemntsPair(
			    new Assignment(assignemntsPair._1.reference(), r1._1.map(j -> (IJavaElement) j), r1._2.orElse(assignemntsPair._1.mostConcreteRecordedType())),
			    new Assignment(assignemntsPair._2.reference(), r2._1.map(j -> (IJavaElement) j), r2._2.orElse(assignemntsPair._2.mostConcreteRecordedType())));

		    newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);
		    stacks.assignmentsPairs().push(newAssignmentsPair);
		});
	    }

	});
    }

    // **************************************************************************************************************
    // Parameter-Invoker Handling
    // **************************************************************************************************************
    private static void handlePT(ILocalVariable param1, IType t2, AssignemntsPair assignemntsPair, State stacks) {
	if (!param1.isParameter())
	    logger.severe(param1.getTypeSignature() + " was not a parameter. This cannot happen!");

	var initialExpressions = IPDerivationCapability.derive(param1);

	initialExpressions.forEach(pairF -> {
	    var pair = pairF.get();

	    var intraDerivation = ExpressionDerivationCapability.derive(pair._2);

	    if (assignemntsPair.passedCombination() || pair._1.isEmpty()) {
		intraDerivation.stream().forEach(dragons -> {
		    var newAssignmentsPair = new AssignemntsPair(
			    new Assignment(assignemntsPair._1.reference(), dragons._1.map(j -> (IJavaElement) j), dragons._2.orElse(assignemntsPair._1.mostConcreteRecordedType())),
			    assignemntsPair._2);

		    newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);

		    if (pair._1.isEmpty())
			newAssignmentsPair.setPassedCombination(false);

		    stacks.assignmentsPairs().push(newAssignmentsPair);
		});
	    } else {
		var invoker = pair._1.get();

		var intraDerivationType = ExpressionDerivationCapability.derive(invoker);

		var product = CommonOperations.cartesianProduct(intraDerivation, intraDerivationType);

		product.stream().forEach(dragons -> {
		    var r1 = dragons._1;
		    var r2 = dragons._2;

		    var newAssignmentsPair = new AssignemntsPair(
			    new Assignment(assignemntsPair._1.reference(), r1._1.map(j -> (IJavaElement) j), r1._2.orElse(assignemntsPair._1.mostConcreteRecordedType())),
			    new Assignment(assignemntsPair._2.reference(), r2._1.map(j -> (IJavaElement) j), r2._2.orElse(assignemntsPair._2.mostConcreteRecordedType())));

		    newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);
		    stacks.assignmentsPairs().push(newAssignmentsPair);
		});
	    }

	});
    }

    // **************************************************************************************************************
    // Parameter-Parameter Handling
    // **************************************************************************************************************
    private static void handlePP(ILocalVariable param1, ILocalVariable param2, AssignemntsPair assignemntsPair, State stacks) {
	if (!param1.isParameter() || !param2.isParameter())
	    logger.severe("Either " + param1.getTypeSignature() + " or " + param2.getTypeSignature() + " was not a parameter. This cannot happen!");

	var initialExpressions = PPDerivationCapability.derive(param1, param2, assignemntsPair.passedCombination());

	initialExpressions.forEach(pairF -> {
	    var pair = pairF.get();

	    var intraDerivation1 = ExpressionDerivationCapability.derive(pair._1);
	    var intraDerivation2 = ExpressionDerivationCapability.derive(pair._2);

	    var product = CommonOperations.cartesianProduct(intraDerivation1, intraDerivation2);

	    product.stream().forEach(dragons -> {
		var r1 = dragons._1;
		var r2 = dragons._2;

		var newAssignmentsPair = new AssignemntsPair(
			new Assignment(assignemntsPair._1.reference(), r1._1.map(j -> (IJavaElement) j), r1._2.orElse(assignemntsPair._1.mostConcreteRecordedType())),
			new Assignment(assignemntsPair._2.reference(), r2._1.map(j -> (IJavaElement) j), r2._2.orElse(assignemntsPair._2.mostConcreteRecordedType())));

		newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);
		stacks.assignmentsPairs().push(newAssignmentsPair);
	    });
	});
    }

    // **************************************************************************************************************
    // Invalid Cases Handling
    // **************************************************************************************************************
    private static void markInvalid(AssignemntsPair assignemntsPair, State stacks) {
	stacks.inconclusive().add(new Pair<>(assignemntsPair._1.mostConcreteRecordedType(), assignemntsPair._2.mostConcreteRecordedType()));
    }

}
