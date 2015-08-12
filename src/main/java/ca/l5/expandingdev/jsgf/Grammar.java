package ca.l5.expandingdev.jsgf;

import java.util.ArrayList;
import java.util.List;

public class Grammar {
	private List<Rule> rules;
	public String name;
	
	public String compileGrammar() {
		String f = "#JSGF\n" + "grammar " + name + ";\n";
		for(int i = 0; i < rules.size(); i++) {
			f = f.concat(rules.get(i).getRuleString() + "\n");
		}
		return f;
	}
	
	public void addRule(Rule r) {
		rules.add(r);
	}
	
	public Grammar() {
		name = "default";
		rules = new ArrayList<Rule>();
	}
	
	public Grammar(String n)  {
		name = n;
		rules = new ArrayList<Rule>();
	}
}
