package ro.lrg.jfamilycounselor.approach.reference.usedtypes.name;

import static ro.lrg.jfamilycounselor.approach.reference.usedtypes.name.TokensUtil.splitNameInTokens;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.approach.reference.usedtypes.CorrelationEstimationUsedTypesApproach;

public class NameBasedLevenshteinApproach extends CorrelationEstimationUsedTypesApproach {
	private static NameBasedLevenshteinApproach instance = new NameBasedLevenshteinApproach();

	private NameBasedLevenshteinApproach() {
	}

	public static NameBasedLevenshteinApproach instance() {
		return instance;
	}

	@Override
	protected boolean areCorrelated(IType t1, IType t2) {
		var tokens1 = splitNameInTokens(t1);
		var tokens2 = splitNameInTokens(t2);

		return levenshteinDistanceOnToken(tokens1, tokens2) <= (Math.max(tokens1.size(), tokens2.size()) / 2.);
	}

	private static int levenshteinDistanceOnToken(List<String> tokens1, List<String> tokens2) {
		var dp = new int[tokens1.size() + 1][tokens2.size() + 1];

		for (int i = 0; i <= tokens1.size(); i++) {
			for (int j = 0; j <= tokens2.size(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(tokens1.get(i - 1), tokens2.get(j - 1)),
							dp[i - 1][j] + 1, dp[i][j - 1] + 1);
				}
			}
		}

		return dp[tokens1.size()][tokens2.size()];
	}

	private static int costOfSubstitution(String a, String b) {
		return a.equals(b) ? 0 : 1;
	}

	private static int min(int... numbers) {
		return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
	}
}
