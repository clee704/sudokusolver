package net.lemonfactory.sudoku;

/**
 *
 * @author Chungmin Lee
 */
public class SymbolSet {

    private final String internalForm;

    public SymbolSet(String s) {
        for (int i = 0; i < s.length(); ++i)
            if (s.substring(i + 1).indexOf(s.charAt(i)) > -1)
                throw new IllegalArgumentException(
                        "duplicated character: " + s.charAt(i) + " in " + s);
        internalForm = s;
    }

    public char get(int index) {
        return internalForm.charAt(index);
    }

    public int indexOf(char symbol) {
        return internalForm.indexOf(symbol);
    }

    public int size() {
        return internalForm.length();
    }

    public boolean contains(char symbol) {
        return internalForm.indexOf(symbol) > -1;
    }

    @Override
    public String toString() {
        return internalForm;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SymbolSet
                 && internalForm.equals(((SymbolSet) o).internalForm);
    }

    @Override
    public int hashCode() {
        return internalForm.hashCode();
    }
}
