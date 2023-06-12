package ro.lrg.jfamilycounselor.util.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches can make analyses lead to erroneous results if the source code
 * changes; therefore they need to be cleared in such cases. All instances of
 * 
 * @see ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache are being
 *      monitored by the
 * @see ro.lrg.jfamilycounselor.util.cache.CacheSupervisor CacheSupervisor and
 *      cleared whenever there is a change in the project structure or in the
 *      source code - @see ro.lrg.jfamilycounselor.plugin.Startup
 * 
 * @author rosualinpetru
 */
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
