package ro.lrg.jfamilycounselor.approach.usedtypes.assignment.handler;

import java.util.Optional;
import java.util.logging.Logger;

import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignedElement;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.State;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * This chain of responsibility allows the extension of the algorithm by
 * implementing new handlers for different kind of assigned elements (JDT).
 * 
 * @author rosualinpetru
 *
 */
public abstract class ReferencesPairHandler {
    private static Logger logger = jFCLogger.getLogger();

    private Optional<ReferencesPairHandler> nextHandler = Optional.empty();

    public void setNextHandler(ReferencesPairHandler handler) {
	nextHandler = Optional.of(handler);
    }

    public void submit(AssignemntsPair assignemntsPair, State state) {
	if (assignemntsPair._1.assignedElement().isEmpty() || assignemntsPair._2.assignedElement().isEmpty()) {
	    state.markInvalid(assignemntsPair);
	    return;
	}

	var ae1 = assignemntsPair._1.assignedElement().get();
	var ae2 = assignemntsPair._2.assignedElement().get();

	if (canHandle(ae1, ae2)) {
	    handle(assignemntsPair, state);
	    return;
	}

	if (nextHandler.isPresent()) {
	    nextHandler.get().submit(assignemntsPair, state);
	    return;
	}

	logger.warning("Assignments pair was not handled: " + assignemntsPair);
    }

    protected abstract void handle(AssignemntsPair assignemntsPair, State state);

    protected abstract boolean canHandle(AssignedElement assignedElement1, AssignedElement assignedElement2);
}
