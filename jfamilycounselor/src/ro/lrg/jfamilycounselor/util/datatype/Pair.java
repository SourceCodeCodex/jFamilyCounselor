package ro.lrg.jfamilycounselor.util.datatype;

import java.util.Objects;

public class Pair<K, V> {
	public final K _1;
	public final V _2;

	public Pair(K _1, V _2) {
		this._1 = _1;
		this._2 = _2;
	}

	public static <K, V> Pair<K, V> of(K _1, V _2) {
		return new Pair<K, V>(_1, _2);
	}

	public Pair<V, K> swap() {
		return new Pair<>(_2, _1);
	}

	public int hashCode() {
		return Objects.hash(_1, _2);
	}

	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		return Objects.equals(_1, other._1) && Objects.equals(_2, other._2);
	}

	public String toString() {
		return "(" + _1 + ", " + _2 + ")";
	}

}
