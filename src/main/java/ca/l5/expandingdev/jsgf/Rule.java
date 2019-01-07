package ca.l5.expandingdev.jsgf;

public class Rule {
    public String name;
    public Expansion expansion;
    public boolean isvisible = true;

    public Rule(String n, boolean visible, Expansion... exp) {
        name = n;
        if (exp.length > 1) {
            expansion = new Sequence(exp);
        } else {
            expansion = exp[0];
        }
        isvisible = visible;
    }

    public Rule(String n, Expansion... exp) {
        name = n;
        if (exp.length > 1) {
            expansion = new Sequence(exp);
        } else {
            expansion = exp[0];
        }
    }

    public String getRuleString() {
        String s = "";
        s = expansion.getString();

        if (isvisible) {
            return "public <" + name + "> = " + s + ";";
        } else {
            return "<" + name + "> = " + s + ";";
        }
    }

    public Expansion getChildExpansion() {
        return expansion;
    }

    public void setPrivate() {
        isvisible = false;
    }

    public void setPublic() {
        isvisible = true;
    }
}
