package ca.l5.expandingdev.jsgf;

public class AlternativeSet implements Expansion {
	private Expansion[] expansions;
	private float[] weights = null;
	
	public String getString() {
		String s = "";
		if(weights == null) {
			for(int i = 0; i < expansions.length; i++) {
				s = s.concat(expansions[i].getString() + " | ");
			}
			s = s.substring(0, (s.length() - 3));
		}
		else {
			for(int i = 0; i < expansions.length; i++) {
				s = s.concat("/" + weights[i] + "/ " + expansions[i].getString() + " | ");
			}
			s = s.substring(0, (s.length() - 3));
		}
		return s;
	}

	public AlternativeSet(Expansion[] e, float[] weightArray) {
		expansions = e;
		weights = weightArray;
	}
	
	public AlternativeSet(Expansion... e) {
		expansions = e;
	}
}
