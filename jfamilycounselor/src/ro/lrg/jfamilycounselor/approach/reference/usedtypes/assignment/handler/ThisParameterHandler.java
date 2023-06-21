package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.handler;

import static ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.util.LowestRecordedTypeUtil.updateLowestRecordedType;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.cartesianProduct;

import java.util.Optional;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.State;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.ParameterDerivationWithTargetObject;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.partial.PartialDerivation;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.Assignment;

/**
 * This handler derives the parameter, recording the target object of the call
 * where the actual parameters are specified. Since the first assigned element
 * is This, depending on the value of the target object, the derivation splits
 * in two cases
 * 
 * 1. There is no target object, which means that the call resolved through
 * derivation is still scoped by the type of this, or by any of its subtypes. In
 * the latter case, lowestRecordedType needs to be updated.
 * 
 * 2. There is some target object, so the derivation left the scope of the type
 * representing This, and therefore partial derivation is applied on the
 * expression of the target object.
 * 
 * A ParameterThisHandler is not needed since the assigned element This can only
 * be found in the first position due to how the references are grouped in the
 * first place, i.e. this reference is always first, regardless with whatever
 * other reference it is paired with.
 * 
 * @author rosualinpetru
 *
 */
public class ThisParameterHandler extends ReferencesPairHandler {

    @Override
    public void handle(AssignemntsPair assignemntsPair, State state) {
	var assignedThis = (AssignedElement.This) assignemntsPair._1.assignedElement().get();
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

		    var actualParamPartialDerivation = PartialDerivation.partialDerive(targetObjectActualParamPair._2);

		    // the derivation is still somewhere within the cone of the type of This
		    if (targetObjectActualParamPair._1.isEmpty()) {
			// identify the type that encloses the method call of the actual parameter
			var newTypeScope = determineNewScopeOfActualParameter(targetObjectActualParamPair._2);

			// update the new assigned This element and the lowestRecordedType
			var newAssignedThis = newTypeScope.map(AssignedElement.This::new).orElse(assignedThis);
			var newRecordedType = updateLowestRecordedType(Optional.of(assignemntsPair._1.lowestRecordedType()), newTypeScope);

			return actualParamPartialDerivation.stream()
				.map(derivationResult -> {
				    var newAssignmentsPair = new AssignemntsPair(
					    new Assignment(assignemntsPair._1.reference(),
						    Optional.of(newAssignedThis),
						    newRecordedType.orElse(assignemntsPair._1.lowestRecordedType())),
					    new Assignment(assignemntsPair._2.reference(),
						    derivationResult.newAssignedElement(),
						    derivationResult.newLowestRecordedType().orElse(assignemntsPair._2.lowestRecordedType())));

				    newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);

				    return newAssignmentsPair;
				});
		    } else {
			var targetObject = targetObjectActualParamPair._1.get();

			var targetObjectDerivation = PartialDerivation.partialDerive(targetObject);

			var product = cartesianProduct(targetObjectDerivation, actualParamPartialDerivation);

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
		    }

		})
		.toList();

	newAssignmentsPairs.forEach(state.assignmentsPairs()::push);
    }

    private Optional<IType> determineNewScopeOfActualParameter(Expression actualParameter) {
	ASTNode auxNode = actualParameter;
	while (auxNode != null && auxNode.getNodeType() != ASTNode.TYPE_DECLARATION) {
	    auxNode = auxNode.getParent();
	}

	if (auxNode == null)
	    return Optional.empty();

	return Optional.ofNullable(((TypeDeclaration) auxNode).resolveBinding())
		.map(tb -> tb.getJavaElement())
		.filter(j -> j instanceof IType)
		.map(t -> (IType) t);

    }

    @Override
    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
	return assignedElement1 instanceof AssignedElement.This && assignedElement2 instanceof AssignedElement.Parameter;
    }
}
