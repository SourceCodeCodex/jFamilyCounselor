package ro.lrg.jfamilycounselor.capability.ast.cast;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;

class TypeCastVisitor extends ASTVisitor {
	private final IType iType;

	private final Set<CastExpression> invocations = new HashSet<>();

	public TypeCastVisitor(IType iType) {
		this.iType = iType;
	}

	public List<CastExpression> getInvocations() {
		return List.copyOf(invocations);
	}

	public boolean visit(CastExpression node) {
		if (Optional.ofNullable(node.getType().resolveBinding()).map(b -> b.getJavaElement()).stream()
				.anyMatch(j -> j.equals(iType)))
			invocations.add(node);

		return false;
	}

}
