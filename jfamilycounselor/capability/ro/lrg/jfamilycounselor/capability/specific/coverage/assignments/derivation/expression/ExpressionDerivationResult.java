package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments.derivation.expression;

import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

/**
 * The 'update' symbol refers to the optionality of obtaining relevant
 * information that can be used to update the assignments pairs, information
 * obtained during derivation.
 * 
 * @author rosualinpetru
 *
 */
public record ExpressionDerivationResult(Optional<? extends IJavaElement> writingElementUpdate, Optional<IType> mostConcreteRecordedTypeUpdate) {

}
