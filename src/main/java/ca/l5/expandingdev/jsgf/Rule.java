package ca.l5.expandingdev.jsgf;

public class Rule {
	public String name;
	public Expansion expansion;
	
	public String getRuleString() {
		return "<" + name + "> = " + expansion.getString() + ";";
	}
	
	public Rule(String n, Expansion exp) {
		name = n;
		expansion = exp;
	}
}
