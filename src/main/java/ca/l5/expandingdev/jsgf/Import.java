package ca.l5.expandingdev.jsgf;

public class Import {
    private String importName;

    public Import(String name) {
        importName = name;
    }

    public String getString() {
        return "import <" + importName + ">;";
    }
}
