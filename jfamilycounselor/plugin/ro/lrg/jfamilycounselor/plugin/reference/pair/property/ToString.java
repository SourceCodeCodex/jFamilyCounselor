package ro.lrg.jfamilycounselor.plugin.reference.pair.property;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MReferencesPair> {

    public String compute(MReferencesPair mReferencesPair) {
	var ref1 = (IJavaElement) mReferencesPair.getUnderlyingObject()._1;
	var ref2 = (IJavaElement) mReferencesPair.getUnderlyingObject()._2;

	return "[" + refererenceToString(ref1) + ", " + refererenceToString(ref2) + "]";
    }

    private String refererenceToString(IJavaElement ref) {
	if (ref instanceof ILocalVariable param) {
	    var method = (IMethod) param.getDeclaringMember();
	    var params = Arrays.asList(method.getParameterTypes()).stream().map(s -> Signature.getSignatureSimpleName(s)).collect(Collectors.joining(","));
	    return method.getElementName() + "(" + params + ") / " + param.getElementName();
	} else if (ref instanceof IType thys) {
	    return thys.getFullyQualifiedName();
	} else {
	    return "-";
	}
    }

}
