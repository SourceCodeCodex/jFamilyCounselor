package ro.lrg.jfamilycounselor.approach.reference.usedtypes.name;

import static ro.lrg.jfamilycounselor.approach.reference.usedtypes.name.TokensUtil.splitNameInTokens;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.CorrelationEstimationUsedTypesApproach;

public class NameBasedApproach extends CorrelationEstimationUsedTypesApproach {

	private static final double SIMRATIO = 0.5;

	private static NameBasedApproach instance = new NameBasedApproach();

	private NameBasedApproach() {
	}

	public static NameBasedApproach instance() {
		return instance;
	}

	@Override
	protected boolean areCorrelated(IType t1, IType t2) {
		var tokens1 = splitNameInTokens(t1);
		var tokens2 = splitNameInTokens(t2);

		var avgTokenLength = (tokens1.size() + tokens2.size()) / 2.0;

		var commonTokensCount = tokens1.stream().filter(s -> tokens2.contains(s)).count();

		return (commonTokensCount / avgTokenLength) >= SIMRATIO;
	}
}