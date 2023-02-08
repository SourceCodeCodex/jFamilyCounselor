package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.java;

import static ro.lrg.jfamilycounselor.util.list.CommonOperations.asSupplier;
import static ro.lrg.jfamilycounselor.util.list.CommonOperations.cartesianProduct;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import ro.lrg.jfamilycounselor.capability.generic.method.invocation.MethodArgumentsCapability;
import ro.lrg.jfamilycounselor.capability.generic.method.invocation.MethodCallCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class PPDerivationCapability {
    private PPDerivationCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static List<Supplier<Pair<Expression, Expression>>> derive(ILocalVariable param1, ILocalVariable param2, boolean passedCombination) {
	if (!(param1.getDeclaringMember() instanceof IMethod) || !(param1.getDeclaringMember() instanceof IMethod)) {
	    logger.severe("Some of the provided 'parameters' do not have the declaring member a method. This cannot happen!");
	    return List.of();
	}

	var m1 = (IMethod) param1.getDeclaringMember();
	var m2 = (IMethod) param2.getDeclaringMember();

	var index1Opt = MethodArgumentsCapability.indexOfParameter(m1, param1);
	var index2Opt = MethodArgumentsCapability.indexOfParameter(m2, param2);

	if (index1Opt.isEmpty() || index2Opt.isEmpty()) {
	    logger.severe("Index of parameter was not found. This cannot happen!");
	    return List.of();
	}

	var index1 = index1Opt.get();
	var index2 = index2Opt.get();

	if (m1.equals(m2)) {
	    var callsOpt = MethodCallCapability.methodCalls(m1);

	    if (callsOpt.isEmpty())
		return List.of();

	    var calls = callsOpt.get();

	    return calls
		    .stream()
		    .map(callF -> {
			var call = callF.get();
			var a1Opt = MethodArgumentsCapability.extractArgument(call, index1);
			var a2Opt = MethodArgumentsCapability.extractArgument(call, index2);

			return a1Opt.flatMap(a1 -> a2Opt.map(a2 -> new Pair<>(a1, a2)));
		    })
		    .filter(o -> o.isPresent())
		    .map(o -> o.get())
		    .map(p -> asSupplier(p))
		    .toList();

	} else {
	    var calls1Opt = MethodCallCapability.methodCalls(m1);

	    if (calls1Opt.isEmpty())
		return List.of();

	    var calls1 = calls1Opt.get();

	    var calls2Opt = MethodCallCapability.methodCalls(m2);

	    if (calls2Opt.isEmpty())
		return List.of();

	    var calls2 = calls2Opt.get();

	    return cartesianProduct(calls1, calls2)
		    .stream()
		    .map(p -> {
			var call1 = p._1.get();
			var call2 = p._2.get();

			if (!passedCombination && calledOnSameObject(call1, call2)) {
			    var a1Opt = MethodArgumentsCapability.extractArgument(call1, index1);
			    var a2Opt = MethodArgumentsCapability.extractArgument(call2, index2);

			    return a1Opt.flatMap(a1 -> a2Opt.map(a2 -> new Pair<>(a1, a2)));
			} else {
			    Optional<Pair<Expression, Expression>> empty = Optional.empty();
			    return empty;
			}
		    })
		    .filter(o -> o.isPresent())
		    .map(o -> o.get())
		    .map(p -> asSupplier(p))
		    .toList();


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
