package ro.lrg.jfamilycounselor.util.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class CommonOperations {
	private CommonOperations() {
	}

	public static <V> Supplier<V> lazy(V v) {
		return () -> v;
	}

	public static <K, V> Stream<Pair<K, V>> cartesianProduct(Stream<K> l1, Stream<V> l2) {
		return l1.flatMap(e1 -> l2.map(e2 -> Pair.of(e1, e2)));
	}

	public static <K, V> List<Pair<K, V>> cartesianProduct(List<K> l1, List<V> l2) {
		return l1.stream().flatMap(e1 -> l2.stream().map(e2 -> Pair.of(e1, e2))).toList();
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
