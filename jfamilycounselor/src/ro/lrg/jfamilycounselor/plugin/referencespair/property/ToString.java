package ro.lrg.jfamilycounselor.plugin.referencespair.property;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.jfamilycounselor.util.stringify.Stringify;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MReferencesPair> {

	@Override
	public String compute(MReferencesPair mReferencesPair) {
		var ref1 = mReferencesPair.getUnderlyingObject()._1;
		var ref2 = mReferencesPair.getUnderlyingObject()._2;

		return "[" + refererenceToString(ref1) + ", " + refererenceToString(ref2) + "]";
	}

	private String refererenceToString(IJavaElement ref) {
		if (ref instanceof ILocalVariable param) {
			var method = (IMethod) param.getDeclaringMember();
			String params;
			try {
				params = Arrays.asList(method.getParameters()).stream().map(p -> p.getElementName())
						.collect(Collectors.joining(","));
			} catch (JavaModelException e) {
				params = "...";
			}
			return method.getElementName() + "(" + params + ")/" + param.getElementName();
		} else if (ref instanceof IType thiz) {
			return Stringify.stringify(thiz);
		} else {
			return "-";
		}
	}

}
