package ca.l5.expandingdev.jsgf;

public class RuleReference implements Word {
	private String name;
	
	public String getString() {
		return "<" + name + ">";
	}
	
	public RuleReference(String rr) {
		name = rr;
	}
}
