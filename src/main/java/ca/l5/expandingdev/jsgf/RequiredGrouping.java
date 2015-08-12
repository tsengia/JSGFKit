package ca.l5.expandingdev.jsgf;

public class RequiredGrouping implements Expansion {
	private Expansion w;
	private Expansion a;
	private boolean onlyinside = true;
	
	public String getString() {
		if(onlyinside) {
			return "(" + w.getString() + ")"; 
		}
		else {
			return "(" + w.getString() + ") " + a.getString();
		}
	}
	
	public RequiredGrouping(Expansion rw) {
		w = rw;
	}

	public RequiredGrouping(Expansion inside, Expansion after) {
		w = inside;
		a = after;
		onlyinside = false;
	}
}
