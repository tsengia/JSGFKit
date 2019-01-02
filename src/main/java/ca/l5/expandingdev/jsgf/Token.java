package ca.l5.expandingdev.jsgf;

public class Token implements Expansion {
	private String text;
	private String tag;
	
	public String getTag() {
		return tag;
	}
	
	public String getString() { 
		return text; 
	}
	
	public Token() {
		
	}
	
	public Token(String s) {
		text = s;
	}
}
