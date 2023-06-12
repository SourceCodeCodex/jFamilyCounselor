package ro.lrg.jfamilycounselor.capability.aperture;

import static ro.lrg.jfamilycounselor.capability.common.parameter.ParameterTypeCapability.parameterType;
import static ro.lrg.jfamilycounselor.capability.common.type.DistinctConcreteConeProductCapability.distinctConcreteConeProduct;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * Capability that computes all possible combinations of types of objects that
 * could be referred by a pair of references.
 * 
 * @author rosualinpetru
 *
 */
public class PossibleTypesCapability {
    private PossibleTypesCapability() {
    }

    public static Optional<List<Pair<IType, IType>>> possibleTypesThisParam(Pair<IType, ILocalVariable> tpReferencesPair) {
	return parameterType(tpReferencesPair._2).flatMap(iType2 -> distinctConcreteConeProduct(tpReferencesPair._1, iType2));
    }

    public static Optional<List<Pair<IType, IType>>> possibleTypesParamParam(Pair<ILocalVariable, ILocalVariable> ppReferencesPair) {
	return parameterType(ppReferencesPair._1).flatMap(t1 -> parameterType(ppReferencesPair._2).flatMap(t2 -> distinctConcreteConeProduct(t1, t2)));
    }
}
