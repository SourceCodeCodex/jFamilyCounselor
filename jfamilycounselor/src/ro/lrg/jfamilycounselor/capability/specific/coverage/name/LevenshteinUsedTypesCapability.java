package ro.lrg.jfamilycounselor.capability.specific.coverage.name;

import static ro.lrg.jfamilycounselor.capability.generic.parameter.ParameterTypeCapability.parameterType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.generic.type.DistinctConcreteConeProductCapability;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class LevenshteinUsedTypesCapability {
    private LevenshteinUsedTypesCapability() {
	
    }
    
    private static final Cache<Pair<IType, IType>, Boolean> areCorrelatedCache = MonitoredUnboundedCache.getCache();

    public static Optional<List<Pair<IType, IType>>> usedTypesTP(Pair<IType, ILocalVariable> tpReferencesPair) {
	return parameterType(tpReferencesPair._2).flatMap(iType2 -> usedTypes(tpReferencesPair._1, iType2));
    }

    public static Optional<List<Pair<IType, IType>>> usedTypesPP(Pair<ILocalVariable, ILocalVariable> ppReferencesPair) {
	return parameterType(ppReferencesPair._1).flatMap(t1 -> parameterType(ppReferencesPair._2).flatMap(t2 -> usedTypes(t1, t2)));
    }

    private static Optional<List<Pair<IType, IType>>> usedTypes(IType iType1, IType iType2) {
	var possibleTypes = DistinctConcreteConeProductCapability.product(iType1, iType2);

	if (possibleTypes.isEmpty()) {
	    return Optional.empty();
	}

	return Optional.of(possibleTypes.get().stream().filter(p -> {
	    if (areCorrelatedCache.contains(p))
		return areCorrelatedCache.get(p).get();

	    var areCorrelated = areCorrelated(p._1, p._2);

	    areCorrelatedCache.put(p, areCorrelated);

	    return areCorrelated;

	}).toList());

    }

    private static boolean areCorrelated(IType iType1, IType iType2) {
	var tokens1 = splitNameInTokens(iType1);
	var tokens2 = splitNameInTokens(iType2);

	return levenshteinDistanceOnToken(tokens1, tokens2) <= (Math.max(tokens1.size(), tokens2.size()) / 2.);
    }

    private static int levenshteinDistanceOnToken(List<String> tokens1, List<String> tokens2) {

	var dp = new int[tokens1.size() + 1][tokens2.size() + 1];

	for (int i = 0; i <= tokens1.size(); i++) {
	    for (int j = 0; j <= tokens2.size(); j++) {
		if (i == 0) {
		    dp[i][j] = j;
		} else if (j == 0) {
		    dp[i][j] = i;
		} else {
		    dp[i][j] = min(dp[i - 1][j - 1]
			    + costOfSubstitution(tokens1.get(i - 1), tokens2.get(j - 1)),
			    dp[i - 1][j] + 1,
			    dp[i][j - 1] + 1);
		}
	    }
	}

	return dp[tokens1.size()][tokens2.size()];
    }

    private static List<String> splitNameInTokens(IType iType) {
	var tokensR = "(?<!(^|\\d))(?=\\d)|(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";
	return Arrays.asList(iType.getElementName().split(tokensR));
    }

    private static int costOfSubstitution(String a, String b) {
	return a.equals(b) ? 0 : 1;
    }

    private static int min(int... numbers) {
	return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

}
