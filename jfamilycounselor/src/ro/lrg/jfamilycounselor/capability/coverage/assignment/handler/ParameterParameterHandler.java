package ro.lrg.jfamilycounselor.capability.coverage.assignment.handler;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;

import ro.lrg.jfamilycounselor.capability.coverage.assignment.derivation.element.ParameterParameterDerivationCapability;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.derivation.expression.ExpressionDerivationCapability;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.Assignment;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.State;
import ro.lrg.jfamilycounselor.util.operations.CommonOperations;

/**
 * Handler specific for Parameter-Parameter references pair. It runs in two
 * steps:
 * 
 * - finds the arguments from the methods' invocations
 * 
 * - derives the expressions until reaching concrete types
 * 
 * @author rosualinpetru
 *
 */
public class ParameterParameterHandler extends ReferencesPairHandler {

    public void handle(AssignemntsPair assignemntsPair, State state) {
	var writingParam1 = (ILocalVariable) assignemntsPair._1.assignedElement().get();
	var writingParam2 = (ILocalVariable) assignemntsPair._2.assignedElement().get();

	var initialExpressions = ParameterParameterDerivationCapability.derive(writingParam1, writingParam2, assignemntsPair.passedCombination());

	var newAssignmentsPairs = initialExpressions.parallelStream()
		.flatMap(pairF -> {
		    var pair = pairF.get();

		    var intraDerivation1 = ExpressionDerivationCapability.derive(pair._1);
		    var intraDerivation2 = ExpressionDerivationCapability.derive(pair._2);

		    var product = CommonOperations.cartesianProduct(intraDerivation1, intraDerivation2);

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
		})
		.toList();

	newAssignmentsPairs.forEach(state.assignmentsPairs()::push);
    }

    protected boolean canHandle(IJavaElement assignedElement1, IJavaElement assignedElement2) {
	return assignedElement1 instanceof ILocalVariable && assignedElement2 instanceof ILocalVariable;
    }

}
