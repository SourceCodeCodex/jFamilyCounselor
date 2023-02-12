package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.handler;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;

import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.element.ParameterParameterDerivationCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.expression.ExpressionDerivationCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.model.Assignment;
import ro.lrg.jfamilycounselor.util.list.CommonOperations;

/**
 * Handler specific for Parameter-Parameter references pair. It runs in two
 * steps:
 * 
 * - finds the arguments from the methods' invocations
 * 
 * - derives the expressions until reaching new Java elements
 * 
 * @author rosualinpetru
 *
 */
public class ParameterParameterHandler {
    private ParameterParameterHandler() {
    }

    public static List<AssignemntsPair> handle(AssignemntsPair assignemntsPair) {
	var writingParam1 = (ILocalVariable) assignemntsPair._1.writingElement().get();
	var writingParam2 = (ILocalVariable) assignemntsPair._2.writingElement().get();

	var initialExpressions = ParameterParameterDerivationCapability.derive(writingParam1, writingParam2, assignemntsPair.passedCombination());

	return initialExpressions.parallelStream()
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
    }

}
