package ca.l5.expandingdev.jsgf;

public class SimpleWord implements Word {
	
	private String text;
	
	public String getString() { 
		return text; 
	}
	
	public SimpleWord(String s) {
		text = s;
	}
}
