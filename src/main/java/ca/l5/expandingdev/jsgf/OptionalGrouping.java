package ca.l5.expandingdev.jsgf;

public class OptionalGrouping implements Expansion {
	private Expansion w;
	private Expansion a;
	private boolean onlyinside = true;
		
	public String getString() {
		if(onlyinside) {
			return "[" + w.getString() + "]"; 
		}
		else {
			return "[" + w.getString() + "] " + a.getString();
		}
	}
	
	
	public OptionalGrouping(Expansion ow) {
		w = ow;
	}
	
	public OptionalGrouping(Expansion ow, Expansion after) {
		w = ow;
		a = after;
		onlyinside = false;
	}
}
