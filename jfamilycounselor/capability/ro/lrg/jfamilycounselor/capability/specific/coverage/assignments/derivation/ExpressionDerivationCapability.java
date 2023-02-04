package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * Each expression type is tagged with one of the following:
 * 
 * <pre>
 * 	IRRELEVANT   - cannot be encountered during derivation / cannot provide useful information in the analysis
 * 	INCONCLUSIVE - might provide useful information, yet its derivation is unknown so it is considered irrelevant (for now)
 * 	REQUIRED     - requires derivation
 * 	FIXED        - derivation results in identity (fixed point)
 * </pre>
 * 
 * All possible expressions:
 * 
 * <pre>
 * 	Annotation                    - IRRELEVANT
 *      ArrayAccess                   - FIXED
 *      ArrayCreation                 - IRRELEVANT
 *      ArrayInitializer              - IRRELEVANT
 *      Assignment                    - REQUIRED
 *      BooleanLiteral                - IRRELEVANT
 *      CastExpression                - REQUIRED
 *      CharacterLiteral              - IRRELEVANT
 *      ClassInstanceCreation         - FIXED
 *      ConditionalExpression         - REQUIRED
 *      CreationReference             - IRRELEVANT
 *      ExpressionMethodReference     - IRRELEVANT
 *      FieldAccess                   - INCONCLUSIVE
 *      InfixExpression               - IRRELEVANT
 *      InstanceofExpression          - IRRELEVANT
 *      LambdaExpression              - IRRELEVANT
 *      MethodInvocation              - INCONCLUSIVE
 *      MethodReference               - IRRELEVANT
 *      SimpleName                    - REQUIRED / FIXED
 *      QualifiedName                 - INCONCLUSIVE
 *      NullLiteral                   - IRRELEVANT
 *      NumberLiteral                 - IRRELEVANT
 *      ParenthesizedExpression       - REQUIRED
 *      PostfixExpression             - IRRELEVANT
 *      PrefixExpression              - IRRELEVANT
 *      StringLiteral                 - IRRELEVANT
 *      SuperFieldAccess              - INCONCLUSIVE
 *      SuperMethodInvocation         - INCONCLUSIVE
 *      SuperMethodReference          - IRRELEVANT
 *      ThisExpression                - FIXED
 *      TypeLiteral                   - IRRELEVANT
 *      TypeMethodReference           - IRRELEVANT
 *      VariableDeclarationExpression - INCONCLUSIVE
 * </pre>
 * 
 * @author rosualinpetru
 *
 */
public class ExpressionDerivationCapability {
    private ExpressionDerivationCapability() {
    }
    
   
    
    private static boolean requiresDerivation(Expression expression) {
	return switch (expression.getNodeType()) {
	case ASTNode.ASSIGNMENT: 
	case ASTNode.CAST_EXPRESSION:
	case ASTNode.CONDITIONAL_EXPRESSION:
	case ASTNode.PARENTHESIZED_EXPRESSION:
	    yield true;
	case ASTNode.SIMPLE_NAME:
	    yield isSimpleNameLocalVariable((SimpleName) expression);
	default:
	    yield false;
	};
    }

    private static boolean isFixed(Expression expression) {
	return switch (expression.getNodeType()) {
	case ASTNode.ARRAY_ACCESS:
	case ASTNode.CLASS_INSTANCE_CREATION:
	case ASTNode.THIS_EXPRESSION:
	    yield true;
	case ASTNode.SIMPLE_NAME:
	    yield isSimpleNameParameter((SimpleName) expression);
	default:
	    yield false;
	};
    }

    private static boolean isSimpleNameLocalVariable(SimpleName simpleName) {
	var bindingOpt = Optional.ofNullable(simpleName.resolveBinding());

	if (bindingOpt.isEmpty() || bindingOpt.stream().anyMatch(b -> b.getKind() != IBinding.VARIABLE))
	    return false;

	var binding = (IVariableBinding) bindingOpt.get();
	return !(binding.isEnumConstant() || binding.isField() || binding.isParameter() || binding.isRecordComponent());
    }
    
    private static boolean isSimpleNameParameter(SimpleName simpleName) {
	var bindingOpt = Optional.ofNullable(simpleName.resolveBinding());

	if (bindingOpt.isEmpty() || bindingOpt.stream().anyMatch(b -> b.getKind() != IBinding.VARIABLE))
	    return false;

	var binding = (IVariableBinding) bindingOpt.get();
	return binding.isParameter();
    }

}
