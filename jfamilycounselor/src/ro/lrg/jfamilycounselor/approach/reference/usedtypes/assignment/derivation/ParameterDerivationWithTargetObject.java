package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation;

import static ro.lrg.jfamilycounselor.capability.ast.methodcall.MethodArgumentsCapability.extractArgument;
import static ro.lrg.jfamilycounselor.capability.ast.methodcall.MethodArgumentsCapability.indexOfParameter;
import static ro.lrg.jfamilycounselor.capability.ast.methodcall.MethodCallCapability.extractMethodCalls;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.lazy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Upon deriving a parameter by finding the corresponding actual parameters, the
 * derivation also record the target objects (if any) of the calls.
 * 
 * @author rosualinpetru
 *
 */
public class ParameterDerivationWithTargetObject {
    private ParameterDerivationWithTargetObject() {
    }

    private static final Logger logger = jFCLogger.getLogger();

    public static List<Supplier<Pair<Optional<Expression>, Expression>>> derive(ILocalVariable param) {
	if (!(param.getDeclaringMember() instanceof IMethod)) {
	    logger.severe("Some of the provided 'parameters' do not have the declaring member a method. This cannot happen!");
	    return List.of();
	}

	var method = (IMethod) param.getDeclaringMember();

	var indexOpt = indexOfParameter(method, param);

	if (indexOpt.isEmpty()) {
	    logger.severe("Index of parameter was not found. This cannot happen!");
	    return List.of();
	}

	var callsOpt = extractMethodCalls(method);

	if (callsOpt.isEmpty())
	    return List.of();

	return callsOpt.get().parallelStream()
		.map(callF -> {
		    var call = callF.get();
		    var argOpt = extractArgument(call, indexOpt.get());

		    return argOpt.map(arg -> {

			var targetObject = switch (call.getNodeType()) {
			case ASTNode.CLASS_INSTANCE_CREATION: {
			    yield Optional.of(call);
			}
			case ASTNode.METHOD_INVOCATION: {
			    yield Optional.ofNullable(((MethodInvocation) call).getExpression())
				    .filter(e -> e.getNodeType() != ASTNode.THIS_EXPRESSION);
			}
			case ASTNode.SUPER_METHOD_INVOCATION: {
			    yield Optional.<Expression>empty();
			}
			default:
			    logger.severe("During ParameterDerivationWithTargetObject derivation, could not determine the target object.");
			    yield Optional.<Expression>empty();
			};

			return Pair.of(targetObject, arg);

		    });

		})
		.filter(o -> o.isPresent())
		.map(o -> o.get())
		.map(p -> lazy(p))
		.toList();

    }
}
