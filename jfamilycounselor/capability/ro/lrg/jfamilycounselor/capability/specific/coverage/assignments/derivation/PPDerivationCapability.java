package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import ro.lrg.jfamilycounselor.capability.generic.method.invocation.StaticInvocationCapability;
import ro.lrg.jfamilycounselor.capability.generic.method.invocation.args.ArgumentsCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.list.ListOperations;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class PPDerivationCapability {
    private PPDerivationCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static List<Pair<Expression, Expression>> derive(ILocalVariable param1, ILocalVariable param2, boolean passedCombination) {
	if (!(param1.getDeclaringMember() instanceof IMethod) || !(param1.getDeclaringMember() instanceof IMethod)) {
	    logger.severe("Some of the provided 'parameters' do not have the declaring member a method. This cannot happen!");
	    return List.of();
	}

	var m1 = (IMethod) param1.getDeclaringMember();
	var m2 = (IMethod) param2.getDeclaringMember();

	var index1Opt = ArgumentsCapability.indexOfParameter(m1, param1);
	var index2Opt = ArgumentsCapability.indexOfParameter(m2, param2);

	if (index1Opt.isEmpty() || index2Opt.isEmpty()) {
	    logger.severe("Index of parameter was not found. This cannot happen!");
	    return List.of();
	}

	var index1 = index1Opt.get();
	var index2 = index2Opt.get();

	if (m1.equals(m2)) {
	    var callsOpt = StaticInvocationCapability.staticInvocations(m1);

	    if (callsOpt.isEmpty())
		return List.of();

	    var calls = callsOpt.get();

	    return calls.stream()
		    .map(call -> {
			var a1Opt = ArgumentsCapability.extractArgument(call, index1);
			var a2Opt = ArgumentsCapability.extractArgument(call, index2);

			return a1Opt.flatMap(a1 -> a2Opt.map(a2 -> new Pair<>(a1, a2)));
		    })
		    .filter(o -> o.isPresent())
		    .map(o -> o.get())
		    .toList();

	} else {
	    var calls1Opt = StaticInvocationCapability.staticInvocations(m1);
	    var calls2Opt = StaticInvocationCapability.staticInvocations(m2);

	    if (calls1Opt.isEmpty() || calls2Opt.isEmpty())
		return List.of();

	    var calls1 = calls1Opt.get();
	    var calls2 = calls2Opt.get();

	    var expressionPairs = ListOperations.cartesianProduct(calls1, calls2).stream()
		    .filter(p -> !passedCombination && calledOnSameObject(p._1, p._2))
		    .map(p -> {
			var a1Opt = ArgumentsCapability.extractArgument(p._1, index1);
			var a2Opt = ArgumentsCapability.extractArgument(p._2, index2);

			return a1Opt.flatMap(a1 -> a2Opt.map(a2 -> new Pair<>(a1, a2)));
		    })
		    .filter(o -> o.isPresent())
		    .map(o -> o.get())
		    .toList();

	    return expressionPairs;

	}

    }

    private static boolean calledOnSameObject(Expression invocation1, Expression invocation2) {
	if (invocation1.equals(invocation2) && invocation1.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION)
	    return true;

	if (invocation1 instanceof SuperMethodInvocation si1) {
	    if (invocation2 instanceof SuperMethodInvocation si2) {
		var dt1 = Optional.ofNullable(si1.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement()).map(m -> m.getDeclaringType().getFullyQualifiedName());
		var dt2 = Optional.ofNullable(si2.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement()).map(m -> m.getDeclaringType().getFullyQualifiedName());

		return dt1.equals(dt2);
	    }
	}

	if (invocation1 instanceof MethodInvocation mi1) {
	    if (invocation2 instanceof MethodInvocation mi2) {
		var dt1 = Optional.ofNullable(mi1.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement()).map(m -> m.getDeclaringType().getFullyQualifiedName());
		var dt2 = Optional.ofNullable(mi2.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement()).map(m -> m.getDeclaringType().getFullyQualifiedName());

		Optional<Expression> ce1Opt = Optional.ofNullable(mi1.getExpression());
		Optional<Expression> ce2Opt = Optional.ofNullable(mi2.getExpression());

		if (ce1Opt.isEmpty() && ce2Opt.isEmpty())
		    return dt1.equals(dt2);

		return ce1Opt.flatMap(ce1 -> ce2Opt.map(ce2 -> {
		    try {
			// CAN BE FURTHER IMPROVED
			return ce1.getNodeType() == ce2.getNodeType() && ce1.resolveTypeBinding().isEqualTo(ce2.resolveTypeBinding());
		    } catch (Exception e) {
			return false;
		    }
		})).orElse(false);

	    }
	}

	return false;

    }

}
