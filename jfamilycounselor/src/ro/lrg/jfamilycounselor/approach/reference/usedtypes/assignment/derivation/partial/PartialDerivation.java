package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.partial;

import static ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.util.LowestRecordedTypeUtil.updateLowestRecordedType;
import static ro.lrg.jfamilycounselor.capability.type.ConcreteConeCapability.concreteCone;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperFieldAccess;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.capability.parse.ParseCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Capability that performs partial-derivation, i.e. derivation of a single
 * expression that will stop at the expression that can no longer be derived
 * without leaving the current scope. The derivation of the JDT assigned
 * elements, which cannot be executed without extending the scope, produces
 * expressions that can be derived in the current scope, leading to new JDT
 * assigned elements.
 * 
 * During each step, partial-derivation tries to update the lowest recored type.
 * If the type of the derived expression is a leaf node in it's hierarchy,
 * partial-derivation succeeds.
 * 
 * Each expression type is tagged with one of the following:
 * 
 * <pre>
 * 	IRRELEVANT   - cannot be encountered during partial-derivation
 * 	CONTINUE     - partial-derivation can continue
 * 	SUCCESS      - partial-derivation stops since it found an assigned concrete type
 * 	HALT         - partial-derivation cannot continue anymore, so we update the lowest recorded type and the assigned element, if possible
 * </pre>
 * 
 * 
 * The Outcome column details all actions taken for each type of expressions.
 * The result of the derivations is represented by a pair of (optional of a new
 * assigned java element, optional of a new lowest recorded type).
 * 
 * For all irrelevant expressions, the outcome is the same, i. e. no writing
 * java element, and the last recorded type is also not updated.
 * 
 * <pre>
 * 	Expression			Tag
 * 
 * 	Annotation                    - IRRELEVANT
 *      ArrayAccess                   - HALT
 *      ArrayCreation                 - HALT
 *      ArrayInitializer              - IRRELEVANT
 *      Assignment                    - CONTINUE
 *      BooleanLiteral                - IRRELEVANT
 *      CastExpression                - CONTINUE
 *      CharacterLiteral              - IRRELEVANT
 *      ClassInstanceCreation         - SUCCESS
 *      ConditionalExpression         - CONTINUE
 *      CreationReference             - IRRELEVANT
 *      ExpressionMethodReference     - IRRELEVANT
 *      FieldAccess                   - HALT
 *      InfixExpression               - IRRELEVANT
 *      InstanceofExpression          - IRRELEVANT
 *      LambdaExpression              - SUCCESS
 *      MethodInvocation              - HALT
 *      MethodReference               - SUCCESS
 *      SimpleName                    - CONTINUE / HALT
 *      QualifiedName                 - HALT
 *      NullLiteral                   - HALT                  
 *      NumberLiteral                 - IRRELEVANT
 *      ParenthesizedExpression       - CONTINUE
 *      PostfixExpression             - IRRELEVANT
 *      PrefixExpression              - IRRELEVANT
 *      StringLiteral                 - IRRELEVANT
 *      SuperFieldAccess              - HALT
 *      SuperMethodInvocation         - HALT
 *      SuperMethodReference          - IRRELEVANT
 *      ThisExpression                - HALT
 *      TypeLiteral                   - IRRELEVANT
 *      TypeMethodReference           - IRRELEVANT
 *      VariableDeclarationExpression - IRRELEVANT
 * </pre>
 * 
 * @author rosualinpetru
 *
 */
public class PartialDerivation {
    private PartialDerivation() {
    }

    private static final Logger logger = jFCLogger.getLogger();

