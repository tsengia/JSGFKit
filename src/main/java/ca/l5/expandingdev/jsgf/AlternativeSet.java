package ca.l5.expandingdev.jsgf;

public class AlternativeSet implements Expansion {
	private Expansion first;
	private Expansion second;
	
	public String getString() {
		return first.getString() + " | " + second.getString();
	}
	
	public AlternativeSet(Expansion f, Expansion s) {
		first = f;
		second = s;
	}
}
