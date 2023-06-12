package ro.lrg.jfamilycounselor.capability.coverage.assignment.model;

import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

public record Assignment(IJavaElement reference, Optional<IJavaElement> assignedElement, IType mostConcreteRecordedType) {
}
