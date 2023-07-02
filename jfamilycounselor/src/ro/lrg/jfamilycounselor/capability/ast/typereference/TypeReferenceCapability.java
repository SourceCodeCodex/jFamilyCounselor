package ro.lrg.jfamilycounselor.capability.ast.typereference;

import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.lazy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.Type;

import ro.lrg.jfamilycounselor.capability.parse.ParseCapability;
import ro.lrg.jfamilycounselor.capability.search.type.TypeReferenceSearchCapability;

public class TypeReferenceCapability {
	private TypeReferenceCapability() {
	}

	public static Optional<List<Supplier<Type>>> extractTypeReferencesFromScope(IType iType,
			List<IMember> encolsingMembers) {
		return Optional.of(encolsingMembers.stream().map(m -> Optional.ofNullable(m.getCompilationUnit()))
				.filter(o -> o.isPresent()).map(o -> o.get()).map(ParseCapability::parse).filter(o -> o.isPresent())
				.map(o -> o.get()).flatMap(ast -> {
					var visitor = new TypeReferenceVisitor(iType);
					ast.accept(visitor);
					return visitor.getReferences().stream().map(p -> lazy(p));
				}).toList());
	}

	public static Optional<List<Supplier<Type>>> extractTypeReferences(IType iType) {
		var typeRefsEnclosingMembers = TypeReferenceSearchCapability.searchTypeReferences(iType);
		if (typeRefsEnclosingMembers.isEmpty())
			return Optional.empty();

		return extractTypeReferencesFromScope(iType, typeRefsEnclosingMembers.get());
	}

}
