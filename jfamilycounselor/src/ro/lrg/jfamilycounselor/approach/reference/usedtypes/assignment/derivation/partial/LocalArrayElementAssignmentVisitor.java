package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.partial;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LocalArrayElementAssignmentVisitor extends ASTVisitor {
	private final ILocalVariable iLocalVariable;

	private final Set<Expression> assignments = new HashSet<>();

	public LocalArrayElementAssignmentVisitor(ILocalVariable iLocalVariable) {
		this.iLocalVariable = iLocalVariable;
	}

	public List<Expression> getAssignments() {
		return List.copyOf(assignments);
	}

	public boolean visit(Assignment node) {
		var left = node.getLeftHandSide();
		// TODO: treat case of not a simple name [ expression ]
		if (left instanceof ArrayAccess aa && aa.getArray() instanceof SimpleName sn) {
			var binding = sn.resolveBinding();
			if(binding == null || binding.getKind() != IBinding.VARIABLE) {
				return true;
			}
			
			var varBinding = (IVariableBinding) binding;
			if (iLocalVariable.equals(varBinding.getJavaElement())) {
				assignments.add(node.getRightHandSide());
			}
		}
		return true;
	}
}
