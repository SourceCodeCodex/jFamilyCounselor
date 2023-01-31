package ro.lrg.jfamilycounselor.util.cache;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class LRUCache<K, V> implements Cache<K, V> {

    private LinkedHashMap<K, V> map = new LinkedHashMap<K, V>() {
	private static final long serialVersionUID = 1L;

	protected boolean removeEldestEntry(Entry<K, V> entry) {
	    return (size() > capacity);
	}
    };

    private int capacity;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    LRUCache(int capacity) {
	this.capacity = capacity;
    }

    public V put(K key, V value) {
	lock.writeLock().lock();
	var v = map.put(key, value);
	lock.writeLock().unlock();
	return v;
    }

    public Optional<V> get(K key) {
	lock.readLock().lock();
	var value = map.get(key);
	lock.readLock().unlock();
	return Optional.ofNullable(value);
    }
    
    public boolean contains(K key) {
	lock.readLock().lock();
	var contains = map.containsKey(key);
	lock.readLock().unlock();
	return contains;
    }

    public int size() {
	lock.readLock().lock();
	var size = map.size();
	lock.readLock().unlock();
	return size;
    }

    public boolean isEmpty() {
	lock.readLock().lock();
	var isEmpty = map.isEmpty();
	lock.readLock().unlock();
	return isEmpty;
    }

    public void clear() {
	lock.writeLock().lock();
	map.clear();
	lock.writeLock().unlock();
    }

}
