package ca.l5.expandingdev.jsgf;

public class RuleReference implements Expansion {
    private String name;

    public RuleReference(String rr) {
        name = rr;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean hasUnparsedChildren() {
        return false;
    }

    public String getString() {
        String s = "<" + name + ">";
        return s;
    }
}
