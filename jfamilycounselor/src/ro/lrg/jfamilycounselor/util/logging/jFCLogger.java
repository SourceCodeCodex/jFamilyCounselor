package ro.lrg.jfamilycounselor.util.logging;

import java.util.Arrays;
import java.util.logging.Logger;

public class jFCLogger {
    private jFCLogger() {
    }

    public static Logger getJavaLogger() {
	var callerFQN = Thread.currentThread().getStackTrace()[2].getClassName();
	var callerNameFragments = Arrays.asList(callerFQN.split("\\."));
	var callerSimpleName = callerNameFragments.get(callerNameFragments.size() - 1);
	var logger = Logger.getLogger(callerSimpleName);
	return logger;
    }

    /**
     * The logging format is set in the Startup class.
     */
    @SuppressWarnings("unused")
    public static String format() {
	var ANSI_RESET = "\u001B[0m";
	var ANSI_BLACK = "\u001B[30m";
	var ANSI_RED = "\u001B[31m";
	var ANSI_GREEN = "\u001B[32m";
	var ANSI_YELLOW = "\u001B[33m";
	var ANSI_BLUE = "\u001B[34m";
	var ANSI_PURPLE = "\u001B[35m";
	var ANSI_CYAN = "\u001B[36m";
	var ANSI_WHITE = "\u001B[37m";

	var ANSI_BOLD_ON = "\u001B[1m";
	var ANSI_BOLD_OFF = "\u001B[22m";

	var builder = new StringBuilder();

	builder.append(ANSI_YELLOW);
	builder.append("%1$tF %1$tT");
	builder.append(ANSI_WHITE);

	builder.append(" | ");

	builder.append(ANSI_CYAN);
	builder.append(ANSI_BOLD_ON);
	builder.append("%4$.4s");
	builder.append(ANSI_BOLD_OFF);
	builder.append(ANSI_WHITE);

	builder.append(" | ");

	builder.append(ANSI_GREEN);
	builder.append("%3$s");
	builder.append(ANSI_WHITE);

	builder.append(" | ");

	builder.append("%5$s %n");

	return builder.toString();
    }

}
