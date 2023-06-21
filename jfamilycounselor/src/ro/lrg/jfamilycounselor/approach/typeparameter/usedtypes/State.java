package ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

public record State(
	List<Pair<IType, IType>> inconclusive,
	List<Pair<IType, IType>> resolved,
	AtomicInteger unknownCounter) {

    public static State empty() {
	return new State(new ArrayList<>(), new ArrayList<>(), new AtomicInteger(0));
    }

}