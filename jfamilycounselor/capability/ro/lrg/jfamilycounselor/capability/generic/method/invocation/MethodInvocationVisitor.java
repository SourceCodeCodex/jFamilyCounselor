package ro.lrg.jfamilycounselor.capability.generic.method.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor {
    private final IMethod iMethod;

    private final List<Expression> invocations = new ArrayList<>();

    public MethodInvocationVisitor(IMethod iMethod) {
	this.iMethod = iMethod;
    }

    public List<Expression> getInvocations() {
	return invocations;
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
