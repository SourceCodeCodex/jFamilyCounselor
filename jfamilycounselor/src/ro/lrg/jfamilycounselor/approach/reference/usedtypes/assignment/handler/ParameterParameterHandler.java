package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.handler;

import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.cartesianProduct;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.ParameterParameterDerivation;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.partial.PartialDerivation;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.Assignment;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.State;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class ParameterParameterHandler extends ReferencesPairHandler {

    @Override
    public void handle(AssignemntsPair assignemntsPair, State state) {
	var assignedParam1 = ((AssignedElement.Parameter) assignemntsPair._1.assignedElement().get());
	var assignedParam2 = ((AssignedElement.Parameter) assignemntsPair._2.assignedElement().get());

	var newExpressions = ParameterParameterDerivation.derive(Pair.of(assignedParam1.iLocalVariable(), assignedParam2.iLocalVariable()), assignemntsPair.isInitial());

	// if there are no new expressions obtained through derivation, mark the pair as
	// invalid
	if (newExpressions.isEmpty()) {
	    state.markInvalid(assignemntsPair);
	    return;
	}

	var newAssignmentsPairs = newExpressions.parallelStream()
		.flatMap(pairF -> {
		    var pair = pairF.get();

		    var partialDerivationResult1 = PartialDerivation.partialDerive(pair._1);
		    var partialDerivationResult2 = PartialDerivation.partialDerive(pair._2);

		    // recombining the results obtained by partial-derivation
		    var product = cartesianProduct(partialDerivationResult1, partialDerivationResult2);

		    return product.stream()
			    .map(derivationResultsPair -> {
				var r1 = derivationResultsPair._1;
				var r2 = derivationResultsPair._2;

				var newAssignmentsPair = new AssignemntsPair(
					new Assignment(assignemntsPair._1.reference(), r1.newAssignedElement(), r1.newLowestRecordedType().orElse(assignemntsPair._1.lowestRecordedType())),
					new Assignment(assignemntsPair._2.reference(), r2.newAssignedElement(), r2.newLowestRecordedType().orElse(assignemntsPair._2.lowestRecordedType())));

				newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);
				return newAssignmentsPair;
			    });
		})
		.toList();

	newAssignmentsPairs.forEach(state.assignmentsPairs()::push);
    }

    @Override
    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
	return assignedElement1 instanceof AssignedElement.Parameter && assignedElement2 instanceof AssignedElement.Parameter;
    }
}
