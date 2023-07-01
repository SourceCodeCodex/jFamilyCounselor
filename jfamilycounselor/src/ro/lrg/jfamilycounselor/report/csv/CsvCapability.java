package ro.lrg.jfamilycounselor.report.csv;

import java.util.List;
import java.util.stream.Collectors;

public class CsvCapability {
    private CsvCapability() {
    }

    public static String joinAsCSVRow(List<String> data) {
	return data.stream().map(CsvCapability::escapeSpecialCharacters).collect(Collectors.joining(",")) + "\n";
    }

    private static String escapeSpecialCharacters(String data) {
	String escapedData = data.replaceAll("\\R", " ");
	if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	    data = data.replace("\"", "\"\"");
	    escapedData = "\"" + data + "\"";
	}
	return escapedData;
    }

}
