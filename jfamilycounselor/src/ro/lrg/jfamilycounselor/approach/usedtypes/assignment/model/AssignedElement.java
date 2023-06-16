package ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public interface AssignedElement {
    
    public static record This(IType iType) implements AssignedElement {
    }
    
    public static record ResolvedType(IType iType) implements AssignedElement {
    }
    
    public static record Parameter(ILocalVariable iLocalVariable) implements AssignedElement {
    }
    
    public static record Field(IField iField) implements AssignedElement {
    }
    
    public static record MethodCall(IMethod iMethod) implements AssignedElement {
    }
}
