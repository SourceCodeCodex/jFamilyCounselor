package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import ro.lrg.jfamilycounselor.capability.generic.method.invocation.StaticInvocationCapability;
import ro.lrg.jfamilycounselor.capability.generic.method.invocation.args.ArgumentsCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class IPDerivationCapability {
    private IPDerivationCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static List<Pair<Optional<Expression>, Expression>> derive(ILocalVariable param) {
	if (!(param.getDeclaringMember() instanceof IMethod)) {
	    logger.severe("Some of the provided 'parameters' do not have the declaring member a method. This cannot happen!");
	    return List.of();
	}

	var method = (IMethod) param.getDeclaringMember();

	var indexOpt = ArgumentsCapability.indexOfParameter(method, param);

	if (indexOpt.isEmpty()) {
	    logger.severe("Index of parameter was not found. This cannot happen!");
	    return List.of();
	}

	var index = indexOpt.get();

	var callsOpt = StaticInvocationCapability.staticInvocations(method);

	if (callsOpt.isEmpty())
	    return List.of();

	var calls = callsOpt.get();

	return calls.stream()
		.map(call -> {
		    var argOpt = ArgumentsCapability.extractArgument(call, index);
		    return argOpt.map(arg -> {
			Optional<Expression> invoker = switch (call.getNodeType()) {
			case ASTNode.CLASS_INSTANCE_CREATION: {
			    yield Optional.of(call);
			}
			case ASTNode.METHOD_INVOCATION: {
			    yield Optional.ofNullable(((MethodInvocation) call).getExpression());
			}
			case ASTNode.SUPER_METHOD_INVOCATION: {
			    yield Optional.empty();
			}
			default:
			    logger.severe("During TP derivation, could not assign anything to the 'this' parameter.");
			    yield Optional.empty();
			};

			return new Pair<>(invoker, arg);

		    });

		})
		.filter(o -> o.isPresent())
		.map(o -> o.get())
		.toList();

    }
}
