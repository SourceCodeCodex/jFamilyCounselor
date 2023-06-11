package ro.lrg.jfamilycounselor.util.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class MonitoredUnboundedCache<K, V> implements Cache<K, V> {

    private ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();

    MonitoredUnboundedCache() {
    }
    
    public static <K, V> Cache<K, V> getCache() {
	var cache = new MonitoredUnboundedCache<K, V>();
	CacheSupervisor.caches.add(cache);
	return cache;
    }

    public void put(K key, V value) {
	map.put(key, value);
    }

    public Optional<V> get(K key) {
	var value = map.get(key);
	return Optional.ofNullable(value);
    }

    public boolean contains(K key) {
	var contains = map.containsKey(key);
	return contains;
    }

    public int size() {
	var size = map.size();
	return size;
    }

    public boolean isEmpty() {
	var isEmpty = map.isEmpty();
	return isEmpty;
    }

    public void clear() {
	map.clear();
    }

}
