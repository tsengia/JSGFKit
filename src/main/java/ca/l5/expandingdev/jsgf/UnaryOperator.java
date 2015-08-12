package ca.l5.expandingdev.jsgf;

public class UnaryOperator implements Expansion {
	public static final int PLUS = 0;
	public static final int STAR = 1;
	private int t;
	private Expansion e;
	private String tag;
	
	public boolean isTag() {
		return (t == 2);
	}
	
	@Override
	public String getString() {
		String s = "" + e.getString();
		switch(t) {
		case UnaryOperator.PLUS:
			s = s.concat("+");
			break;
		case UnaryOperator.STAR:
			s = s.concat("*");
			break;
		case 2:
			s = s.concat(" {" + tag + "}");
			break;
		}
		return s;
	}
	
	public UnaryOperator(Expansion exp, String tagName) throws IllegalArgumentException {
		t = 2;
		tag = tagName;
		e = exp;
		if(e instanceof UnaryOperator) {
			UnaryOperator uo = (UnaryOperator) e;
			if(!uo.isTag()) { throw new IllegalArgumentException("There cannot be more than one Unary Operator per Expansion!"); }
		}
	}
	
	public UnaryOperator(Expansion exp, int type) throws IllegalArgumentException {
		t = type;
		e = exp;
		if(e instanceof UnaryOperator) {
			throw new IllegalArgumentException("There cannot be more than one Unary Operator per Expansion!");
		}
	}
}
