package ro.lrg.jfamilycounselor.util.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches can make analyses lead to erroneous results if the source code
 * changes; therefore they need to be cleared in such cases. All instances of
 * are being monitored by a supervisor and cleared whenever there is a change in
 * the project structure or in the source code - @see
 * ro.lrg.jfamilycounselor.plugin.Startup
 * 
 * @author rosualinpetru
 */
public final class MonitoredUnboundedCache<K, V> implements Cache<K, V> {

    private ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();

    /**
     * Caches that use a lot of memory would be cleared more frequently then those
     * which consume lesser amount of memory.
     */
    private boolean isHighMemoryConsumer;

    private MonitoredUnboundedCache(boolean isHighMemoryConsumer) {
	this.isHighMemoryConsumer = isHighMemoryConsumer;
	CacheSupervisor.caches.add(this);
    }

    public static <K, V> Cache<K, V> getLowConsumingCache() {
	return new MonitoredUnboundedCache<K, V>(false);
    }
    
    public static <K, V> Cache<K, V> getHighConsumingCache() {
	return new MonitoredUnboundedCache<K, V>(true);
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

    public boolean isBigMemoryConsumer() {
	return isHighMemoryConsumer;
    }

}
