package ro.lrg.jfamilycounselor.capability.coverage.assignment.handler;

import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IJavaElement;

import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.State;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

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
	
	if(canHandle(ae1, ae2)) {
	    handle(assignemntsPair, state);
	    return;
	}
	
	if(nextHandler.isPresent()) {
	    nextHandler.get().submit(assignemntsPair, state);
	    return;
	}
	
	logger.warning("Assignments pair was not handled: " + assignemntsPair);
    }
    
    
    protected abstract void handle(AssignemntsPair assignemntsPair, State state);
    
    protected abstract boolean canHandle(IJavaElement assignedElement1, IJavaElement assignedElement2);
    
}
