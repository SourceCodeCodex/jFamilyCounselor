package ro.lrg.jfamilycounselor.capability.specific.coverage.assignment.derivation.expression;

import static ro.lrg.jfamilycounselor.capability.generic.cone.SubtypeCapability.isSubtypeOf;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.ThisExpression;

import ro.lrg.jfamilycounselor.capability.generic.parse.ParseCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Each expression type is tagged with one of the following:
 * 
 * <pre>
 * 	IRRELEVANT   - cannot be encountered during derivation
 * 	CONTINUE     - derivation required
 * 	SUCCESS      - derivation stops, resulting in a new Java writing element
 * 	HALT         - derivation cannot continue anymore, so we update the last recorded type
 * </pre>
 * 
 * 
 * The Outcome column details all actions taken for each type of expressions.
 * The result of the derivations is represented by a pair of (maybe a writing
 * java element, maybe update last recorded type).
 * 
 * For all irrelevant expressions, the outcome is the same, i. e. no writing
 * java element, and the last recorded type is also not updated.
 * 
 * All possible expressions:
 * 
 * <pre>
 * 	Expression			Tag			Outcome	
 * 
 * 	Annotation                    - IRRELEVANT
 *      ArrayAccess                   - HALT                  - t: IType => (None, Some(t))
 *      ArrayCreation                 - HALT                  - t: IType => (None, Some(t))
 *      ArrayInitializer              - IRRELEVANT
 *      Assignment                    - CONTINUE
 *      BooleanLiteral                - IRRELEVANT
 *      CastExpression                - CONTINUE              - derive and update last recorded type
 *      CharacterLiteral              - IRRELEVANT
 *      ClassInstanceCreation         - SUCCESS               - t: IType => (Some(t), Some(t))
 *      ConditionalExpression         - CONTINUE
 *      CreationReference             - IRRELEVANT
 *      ExpressionMethodReference     - IRRELEVANT
 *      FieldAccess                   - HALT                  - f: IField, t: IType => (Some(f), Some(t))
 *      InfixExpression               - IRRELEVANT
 *      InstanceofExpression          - IRRELEVANT
 *      LambdaExpression              - SUCCESS
 *      MethodInvocation              - HALT                  - m: IMethod, t: IType => (Some(m), Some(t))
 *      MethodReference               - SUCCESS
 *      SimpleName                    - CONTINUE / SUCCESS => - param: ILocalVariable, t: IType => (Some(param), Some(t))
 *      QualifiedName                 - HALT
 *      NullLiteral                   - HALT                  - dead end
 *      NumberLiteral                 - IRRELEVANT
 *      ParenthesizedExpression       - CONTINUE
 *      PostfixExpression             - IRRELEVANT
 *      PrefixExpression              - IRRELEVANT
 *      StringLiteral                 - IRRELEVANT
 *      SuperFieldAccess              - HALT                  - f: IField, t: IType => (Some(f), Some(t))
 *      SuperMethodInvocation         - HALT                  - m: IMethod, t: IType => (Some(m), Some(t))
 *      SuperMethodReference          - IRRELEVANT
 *      ThisExpression                - HALT                  - t: IType => (None, Some(t))
 *      TypeLiteral                   - IRRELEVANT
 *      TypeMethodReference           - IRRELEVANT
 *      VariableDeclarationExpression - IRRELEVANT
 * </pre>
 * 
 * @author rosualinpetru
 *
 */
