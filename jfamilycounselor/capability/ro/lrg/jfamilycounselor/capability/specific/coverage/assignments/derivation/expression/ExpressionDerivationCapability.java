package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.expression;

import static ro.lrg.jfamilycounselor.capability.generic.subtype.SubtypeCapability.isSubtypeOf;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
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
 *      ArrayCreation                 - IRRELEVANT
 *      ArrayInitializer              - IRRELEVANT
 *      Assignment                    - CONTINUE
 *      BooleanLiteral                - IRRELEVANT
 *      CastExpression                - CONTINUE              - derive and update last recorded type
 *      CharacterLiteral              - IRRELEVANT
 *      ClassInstanceCreation         - SUCCESS               - t: IType => (Some(t), Some(t))
 *      ConditionalExpression         - CONTINUE
 *      CreationReference             - IRRELEVANT
 *      ExpressionMethodReference     - IRRELEVANT
 *      FieldAccess                   - SUCCESS               - f: IField, t: IType => (Some(f), Some(t))
 *      InfixExpression               - IRRELEVANT
 *      InstanceofExpression          - IRRELEVANT
 *      LambdaExpression              - IRRELEVANT
 *      MethodInvocation              - HALT                  - m: IMethod, t: IType => (Some(m), Some(t))
 *      MethodReference               - IRRELEVANT
 *      SimpleName                    - CONTINUE / SUCCESS => - param: ILocalVariable, t: IType => (Some(param), Some(t))
 *      QualifiedName                 - IRRELEVANT
 *      NullLiteral                   - IRRELEVANT
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

    public static List<Pair<Optional<? extends IJavaElement>, Optional<IType>>> derive(Expression expression) {
	// track all derived expressions to prevent loops
	var derived = new Stack<Expression>();

	// for each derivation, track the last recorded type
	var workingStack = new Stack<Pair<Expression, Optional<IType>>>();

	// all results will be added in this stack
	var succeddedOrHaltedDerivations = new Stack<Pair<Optional<? extends IJavaElement>, Optional<IType>>>();

	workingStack.push(new Pair<>(expression, Optional.empty()));

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
		var newRecordedType = Optional.ofNullable(arrayAccess.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new Pair<>(Optional.empty(), updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.ASSIGNMENT: {
		var assignment = (Assignment) currentExpression;
		workingStack.push(new Pair<>(assignment.getRightHandSide(), lastRecordedType));
		break;
	    }

	    case ASTNode.CAST_EXPRESSION: {
		var cast = (CastExpression) currentExpression;
		var newRecordedType = Optional.ofNullable(cast.getType().resolveBinding()).map(b -> (IType) b.getJavaElement());
		workingStack.push(new Pair<>(cast.getExpression(), updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.CLASS_INSTANCE_CREATION: {
		var instantiation = (ClassInstanceCreation) currentExpression;
		var newRecordedType = Optional.ofNullable(instantiation.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new Pair<>(newRecordedType, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.CONDITIONAL_EXPRESSION: {
		var conditionalExpression = (ConditionalExpression) currentExpression;
		workingStack.push(new Pair<>(conditionalExpression.getExpression(), lastRecordedType));
		workingStack.push(new Pair<>(conditionalExpression.getElseExpression(), lastRecordedType));
		break;
	    }

	    case ASTNode.FIELD_ACCESS: {
		var fieldAccess = (FieldAccess) currentExpression;
		var newRecordedType = Optional.ofNullable(fieldAccess.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
		var field = Optional.ofNullable(fieldAccess.resolveFieldBinding()).map(b -> (IField) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new Pair<>(field, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.METHOD_INVOCATION: {
		var methodInvocation = (MethodInvocation) currentExpression;
		var newRecordedType = Optional.ofNullable(methodInvocation.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
		var method = Optional.ofNullable(methodInvocation.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new Pair<>(method, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.SIMPLE_NAME: {
		var simpleName = (SimpleName) currentExpression;
		var bindingOpt = Optional.ofNullable(simpleName.resolveBinding());

		if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
		    var binding = (IVariableBinding) bindingOpt.get();
		    var newRecordedType = Optional.ofNullable(simpleName.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());

		    if (binding.isParameter()) {
			var param = Optional.of((ILocalVariable) binding.getJavaElement());
			succeddedOrHaltedDerivations.add(new Pair<>(param, updateRecordedType(lastRecordedType, newRecordedType)));
			break;
		    }

		    if (binding.isField()) {
			var field = Optional.ofNullable(simpleName.resolveBinding()).map(b -> (IField) b.getJavaElement());
			succeddedOrHaltedDerivations.add(new Pair<>(field, updateRecordedType(lastRecordedType, newRecordedType)));
			break;
		    }

		    // local variable
		    if (!binding.isRecordComponent() && !binding.isEnumConstant() && Optional.ofNullable(binding.getDeclaringMethod()).isPresent() && Optional.ofNullable(binding.getJavaElement()).isPresent()) {
			var method = (IMethod) binding.getDeclaringMethod().getJavaElement();
			var methodAST = ParseCapability.parse(method);
			var visitor = new LocalVariableAssignemntVisitor((ILocalVariable) binding.getJavaElement());
			methodAST.stream().forEach(ast -> ast.accept(visitor));
			if (visitor.getAssignments().isEmpty())
			    succeddedOrHaltedDerivations.add(new Pair<>(Optional.empty(), updateRecordedType(lastRecordedType, newRecordedType)));
			else
			    visitor.getAssignments().forEach(e -> workingStack.push(new Pair<>(e, updateRecordedType(lastRecordedType, newRecordedType))));
			break;
		    }

		    logger.warning("Simple name expression skipped: " + simpleName);

		}
	    }

	    case ASTNode.PARENTHESIZED_EXPRESSION: {
		var parenthesizedExpression = (ParenthesizedExpression) currentExpression;
		workingStack.push(new Pair<>(parenthesizedExpression.getExpression(), lastRecordedType));
		break;
	    }

	    case ASTNode.SUPER_FIELD_ACCESS: {
		var superFieldAccess = (SuperFieldAccess) currentExpression;
		var newRecordedType = Optional.ofNullable(superFieldAccess.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
		var field = Optional.ofNullable(superFieldAccess.resolveFieldBinding()).map(b -> (IField) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new Pair<>(field, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.SUPER_METHOD_INVOCATION: {
		var superMethodInvocation = (MethodInvocation) currentExpression;
		var newRecordedType = Optional.ofNullable(superMethodInvocation.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
		var method = Optional.ofNullable(superMethodInvocation.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement());

		succeddedOrHaltedDerivations.add(new Pair<>(method, updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    case ASTNode.THIS_EXPRESSION: {
		var thisExpression = (ThisExpression) currentExpression;
		var newRecordedType = Optional.ofNullable(thisExpression.resolveTypeBinding()).map(b -> (IType) b.getJavaElement());
		succeddedOrHaltedDerivations.add(new Pair<>(Optional.empty(), updateRecordedType(lastRecordedType, newRecordedType)));
		break;
	    }

	    default:
		logger.warning("Irrelevant expression was in fact encountered: " + currentExpression + ". Type: " + currentExpression.getNodeType());
		succeddedOrHaltedDerivations.add(new Pair<>(Optional.empty(), lastRecordedType));
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
