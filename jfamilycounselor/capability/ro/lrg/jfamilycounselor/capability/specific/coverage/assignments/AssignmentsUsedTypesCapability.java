package ro.lrg.jfamilycounselor.capability.specific.coverage.assignments;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class AssignmentsUsedTypesCapability {
    private AssignmentsUsedTypesCapability() {
    }
    
    public static Optional<List<Pair<IType, IType>>> usedTypes(Pair<IJavaElement, IJavaElement> referencesPair) {
	return Optional.empty();
	
    }
    
    
    

}
