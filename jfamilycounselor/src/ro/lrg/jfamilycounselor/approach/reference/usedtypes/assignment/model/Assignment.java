package ro.lrg.jfamilycounselor.approach.reference.usedtypes.assignment.model;

import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

/**
 * An assignment of a reference records two important things:
 * 
 * @param assignedElement     The JDT object describing the element that is
 *                            being assigned to a reference. If derivation leads
 *                            to expressions whose JDT element cannot be
 *                            determined (e.g. NullExpression), then the
 *                            assignedElement would be empty.
 * 
 * @param lowestRecordedType: Since the derivation of assignments can generate
 *                            new assignments, it is important to record the
 *                            changes in the assigned expressions' types. As
 *                            derivation gets further away from the parent class
 *                            of the reference, it is important to record what
 *                            are the types of all expressions that have been
 *                            assigned to the reference so far. In fact, what
 *                            matters is the lowest type in its hierarchy of
 *                            some expression that was assigned to the reference
 *                            at some point,
 * 
 * @author rosualinpetru
 *
 */
public record Assignment(IJavaElement reference, Optional<AssignedElement> assignedElement, IType lowestRecordedType) {
}
