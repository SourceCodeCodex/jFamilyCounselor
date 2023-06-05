package ro.lrg.jfamilycounselor.capability.generic.expression.method.invocation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

public class MethodArgumentsCapability {
    private MethodArgumentsCapability() {
    }

    @SuppressWarnings("unchecked")
    public static Optional<Expression> extractArgument(Expression invocation, int index) {
	Optional<List<Expression>> args = switch (invocation.getNodeType()) {
	case ASTNode.METHOD_INVOCATION: {
	    var methodInvocation = (MethodInvocation) invocation;
	    yield Optional.of((List<Expression>) methodInvocation.arguments());
	}
	case ASTNode.SUPER_METHOD_INVOCATION: {
	    var superMethodInvocation = (SuperMethodInvocation) invocation;
	    yield Optional.of((List<Expression>) superMethodInvocation.arguments());
	}
	case ASTNode.CLASS_INSTANCE_CREATION: {
	    var constructorInvocation = (ClassInstanceCreation) invocation;
	    yield Optional.of((List<Expression>) constructorInvocation.arguments());
	}
	default:
	    yield Optional.empty();
	};

	return args.flatMap(l -> {
	    try {
		return Optional.of(l.get(index));
	    } catch (Exception e) {
		return Optional.empty();
	    }
	});
    }

    public static Optional<Integer> indexOfParameter(IMethod iMethod, ILocalVariable iLocalVariable) {
	try {
	    var parameters = Stream.of(iMethod.getParameters()).toList();
	    for (int i = 0; i < parameters.size(); i++) {
		if (iLocalVariable.equals(parameters.get(i))) {
		    return Optional.of(i);
		}
	    }
	    return Optional.empty();
	} catch (Exception e) {
	    return Optional.empty();
	}
    }

}
