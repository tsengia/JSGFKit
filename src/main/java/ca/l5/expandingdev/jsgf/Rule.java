package ca.l5.expandingdev.jsgf;

public class Rule {
	public String name;
	public Expansion[] expansions;
	
	public String getRuleString() {
		String s = "";
		for(int i = 0; i < expansions.length; i++) {
			s = s.concat(expansions[i].getString());
		}
		return "<" + name + "> = " + s + ";";
	}
	
	public Rule(String n, Expansion... exp) {
		name = n;
		expansions = exp;
	}
}
