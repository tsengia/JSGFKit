package ca.l5.expandingdev.jsgf;

public class Rule {
	public String name;
	public Expansion[] expansions;
	public boolean isvisible = true;
	
	public String getRuleString() {
		String s = "";
		for(int i = 0; i < expansions.length; i++) {
			s = s.concat(expansions[i].getString());
		}

		if(isvisible) {
			return "public <" + name + "> = " + s + ";";
		}
		else {
			return "<" + name + "> = " + s + ";";
		}
	}
	
	public void setPrivate() {
		isvisible = false;
	}
	
	public void setPublic() {
		isvisible = true;
	}
	
	public Rule(String n, boolean visible, Expansion... exp) {
		name = n;
		expansions = exp;
		isvisible = visible;
	}
	
	public Rule(String n, Expansion... exp) {
		name = n;
		expansions = exp;
	}
}
