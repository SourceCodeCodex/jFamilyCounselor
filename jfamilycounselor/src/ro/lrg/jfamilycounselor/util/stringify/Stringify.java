package ro.lrg.jfamilycounselor.util.stringify;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

public class Stringify {
	private Stringify() {
	}

	public static String stringify(IType iType) {
		var fragments = Signature.toString(new BindingKey(iType.getKey()).toSignature()).split("[.]");

		var stringBuilder = new StringBuilder();
		stringBuilder.append(iType.getJavaProject().getElementName()).append("/");

		for (int i = 0; i < fragments.length - 1; i++) {
			stringBuilder.append(fragments[i].charAt(0)).append(".");
		}
		stringBuilder.append(fragments[fragments.length - 1]);

		return stringBuilder.toString();
	}
}
