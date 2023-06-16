package ro.lrg.jfamilycounselor.approach.usedtypes.assignment;

import static ro.lrg.jfamilycounselor.capability.parameter.ParameterTypeCapability.parameterType;
import static ro.lrg.jfamilycounselor.capability.type.DistinctConcreteConeProductCapability.distinctConcreteConeProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler.ParameterParameterHandler;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler.ParameterTypeHandler;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler.ReferencesPairHandler;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler.ThisParameterHandler;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler.ThisTypeHandler;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler.TypeParameterHandler;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.Assignment;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.State;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Capability that computes the used types using the assignments-based approach
 * for a pair of references.
 * 
 * Through derivation we roughly refer to the process in which we "go back" from
 * the class towards exterior, hoping to find instantiations or any expression
 * that might indicate a concrete type of object that could be referred at some
 * point by an analyzed reference.
 * 
 * For instance, the derivation of a parameter assigned to a reference is the
 * list of all expressions that correspond to its actual parameters. For a local
 * variable, is is the list of all expressions that are being assigned to that
 * variable.
 * 
 * The assignments-based estimation is a worklist algorithm that derives
 * assignments pairs until no new ones can be generated, updating the state in
 * the process.
 * 
 * The derivation of pairs of assignments (their expressions, which lead to new
 * pairs) depends on what the assigned elements are, e.g. parameters, fields,
 * etc. Also, the derivation is different in some cases if both assignments of a
 * pair have particular assigned elements.
 * 
 * Also, if the entire algorithm does not resolve sufficient relevant data, i.e.
 * there are a lot of inconclusive derivations, the algorithm will also take
 * into consideration the latter.
 * 
 * @author rosualinpetru
 *
 */
public class AssignmentsBasedApproach {
    private AssignmentsBasedApproach() {
    }

    private static final int MAX_DEPTH = 4;

    private static ReferencesPairHandler handlerChain;

    private static Logger logger = jFCLogger.getLogger();

    // Building the chain responsible for the derivation of assigned elements
    static {
	// handlers of fields and methods can represent a future extension
	var inconclusiveHandler = new ReferencesPairHandler() {

	    protected void handle(AssignemntsPair assignemntsPair, State state) {
		state.markInvalid(assignemntsPair);
	    }

	    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
		return assignedElement1 instanceof AssignedElement.MethodCall || assignedElement1 instanceof AssignedElement.Field || assignedElement2 instanceof AssignedElement.MethodCall
			|| assignedElement2 instanceof AssignedElement.Field;
	    }
	};

