package ro.lrg.jfamilycounselor.capability.specific.coverage.name;

import static ro.lrg.jfamilycounselor.capability.generic.cone.ConcreteConeCapability.concreteCone;
import static ro.lrg.jfamilycounselor.capability.generic.type.ParameterTypeCapability.parameterType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * Capability that computes the used types using the name-based estimation out of a
 * list of all possible types.
 * 
 * @author rosualinpetru
 *
 */
public class NameUsedTypesCapability {
    private NameUsedTypesCapability() {
    }
    
    private static final double CORRELATION_TRESHOLD = 0.5;

    public static Optional<List<Pair<IType, IType>>> usedTypesTP(Pair<IType, ILocalVariable> tpReferencesPair) {
	return parameterType(tpReferencesPair._2).flatMap(iType2 -> usedTypes(tpReferencesPair._1, iType2));
    }

    public static Optional<List<Pair<IType, IType>>> usedTypesPP(Pair<ILocalVariable, ILocalVariable> ppReferencesPair) {
	return parameterType(ppReferencesPair._1).flatMap(t1 -> parameterType(ppReferencesPair._2).flatMap(t2 -> usedTypes(t1, t2)));
    }

    private static Optional<List<Pair<IType, IType>>> usedTypes(IType iType1, IType iType2) {
	var cone1 = concreteCone(iType1);
	var cone2 = concreteCone(iType2);

	if (cone1.isEmpty() || cone2.isEmpty()) {
	    return Optional.empty();
	}

	var tokensMap1 = cone1.get().stream().collect(Collectors.toMap(Function.identity(), NameUsedTypesCapability::splitNameInTokens));
	var tokensMap2 = cone2.get().stream().collect(Collectors.toMap(Function.identity(), NameUsedTypesCapability::splitNameInTokens));

	var correlatedTypesPairs = new ArrayList<Pair<IType, IType>>();

	tokensMap1.forEach((t1, tokens1) -> {

	    tokensMap2.forEach((t2, tokens2) -> {
		if (correlationCondition(tokens1, tokens2)) {
		    correlatedTypesPairs.add(new Pair<>(t1, t2));
		}
	    });
	});

	return Optional.of(correlatedTypesPairs);

    }

    private static final String tokensR = "(?<!(^|\\d))(?=\\d)|(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";

    private static List<String> splitNameInTokens(IType iType) {
	return Arrays.asList(iType.getElementName().split(tokensR));
    }

    private static boolean correlationCondition(List<String> tokens1, List<String> tokens2) {
	var avgTokenLength = (tokens1.size() + tokens2.size()) / 2.0;

	var commonTokensCount = tokens1.stream().filter(s -> tokens2.contains(s)).count();

	return (commonTokensCount / avgTokenLength) >= CORRELATION_TRESHOLD;
    }

}
