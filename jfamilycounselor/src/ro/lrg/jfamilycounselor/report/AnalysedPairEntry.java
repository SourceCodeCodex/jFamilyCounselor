package ro.lrg.jfamilycounselor.report;

import java.util.List;

public record AnalysedPairEntry(String name, String duration, Double apertureCoverage, List<String> correlationIds){

}
