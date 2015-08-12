package ca.l5.expandingdev.jsgf;

public class Rule {
	public String name;
	public Word expansion;
	
	public String getRuleString() {
		return "<" + name + "> = " + expansion.getString() + ";";
	}
	
	public Rule(String n, Word exp) {
		name = n;
		expansion = exp;
	}
}
