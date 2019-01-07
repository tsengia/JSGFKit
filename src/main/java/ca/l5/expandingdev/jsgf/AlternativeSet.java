package ca.l5.expandingdev.jsgf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlternativeSet implements Expansion {
    private List<Expansion> expansions;
    private float[] weights = null;

    public AlternativeSet(Expansion[] e, float[] weightArray) {
        expansions = Arrays.asList(e);
        weights = weightArray;
    }

    public AlternativeSet(Expansion... e) {
        expansions = Arrays.asList(e);
    }

    public AlternativeSet() {
        expansions = new ArrayList<>();
    }

    public List<Expansion> getChildExpansions() {
        return expansions;
    }

    @Override
    public boolean hasChildren() {
        return expansions.size() != 0;
    }

    @Override
    public boolean hasUnparsedChildren() {
        for (Expansion e : expansions) {
            if (e instanceof Grammar.UnparsedSection) {
                return true;
            }
        }
        return false;
    }

    public void addExpansion(Expansion e) {
        expansions.add(e);
    }

    public String getString() {
        String s = "";
        if (weights == null) {
            for (int i = 0; i < expansions.size(); i++) {
                s = s.concat(expansions.get(i).getString() + " | ");
            }
            s = s.substring(0, (s.length() - 3));
        } else {
            for (int i = 0; i < expansions.size(); i++) {
                s = s.concat("/" + weights[i] + "/ " + expansions.get(i).getString() + " | ");
            }
            s = s.substring(0, (s.length() - 3));
        }

        return s;
    }

}
