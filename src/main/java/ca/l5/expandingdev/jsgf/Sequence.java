package ca.l5.expandingdev.jsgf;

import java.util.*;

public class Sequence implements Expansion, Collection {
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

    public boolean addExpansion(Expansion e) {
        return exp.add(e);
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

    @Override
    public int size() {
        return exp.size();
    }

    @Override
    public boolean isEmpty() {
        return exp.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Expansion) {
            return exp.contains((Expansion) o);
        }
        return false;
    }

    @Override
    public Iterator<Expansion> iterator() {
        return exp.iterator();
    }

    @Override
    public Object[] toArray() {
        Object[] o = new Object[exp.size()];
        exp.toArray(o);
        return o;
    }

    @Override
    public boolean add(Object o) {
        if (o instanceof Expansion) {
            return this.addExpansion((Expansion) o);
        }
        else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Expansion) {
            return this.removeExpansion((Expansion) o);
        }
        else {
            return false;
        }
    }

    @Override
    public boolean addAll(Collection c) {
        for (Object o : c) {
            this.add(o);
        }
        return true;
    }

    @Override
    public void clear() {
        exp.clear();
    }

    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        for (Object o : c) {
            this.remove(o);
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object o : c) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }
}
