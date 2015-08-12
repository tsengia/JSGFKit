package ca.l5.expandingdev.jsgf;

public class AlternateWord implements Word {
	private Word first;
	private Word second;
	
	public String getString() {
		return first.getString() + " | " + second.getString();
	}
	
	public AlternateWord(Word f, Word s) {
		first = f;
		second = s;
	}
}
