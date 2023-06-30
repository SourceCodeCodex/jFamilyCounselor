package ro.lrg.jfamilycounselor.plugin.typeparameterspair.property;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;

import jfamilycounselor.metamodel.entity.MTypeParametersPair;
import ro.lrg.jfamilycounselor.util.stringify.Stringify;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MTypeParametersPair> {

    @Override
    public String compute(MTypeParametersPair mTypeParametersPair) {
	var t1 = mTypeParametersPair.getUnderlyingObject()._1;
	var t2 = mTypeParametersPair.getUnderlyingObject()._2;
	return "[" + typeParameterToString(t1) + ", " + typeParameterToString(t2) + "]";
    }

    private String typeParameterToString(IJavaElement typeParameter) {
	if (typeParameter instanceof ITypeParameter iTypeParameter) {
	    try {
		return iTypeParameter.getElementName() + " <: " + Arrays.asList(iTypeParameter.getBounds()).stream().collect(Collectors.joining(","));
	    } catch (JavaModelException e) {
		return iTypeParameter.getElementName() + " <: " + "...";
	    }
	} else if (typeParameter instanceof IType thiz) {
	    return Stringify.stringify(thiz);
	} else {
	    return "-";
	}
    }

}
