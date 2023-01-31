package ro.lrg.jfamilycounselor.capability.specific.coverage.name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * Service that computes the used types using the name-based estimation out of a
 * list of all possible types.
 * 
 * @author rosualinpetru
 *
 */
public class UsedTypesCapability {
    private UsedTypesCapability() {
    }

    private static final String tokensR = "(?<!(^|\\d))(?=\\d)|(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";

    public static List<Pair<IType, IType>> usedTypes(List<Pair<IType, IType>> possibleTypes) {
	var correlated = possibleTypes.stream().filter(p -> areTypesCorrelatedByName(p._1, p._2)).toList();

	var comparator = new Comparator<IType>() {
	    public int compare(IType o1, IType o2) {
		return o1.getFullyQualifiedName().compareTo(o2.getFullyQualifiedName());
	    }
	};

	var correlatedT1 = new TreeSet<IType>(comparator);
	correlated.stream().map(p -> p._1).forEach(t -> correlatedT1.add(t));

	var correlatedT2 = new TreeSet<IType>(comparator);
	correlated.stream().map(p -> p._2).forEach(t -> correlatedT2.add(t));

	var notCorrelatedT1 = new TreeSet<IType>(comparator);
	var notCorrelatedT2 = new TreeSet<IType>(comparator);

	possibleTypes.stream().filter(p -> !correlated.contains(p)).forEach(p -> {
	    if (!correlatedT1.contains(p._1))
		notCorrelatedT1.add(p._1);

	    if (!correlatedT2.contains(p._2))
		notCorrelatedT2.add(p._2);
	});

	var autoCorrelated = notCorrelatedT1.stream().flatMap(t1 -> notCorrelatedT2.stream().map(t2 -> new Pair<>(t1, t2))).toList();

	var result = new ArrayList<>(correlated);
	result.addAll(autoCorrelated);

	return result;

    }

    private static boolean areTypesCorrelatedByName(IType iType1, IType iType2) {
	var tokens1 = new HashSet<String>(Arrays.asList(iType1.getElementName().split(tokensR)));
	var tokens2 = new HashSet<String>(Arrays.asList(iType2.getElementName().split(tokensR)));

	var avgTokenLength = (tokens1.size() + tokens2.size()) / 2.0;

	tokens1.retainAll(tokens2);
	var commonTokensCount = tokens1.size();

	return (commonTokensCount / avgTokenLength) >= 0.5;
    }

}
