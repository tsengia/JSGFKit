package ca.l5.expandingdev.jsgf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sequence implements Expansion {
    private List<Expansion> exp;

    public Sequence(Expansion... e) {
        exp = Arrays.asList(e);
    }

    public Sequence() {
        exp = new ArrayList<>();
    }

    @Override
    public boolean hasChildren() {
        return exp.size() != 0;
    }

    @Override
    public boolean hasUnparsedChildren() {
        for (Expansion e : exp) {
            if (e instanceof Grammar.UnparsedSection) {
                return true;
            }
        }
        return false;
    }

    public void addExpansion(Expansion e) {
        exp.add(e);
    }

    public boolean removeExpansion(Expansion e) {
        return exp.remove(e);
    }

    @Override
    public String getString() {
        String s = "";
        for (Expansion e : exp) {
            s = s.concat(e.getString() + " ");
        }
        s = s.trim();
        return s;
    }

    // Checks to see if the sequence container is necessary or not
    public Expansion simplestForm() {
        if (exp.size() == 1) { // Has only one Expansion, so return that simpler expansion
            return exp.get(0);
        } else { // Cannot be simplified
            return this;
        }
    }
}
