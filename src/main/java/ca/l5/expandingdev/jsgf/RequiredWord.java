package ca.l5.expandingdev.jsgf;

public class RequiredWord implements Word {
	private Word w;
	private Word a;
	private boolean onlyinside = true;
	
	public String getString() {
		if(onlyinside) {
			return "(" + w.getString() + ")"; 
		}
		else {
			return "(" + w.getString() + ") " + a.getString();
		}
	}
	
	public RequiredWord(Word rw) {
		w = rw;
	}

	public RequiredWord(Word inside, Word after) {
		w = inside;
		a = after;
		onlyinside = false;
	}
}
