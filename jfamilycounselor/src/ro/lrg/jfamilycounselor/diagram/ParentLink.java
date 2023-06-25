package ro.lrg.jfamilycounselor.diagram;

import java.util.Objects;

public class ParentLink {
	@Override
	public int hashCode() {
		return Objects.hash(name, parent);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParentLink other = (ParentLink) obj;
		return Objects.equals(name, other.name) && Objects.equals(parent, other.parent);
	}

	private final String parent;
	private final String name;
	
	public ParentLink(String parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}
}
