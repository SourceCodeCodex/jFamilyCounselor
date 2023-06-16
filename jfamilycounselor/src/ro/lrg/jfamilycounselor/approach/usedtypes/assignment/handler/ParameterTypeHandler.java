package ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.State;

public class ParameterTypeHandler extends ReferencesPairHandler {
    private TypeParameterHandler thisParameterHandler = new TypeParameterHandler();

    @Override
    public void handle(AssignemntsPair assignemntsPair, State state) {
	thisParameterHandler.handle(assignemntsPair.swap(), state);
    }

    @Override
    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
	return assignedElement1 instanceof ILocalVariable && assignedElement2 instanceof IType;
    }

}
