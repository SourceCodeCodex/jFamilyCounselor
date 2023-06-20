package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation;

import static ro.lrg.jfamilycounselor.capability.ast.methodcall.MethodArgumentsCapability.extractArgument;
import static ro.lrg.jfamilycounselor.capability.ast.methodcall.MethodArgumentsCapability.indexOfParameter;
import static ro.lrg.jfamilycounselor.capability.ast.methodcall.MethodCallCapability.extractMethodCalls;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.cartesianProduct;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.lazy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;

import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Derivation that extracts the arguments from the calls of the parent method of
 * the specified parameters and, if needed, executes the combination step.
 * 
 * param1 and param2 represent assigned expressions in this case. There are two
 * cases:
 * 
 * 1. param1 and param2 are the same as the analyzed references pairs, i.e. the
 * initial assignments pairs is being derived and we might need to execute the
 * combination phase. Combination is executed in the case that param1 and param2
 * are declared in different methods (let them be m1, m2), e.g. distinct
 * setters, and is represents computing the cartesian product of the calls of m1
 * and the calls of m2, keeping only the pair of calls that are invoked on the
 * same target object. Combination need to be executed only once, for the initial
 * assignments pair.
 * 
 * 2. param1 and param2 are parameters that were obtained through derivation
 * beforehand. In this case, if the parameters are declared in different
 * methods, we mark the case as inconclusive.
 * 
 * The result is a list of pairs of expressions.The Supplier is used for
 * laziness, as keeping ASTs in memory can lead to OutOfMemoryException.
 * 
 * @author rosualinpetru
 *
 */
public class ParameterParameterDerivation {
    private ParameterParameterDerivation() {
    }

    private static final Logger logger = jFCLogger.getLogger();

    public static List<Supplier<Pair<Expression, Expression>>> derive(Pair<ILocalVariable, ILocalVariable> assignedParameters, boolean isInitial) {
	if (!(assignedParameters._1.getDeclaringMember() instanceof IMethod) || !(assignedParameters._2.getDeclaringMember() instanceof IMethod)) {
	    logger.severe("Some of the provided 'parameters' do not have the declaring member a method. This cannot happen!");
	    return List.of();
	}

	var m1 = (IMethod) assignedParameters._1.getDeclaringMember();
	var m2 = (IMethod) assignedParameters._2.getDeclaringMember();

	var index1Opt = indexOfParameter(m1, assignedParameters._1);
	var index2Opt = indexOfParameter(m2, assignedParameters._2);

	if (index1Opt.isEmpty() || index2Opt.isEmpty()) {
	    logger.severe("Index of parameter was not found. This cannot happen!");
	    return List.of();
	}

	var index1 = index1Opt.get();
	var index2 = index2Opt.get();

	if (m1.equals(m2)) {
	    // If the parameters are declared in the same method
	    var callsOpt = extractMethodCalls(m1);

	    if (callsOpt.isEmpty())
		return List.of();

	    var calls = callsOpt.get();

	    return calls.parallelStream()
		    .map(callF -> {
			var call = callF.get();
			var a1Opt = extractArgument(call, index1);
			var a2Opt = extractArgument(call, index2);

			return a1Opt.flatMap(a1 -> a2Opt.map(a2 -> Pair.of(a1, a2)));
		    })
		    .filter(o -> o.isPresent())
		    .map(o -> o.get())
		    .map(p -> lazy(p))
		    .toList();

	} else {
	    // Else, the parameters are declared in different methods
	    if (!isInitial)
		return List.of();

	    var calls1Opt = extractMethodCalls(m1);

	    if (calls1Opt.isEmpty())
		return List.of();

	    var calls2Opt = extractMethodCalls(m2);

	    if (calls2Opt.isEmpty())
		return List.of();

	    return cartesianProduct(calls1Opt.get(), calls2Opt.get()).parallelStream()
		    .map(p -> {
			var call1 = p._1.get();
			var call2 = p._2.get();

			if (haveSameTargetObject(call1, call2)) {
			    var a1Opt = extractArgument(call1, index1);
			    var a2Opt = extractArgument(call2, index2);

			    return a1Opt.flatMap(a1 -> a2Opt.map(a2 -> Pair.of(a1, a2)));
			} else {
			    return Optional.<Pair<Expression, Expression>>empty();
			}
		    })
		    .filter(o -> o.isPresent())
		    .map(o -> o.get())
		    .map(p -> lazy(p))
		    .toList();

	}

    }

    private static boolean haveSameTargetObject(Expression invocation1, Expression invocation2) {
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
			if (ce1 instanceof ThisExpression t1 && ce2 instanceof ThisExpression t2)
			    return Optional.ofNullable(t1.resolveTypeBinding()).stream().anyMatch(b -> b.isEqualTo(t2.resolveTypeBinding()));

			if (ce1 instanceof Name n1 && ce2 instanceof Name n2)
			    return Optional.ofNullable(n1.resolveBinding()).stream()
				    .filter(b -> b.getKind() == IBinding.VARIABLE)
				    .anyMatch(b -> b.isEqualTo(n2.resolveBinding()));

			if (ce1 instanceof Name n && ce2 instanceof FieldAccess f)
			    return Optional.ofNullable(n.resolveBinding()).stream()
				    .filter(b -> b.getKind() == IBinding.VARIABLE)
				    .anyMatch(b -> b.isEqualTo(f.resolveFieldBinding()));

			if (ce1 instanceof FieldAccess f && ce2 instanceof Name n)
			    return Optional.ofNullable(f.resolveFieldBinding()).stream()
				    .filter(b -> b.getKind() == IBinding.VARIABLE)
				    .anyMatch(b -> b.isEqualTo(n.resolveBinding()));

			return false;
		    } catch (Exception e) {
			return false;
		    }
		})).orElse(false);

	    }
	}

	return false;
    }

}
