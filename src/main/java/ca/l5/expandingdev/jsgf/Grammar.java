package ca.l5.expandingdev.jsgf;

import java.util.ArrayList;
import java.util.List;

public class Grammar {
	private List<Rule> rules;
	private List<Import> imports;
	public String name;
	private GrammarHeader header;
	
	public String compileGrammar() {
		String f = header.getHeader();
		f = f.concat("grammar " + name + ";\n");
		
		for(Import i : imports) {
			f = f.concat(i.getString() + "\n");
		}
		for(Rule r : rules) {
			f = f.concat(r.getRuleString() + "\n");
		}
		return f;
	}
	
	public void addRule(Rule r) {
		rules.add(r);
	}
	
	public void addImport(Import i) {
		imports.add(i);
	}
	
	public void addImport(String i) {
		imports.add(new Import(i));
	}
	
	public List<Import> getImports() {
		return imports;
	}
	
	public List<Rule> getRules() {
		return rules;
	}
	
	public void setGrammarHeader(GrammarHeader h) {
		header = h;
	}
	
	public GrammarHeader getGrammarHeader() {
		return header;
	}
	
	public Grammar() {
		header = new GrammarHeader();
		rules = new ArrayList<Rule>();
		imports = new ArrayList<Import>();
		name = "default";
	}
	
	public Grammar(String n)  {
		header = new GrammarHeader();
		rules = new ArrayList<Rule>();
		imports = new ArrayList<Import>();
		name = n;
	}
}
