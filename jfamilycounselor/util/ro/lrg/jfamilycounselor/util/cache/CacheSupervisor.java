package ro.lrg.jfamilycounselor.util.cache;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

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
public class CacheSupervisor {

    private static final double MAX_MEMORY_USAGE = 0.9;
    private static final Duration SLEEP_DURATION = Duration.ofSeconds(5);

    private CacheSupervisor() {
    }
    
    @SuppressWarnings("rawtypes")
    static List<Cache> caches = new LinkedList<Cache>();

    private static volatile boolean isMemorySupervisorRunning = false;
    private static Thread memorySupervisingThread;

    private static final Logger logger = jFCLogger.getJavaLogger();


    @SuppressWarnings("preview")
    public static void startMemorySupervisor() {
	logger.info("Memory supervisor: Starting automatic cache cleaner...");
	if (!isMemorySupervisorRunning) {
	    memorySupervisingThread = Thread.startVirtualThread(() -> {
		isMemorySupervisorRunning = true;
		var r = Runtime.getRuntime();
		while (!Thread.interrupted()) {
		    var total = r.totalMemory() * 1.0 / 1048576;
		    var free = r.freeMemory() * 1.0 / 1048576;
		    var max = r.maxMemory() * 1.0 / 1048576;
		    var used = total - free;
		    var usedPercentage = used / max;
		    if (usedPercentage > MAX_MEMORY_USAGE) {
			logger.warning("Performing cache clearing. " + String.format("Memory report: total=%.2f, free=%.2f, max=%.2f, used=%.2f (%.2f)", total, free, max, used, usedPercentage)) ;
			clearCaches();
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
