package ca.l5.expandingdev.jsgf;

public class PlusOperator implements Expansion {
	private Expansion e;
	
	public String getString() {
		return e.getString() + "+ ";
	}
	
	public PlusOperator(Expansion ex) {
		e = ex;
	}
}
