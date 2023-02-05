package ro.lrg.jfamilycounselor.capability.generic.method.invocation;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.Expression;

import ro.lrg.jfamilycounselor.capability.generic.parse.ParseCapability;

public class StaticInvocationCapability {
    private StaticInvocationCapability() {
    }

    public static Optional<List<Expression>> staticInvocations(IMethod iMethod) {
	var sites = MethodInvocationSearchCapability.searchMethodInvocationSites(iMethod);

	var asts = sites.map(s -> s.stream().map(ParseCapability::parse).filter(o -> o.isPresent()).map(o -> o.get()).toList());

	return asts.map(s -> s.stream().flatMap(ast -> {
	    var visitor = new MethodInvocationVisitor(iMethod);
	    ast.accept(visitor);
	    return visitor.getInvocations().stream();
	})).map(s -> s.toList());
    }

}
