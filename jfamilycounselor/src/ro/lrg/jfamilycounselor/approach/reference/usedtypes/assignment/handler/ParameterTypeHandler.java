package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.handler;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.State;

public class ParameterTypeHandler extends ReferencesPairHandler {
    private TypeParameterHandler thisParameterHandler = new TypeParameterHandler();

    @Override
    public void handle(AssignemntsPair assignemntsPair, State state) {
	thisParameterHandler.handle(assignemntsPair.swap(), state);
    }

    @Override
    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
	return assignedElement1 instanceof AssignedElement.Parameter && assignedElement2 instanceof AssignedElement.ResolvedType;
    }

}
