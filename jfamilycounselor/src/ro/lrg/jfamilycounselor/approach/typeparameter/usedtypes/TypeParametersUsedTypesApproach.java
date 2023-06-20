package ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes;

import static ro.lrg.jfamilycounselor.capability.ast.typereference.TypeReferenceCapability.extractTypeReferences;
import static ro.lrg.jfamilycounselor.capability.type.DistinctConcreteConeProductCapability.distinctConcreteConeProduct;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.lazy;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;

import ro.lrg.jfamilycounselor.util.datatype.Pair;;

public class TypeParametersUsedTypesApproach {
    private TypeParametersUsedTypesApproach() {
    }

    public static Optional<List<Pair<IType, IType>>> usedTypes(Pair<IJavaElement, IJavaElement> typeParametersPair) {
	IType parameterizedType;
	if (typeParametersPair._1 instanceof IType thiz)
	    parameterizedType = thiz;
	else if (typeParametersPair._1 instanceof ITypeParameter typeParameter && typeParameter.getDeclaringMember() instanceof IType thiz2)
	    parameterizedType = thiz2;
	else
	    return Optional.empty();

	// Find all references of the parameterized type
	var parameterizedReferencesOpt = parameterizedReferences(parameterizedType);

	if (parameterizedReferencesOpt.isEmpty())
	    return Optional.empty();

	var parameterizedReferences = parameterizedReferencesOpt.get();

	var result = new HashSet<Pair<IType, IType>>();

	var inconclsuiveCounter = new AtomicInteger(0);

	// If the analyzed pair is formed by two real type parameters,
	// then extract the corresponding arguments from the references and compute the
	// distinctConcreteConeProduct
	if (typeParametersPair._1 instanceof ITypeParameter tp1 && typeParametersPair._2 instanceof ITypeParameter tp2) {
	    var index1Opt = indexOfTypeParameter(tp1);
	    var index2Opt = indexOfTypeParameter(tp2);

	    if (index1Opt.isEmpty() || index2Opt.isEmpty())
		return Optional.empty();

	    var index1 = index1Opt.get();
	    var index2 = index2Opt.get();

	    parameterizedReferences.stream()
		    .forEach(s -> {
			try {
			    var pt = s.get();
			    var typeArguments = pt.typeArguments();
			    var type1Node = (Type) typeArguments.get(index1);
			    var type2Node = (Type) typeArguments.get(index2);
			    var t1 = (IType) type1Node.resolveBinding().getJavaElement();
			    var t2 = (IType) type2Node.resolveBinding().getJavaElement();
			    distinctConcreteConeProduct(t1, t2).get().forEach(p -> result.add(p));
			} catch (Exception e) {
			    inconclsuiveCounter.incrementAndGet();
			}
		    });
	    // If the analyzed pair is formed by the synthetic this type parameter and a
	    // type parameter
	    // then extract the corresponding type argument and:
	    // 1. if the reference is used in a type declaration for "extends" or
	    // "implements",
	    // write the this synthetic type parameter with the declared type
	    // 2. otherwise, compute the product between the type of the synthetic this type
	    // parameter and the extracted type argument
	} else if (typeParametersPair._1 instanceof IType thiz && typeParametersPair._2 instanceof ITypeParameter typeParam) {
	    var indexOpt = indexOfTypeParameter(typeParam);

	    if (indexOpt.isEmpty())
		return Optional.empty();

	    var index = indexOpt.get();

	    parameterizedReferences.stream()
		    .forEach(s -> {
			try {
			    var pt = s.get();

			    var typeArguments = pt.typeArguments();
			    IType t1;

			    if (pt.getParent() instanceof AbstractTypeDeclaration atd)
				t1 = (IType) atd.resolveBinding().getJavaElement();
			    else
				t1 = (IType) pt.getType().resolveBinding().getJavaElement();

			    var t2 = (IType) ((Type) typeArguments.get(index)).resolveBinding().getJavaElement();
			    distinctConcreteConeProduct(t1, t2).get().forEach(p -> result.add(p));
			} catch (Exception e) {
			    inconclsuiveCounter.incrementAndGet();
			}
		    });
	} else
	    return Optional.empty();

	// if there are a lot of inconclusive cases, discard all resolved pairs
	if (inconclsuiveCounter.get() > result.size())
	    return Optional.of(List.of());

	return Optional.of(result.stream().toList());
    }

    private static Optional<List<Supplier<ParameterizedType>>> parameterizedReferences(IType parameterizedType) {
	return extractTypeReferences(parameterizedType)
		.map(l -> l.stream()
			.map(s -> s.get())
			.filter(t -> t instanceof ParameterizedType pt && !pt.typeArguments().isEmpty())
			.map(t -> (ParameterizedType) t)
			.map(pt -> lazy(pt))
			.toList());
    }

    private static Optional<Integer> indexOfTypeParameter(ITypeParameter typeParameter) {
	var type = (IType) typeParameter.getDeclaringMember();

	try {
	    return Optional.of(List.of(type.getTypeParameters()).indexOf(typeParameter)).filter(i -> i >= 0);
	} catch (JavaModelException e) {
	    return Optional.empty();
	}
    }

}
