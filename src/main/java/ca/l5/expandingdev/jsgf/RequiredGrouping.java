package ca.l5.expandingdev.jsgf;

public class RequiredGrouping implements Expansion {
    private Expansion exp;

    public RequiredGrouping(Expansion... rw) {
        Sequence s = new Sequence(rw);
        exp = s.simplestForm();
    }

    public Expansion getChildExpansion() {
        return exp;
    }

    public void setChildExpansion(Expansion e) {
        exp = e;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public boolean hasUnparsedChildren() {
        return exp instanceof Grammar.UnparsedSection;
    }

    public String getString() {
        String s = "(";
        s = s.concat(exp.getString());
        s = s.concat(")");
        return s;
    }
}
