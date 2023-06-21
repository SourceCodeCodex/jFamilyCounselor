package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.handler;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.State;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.ParameterDerivationWithTargetObject;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.partial.PartialDerivation;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.Assignment;

/**
 * The derivation in this case is simple: just derive the parameter and make the
 * product between the former resolved type and the new generated assignments
 * obtained through the derivation of the parameter.
 * 
 * @author rosualinpetru
 *
 */
public class TypeParameterHandler extends ReferencesPairHandler {

    @Override
    public void handle(AssignemntsPair assignemntsPair, State state) {
	var assignedParam = (AssignedElement.Parameter) assignemntsPair._2.assignedElement().get();

	var newExpressions = ParameterDerivationWithTargetObject.derive(assignedParam.iLocalVariable());
	
	// if there are no new expressions obtained through derivation, mark the pair as
	// invalid
	if (newExpressions.isEmpty()) {
	    state.markInvalid(assignemntsPair);
	    return;
	}

	var newAssignmentsPairs = newExpressions.parallelStream()
		.flatMap(pairF -> {
		    var targetObjectActualParamPair = pairF.get();

		    var partialDerivation = PartialDerivation.partialDerive(targetObjectActualParamPair._2);

		    return partialDerivation.stream()
			    .map(derivationResult -> {
				var newAssignmentsPair = new AssignemntsPair(
					assignemntsPair._1,
					new Assignment(assignemntsPair._2.reference(),
						derivationResult.newAssignedElement(),
						derivationResult.newLowestRecordedType().orElse(assignemntsPair._2.lowestRecordedType())));

				newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);

				return newAssignmentsPair;
			    });

		})
		.toList();

	newAssignmentsPairs.forEach(state.assignmentsPairs()::push);
    }

    @Override
    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
	return assignedElement1 instanceof AssignedElement.ResolvedType && assignedElement2 instanceof AssignedElement.Parameter;
    }
}
