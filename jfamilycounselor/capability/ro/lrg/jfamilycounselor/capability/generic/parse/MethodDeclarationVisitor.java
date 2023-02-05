package ro.lrg.jfamilycounselor.capability.generic.parse;

import java.util.Optional;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

class MethodDeclarationVisitor extends ASTVisitor {
    private Optional<MethodDeclaration> lastNode = Optional.empty();
    private final IMethod iMethod;

    public MethodDeclarationVisitor(IMethod iMethod) {
	this.iMethod = iMethod;
    }

    public boolean visit(MethodDeclaration node) {
	if (Optional.ofNullable(node.resolveBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iMethod))) {
	    setLastNode(Optional.ofNullable(node));
	}

	return true;
    }

    public Optional<MethodDeclaration> getLastNode() {
	return lastNode;
    }

    public void setLastNode(Optional<MethodDeclaration> lastNode) {
	this.lastNode = lastNode;
    }
}
