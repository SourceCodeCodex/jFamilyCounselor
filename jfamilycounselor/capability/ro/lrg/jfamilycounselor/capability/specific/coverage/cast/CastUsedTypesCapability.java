package ro.lrg.jfamilycounselor.capability.specific.coverage.cast;

import static ro.lrg.jfamilycounselor.capability.generic.cone.ConcreteConeCapability.concreteCone;
import static ro.lrg.jfamilycounselor.capability.generic.type.ParameterTypeCapability.parameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.generic.cone.DistinctConcreteConeProductCapability;
import ro.lrg.jfamilycounselor.capability.generic.search.type.cast.TypeCastSearchCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * Capability that computes the used types using the cast-based estimation.
 * 
 * @author rosualinpetru
 *
 */
public class CastUsedTypesCapability {
    private CastUsedTypesCapability() {
    }

    public static Optional<List<Pair<IType, IType>>> usedTypesTP(Pair<IType, ILocalVariable> tpReferencesPair) {
	return parameterType(tpReferencesPair._2).flatMap(iType2 -> usedTypes(tpReferencesPair._1, iType2));
    }

    public static Optional<List<Pair<IType, IType>>> usedTypesPP(Pair<ILocalVariable, ILocalVariable> ppReferencesPair) {
	return parameterType(ppReferencesPair._1).flatMap(t1 -> parameterType(ppReferencesPair._2).flatMap(t2 -> usedTypes(t1, t2)));
    }

    private static Optional<List<Pair<IType, IType>>> usedTypes(IType iType1, IType iType2) {
	var cone1Opt = concreteCone(iType1);
	var cone2Opt = concreteCone(iType2);

	if (cone1Opt.isEmpty() || cone2Opt.isEmpty()) {
	    return Optional.empty();
	}

	var cone1 = cone1Opt.get();
	var cone2 = cone2Opt.get();

	var correlated = new ArrayList<Pair<IType, IType>>();

	for (IType t1 : cone1) {
	    for (IType t2 : cone2) {
		var enclosingMethodsT1 = TypeCastSearchCapability.searchTypeCasts(t1);

		if (enclosingMethodsT1.stream().anyMatch(ms -> ms.stream().anyMatch(m -> t2.equals(m.getDeclaringType()))))
		    correlated.add(Pair.of(t1, t2));

		var enclosingMethodsT2 = TypeCastSearchCapability.searchTypeCasts(t2);

		if (enclosingMethodsT2.stream().anyMatch(ms -> ms.stream().anyMatch(m -> t1.equals(m.getDeclaringType()))))
		    correlated.add(Pair.of(t1, t2));

		enclosingMethodsT1.ifPresent(ems1 -> enclosingMethodsT2.ifPresent(ems2 -> {
		    ems1.forEach(m1 -> ems2.forEach(m2 -> {
			if (m1.equals(m2))
			    correlated.add(Pair.of(t1, t2));

//			TO BE DISCUSSED
//			if (m1.getDeclaringType().equals(m2.getDeclaringType())) {
//			    correlated.add(Pair.of(t1, t2));
//			}
		    }));
		}));
	    }
	}

	var distinctConcreteConeProduct = DistinctConcreteConeProductCapability.product(iType1, iType2);

	return Optional.of(correlated.stream().filter(pair -> distinctConcreteConeProduct.map(p -> p.contains(pair)).orElse(true)).toList());

    }

}
