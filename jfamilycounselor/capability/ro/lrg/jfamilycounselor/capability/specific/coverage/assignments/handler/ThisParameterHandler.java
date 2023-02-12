package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.handler;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;

import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.element.InvokerParameterDerivationCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.expression.ExpressionDerivationCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.Assignment;
import ro.lrg.jfamilycounselor.util.list.CommonOperations;

/**
 * Handler specific for This-Parameter / Parameter-This (swapping) references
 * pair. Its behavior varies depending on a manifold of factors.
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
 * cases, the first assignments does not need to be derivated.
 * 
 * 
 * The 'else' branch handles the case when the derivation is going to step out
 * of the references' declaring type. Therefore, we have an invoking expression
 * for the method call, which needs to be derivated.
 * 
 * 
 * @author rosualinpetru
 *
 */
public class ThisParameterHandler {
    private ThisParameterHandler() {
    }

    public static List<AssignemntsPair> handle(AssignemntsPair assignemntsPair) {
	var writingParam = (ILocalVariable) assignemntsPair._2.writingElement().get();

	var initialExpressions = InvokerParameterDerivationCapability.derive(writingParam);

	return initialExpressions.parallelStream()
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
    }

}
