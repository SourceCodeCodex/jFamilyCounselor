package ro.lrg.jfamilycounselor.capability.coverage.assignment;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.common.type.DistinctConcreteConeProductCapability;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.handler.ParameterParameterHandler;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.handler.ParameterThisHandler;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.handler.ReferencesPairHandler;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.handler.ThisParameterHandler;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.State;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * Capability that computes the used types using the assignments-based
 * estimation for a pair of references.
 * 
 * Through (assignments') expression derivation we roughly refer to the process
 * in which we "go back" from the class towards exterior, hoping to find
 * instantiations or any expression that might indicate a concrete type of
 * object that could be referred at some point inside by an analyzed reference.
 * 
 * For instance, the derivation of a parameter expression is the list of all
 * expressions that correspond to its actual parameters. For a local variable,
 * is is the list of all expressions that are being assigned to that
 * variable. @see
 * ro.lrg.jfamilycounselor.capability.coverage.assignment.derivation.expression.ExpressionDerivationCapability
 * 
 * The assignments-based estimation is a worklist algorithm that derives
 * assignments pairs until no new ones can be generated, updating the state in
 * the process. @see
 * ro.lrg.jfamilycounselor.capability.coverage.assignment.model.State
 * 
 * The derivation of pairs of assignments (their expressions, which lead to new
 * pairs) depends on what the assigned expressions are, e.g. parameters, local
 * variables, fields, etc. Also, the derivation is different in some cases if
 * both assignments of a pair have particular expressions.
 * 
 * For now, we handle the following pairs of assigned expressions by chaining
 * their respective handlers:
 * 
 * - parameter - parameter
 * 
 * - this - parameter
 * 
 * - parameter - this
 * 
 * Other types of assigned expressions are not currently treated.
 * 
 * Also, if the entire algorithm does not resolve sufficient relevant data, i.e.
 * there are a lot of inconclusive derivations, the algorithm will not consider
 * the obtained data relevant as a whole.
 * 
 * @author rosualinpetru
 *
 */
public class AssignmentUsedTypesCapability {
    private AssignmentUsedTypesCapability() {
    }

    private static final int MAX_DEPTH = 4;

    private static final double INCONCLUSIVE_THRESHOLD = 1. / 3.;

    private static ReferencesPairHandler handlerChain;

    static {

	var inconclusiveHandler = new ReferencesPairHandler() {

	    protected void handle(AssignemntsPair assignemntsPair, State state) {
		state.markInvalid(assignemntsPair);
	    }

	    protected boolean canHandle(IJavaElement assignedElement1, IJavaElement assignedElement2) {
		return assignedElement1 instanceof IMethod || assignedElement1 instanceof IField || assignedElement2 instanceof IMethod || assignedElement2 instanceof IField;
	    }
	};

	var solutionHandler = new ReferencesPairHandler() {

	    protected void handle(AssignemntsPair assignemntsPair, State state) {
		state.resolved().add(Pair.of((IType) assignemntsPair._1.assignedElement().get(), (IType) assignemntsPair._2.assignedElement().get()));
	    }

	    protected boolean canHandle(IJavaElement assignedElement1, IJavaElement assignedElement2) {
		return assignedElement1 instanceof IType && assignedElement2 instanceof IType;
	    }
	};

	var pph = new ParameterParameterHandler();
	var tph = new ThisParameterHandler();
	var pth = new ParameterThisHandler();

	inconclusiveHandler.setNextHandler(pph);
	pph.setNextHandler(tph);
	tph.setNextHandler(pth);
	pth.setNextHandler(solutionHandler);

	handlerChain = inconclusiveHandler;

    }

    public static Optional<List<Pair<IType, IType>>> usedTypes(Pair<IJavaElement, IJavaElement> referencesPair, Pair<IType, IType> referencesTypes) {
	var state = State.empty();

	var initialPair = AssignemntsPair.initialAssignmentsPair(referencesPair, referencesTypes);

	state.assignmentsPairs().push(initialPair);

	while (!state.assignmentsPairs().isEmpty()) {
	    var assignemntsPair = state.assignmentsPairs().pop();

	    if (assignemntsPair.depth() > MAX_DEPTH) {
		state.markInvalid(assignemntsPair);
		continue;
	    }

	    handlerChain.submit(assignemntsPair, state);
	}

	var totalAssignmentsDerivations = state.resolved().size() + state.inconclusive().size();

	if (state.inconclusive().size() * 1.0 / totalAssignmentsDerivations >= INCONCLUSIVE_THRESHOLD)
	    return Optional.empty();

	var distinctConcreteConeProduct = DistinctConcreteConeProductCapability.distinctConcreteConeProduct(referencesTypes._1, referencesTypes._2);

	return Optional.of(state.resolved().stream().distinct().filter(pair -> distinctConcreteConeProduct.map(p -> p.contains(pair)).orElse(true)).toList());

    }

}
