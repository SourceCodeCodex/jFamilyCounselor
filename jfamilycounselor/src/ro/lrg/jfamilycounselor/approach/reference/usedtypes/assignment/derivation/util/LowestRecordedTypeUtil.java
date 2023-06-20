package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.derivation.util;

import static ro.lrg.jfamilycounselor.capability.type.SubtypeCapability.isSubtypeOf;

import java.util.Optional;

import org.eclipse.jdt.core.IType;

public class LowestRecordedTypeUtil {
    private LowestRecordedTypeUtil() {
    }

    public static Optional<IType> updateLowestRecordedType(Optional<IType> previous, Optional<IType> current) {
	if (previous.stream().anyMatch(t0 -> current.stream().anyMatch(t1 -> isSubtypeOf(t1, t0))))
	    return current;

	return previous;
    }
}
