package ro.lrg.jfamilycounselor.approach.usedtypes.assignment.derivation.partial;

import java.util.Optional;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model.AssignedElement;

public record PartialDerivationResult(Optional<AssignedElement> newAssignedElement, Optional<IType> newLowestRecordedType) {

}
