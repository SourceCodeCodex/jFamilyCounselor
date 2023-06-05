package ro.lrg.jfamilycounselor.capability.specific.coverage.cast;

import static ro.lrg.jfamilycounselor.capability.generic.parameter.ParameterTypeCapability.parameterType;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperFieldAccess;

import ro.lrg.jfamilycounselor.capability.generic.expression.type.cast.TypeCastCapability;
import ro.lrg.jfamilycounselor.capability.generic.method.MethodOverrideCapability;
import ro.lrg.jfamilycounselor.capability.generic.search.type.cast.TypeCastSearchCapability;
import ro.lrg.jfamilycounselor.capability.generic.type.DistinctConcreteConeProductCapability;
import ro.lrg.jfamilycounselor.util.Constants;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.list.CommonOperations;

/**
 * Capability that computes the used types using the cast-based estimation.
 * 
 * @author rosualinpetru
 *
 */
public class CastUsedTypesCapability {
    private CastUsedTypesCapability() {
    }

    private static final Cache<Pair<IType, IType>, Boolean> areCorrelatedCache = MonitoredUnboundedCache.getCache();

    public static Optional<List<Pair<IType, IType>>> usedTypesTP(Pair<IType, ILocalVariable> tpReferencesPair) {
	return parameterType(tpReferencesPair._2).flatMap(iType2 -> usedTypes(tpReferencesPair._1, iType2));
    }

    public static Optional<List<Pair<IType, IType>>> usedTypesPP(Pair<ILocalVariable, ILocalVariable> ppReferencesPair) {
	return parameterType(ppReferencesPair._1).flatMap(t1 -> parameterType(ppReferencesPair._2).flatMap(t2 -> usedTypes(t1, t2)));
    }

    private static Optional<List<Pair<IType, IType>>> usedTypes(IType iType1, IType iType2) {

	var productOpt = DistinctConcreteConeProductCapability.product(iType1, iType2);

	var correlated = productOpt.map(product -> product.parallelStream().filter(p -> {

	    if (areCorrelatedCache.contains(p))
		return areCorrelatedCache.get(p).get();

	    if (areCorrelatedCache.contains(p.swap()))
		return areCorrelatedCache.get(p.swap()).get();

	    var castsEnclosingMethodsT1Opt = TypeCastSearchCapability.searchTypeCasts(p._1);
	    var castsEnclosingMethodsT2Opt = TypeCastSearchCapability.searchTypeCasts(p._2);

	    if (castsEnclosingMethodsT1Opt.isEmpty() || castsEnclosingMethodsT2Opt.isEmpty()) {
		areCorrelatedCache.put(p, false);
		areCorrelatedCache.put(p.swap(), false);
		return false;
	    }

	    var castsEnclosingMethodsT1 = castsEnclosingMethodsT1Opt.get().stream()
		    .filter(m -> !m.getElementName().contains(Constants.EQUAL))
		    .filter(m -> MethodOverrideCapability.isOverriding(m).orElse(false))
		    .toList();

	    var castsEnclosingMethodsT2 = castsEnclosingMethodsT2Opt.get().stream()
		    .filter(m -> !m.getElementName().contains(Constants.EQUAL))
		    .filter(m -> MethodOverrideCapability.isOverriding(m).orElse(false))
		    .toList();

	    if (castsEnclosingMethodsT1.isEmpty() && castsEnclosingMethodsT2.isEmpty()) {
		areCorrelatedCache.put(p, false);
		areCorrelatedCache.put(p.swap(), false);
		return false;
	    }

	    // CASE I: There is a relevant cast of T1 in T2
	    try {
		var castsT1Opt = TypeCastCapability.typeCasts(p._1, castsEnclosingMethodsT1.stream().filter(m -> m.getDeclaringType().equals(p._2)).toList());

		var relevantCastsT1 = castsT1Opt.get().stream()
			.map(supplier -> supplier.get())
			.filter(ce -> TypeCastCapability.isDowncast(ce).orElse(false) &&
				castsExternalVariable(ce) &&
				!TypeCastCapability.isGuarded(ce))
			.toList();

		if (!relevantCastsT1.isEmpty()) {
		    areCorrelatedCache.put(p, true);
		    areCorrelatedCache.put(p.swap(), true);
		    return true;
		}

	    } catch (Throwable e) {
		areCorrelatedCache.put(p, false);
		areCorrelatedCache.put(p.swap(), false);
		return false;
	    }

	    // CASE II: There is a relevant cast of T2 in T1
	    try {
		var castsT2Opt = TypeCastCapability.typeCasts(p._2, castsEnclosingMethodsT2.stream().filter(m -> m.getDeclaringType().equals(p._1)).toList());

		var relevantCastsT2 = castsT2Opt.get().stream()
			.map(supplier -> supplier.get())
			.filter(ce -> TypeCastCapability.isDowncast(ce).orElse(false) &&
				castsExternalVariable(ce) &&
				!TypeCastCapability.isGuarded(ce))
			.toList();

		if (!relevantCastsT2.isEmpty()) {
		    areCorrelatedCache.put(p, true);
		    areCorrelatedCache.put(p.swap(), true);
		    return true;
		}

	    } catch (Throwable e) {
		areCorrelatedCache.put(p, false);
		areCorrelatedCache.put(p.swap(), false);
		return false;
	    }

	    // CASE III: There are some relevant casts for both T1 and T2 in the same method
	    try {
		var common = castsEnclosingMethodsT1.stream().filter(m -> castsEnclosingMethodsT2.contains(m)).toList();

		if (common.isEmpty()) {
		    areCorrelatedCache.put(p, false);
		    areCorrelatedCache.put(p.swap(), false);
		    return false;
		}

		var castsT1CommonOpt = TypeCastCapability.typeCasts(p._1, common);
		var castsT2CommonOpt = TypeCastCapability.typeCasts(p._2, common);

		if (castsT1CommonOpt.isEmpty() || castsT2CommonOpt.isEmpty()) {
		    areCorrelatedCache.put(p, false);
		    areCorrelatedCache.put(p.swap(), false);
		    return false;
		}

		var relevantCastsT1Common = castsT1CommonOpt.get().stream()
			.map(supplier -> supplier.get())
			.filter(ce -> TypeCastCapability.isDowncast(ce).orElse(false) &&
				castsExternalVariable(ce) &&
				!TypeCastCapability.isGuarded(ce))
			.toList();

		var relevantCastsT2Common = castsT2CommonOpt.get().stream()
			.map(supplier -> supplier.get())
			.filter(ce -> TypeCastCapability.isDowncast(ce).orElse(false) &&
				castsExternalVariable(ce) &&
				!TypeCastCapability.isGuarded(ce))
			.toList();

		var castsProduct = CommonOperations.cartesianProduct(relevantCastsT1Common, relevantCastsT2Common).stream()
			.filter(castsPair -> TypeCastCapability.enclosingMethod(castsPair._1).equals(TypeCastCapability.enclosingMethod(castsPair._2)))
			.filter(castsPair -> castsDifferentVariables(castsPair._1, castsPair._2))
			.toList();

		if (!castsProduct.isEmpty()) {
		    areCorrelatedCache.put(p, true);
		    areCorrelatedCache.put(p.swap(), true);
		    return true;
		}
	    } catch (Throwable e) {
	    }

	    areCorrelatedCache.put(p, false);
	    areCorrelatedCache.put(p.swap(), false);
	    return false;

	}).toList());

	return correlated;

    }

