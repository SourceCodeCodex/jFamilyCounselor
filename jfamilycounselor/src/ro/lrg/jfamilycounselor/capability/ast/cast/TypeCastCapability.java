package ro.lrg.jfamilycounselor.capability.ast.cast;

import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.lazy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import ro.lrg.jfamilycounselor.capability.parse.ParseCapability;
import ro.lrg.jfamilycounselor.capability.search.cast.TypeCastSearchCapability;
import ro.lrg.jfamilycounselor.capability.type.SubtypeCapability;

/**
 * Capability that extracts all type cast expressions, either from the entire
 * workspace, or from some specified enclosing methods.
 * 
 * Also, the capability provide some extra checks related to casts.
 * 
 * @author rosualinpetru
 *
 */
public class TypeCastCapability {
    private TypeCastCapability() {
    }

    public static Optional<List<Supplier<CastExpression>>> extractTypeCastsFromScope(IType iType, List<IMethod> enclosingMethods) {
	return Optional.of(
		enclosingMethods.stream()
			.map(ParseCapability::parse)
			.filter(o -> o.isPresent())
			.map(o -> o.get())
			.flatMap(ast -> {
			    var visitor = new TypeCastVisitor(iType);
			    ast.accept(visitor);
			    return visitor.getInvocations().stream().map(p -> lazy(p));
			}).toList());
    }

    public static Optional<List<Supplier<CastExpression>>> extractTypeCasts(IType iType) {
	var typeCastsEnclosingMethods = TypeCastSearchCapability.searchTypeCasts(iType);
	if (typeCastsEnclosingMethods.isEmpty())
	    return Optional.empty();

	return extractTypeCastsFromScope(iType, typeCastsEnclosingMethods.get());
    }

    public static boolean isGuarded(CastExpression castExpression) {
	var methodDeclaration = enclosingMethodAST(castExpression);
	var unguardedCastVisitor = new UnguardedCastVisitor(castExpression);
	methodDeclaration.accept(unguardedCastVisitor);
	return unguardedCastVisitor.isGuarded();
    }

    public static Optional<Boolean> isDowncast(CastExpression castExpression) {
	try {
	    var castTypeOpt = Optional.ofNullable(castExpression.getType().resolveBinding()).map(b -> (IType) b.getJavaElement());
	    var castExpressionTypeOpt = Optional.ofNullable(castExpression.getExpression().resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
	    return castTypeOpt.flatMap(castType -> castExpressionTypeOpt.map(castExpressionType -> SubtypeCapability.isSubtypeOf(castType, castExpressionType)));
	} catch (Throwable e) {
	    return Optional.empty();
	}
    }

    private static MethodDeclaration enclosingMethodAST(CastExpression castExpression) {
	ASTNode methodDeclaration = castExpression;

	while (methodDeclaration.getNodeType() != ASTNode.METHOD_DECLARATION)
	    methodDeclaration = methodDeclaration.getParent();

	return (MethodDeclaration) methodDeclaration;

    }

}
