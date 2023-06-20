package ro.lrg.jfamilycounselor.capability.expression.methodcall;

import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.lazy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.Expression;

import ro.lrg.jfamilycounselor.capability.parse.ParseCapability;
import ro.lrg.jfamilycounselor.capability.search.methodcall.MethodCallSearchCapability;

/**
 * Capability that extracts all methods invocation expressions, either from the
 * entire workspace, or from some specified enclosing methods.
 * 
 * @author rosualinpetru
 *
 */
public class MethodCallCapability {
    private MethodCallCapability() {
    }

    public static Optional<List<Supplier<Expression>>> extractMethodCallsFromScope(IMethod iMethod, List<IMethod> enclosingMethods) {
	return Optional.of(
		enclosingMethods.stream()
			.map(ParseCapability::parse)
			.filter(o -> o.isPresent())
			.map(o -> o.get())
			.flatMap(ast -> {
			    var visitor = new MethodCallVisitor(iMethod);
			    ast.accept(visitor);
			    return visitor.getInvocations().stream().map(p -> lazy(p));
			}).toList());
    }

    public static Optional<List<Supplier<Expression>>> extractMethodCalls(IMethod iMethod) {
	var methodCallsEnclosingMethodsOpt = MethodCallSearchCapability.searchMethodCalls(iMethod);

	if (methodCallsEnclosingMethodsOpt.isEmpty())
	    return Optional.empty();

	return extractMethodCallsFromScope(iMethod, methodCallsEnclosingMethodsOpt.get());
    }

}
