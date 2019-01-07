package ca.l5.expandingdev.jsgf;

public class KleeneStar implements Expansion {
    private Expansion e;

    public KleeneStar(Expansion expansion) {
        e = expansion;
    }

    public Expansion getChildExpansion() {
        return e;
    }

    public void setChildExpansion(Expansion x) {
        e = x;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public boolean hasUnparsedChildren() {
        return e instanceof Grammar.UnparsedSection;
    }

    public String getString() {
        return e.getString() + "*";
    }
}
