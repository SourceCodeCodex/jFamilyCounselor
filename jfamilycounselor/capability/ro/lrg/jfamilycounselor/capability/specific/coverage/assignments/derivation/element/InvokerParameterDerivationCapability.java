package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.element;

import static ro.lrg.jfamilycounselor.util.list.CommonOperations.asSupplier;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;

import ro.lrg.jfamilycounselor.capability.generic.method.invocation.MethodArgumentsCapability;
import ro.lrg.jfamilycounselor.capability.generic.method.invocation.MethodCallCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Derivation that extracts the arguments and method calls and, if needed,
 * executes the combination step.
 * 
 * The first element from the pair represents the invoker. It is only set if the
 * invoker is different than 'this'.
 * 
 * The Supplier is used as keeping ASTs in memory can lead to
 * OutOfMemoryException.
 * 
 * @author rosualinpetru
 *
 */
public class InvokerParameterDerivationCapability {
    private InvokerParameterDerivationCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static List<Supplier<Pair<Optional<Expression>, Expression>>> derive(ILocalVariable param) {
	if (!(param.getDeclaringMember() instanceof IMethod)) {
	    logger.severe("Some of the provided 'parameters' do not have the declaring member a method. This cannot happen!");
	    return List.of();
	}

	var method = (IMethod) param.getDeclaringMember();

	var indexOpt = MethodArgumentsCapability.indexOfParameter(method, param);

	if (indexOpt.isEmpty()) {
	    logger.severe("Index of parameter was not found. This cannot happen!");
	    return List.of();
	}

	var index = indexOpt.get();

	var callsOpt = MethodCallCapability.methodCalls(method);

	if (callsOpt.isEmpty())
	    return List.of();

	var calls = callsOpt.get();

	return calls.stream()
		.map(callF -> {
		    var call = callF.get();
		    var argOpt = MethodArgumentsCapability.extractArgument(call, index);

		    return argOpt.map(arg -> {
			Optional<Expression> invoker = switch (call.getNodeType()) {
			case ASTNode.CLASS_INSTANCE_CREATION: {
			    yield Optional.of(call);
			}
			case ASTNode.METHOD_INVOCATION: {
			    yield Optional.ofNullable(((MethodInvocation) call).getExpression())
				    .filter(e -> switch (e.getNodeType()) {
				    case ASTNode.SIMPLE_NAME:
				    case ASTNode.QUALIFIED_NAME:
					var name = (Name) e;
					yield Optional.ofNullable(name.resolveBinding()).stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE);
				    default:
					yield false;
				    });
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
		.map(p -> asSupplier(p))
		.toList();

    }
}
