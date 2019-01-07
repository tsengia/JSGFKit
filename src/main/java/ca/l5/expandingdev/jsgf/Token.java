package ca.l5.expandingdev.jsgf;

public class Token implements Expansion {
    private String text;

    public Token() {
        text = "";
    }

    public Token(String s) {
        text = s;
    }

    public String getText() {
        return text;
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
