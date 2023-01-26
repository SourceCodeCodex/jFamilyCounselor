package ro.lrg.jfamilycounselor.services.cache;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

/**
 * Service responsible for the management of all caches across the system. It
 * has the functionality to regularly clear all caches when the memory usage
 * raises above the tolerable levels. Therefore, any other service should only
 * used caches provided by this service for proper supervision.
 * 
 * @author rosualinpetru
 *
 */
public class CacheService implements Runnable {

    private static final double MAX_MEMORY_USAGE = 0.75;
    private static final Duration SLEEP_DURATION = Duration.ofSeconds(10);

    private static CacheService instance;

    private CacheService() {
    }

    private static volatile boolean isMemorySupervisorRunning = false;
    private static Thread memorySupervisingThread;

    @SuppressWarnings("rawtypes")
    private static List<Cache> caches = new LinkedList<Cache>();

    public static <K, V> Cache<K, V> getCache(int capacity) {
	var cache = new LRUCache<K, V>(capacity);
	caches.add(cache);
	return cache;
    }

    @SuppressWarnings("preview")
    public static void startMemorySupervisor() {
	if (!isMemorySupervisorRunning) {
	    memorySupervisingThread = Thread.startVirtualThread(instance);
	}
    }

    public static void stopMemorySupervisor() {
	if (isMemorySupervisorRunning) {
	    memorySupervisingThread.interrupt();
	}
    }

    public void clearCaches() {
	caches.stream().forEach(c -> c.clear());
    }

    @Override
    public void run() {
	isMemorySupervisorRunning = true;
	var r = Runtime.getRuntime();
	while (!Thread.interrupted()) {
	    var usedMemoryPercentage = (1.0 * r.totalMemory() - r.freeMemory()) / r.freeMemory();
	    if (usedMemoryPercentage > MAX_MEMORY_USAGE) {
		r.gc();
	    }
	    try {
		Thread.sleep(SLEEP_DURATION);
	    } catch (InterruptedException e) {
		isMemorySupervisorRunning = false;
	    }
	}
	isMemorySupervisorRunning = false;
    }

}
