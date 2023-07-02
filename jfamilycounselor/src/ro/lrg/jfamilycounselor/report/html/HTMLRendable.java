package ro.lrg.jfamilycounselor.report.html;

import java.util.Optional;

interface HTMLRendable {
	public default Optional<String> html() {
		var raw = htmlRaw();
		if (containsUnsetVariables(raw))
			return Optional.empty();

		return Optional.of(raw);
	}

	public String htmlRaw();

	private static boolean containsUnsetVariables(String html) {
		var regex = "\\{([A-Z]*_)*[A-Z]+\\}";
		return html.matches(regex);
	}
}
