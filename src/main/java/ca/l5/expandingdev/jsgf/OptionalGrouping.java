package ca.l5.expandingdev.jsgf;

public class OptionalGrouping implements Expansion {
	private Expansion[] exp;
		
	public String getString() {
		String s = "[";
		for(Expansion e : exp) {
			s = s.concat(e.getString());
		}
		s = s.concat("]");
		return s;
	}
	
	public OptionalGrouping(Expansion... e) {
		exp = e;
	}
}
