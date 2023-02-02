package ro.lrg.jfamilycounselor.util.cache;

import java.util.logging.Logger;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Service responsible for the management of all caches across the system. It
 * has the functionality to regularly clear all caches when the memory usage
 * raises above the tolerable levels. Therefore, any other service should only
 * used caches provided by this service for proper supervision.
 * 
 * @author rosualinpetru
 *
 */
public class CacheManager {

    private static final int DEFAULT_CAPACITY = 128;

    private static final double MAX_MEMORY_USAGE = 0.75;
    private static final Duration SLEEP_DURATION = Duration.ofSeconds(10);

    private CacheManager() {
    }

    private static volatile boolean isMemorySupervisorRunning = false;
    private static Thread memorySupervisingThread;
    
    private static final Logger logger = jFCLogger.getJavaLogger();

    @SuppressWarnings("rawtypes")
    private static List<Cache> caches = new LinkedList<Cache>();

    public static <K, V> Cache<K, V> getCache() {
	var cache = new LRUCache<K, V>(DEFAULT_CAPACITY);
	caches.add(cache);
	return cache;
    }

    public static <K, V> Cache<K, V> getCache(int capacity) {
	var cache = new LRUCache<K, V>(capacity);
	caches.add(cache);
	return cache;
    }

    @SuppressWarnings("preview")
    public static void startMemorySupervisor() {
	logger.info("Memory supervisor: Starting automatic cache cleaner...");
	if (!isMemorySupervisorRunning) {
	    memorySupervisingThread = Thread.startVirtualThread(() -> {
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
			return;
		    }
		}
		isMemorySupervisorRunning = false;

	    });
	}
    }

    public static void stopMemorySupervisor() {
	logger.info("Memory supervisor: Stopping automatic cache cleaner...");
	if (isMemorySupervisorRunning) {
	    memorySupervisingThread.interrupt();
	}
    }

    public static void clearCaches() {
	caches.stream().forEach(c -> c.clear());
    }
}
