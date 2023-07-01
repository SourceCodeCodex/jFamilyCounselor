package ro.lrg.jfamilycounselor.util;

public class Constants {
    private Constants() {
    }

    public static final String OBJECT_FQN = "java.lang.Object";
    public static final String EQUAL = "equal";

    public enum EstimationType {
	NAME_BASED("Name-based"),
	NAME_BASED_LEVENSHTEIN("Name-based-levenshtein"),
	ASSIGNMENTS_BASED("Assignments-based"),
	CASTS_BASED("Casts-based"),
	TYPE_PARAMETERS_BASED("TypeParameters-based");

	public final String label;

	private EstimationType(String label) {
	    this.label = label;
	}

	@Override
	public String toString() {
	    return label;
	}
    }

}
