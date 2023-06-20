package ro.lrg.jfamilycounselor.capability.ast.cast;

import java.util.Optional;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Statement;

public class UnguardedCastVisitor extends ASTVisitor {
    private final Optional<IType> castType;
    private final CastExpression castExpression;
    private boolean isGuarded = false;

    public UnguardedCastVisitor(CastExpression castExpression) {
	castType = Optional.ofNullable(castExpression.getType().resolveBinding()).map(b -> (IType) b.getJavaElement());
	this.castExpression = castExpression;
    }

    public Boolean isGuarded() {
	return isGuarded;
    }

    public boolean visit(InstanceofExpression node) {
	if (isGuarded)
	    return true;

	var ioType = Optional.ofNullable(node.getRightOperand().resolveBinding()).map(b -> (IType) b.getJavaElement());
	if (ioType.equals(castType)) {
	    ASTNode enclosingIOStatement = node;
	    while (!(enclosingIOStatement instanceof Statement)) {
		enclosingIOStatement = enclosingIOStatement.getParent();
	    }
	    ASTNode enclosingStatement = castExpression;
	    while (Optional.ofNullable(enclosingStatement).isPresent() && enclosingStatement.getNodeType() != ASTNode.METHOD_DECLARATION) {
		if (enclosingIOStatement.equals(enclosingStatement)) {
		    isGuarded = true;
		}
		enclosingStatement = enclosingStatement.getParent();
	    }
	}

	return false;
    }

}
