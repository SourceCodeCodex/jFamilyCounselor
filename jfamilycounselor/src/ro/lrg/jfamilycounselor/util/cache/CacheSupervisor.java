package ro.lrg.jfamilycounselor.util.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Regularly clears all caches when the memory usage raises above the tolerable
 * levels or when there is a change in the project's source files.
 * 
 * @author rosualinpetru
 *
 */
public class CacheSupervisor {

    private static final double MAX_HIGH_CONSUMER_MEMORY_USAGE = 0.9;
    private static final double MAX_LOW_CONSUMER_MEMORY_USAGE = 0.7;
    private static final long SLEEP_DURATION = 5000;

    private CacheSupervisor() {
    }

    @SuppressWarnings("rawtypes")
    static List<Cache> caches = new LinkedList<Cache>();

    private static volatile boolean isMemorySupervisorRunning = false;
    private static Thread memorySupervisingThread;

    private static final Logger logger = jFCLogger.getLogger();

    public static void startMemorySupervisor() {
	logger.info("Memory supervisor: Starting automatic cache cleaner...");
	if (!isMemorySupervisorRunning) {
	    memorySupervisingThread = new Thread(() -> {
		isMemorySupervisorRunning = true;

		while (!Thread.interrupted()) {

		    if (usedMemoryPercentage() > MAX_HIGH_CONSUMER_MEMORY_USAGE) {
			logger.warning("Performing high memory consumer cache clearing.");
			clearHighMemoryConsumerCaches();

			try {
			    Thread.sleep(1200);
			    if (usedMemoryPercentage() > MAX_LOW_CONSUMER_MEMORY_USAGE) {
				logger.warning("Performing all cache clearing. ");
				clearAllCaches();
			    }
			} catch (InterruptedException e) {
			    isMemorySupervisorRunning = false;
			    return;
			}
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

	    memorySupervisingThread.start();
	}
    }

    private static double usedMemoryPercentage() {
	var r = Runtime.getRuntime();
	var total = r.totalMemory() * 1.0 / 1048576;
	var free = r.freeMemory() * 1.0 / 1048576;
	var max = r.maxMemory() * 1.0 / 1048576;
	var used = total - free;
	return used / max;
    }

    public static void stopMemorySupervisor() {
	logger.info("Memory supervisor: Stopping automatic cache cleaner...");
	if (isMemorySupervisorRunning) {
	    memorySupervisingThread.interrupt();
	}
    }

    public static void clearAllCaches() {
	caches.stream().forEach(c -> c.clear());
	System.gc();
    }

    public static void clearHighMemoryConsumerCaches() {
	caches.stream().filter(c -> c.isBigMemoryConsumer()).forEach(c -> c.clear());
	System.gc();
    }
}
