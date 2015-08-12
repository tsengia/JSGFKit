package ca.l5.expandingdev.jsgf;

public class RuleReference implements Expansion {
	private String name;
	
	public String getString() {
		return "<" + name + ">";
	}
	
	public RuleReference(String rr) {
		name = rr;
	}
}
