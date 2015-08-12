package ca.l5.expandingdev.jsgf;

public class KleeneStar implements Expansion {
	private Expansion e;
	
	public String getString() {
		return e.getString() + "* ";
	}
	
	public KleeneStar(Expansion expansion) {
		e = expansion;
	}
}
