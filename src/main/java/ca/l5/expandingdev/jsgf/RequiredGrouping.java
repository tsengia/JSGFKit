package ca.l5.expandingdev.jsgf;

public class RequiredGrouping implements Expansion {
	private Expansion[] exp;
	
	public String getString() {
		String s = "(";
		for(Expansion e : exp) {
			s = s.concat(e.getString());
		}
		return s.concat(")");
	}
	
	public RequiredGrouping(Expansion... rw) {
		exp = rw;
	}
}
