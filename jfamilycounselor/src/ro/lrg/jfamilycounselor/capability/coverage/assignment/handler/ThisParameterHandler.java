package ro.lrg.jfamilycounselor.capability.coverage.assignment.handler;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.coverage.assignment.derivation.element.InvokerParameterDerivationCapability;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.derivation.expression.ExpressionDerivationCapability;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.Assignment;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.State;
import ro.lrg.jfamilycounselor.util.operations.CommonOperations;

/**
 * Handler specific for This-Parameter / Parameter-This (swapping) references
 * pair. Its behavior varies depending on manifold factors.
 * 
 * This happens because of the following collision: IType as an writing element
 * can represent two things:
 * 
 * - the self-writing this reference
 * 
 * - an actually resolved type
 * 
 * The 'then' branch handles the case when we passed combination, so an writing
 * IType represents an actually resolved type or the derivation is still in the
 * references' declaring type (therefore we do not have an invoker) In both
 * cases, the first assignments does not need to be derived.
 * 
 * 
 * The 'else' branch handles the case when the derivation is going to step out
 * of the references' declaring type. Therefore, we have an invoking expression
 * for the method call, which needs to be derived.
 * 
 * 
 * @author rosualinpetru
 *
 */
public class ThisParameterHandler extends ReferencesPairHandler {

    public void handle(AssignemntsPair assignemntsPair, State state) {
	var writingParam = (ILocalVariable) assignemntsPair._2.assignedElement().get();

	var initialExpressions = InvokerParameterDerivationCapability.derive(writingParam);

	var newAssignmentsPairs = initialExpressions.parallelStream()
		.flatMap(pairF -> {
		    var pair = pairF.get();

		    var intraDerivation = ExpressionDerivationCapability.derive(pair._2);

		    if (assignemntsPair.passedCombination() || pair._1.isEmpty()) {
			return intraDerivation.stream()
				.map(derivationResult -> {
				    var newAssignmentsPair = new AssignemntsPair(
					    assignemntsPair._1,
					    new Assignment(assignemntsPair._2.reference(), derivationResult.writingElementUpdate().map(j -> (IJavaElement) j),
						    derivationResult.mostConcreteRecordedTypeUpdate().orElse(assignemntsPair._2.mostConcreteRecordedType())));

				    newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);

				    if (pair._1.isEmpty())
					newAssignmentsPair.setPassedCombination(false);

				    return newAssignmentsPair;
				});
		    } else {
			var invoker = pair._1.get();

			var intraDerivationType = ExpressionDerivationCapability.derive(invoker);

			var product = CommonOperations.cartesianProduct(intraDerivationType, intraDerivation);

			return product.stream()
				.map(derivationResultsPair -> {
				    var r1 = derivationResultsPair._1;
				    var r2 = derivationResultsPair._2;

				    var newAssignmentsPair = new AssignemntsPair(
					    new Assignment(assignemntsPair._1.reference(), r1.writingElementUpdate().map(j -> (IJavaElement) j), r1.mostConcreteRecordedTypeUpdate().orElse(assignemntsPair._1.mostConcreteRecordedType())),
					    new Assignment(assignemntsPair._2.reference(), r2.writingElementUpdate().map(j -> (IJavaElement) j), r2.mostConcreteRecordedTypeUpdate().orElse(assignemntsPair._2.mostConcreteRecordedType())));

				    newAssignmentsPair.setDepth(assignemntsPair.depth() + 1);
				    return newAssignmentsPair;
				});
		    }

		})
		.toList();

	newAssignmentsPairs.forEach(state.assignmentsPairs()::push);
    }

 
    protected boolean canHandle(IJavaElement assignedElement1, IJavaElement assignedElement2) {
	return assignedElement1 instanceof IType && assignedElement2 instanceof ILocalVariable;
    }

}
