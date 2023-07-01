package ro.lrg.jfamilycounselor.capability.ast.typereference;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

class TypeReferenceVisitor extends ASTVisitor {
    private final IType iType;

    private final Set<Type> typeReferences = new HashSet<>();

    public TypeReferenceVisitor(IType iType) {
	this.iType = iType;
    }

    public List<Type> getReferences() {
	return List.copyOf(typeReferences);
    }

    @Override
    public boolean visit(PrimitiveType node) {
	if (Optional.ofNullable(node.resolveBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iType)))
	    typeReferences.add(node);

	return true;
    }

    @Override
    public boolean visit(ArrayType node) {
	if (Optional.ofNullable(node.getElementType().resolveBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iType)))
	    typeReferences.add(node);

	return true;
    }

    @Override
    public boolean visit(SimpleType node) {
	if (Optional.ofNullable(node.resolveBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iType)))
	    typeReferences.add(node);

	return true;
    }

    @Override
    public boolean visit(QualifiedType node) {
	if (Optional.ofNullable(node.resolveBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iType)))
	    typeReferences.add(node);

	return true;
    }

    @Override
    public boolean visit(NameQualifiedType node) {
	if (Optional.ofNullable(node.resolveBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iType)))
	    typeReferences.add(node);

	return true;
    }

    @Override
    public boolean visit(ParameterizedType node) {
	if (Optional.ofNullable(node.getType().resolveBinding()).map(b -> b.getJavaElement()).stream().anyMatch(j -> j.equals(iType))) {
	    typeReferences.add(node);
	    return false;
	}

	return true;
    }

}
