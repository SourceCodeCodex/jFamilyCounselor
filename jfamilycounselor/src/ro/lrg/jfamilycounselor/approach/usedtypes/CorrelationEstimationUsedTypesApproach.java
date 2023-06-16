package ro.lrg.jfamilycounselor.approach.usedtypes;

import static ro.lrg.jfamilycounselor.capability.type.ConcreteConeCapability.concreteCone;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.cartesianProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * Template for approaches where the correlation between two types can be
 * heuristically determined.
 * 
 * @author rosualinpetru
 *
 */
public abstract class CorrelationEstimationUsedTypesApproach {
    private final Cache<Pair<IType, IType>, Boolean> areCorrelatedCache = MonitoredUnboundedCache.getLowConsumingCache();

    protected abstract boolean areCorrelated(IType t1, IType t2);

    public Optional<List<Pair<IType, IType>>> usedTypes(IType refType1, IType refType2) {
	var cone1Opt = concreteCone(refType1);
	var cone2Opt = concreteCone(refType2);

	if (cone1Opt.isEmpty() || cone2Opt.isEmpty())
	    return Optional.empty();

	var correlated1 = new ConcurrentLinkedQueue<IType>();
	var correlated2 = new ConcurrentLinkedQueue<IType>();

	var result = new ConcurrentLinkedQueue<Pair<IType, IType>>();

	cone1Opt.get().parallelStream().forEach(t1 -> cone2Opt.get().parallelStream().forEach(t2 -> {
	    var pair = Pair.of(t1, t2);

	    boolean areCorrelated;
	    if (areCorrelatedCache.get(pair).isPresent())
		areCorrelated = areCorrelatedCache.get(pair).get();
	    else {
		areCorrelated = areCorrelated(t1, t2);
		areCorrelatedCache.put(pair, areCorrelated);
		areCorrelatedCache.put(pair.swap(), areCorrelated);
	    }

	    if (areCorrelated) {
		result.offer(pair);
		correlated1.offer(t1);
		correlated2.offer(t2);
	    }
	}));

	var notCorrelated1 = new ArrayList<>(cone1Opt.get());
	var notCorrelated2 = new ArrayList<>(cone2Opt.get());

	notCorrelated1.removeAll(correlated1);
	notCorrelated2.removeAll(correlated2);

	result.addAll(cartesianProduct(notCorrelated1, notCorrelated2));

	return Optional.of(result.stream().toList());
    }

}
