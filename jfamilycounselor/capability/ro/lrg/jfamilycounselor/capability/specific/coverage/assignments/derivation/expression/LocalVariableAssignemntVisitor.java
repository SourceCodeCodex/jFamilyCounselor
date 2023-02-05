package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

class LocalVariableAssignemntVisitor extends ASTVisitor {
    private final ILocalVariable iLocalVariable;

    private final List<Expression> assignments = new ArrayList<>();

    public LocalVariableAssignemntVisitor(ILocalVariable iLocalVariable) {
	this.iLocalVariable = iLocalVariable;
    }

    public List<Expression> getAssignments() {
	return assignments;
    }

    public boolean visit(VariableDeclarationFragment node) {
	if (Optional.ofNullable(node.resolveBinding()).isPresent() && iLocalVariable.equals(node.resolveBinding().getJavaElement()))
	    if (Optional.ofNullable(node.getInitializer()).isPresent())
		assignments.add(node.getInitializer());
	return false;
    }

    public boolean visit(Assignment node) {
	var left = node.getLeftHandSide();
	if (left instanceof SimpleName sn && Optional.ofNullable(sn.resolveBinding()).stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
	    var varBinding = (IVariableBinding) sn.resolveBinding();
	    if (iLocalVariable.equals(varBinding.getJavaElement())) {
		assignments.add(node.getRightHandSide());
	    }
	}
	return true;
    }

}
