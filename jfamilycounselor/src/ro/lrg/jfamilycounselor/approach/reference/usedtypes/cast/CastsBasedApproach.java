package ro.lrg.jfamilycounselor.approach.reference.usedtypes.cast;

import static ro.lrg.jfamilycounselor.capability.ast.cast.TypeCastCapability.extractTypeCastsFromScope;
import static ro.lrg.jfamilycounselor.capability.ast.cast.TypeCastCapability.isDowncast;
import static ro.lrg.jfamilycounselor.capability.ast.cast.TypeCastCapability.isGuarded;
import static ro.lrg.jfamilycounselor.capability.method.MethodOverrideCapability.isMethodOverriding;
import static ro.lrg.jfamilycounselor.capability.search.cast.TypeCastSearchCapability.searchTypeCasts;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperFieldAccess;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.CorrelationEstimationUsedTypesApproach;
import ro.lrg.jfamilycounselor.util.Constants;

public class CastsBasedApproach extends CorrelationEstimationUsedTypesApproach {
	private static CastsBasedApproach instance = new CastsBasedApproach();

	private CastsBasedApproach() {
	}

	public static CastsBasedApproach instance() {
		return instance;
	}

	@Override
	public boolean areCorrelated(IType t1, IType t2) {

		// search for type casts and find the methods that enclose them
		// it is also important that these methods override
		var enclosingT1CastsMethodsOpt = searchTypeCasts(t1);
		var enclosingT2CastsMethodsOpt = searchTypeCasts(t2);

		var enclosingT1CastsMethods = filterRelevantMethods(enclosingT1CastsMethodsOpt.orElse(List.of()));
		var enclosingT2CastsMethods = filterRelevantMethods(enclosingT2CastsMethodsOpt.orElse(List.of()));

		// if there are no casts of any of the analyzed types in relevant methods, then
		// it is unknown if they are correlated
		if (enclosingT1CastsMethods.isEmpty() && enclosingT2CastsMethods.isEmpty())
			return false;

		try {
			// ---------------------------------------------------------------------------------------------
			// CASE I: There is a relevant cast of T1 in T2

			// we check if there are casts to T1 present in the methods of T2
			var castsT1Opt = extractTypeCastsFromScope(t1,
					enclosingT1CastsMethods.stream().filter(m -> m.getDeclaringType().equals(t2)).toList());

			// check if the cast follows the pattern that could indicate the presence of
			// hidden type correlationsTypeCastCapability
			var relevantCastsT1 = filterRelevantCasts(castsT1Opt.get());

			if (!relevantCastsT1.isEmpty())
				return true;
		} catch (Throwable e) {
		}

		try {
			// ---------------------------------------------------------------------------------------------
			// CASE II: There is a relevant cast of T2 in T1
			var castsT2Opt = extractTypeCastsFromScope(t2,
					enclosingT2CastsMethods.stream().filter(m -> m.getDeclaringType().equals(t1)).toList());

			var relevantCastsT2 = filterRelevantCasts(castsT2Opt.get());

			if (!relevantCastsT2.isEmpty())
				return true;

		} catch (Throwable e) {
		}

		try {

			// ---------------------------------------------------------------------------------------------
			// CASE III: There are some relevant casts for both T1 and T2 in the same
			// method that is not necessarily a member of one of the two types
			var common = enclosingT1CastsMethods.stream().filter(m -> enclosingT2CastsMethods.contains(m)).toList();

			if (common.isEmpty())
				return false;

			for (IMethod m : common) {
				var castsT1Opt = extractTypeCastsFromScope(t1, List.of(m));
				var castsT2Opt = extractTypeCastsFromScope(t2, List.of(m));

				if (castsT1Opt.isEmpty() || castsT2Opt.isEmpty())
					continue;

				var relevantCastsT1 = filterRelevantCasts(castsT1Opt.get());
				var relevantCastsT2 = filterRelevantCasts(castsT2Opt.get());

				var t1CastedVariables = relevantCastsT1.stream().map(CastsBasedApproach::extractCastedVariable)
						.filter(o -> o.isPresent()).map(o -> o.get()).toList();

				var t2CastedVariables = relevantCastsT2.stream().map(CastsBasedApproach::extractCastedVariable)
						.filter(o -> o.isPresent()).map(o -> o.get()).toList();

				if (!t1CastedVariables.isEmpty() && !t2CastedVariables.isEmpty()
						&& t1CastedVariables.stream().anyMatch(v -> !t2CastedVariables.contains(v)))
					return true;
			}

		} catch (Throwable e) {
		}

		return false;

	}

	private static List<IMethod> filterRelevantMethods(List<IMethod> enclosingCastsMethods) {
		return enclosingCastsMethods.stream().filter(m -> !m.getElementName().contains(Constants.EQUAL))
				.filter(m -> isMethodOverriding(m).orElse(false)).toList();
	}

	private static List<CastExpression> filterRelevantCasts(List<Supplier<CastExpression>> casts) {
		return casts.stream().map(supplier -> supplier.get())
				.filter(ce -> isDowncast(ce).orElse(false) && castsFieldOrParameter(ce) && !isGuarded(ce)).toList();
	}

	private static boolean castsFieldOrParameter(CastExpression castExpression) {
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

	private static Optional<IJavaElement> extractCastedVariable(CastExpression castExpression) {
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
