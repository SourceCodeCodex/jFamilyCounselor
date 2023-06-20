package ro.lrg.jfamilycounselor.util;

public class Constants {
    private Constants() {
    }

    public static final String OBJECT_FQN = "java.lang.Object";
    public static final String EQUAL = "equal";

    public enum EstimationType {
	NAME_BASED, NAME_BASED_LEVENSHTEIN, ASSIGNMENTS_BASED, CASTS_BASED, TYPE_PARAMETERS_BASED
    }

}
