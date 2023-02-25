package ro.lrg.jfamilycounselor.capability.generic.type;

import static ro.lrg.jfamilycounselor.capability.generic.type.ConcreteConeCapability.concreteCone;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.list.CommonOperations;

/**
 * Capability that computes the cartesian product between the concrete cones of
 * two types, disregarding the the any common subtype hierarchies of the
 * corresponding types.
 * 
 * <pre>
 * 	A       B
 *    /   \   /   \
 *   A1     C     B1
 *          |      
 *         ...
 * </pre>
 * 
 * The entire C type hierarchy will be ignored in the computation of the
 * product. Product is: (A1, B1)
 * 
 * @author rosualinpetru
 *
 */
public class DistinctConcreteConeProductCapability {
    private DistinctConcreteConeProductCapability() {
    }

    public static Optional<List<Pair<IType, IType>>> product(IType iType1, IType iType2) {
	var c1Opt = concreteCone(iType1);
	var c2Opt = concreteCone(iType2);

	var intersectionOpt = c1Opt.flatMap(c1 -> c2Opt.map(c2 -> c1.stream().filter(t -> c2.contains(t)).toList()));

	return intersectionOpt.flatMap(intersection -> c1Opt.flatMap(c1 -> c2Opt.map(c2 -> CommonOperations.cartesianProduct(
		c1.stream().filter(t -> !intersection.contains(t)).toList(),
		c2.stream().filter(t -> !intersection.contains(t)).toList()))));
    }

}
