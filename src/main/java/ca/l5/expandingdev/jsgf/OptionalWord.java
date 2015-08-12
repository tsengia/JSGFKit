package ca.l5.expandingdev.jsgf;

public class OptionalWord implements Word {
	private Word w;
	private Word a;
	private boolean onlyinside = true;
		
	public String getString() {
		if(onlyinside) {
			return "[" + w.getString() + "]"; 
		}
		else {
			return "[" + w.getString() + "] " + a.getString();
		}
	}
	
	
	public OptionalWord(Word ow) {
		w = ow;
	}
	
	public OptionalWord(Word ow, Word after) {
		w = ow;
		a = after;
		onlyinside = false;
	}
}
