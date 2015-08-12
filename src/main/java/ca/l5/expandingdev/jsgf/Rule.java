package ca.l5.expandingdev.jsgf;

public class Rule {
	public String name;
	public Expansion[] expansions;
	public boolean isvisable = true;
	
	public String getRuleString() {
		String s = "";
		for(int i = 0; i < expansions.length; i++) {
			s = s.concat(expansions[i].getString());
		}
		if(isvisable) { 
			return "public <" + name + "> = " + s + ";";
		}
		else {
			return "<" + name + "> = " + s + ";";
		}
	}
	
	public void setPrivate() {
		isvisable = false;
	}
	
	public void setPublic() {
		isvisable = true;
	}
	
	public Rule(String n, boolean visable, Expansion... exp) {
		name = n;
		expansions = exp;
		isvisable = visable;
	}
	
	public Rule(String n, Expansion... exp) {
		name = n;
		expansions = exp;
	}
}
