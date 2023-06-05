package ro.lrg.jfamilycounselor.capability.specific.coverage.name;

import static ro.lrg.jfamilycounselor.capability.generic.parameter.ParameterTypeCapability.parameterType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.generic.type.DistinctConcreteConeProductCapability;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * Capability that computes the used types using the name-based estimation out
 * of a list of all possible types.
 * 
 * @author rosualinpetru
 *
 */
public class NameUsedTypesCapability {
    private NameUsedTypesCapability() {
    }

    private static final Cache<Pair<IType, IType>, List<Pair<IType, IType>>> cache = MonitoredUnboundedCache.getCache();
    private static final Cache<IType, List<String>> tokenCache = MonitoredUnboundedCache.getCache();

    public static Optional<List<Pair<IType, IType>>> usedTypesTP(Pair<IType, ILocalVariable> tpReferencesPair) {
	return parameterType(tpReferencesPair._2).flatMap(iType2 -> usedTypes(tpReferencesPair._1, iType2));
    }

    public static Optional<List<Pair<IType, IType>>> usedTypesPP(Pair<ILocalVariable, ILocalVariable> ppReferencesPair) {
	return parameterType(ppReferencesPair._1).flatMap(t1 -> parameterType(ppReferencesPair._2).flatMap(t2 -> usedTypes(t1, t2)));
    }

    private static Optional<List<Pair<IType, IType>>> usedTypes(IType iType1, IType iType2) {
	var typesPair = Pair.of(iType1, iType2);

	if (cache.contains(typesPair))
	    return cache.get(typesPair);

	var distinctConcreteConeProduct = DistinctConcreteConeProductCapability.product(iType1, iType2);

	if (distinctConcreteConeProduct.isEmpty())
	    return Optional.empty();

	var correlationFactorsMap = new HashMap<Pair<IType, IType>, Double>();

	for (Pair<IType, IType> pair : distinctConcreteConeProduct.get()) {
	    var tokens1 = splitNameInTokens(pair._1);
	    var tokens2 = splitNameInTokens(pair._2);

	    var correlationFactor = correlationFactor(tokens1, tokens2);
	    correlationFactorsMap.put(pair, correlationFactor);
	}

	var maxFactor = correlationFactorsMap.entrySet().stream().max((e1, e2) -> Double.compare(e1.getValue(), e2.getValue())).map(e -> e.getValue());

	var result = maxFactor.map(factor -> correlationFactorsMap.entrySet()
		.stream()
		.filter(e -> e.getValue().equals(factor))
		.map(e -> e.getKey())
		.toList());

	result.ifPresent(r -> cache.put(typesPair, r));

	return result;

    }

    private static final String tokensR = "(?<!(^|\\d))(?=\\d)|(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";

    private static List<String> splitNameInTokens(IType iType) {
	return tokenCache.get(iType).orElseGet(() -> {
	    var r = Arrays.asList(iType.getElementName().split(tokensR));
	    tokenCache.put(iType, r);
	    return r;
	});
    }

    private static double correlationFactor(List<String> tokens1, List<String> tokens2) {
	var avgTokenLength = (tokens1.size() + tokens2.size()) / 2.0;

	var commonTokensCount = tokens1.stream().filter(s -> tokens2.contains(s)).count();

	return (commonTokensCount / avgTokenLength);
    }

}