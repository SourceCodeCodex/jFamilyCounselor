package ro.lrg.jfamilycounselor.capability.specific.aperture;

import static ro.lrg.jfamilycounselor.capability.generic.cone.ConcreteConeCapability.concreteCone;
import static ro.lrg.jfamilycounselor.capability.generic.resolver.ParameterTypeCapability.parameterType;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.list.ListOperations;

/**
 * Service that computes all possible combinations of types of objects that a
 * pair of references might refer.
 * 
 * @author rosualinpetru
 *
 */
public class PossibleTypesCapability {
    private PossibleTypesCapability() {
    }

    public static Optional<List<Pair<IType, IType>>> possibleTypesTP(Pair<IType, ILocalVariable> tpReferencesPair) {
	return parameterType(tpReferencesPair._2).flatMap(iType2 -> typesConeProduct(tpReferencesPair._1, iType2));
    }

    public static Optional<List<Pair<IType, IType>>> possibleTypesPP(Pair<ILocalVariable, ILocalVariable> ppReferencesPair) {
	return parameterType(ppReferencesPair._1).flatMap(t1 -> parameterType(ppReferencesPair._2).flatMap(t2 -> typesConeProduct(t1, t2)));
    }

    private static Optional<List<Pair<IType, IType>>> typesConeProduct(IType iType1, IType iType2) {
	var cone1 = concreteCone(iType1);
	var cone2 = concreteCone(iType2);

	return cone1.flatMap(c1 -> cone2.map(c2 -> ListOperations.cartesianProduct(c1, c2)));
    }
}