	var solutionHandler = new ReferencesPairHandler() {

	    protected void handle(AssignemntsPair assignemntsPair, State state) {
		state.resolved().add(Pair.of(((AssignedElement.ResolvedType) assignemntsPair._1.assignedElement().get()).iType(), ((AssignedElement.ResolvedType) assignemntsPair._2.assignedElement().get()).iType()));
	    }

	    protected boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2) {
		return assignedElement1 instanceof AssignedElement.ResolvedType && assignedElement2 instanceof AssignedElement.ResolvedType;
	    }
	};

	var thisParamHandler = new ThisParameterHandler();
	var thisTypeHandler = new ThisTypeHandler();
	var paramParamHandler = new ParameterParameterHandler();
	var paramTypeHandler = new ParameterTypeHandler();
	var typeParamHandler = new TypeParameterHandler();

	inconclusiveHandler.setNextHandler(thisParamHandler);
	thisParamHandler.setNextHandler(thisTypeHandler);
	thisTypeHandler.setNextHandler(paramParamHandler);
	paramParamHandler.setNextHandler(paramTypeHandler);
	paramTypeHandler.setNextHandler(typeParamHandler);
	typeParamHandler.setNextHandler(solutionHandler);

	handlerChain = inconclusiveHandler;
    }

    public static Optional<List<Pair<IType, IType>>> usedTypes(Pair<IJavaElement, IJavaElement> referencesPair) {
	var state = State.empty();

	// The initial assignment = the references are being assigned to themselves
	var initialAssignmentsPair = initialAssignmentsPair(referencesPair);
	if (initialAssignmentsPair.isEmpty()) {
	    logger.severe("The initial assignemnts pair was not contructed for: " + referencesPair.toString() + ". The approach cannot continue.");
	    return Optional.empty();
	}

	state.assignmentsPairs().push(initialAssignmentsPair.get());

	while (!state.assignmentsPairs().isEmpty()) {
	    var assignemntsPair = state.assignmentsPairs().pop();

	    if (assignemntsPair.depth() > MAX_DEPTH) {
		state.markInvalid(assignemntsPair);
		continue;
	    }

	    handlerChain.submit(assignemntsPair, state);
	}

	var result = new ArrayList<Pair<IType, IType>>();
	result.addAll(state.resolved());

	// An alternative to comparing the number of resolved and inconclusive cases
	// could be to set a max percentage threshold of the number of inconclusive
	// cases. If exceeded, take into consideration the inconclusive cases
	if (state.resolved().size() < state.inconclusive().size())
	    for (Pair<IType, IType> inconclusive : state.inconclusive()) {
		var inconclusiveDistinctConcreteConeProduct = distinctConcreteConeProduct(inconclusive._1, inconclusive._2);
		result.addAll(inconclusiveDistinctConcreteConeProduct.orElse(List.of()));
	    }

	var referenceType1 = initialAssignmentsPair.get()._1.lowestRecordedType();
	var referenceType2 = initialAssignmentsPair.get()._2.lowestRecordedType();
	var possibleTypes = distinctConcreteConeProduct(referenceType1, referenceType2);

	// We filter the result such that all types pairs are included in the possible
	// types pairs set
	return Optional.of(state.resolved().stream().distinct().filter(pair -> possibleTypes.map(p -> p.contains(pair)).orElse(false)).toList());
    }

    /**
     * The initial assignments pair is considered such that a reference is being
     * written by itself (i.e. the assignment element wraps the JDT element that
     * represents the reference) in order to be able to begin the derivation
     * process.
     */
    private static Optional<AssignemntsPair> initialAssignmentsPair(Pair<IJavaElement, IJavaElement> referencesPair) {

	if (referencesPair._1 instanceof IType iType1 && referencesPair._2 instanceof ILocalVariable iLocalVariable) {
	    var iType2 = parameterType(iLocalVariable);
	    if (iType2.isEmpty()) {
		logger.severe("The construction of the initial assignemnts pair for: " + referencesPair.toString() + " failed as one of the references' type could not be determined.");
		return Optional.<AssignemntsPair>empty();
	    }

	    return Optional.of(AssignemntsPair.initial(
		    new Assignment(referencesPair._1, Optional.of(new AssignedElement.This(iType1)), iType1),
		    new Assignment(referencesPair._2, Optional.of(new AssignedElement.Parameter(iLocalVariable)), iType2.get())));

	} else if (referencesPair._1 instanceof ILocalVariable iLocalVariable1 && referencesPair._2 instanceof ILocalVariable iLocalVariable2) {
	    var iType1 = parameterType(iLocalVariable1);
	    var iType2 = parameterType(iLocalVariable2);

	    if (iType1.isEmpty() || iType2.isEmpty()) {
		logger.severe("The construction of the initial assignemnts pair for: " + referencesPair.toString() + " failed as one of the references' type could not be determined.");
		return Optional.<AssignemntsPair>empty();
	    }

	    return Optional.of(AssignemntsPair.initial(
		    new Assignment(referencesPair._1, Optional.of(new AssignedElement.Parameter(iLocalVariable1)), iType1.get()),
		    new Assignment(referencesPair._2, Optional.of(new AssignedElement.Parameter(iLocalVariable2)), iType2.get())));
	}

	logger.severe("The construction of the initial assignemnts pair for: " + referencesPair.toString() + " failed as the provided referencesPair cannot be analyzed.");

	return Optional.<AssignemntsPair>empty();
    }

}
