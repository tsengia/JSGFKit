package ca.l5.expandingdev.jsgf;

public class AlternateSimpleWordList implements Word {
	private String[] list; 
	
	public String getString() {
		String s = "";
		if(list.length > 0) {
			for(int i = 0; i < list.length; i++) {
				s = s.concat(list[i] + " | ");
			}
				s = s.substring(0, (s.length()-3));
			}
		return s;
	}
	
	public AlternateSimpleWordList(String[] l) {
		list = l;
	}
}
