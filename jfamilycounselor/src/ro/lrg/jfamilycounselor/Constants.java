package ro.lrg.jfamilycounselor;

public class Constants {
    private Constants() {
    }

    public static final String OBJECT_FQN = "java.lang.Object";
    public static final String EQUAL = "equal";

    public enum EstimationType {
	NAME_BASED, LEVENSHTEIN_BASED, ASSIGNMENT_BASED, CAST_BASED
    }

}
