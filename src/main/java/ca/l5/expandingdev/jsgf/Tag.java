package ca.l5.expandingdev.jsgf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tag implements Expansion {

    private Expansion childExpansion;
    private List<String> contents;

    public Tag(Expansion e, String t) {
        childExpansion = e;
        contents = new ArrayList<>();
        contents.add(t);
    }

    public Tag(Expansion e) {
        childExpansion = e;
        contents = new ArrayList<>();
    }

    public Tag(Expansion e, String... tags) {
        childExpansion = e;
        contents = Arrays.asList(tags);
    }

    public Expansion getChildExpansion() {
        return childExpansion;
    }

    public void setChildExpansion(Expansion e) {
        childExpansion = e;
    }

    @Override
    public String getString() {
        String s = childExpansion.getString();

        //Check to see if we need to add a grouping to this
        if (childExpansion instanceof Sequence || childExpansion instanceof AlternativeSet) {
            StringBuilder sb = new StringBuilder(s);
            sb.insert(0, "(");
            sb.append(")");
            s = sb.toString();
        }

        for (String t : contents) {
            s += " {" + t + "}";
        }
        return s;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public boolean hasUnparsedChildren() {
        return childExpansion instanceof Grammar.UnparsedSection;
    }

    public String[] getTags() {
        String[] arr = new String[contents.size()];
        contents.toArray(arr);
        return arr;
    }

    public boolean removeTag(String s) {
        return contents.remove(s);
    }

    public void addTag(String s) {
        contents.add(s);
    }

    public void addTags(String... tags) {
        contents.addAll(Arrays.asList(tags));
    }
}
