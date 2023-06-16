package ro.lrg.jfamilycounselor.approach.usedtypes.assignment.model;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.datatype.Pair;

/**
 * @param needsExpansion expansion refers to the computation of the
 *                       distinctConcreteConeProduct between the types of the
 *                       pair. There are some cases when this computation is not
 *                       needed and therefore this parameters helps to
 *                       distinguish such cases
 * 
 * @author rosualinpetru
 *
 */
public record InconclusiveTypesPair(Pair<IType, IType> types, boolean needsExpansion) {

}
