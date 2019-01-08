package ca.l5.expandingdev.jsgf;

public class RuleReference implements Expansion {
    private String ruleName;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public RuleReference(String rr) {
        ruleName = rr;
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
        String s = "<" + ruleName + ">";
        return s;
    }
}