    private static boolean castsExternalVariable(CastExpression castExpression) {
	var expr = castExpression.getExpression();
	return switch (expr.getNodeType()) {

	case ASTNode.FIELD_ACCESS:
	case ASTNode.SUPER_FIELD_ACCESS:
	    yield true;

	case ASTNode.SIMPLE_NAME: {
	    var simpleName = (SimpleName) expr;
	    var bindingOpt = Optional.ofNullable(simpleName.resolveBinding());
	    if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
		var binding = (IVariableBinding) bindingOpt.get();
		if (binding.isField() || binding.isParameter())
		    yield true;
	    }
	    yield false;
	}

	case ASTNode.QUALIFIED_NAME: {
	    var qualifiedName = (QualifiedName) expr;
	    var bindingOpt = Optional.ofNullable(qualifiedName.resolveBinding());

	    if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
		var binding = (IVariableBinding) bindingOpt.get();
		if (binding.isField())
		    yield true;

	    }
	    yield false;
	}

	default:
	    yield false;
	};

    }

    private static boolean castsDifferentVariables(CastExpression ce1, CastExpression ce2) {
	return !castedVariable(ce1).equals(castedVariable(ce2));
    }

    private static Optional<IJavaElement> castedVariable(CastExpression castExpression) {
	var expr = castExpression.getExpression();
	return switch (expr.getNodeType()) {

	case ASTNode.FIELD_ACCESS:
	    yield Optional.ofNullable(((FieldAccess) expr).resolveFieldBinding()).map(b -> b.getJavaElement());
	case ASTNode.SUPER_FIELD_ACCESS:
	    yield Optional.ofNullable(((SuperFieldAccess) expr).resolveFieldBinding()).map(b -> b.getJavaElement());

	case ASTNode.SIMPLE_NAME: {
	    var simpleName = (SimpleName) expr;
	    var bindingOpt = Optional.ofNullable(simpleName.resolveBinding());
	    if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE))
		yield bindingOpt.map(b -> ((IVariableBinding) b).getJavaElement());
	    yield Optional.empty();
	}

	case ASTNode.QUALIFIED_NAME: {
	    var qualifiedName = (QualifiedName) expr;
	    var bindingOpt = Optional.ofNullable(qualifiedName.resolveBinding());
	    if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE))
		yield bindingOpt.map(b -> ((IVariableBinding) b).getJavaElement());
	    yield Optional.empty();
	}

	default:
	    yield Optional.empty();
	};
    }

}
