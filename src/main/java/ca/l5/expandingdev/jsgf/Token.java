package ca.l5.expandingdev.jsgf;

import java.util.regex.Pattern;

public class Token implements Expansion {
    private String text;
    private Pattern pattern;

    public Token() {
        text = "";
    }

    public Token(String s) {
        if (s.startsWith("/") && s.endsWith("/")) {
            pattern = Pattern.compile(s.substring(1, s.length() - 1));
        }
        text = s;
    }

    public String getText() {
        return text;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean hasUnparsedChildren() {
        return false;
    }

    @Override
    public String getString() {
        return text;
    }
}
