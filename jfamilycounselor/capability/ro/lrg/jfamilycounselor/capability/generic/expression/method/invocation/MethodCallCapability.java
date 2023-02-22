package ro.lrg.jfamilycounselor.capability.generic.expression.method.invocation;

import static ro.lrg.jfamilycounselor.util.list.CommonOperations.asSupplier;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.Expression;

import ro.lrg.jfamilycounselor.capability.generic.parse.ParseCapability;
import ro.lrg.jfamilycounselor.capability.generic.search.method.invocation.MethodCallSearchCapability;

public class MethodCallCapability {
    private MethodCallCapability() {
    }

    public static Optional<List<Supplier<Expression>>> methodCalls(IMethod iMethod) {
	var callSitesOpt = MethodCallSearchCapability.searchMethodCalls(iMethod);

	if (callSitesOpt.isEmpty())
	    return Optional.empty();

	var callSites = callSitesOpt.get();

	return Optional.of(
		callSites.stream()
			.map(ParseCapability::parse)
			.filter(o -> o.isPresent())
			.map(o -> o.get())
			.flatMap(ast -> {
			    var visitor = new MethodCallVisitor(iMethod);
			    ast.accept(visitor);
			    return visitor.getInvocations().stream().map(p -> asSupplier(p));
			}).toList());
    }

}
