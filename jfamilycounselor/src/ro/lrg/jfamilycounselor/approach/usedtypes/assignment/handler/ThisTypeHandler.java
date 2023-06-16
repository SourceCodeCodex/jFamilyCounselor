package ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler;

import static ro.lrg.jfamilycounselor.capability.type.ConcreteConeCapability.concreteCone;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.cartesianProduct;

import java.util.List;

import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.State;

/**
 * In this point, the derivation resolves multiple types pairs, i.e. the product
 * between the concrete cone of the lowestRecordedType of the This assignment
 * and the ResolvedType.
 * 
 * A TypeThisHandler is not needed since the assigned element This can only be
 * found in the first position due to how the references are grouped in the
 * first place, i.e. this reference is always first, regardless with whatever
 * other reference it is paired with.
 * 
 * @author rosualinpetru
 *
 */
public class ThisTypeHandler extends ReferencesPairHandler {

    @Override
    public void handle(AssignemntsPair assignemntsPair, State state) {
	var assignedThis = (AssignedElement.This) assignemntsPair._1.assignedElement().get();
	var resolvedType = (AssignedElement.ResolvedType) assignemntsPair._2.assignedElement().get();

	var thisCone = concreteCone(assignedThis.iType());
	if (thisCone.isEmpty()) {
	    state.markInvalid(assignemntsPair);
	    return;
	}

	var resolvedTypesPairs = cartesianProduct(thisCone.get(), List.of(resolvedType.iType()));

	resolvedTypesPairs.forEach(p -> state.resolved().add(p));
    }

    @Override
    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
	return assignedElement1 instanceof AssignedElement.This && assignedElement2 instanceof AssignedElement.ResolvedType;
    }
}