    public static List<PartialDerivationResult> partialDerive(Expression expression) {
	// track all derived expressions to prevent loops
	var derived = new Stack<Expression>();

	// for each derivation, track the last recorded type
	var workingStack = new Stack<Pair<Expression, Optional<IType>>>();

	// all results will be added in this stack
	var succeddedOrHaltedResults = new Stack<PartialDerivationResult>();

	workingStack.push(Pair.of(expression, Optional.empty()));

	while (!workingStack.isEmpty()) {
	    var current = workingStack.pop();
	    var currentExpression = current._1;
	    var lastRecordedType = current._2;

	    if (derived.contains(currentExpression)) {
		continue;
	    }

	    derived.push(currentExpression);

	    var newRecordedType = Optional.ofNullable(currentExpression.resolveTypeBinding()).filter(b -> b.getJavaElement() instanceof IType).map(b -> (IType) b.getJavaElement());

	    var updatedLowestRecordedType = updateLowestRecordedType(lastRecordedType, newRecordedType);

	    var haltNoElementResult = new PartialDerivationResult(Optional.empty(), updatedLowestRecordedType);
	    var successResult = new PartialDerivationResult(newRecordedType.map(AssignedElement.ResolvedType::new), updatedLowestRecordedType);

	    if (newRecordedType.stream().allMatch(t -> concreteCone(t).stream().allMatch(l -> l.size() == 1))) {
		succeddedOrHaltedResults.add(successResult);
		continue;
	    }

	    switch (currentExpression.getNodeType()) {
	    case ASTNode.ARRAY_ACCESS: {
		succeddedOrHaltedResults.add(haltNoElementResult);
		break;
	    }

	    case ASTNode.ARRAY_CREATION: {
		succeddedOrHaltedResults.add(haltNoElementResult);
		break;
	    }

	    case ASTNode.ASSIGNMENT: {
		var assignment = (Assignment) currentExpression;
		workingStack.push(Pair.of(assignment.getRightHandSide(), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.CAST_EXPRESSION: {
		var cast = (CastExpression) currentExpression;
		workingStack.push(Pair.of(cast.getExpression(), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.CLASS_INSTANCE_CREATION: {
		succeddedOrHaltedResults.add(successResult);
		break;
	    }

	    case ASTNode.CONDITIONAL_EXPRESSION: {
		var conditionalExpression = (ConditionalExpression) currentExpression;
		workingStack.push(Pair.of(conditionalExpression.getExpression(), updatedLowestRecordedType));
		workingStack.push(Pair.of(conditionalExpression.getElseExpression(), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.FIELD_ACCESS: {
		var fieldAccess = (FieldAccess) currentExpression;
		var field = Optional.ofNullable(fieldAccess.resolveFieldBinding()).map(b -> (IField) b.getJavaElement());

		succeddedOrHaltedResults.add(new PartialDerivationResult(field.map(AssignedElement.Field::new), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.METHOD_INVOCATION: {
		var methodInvocation = (MethodInvocation) currentExpression;
		var method = Optional.ofNullable(methodInvocation.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement());

		succeddedOrHaltedResults.add(new PartialDerivationResult(method.map(AssignedElement.MethodCall::new), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.SIMPLE_NAME: {
		var simpleName = (SimpleName) currentExpression;
		var bindingOpt = Optional.ofNullable(simpleName.resolveBinding());

		if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
		    var binding = (IVariableBinding) bindingOpt.get();

		    if (binding.isParameter()) {
			var param = Optional.of((ILocalVariable) binding.getJavaElement());
			succeddedOrHaltedResults.add(new PartialDerivationResult(param.map(AssignedElement.Parameter::new), updatedLowestRecordedType));
			break;
		    }

		    if (binding.isField()) {
			var field = Optional.ofNullable(simpleName.resolveBinding()).map(b -> (IField) b.getJavaElement());
			succeddedOrHaltedResults.add(new PartialDerivationResult(field.map(AssignedElement.Field::new), updatedLowestRecordedType));
			break;
		    }

		    // local variable
		    if (!binding.isRecordComponent() && !binding.isEnumConstant() && Optional.ofNullable(binding.getDeclaringMethod()).isPresent() && Optional.ofNullable(binding.getJavaElement()).isPresent()) {
			var method = (IMethod) binding.getDeclaringMethod().getJavaElement();
			var methodAST = ParseCapability.parse(method);
			var visitor = new LocalVariableAssignemntVisitor((ILocalVariable) binding.getJavaElement());
			methodAST.stream().forEach(ast -> ast.accept(visitor));
			if (visitor.getAssignments().isEmpty())
			    succeddedOrHaltedResults.add(haltNoElementResult);
			else
			    visitor.getAssignments().forEach(e -> workingStack.push(Pair.of(e, updatedLowestRecordedType)));
			break;
		    }

		    logger.warning("Simple name expression skipped: " + simpleName);
		    break;
		}
		logger.warning("Simple name was not a variable: " + simpleName + ".Type: " + bindingOpt.map(b -> b.getKind()));
		break;
	    }

	    case ASTNode.QUALIFIED_NAME: {
		var qualifiedName = (QualifiedName) currentExpression;
		var bindingOpt = Optional.ofNullable(qualifiedName.resolveBinding());

		if (bindingOpt.isPresent() && bindingOpt.stream().anyMatch(b -> b.getKind() == IBinding.VARIABLE)) {
		    var binding = (IVariableBinding) bindingOpt.get();

		    if (binding.isField()) {
			var field = Optional.ofNullable(qualifiedName.resolveBinding()).map(b -> (IField) b.getJavaElement());
			succeddedOrHaltedResults.add(new PartialDerivationResult(field.map(AssignedElement.Field::new), updatedLowestRecordedType));
			break;
		    }

		    logger.warning("Qualified name expression skipped: " + qualifiedName);
		    succeddedOrHaltedResults.add(haltNoElementResult);
		    break;
		}
		logger.warning("Qualified name was not a variable: " + qualifiedName);
		succeddedOrHaltedResults.add(haltNoElementResult);
		break;
	    }

	    case ASTNode.PARENTHESIZED_EXPRESSION: {
		var parenthesizedExpression = (ParenthesizedExpression) currentExpression;
		workingStack.push(Pair.of(parenthesizedExpression.getExpression(), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.SUPER_FIELD_ACCESS: {
		var superFieldAccess = (SuperFieldAccess) currentExpression;
		var field = Optional.ofNullable(superFieldAccess.resolveFieldBinding()).map(b -> (IField) b.getJavaElement());

		succeddedOrHaltedResults.add(new PartialDerivationResult(field.map(AssignedElement.Field::new), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.SUPER_METHOD_INVOCATION: {
		var superMethodInvocation = (MethodInvocation) currentExpression;
		var method = Optional.ofNullable(superMethodInvocation.resolveMethodBinding()).map(b -> (IMethod) b.getJavaElement());

		succeddedOrHaltedResults.add(new PartialDerivationResult(method.map(AssignedElement.MethodCall::new), updatedLowestRecordedType));
		break;
	    }

	    case ASTNode.THIS_EXPRESSION: {
		succeddedOrHaltedResults.add(haltNoElementResult);
		break;
	    }

	    case ASTNode.NULL_LITERAL: {
		succeddedOrHaltedResults.add(haltNoElementResult);
		break;
	    }

	    case ASTNode.EXPRESSION_METHOD_REFERENCE: {
		succeddedOrHaltedResults.add(successResult);
		break;
	    }

	    case ASTNode.LAMBDA_EXPRESSION: {
		succeddedOrHaltedResults.add(successResult);
		break;
	    }

	    default:
		logger.info("Irrelevant expression was encountered: " + currentExpression + ". Type: " + currentExpression.getNodeType());
		succeddedOrHaltedResults.add(new PartialDerivationResult(Optional.empty(), lastRecordedType));
	    }

	}

	return succeddedOrHaltedResults;

    }
}
