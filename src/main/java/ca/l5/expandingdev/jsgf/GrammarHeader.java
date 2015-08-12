package ca.l5.expandingdev.jsgf;

import java.nio.charset.Charset;
import java.util.Locale;



public class GrammarHeader {
	public final String jsgfVersion = "1.0";
	public Locale locale;
	public Charset charset;
	
	public String getHeader() {
		return "#JSGF V" + jsgfVersion + " " + charset.name() + " " + locale.getLanguage() + ";\n";
	}
	
	public GrammarHeader(Charset c, Locale l) {
		charset = c;
		locale = l;
	}
	
	public GrammarHeader(Charset c) {
		charset = c;
		locale = Locale.getDefault();
	}
	
	public GrammarHeader() {
		charset = Charset.defaultCharset();
		locale = Locale.getDefault();
	}
}