public class ExpressionDerivationCapability {
    private ExpressionDerivationCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static List<ExpressionDerivationResult> derive(Expression expression) {
	// track all derived expressions to prevent loops
	var derived = new Stack<Expression>();

	// for each derivation, track the last recorded type
	var workingStack = new Stack<Pair<Expression, Optional<IType>>>();

	// all results will be added in this stack
	var succeddedOrHaltedDerivations = new Stack<ExpressionDerivationResult>();

	workingStack.push(Pair.of(expression, Optional.empty()));

	while (!workingStack.isEmpty()) {
	    var current = workingStack.pop();
	    var currentExpression = current._1;
	    var lastRecordedType = current._2;

	    if (derived.contains(currentExpression)) {
		continue;
	    }

	    derived.push(currentExpression);

	    switch (currentExpression.getNodeType()) {
	    case ASTNode.ARRAY_ACCESS: {
		var arrayAccess = (ArrayAccess) currentExpression;
		var newRecordedType = Optional.ofNullable(arrayAccess.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }
	    
	    case ASTNode.ARRAY_CREATION: {
		var arrayCreation = (ArrayCreation) currentExpression;
		var newRecordedType = Optional.ofNullable(arrayCreation.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.ASSIGNMENT: {
		var assignment = (Assignment) currentExpression;
		workingStack.push(Pair.of(assignment.getRightHandSide(), lastRecordedType));
		break;
	    }

	    case ASTNode.CAST_EXPRESSION: {
		var cast = (CastExpression) currentExpression;
		var newRecordedType = Optional.ofNullable(cast.getType().resolveBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		workingStack.push(Pair.of(cast.getExpression(), updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.CLASS_INSTANCE_CREATION: {
		var instantiation = (ClassInstanceCreation) currentExpression;
		var newRecordedType = Optional.ofNullable(instantiation.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(newRecordedType, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.CONDITIONAL_EXPRESSION: {
		var conditionalExpression = (ConditionalExpression) currentExpression;
		workingStack.push(Pair.of(conditionalExpression.getExpression(), lastRecordedType));
		workingStack.push(Pair.of(conditionalExpression.getElseExpression(), lastRecordedType));
		break;
	    }

	    case ASTNode.FIELD_ACCESS: {
		var fieldAccess = (FieldAccess) currentExpression;
		var newRecordedType = Optional.ofNullable(fieldAccess.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		var field = Optional.ofNullable(fieldAccess.resolveFieldBinding()).map(b -> (IField) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(field, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.METHOD_INVOCATION: {
		var methodInvocation = (MethodInvocation) currentExpression;
		var newRecordedType = Optional.ofNullable(methodInvocation.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		var method = Optional.ofNullable(methodInvocation.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(method, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.SIMPLE_NAME: {
		var simpleName = (SimpleName) currentExpression;
		var bindingOpt = Optional.ofNullable(simpleName.resolveBinding());

		if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
		    var binding = (IVariableBinding) bindingOpt.get();
		    var newRecordedType = Optional.ofNullable(simpleName.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());

		    if (binding.isParameter()) {
			var param = Optional.of((ILocalVariable) binding.getJavaElement());
			succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(param, updateRecordedType(lastRecordedType, newRecordedType)));
			break;
		    }

		    if (binding.isField()) {
			var field = Optional.ofNullable(simpleName.resolveBinding()).map(b -> (IField) b.getJavaElement());
			succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(field, updateRecordedType(lastRecordedType, newRecordedType)));
			break;
		    }

		    // local variable
		    if (!binding.isRecordComponent() && !binding.isEnumConstant() && Optional.ofNullable(binding.getDeclaringMethod()).isPresent() && Optional.ofNullable(binding.getJavaElement()).isPresent()) {
			var method = (IMethod) binding.getDeclaringMethod().getJavaElement();
			var methodAST = ParseCapability.parse(method);
			var visitor = new LocalVariableAssignemntVisitor((ILocalVariable) binding.getJavaElement());
			methodAST.stream().forEach(ast -> ast.accept(visitor));
			if (visitor.getAssignments().isEmpty())
			    succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), updateRecordedType(lastRecordedType, newRecordedType)));
			else
			    visitor.getAssignments().forEach(e -> workingStack.push(Pair.of(e, updateRecordedType(lastRecordedType, newRecordedType))));
			break;
		    }

		    logger.warning("Simple name expression skipped: " + simpleName);
		    break;
		}
		logger.warning("Simple name was not a variable: " + simpleName  + ".Type: " + bindingOpt.map(b -> b.getKind()));
		break;
	    }

	    case ASTNode.QUALIFIED_NAME: {
		var qualifiedName = (QualifiedName) currentExpression;
		var bindingOpt = Optional.ofNullable(qualifiedName.resolveBinding());

		if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
		    var binding = (IVariableBinding) bindingOpt.get();
		    var newRecordedType = Optional.ofNullable(qualifiedName.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());

		    if (binding.isField()) {
			var field = Optional.ofNullable(qualifiedName.resolveBinding()).map(b -> (IField) b.getJavaElement());
			succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(field, updateRecordedType(lastRecordedType, newRecordedType)));
			break;
		    }

		    logger.warning("Qualified name expression skipped: " + qualifiedName);
		    succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), lastRecordedType));
		    break;
		}
		logger.warning("Qualified name was not a variable: " + qualifiedName);
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), lastRecordedType));
		break;
	    }

	    case ASTNode.PARENTHESIZED_EXPRESSION: {
		var parenthesizedExpression = (ParenthesizedExpression) currentExpression;
		workingStack.push(Pair.of(parenthesizedExpression.getExpression(), lastRecordedType));
		break;
	    }

	    case ASTNode.SUPER_FIELD_ACCESS: {
		var superFieldAccess = (SuperFieldAccess) currentExpression;
		var newRecordedType = Optional.ofNullable(superFieldAccess.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		var field = Optional.ofNullable(superFieldAccess.resolveFieldBinding()).map(b -> (IField) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(field, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.SUPER_METHOD_INVOCATION: {
		var superMethodInvocation = (MethodInvocation) currentExpression;
		var newRecordedType = Optional.ofNullable(superMethodInvocation.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		var method = Optional.ofNullable(superMethodInvocation.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(method, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.THIS_EXPRESSION: {
		var thisExpression = (ThisExpression) currentExpression;
		var newRecordedType = Optional.ofNullable(thisExpression.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }
	    
	    case ASTNode.NULL_LITERAL: {
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), lastRecordedType));
		break;
	    }
	    
	    case ASTNode.EXPRESSION_METHOD_REFERENCE: {
		var methodReference = (ExpressionMethodReference) currentExpression;
		var newRecordedType = Optional.ofNullable(methodReference.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(newRecordedType, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }
	    
	    case ASTNode.LAMBDA_EXPRESSION: {
		var lambdaExpression = (LambdaExpression) currentExpression;
		var newRecordedType = Optional.ofNullable(lambdaExpression.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(newRecordedType, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }
	    
	    default:
		logger.info("Irrelevant expression was encountered: " + currentExpression + ". Type: " + currentExpression.getNodeType());
		succeddedOrHaltedDerivations.add(new ExpressionDerivationResult(Optional.empty(), lastRecordedType));
	    }

	}

	return succeddedOrHaltedDerivations;

    }

    private static Optional<IType> updateRecordedType(Optional<IType> previous, Optional<IType> current) {
	if (previous.isEmpty())
	    return current;

	if (current.isEmpty())
	    return previous;

	if (previous.stream().anyMatch(t1 -> current.stream().anyMatch(t2 -> isSubtypeOf(t1, t2))))
	    return current;

	return previous;
    }
}
