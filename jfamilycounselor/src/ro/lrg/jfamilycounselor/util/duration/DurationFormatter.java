package ro.lrg.jfamilycounselor.util.duration;

import java.time.Duration;

public class DurationFormatter {
    private DurationFormatter() {
    }

    public static String format(Duration d) {
	var s = d.getSeconds();
	return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

}
