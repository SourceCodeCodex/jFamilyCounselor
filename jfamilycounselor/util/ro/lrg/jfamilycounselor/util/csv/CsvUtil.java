package ro.lrg.jfamilycounselor.util.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class CsvUtil {
    private CsvUtil() {
    }

    private static Logger logger = jFCLogger.getJavaLogger();

    public record CsvRecord(String record) {
    }

    @SuppressWarnings("preview")
    public static void writeRecords(File csvFile, Stream<CsvRecord> records) {

	try (PrintWriter pw = new PrintWriter(csvFile)) {
	    var flushThread = Thread.startVirtualThread(() -> {
		while (!Thread.interrupted()) {
		    pw.flush();
		    try {
			Thread.sleep(Duration.ofSeconds(5));
		    } catch (InterruptedException e) {
			pw.flush();
			return;
		    }
		}
	    });

	    records.map(CsvRecord::record).forEach(pw::println);
	    flushThread.interrupt();
	    pw.close();
	} catch (FileNotFoundException e) {
	    logger.warning("Csv file " + csvFile.getName() + " was not found");
	}
    }

    public static CsvRecord convertToCsv(List<String> data) {
	return new CsvRecord(data.stream().map(CsvUtil::escapeSpecialCharacters)
		.collect(Collectors.joining(",")));
    }

    public static String escapeSpecialCharacters(String data) {
	String escapedData = data.replaceAll("\\R", " ");
	if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	    data = data.replace("\"", "\"\"");
	    escapedData = "\"" + data + "\"";
	}
	return escapedData;
    }

}
