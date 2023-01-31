package ro.lrg.jfamilycounselor.util.list;

import java.util.ArrayList;
import java.util.List;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class ListOperations {
    private ListOperations() {
    }

    public static <K, V> List<Pair<K, V>> cartesianProduct(List<K> l1, List<V> l2) {
	return l1.stream().flatMap(e1 -> l2.stream().map(e2 -> new Pair<>(e1, e2))).toList();
    }

    public static <K> List<Pair<K, K>> distrinctCombinations2(List<K> l) {
	var result = new ArrayList<Pair<K, K>>();
	for (int i = 0; i < l.size(); i++) {
	    for (int j = i + 1; j < l.size(); j++) {
		result.add(new Pair<K, K>(l.get(i), l.get(j)));
	    }
	}
	return result;
    }

}
