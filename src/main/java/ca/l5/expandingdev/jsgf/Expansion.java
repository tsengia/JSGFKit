package ca.l5.expandingdev.jsgf;

public interface Expansion {
    String getString();

    boolean hasChildren();

    boolean hasUnparsedChildren();

   // MatchInfo matchesString(Grammar g, String s);
}
