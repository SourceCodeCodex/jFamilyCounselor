package ro.lrg.jfamilycounselor.plugin.project.action.util.csv;

import java.util.List;
import java.util.stream.Collectors;

public class CsvUtil {
    private CsvUtil() {
    }


    public static String convertToCsv(List<String> data) {
	return data.stream().map(CsvUtil::escapeSpecialCharacters).collect(Collectors.joining(",")) + "\n";
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
