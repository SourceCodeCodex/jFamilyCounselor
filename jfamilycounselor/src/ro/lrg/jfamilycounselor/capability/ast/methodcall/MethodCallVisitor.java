package ro.lrg.jfamilycounselor.capability.ast.methodcall;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

class MethodCallVisitor extends ASTVisitor {
    private final IMethod iMethod;

    private final Set<Expression> invocations = new HashSet<>();

    public MethodCallVisitor(IMethod iMethod) {
	this.iMethod = iMethod;
    }

    public List<Expression> getInvocations() {
	return List.copyOf(invocations);
    }

    public boolean visit(ClassInstanceCreation node) {
	if (Optional.ofNullable(node.resolveConstructorBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iMethod)))
	    invocations.add(node);

	return false;
    }

    public boolean visit(MethodInvocation node) {
	if (Optional.ofNullable(node.resolveMethodBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iMethod)))
	    invocations.add(node);

	return false;
    }

    public boolean visit(SuperMethodInvocation node) {
	if (Optional.ofNullable(node.resolveMethodBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iMethod)))
	    invocations.add(node);

	return false;
    }

}
