package ro.lrg.jfamilycounselor.util.cache;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class KeyManager {
    private KeyManager() {
    }

    public static String compileationUnit(ICompilationUnit iCompilationUnit) {
	return iCompilationUnit.getElementName();
    }

    public static String type(IType iType) {
	return iType.getFullyQualifiedName();
    }

    public static String method(IMethod iMethod) {
	try {
	    return iMethod.getSignature();
	} catch (JavaModelException e) {
	    return iMethod.getDeclaringType().getFullyQualifiedName() + "/" + iMethod.getElementName() + "(...)";
	}
    }

    public static String parameter(ILocalVariable iLocalVariable) {
	var iMethod = (IMethod) iLocalVariable.getDeclaringMember();
	return method(iMethod) + "/" + iLocalVariable.getElementName();
    }

}
