package ca.l5.expandingdev.jsgf;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GrammarHeader {
    public String jsgfVersion = "1.0";
    public Locale locale;
    public Charset charset;

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

    public String getHeader() {
        return "#JSGF V" + jsgfVersion + " " + charset.name() + " " + locale.getLanguage() + ";\n";
    }
}