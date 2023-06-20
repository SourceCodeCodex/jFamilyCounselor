package ro.lrg.jfamilycounselor.approach.reference.usedtypes.name;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;

class TokensUtil {
    private TokensUtil() {
    }

    private static final Cache<IType, List<String>> tokenCache = MonitoredUnboundedCache.getHighConsumingCache();

    private static final String tokensR = "(?<!(^|\\d))(?=\\d)|(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";

    public static List<String> splitNameInTokens(IType iType) {
	return tokenCache.get(iType).orElseGet(() -> {
	    var r = Arrays.asList(iType.getElementName().split(tokensR));
	    tokenCache.put(iType, r);
	    return r;
	});
    }
}
