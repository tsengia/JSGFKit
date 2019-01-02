package ca.l5.expandingdev.jsgf;

public class Token implements Expansion {
	private String text;
	private String tag;
	
	public String getTag() {
		return tag;
	}
	
	public String getText() {
		return text;
	}
	
	public void setTag(String s) {
		tag = s;
	}
	
	public String getString() {
		if(tag.equals("")) {
			return text;
		}
		else {
			return text + " {" + tag + "}";
		}
	}
	
	public Token() {
		text = "";
		tag = ""
	}
	
	public Token(String s) {
		text = s;
		tag = "";
	}
	
	public Token(String s, String g) {
		text = s;
		tag = g;
	}
}
