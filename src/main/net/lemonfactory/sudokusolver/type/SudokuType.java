package net.lemonfactory.sudokusolver.type;

import java.util.regex.Pattern;

/**
 * <p>
 * Sudoku type consists of {@link SudokuTypeStructure}, symbols and a blank
 * character. {@link SudokuTypeStructure} represents a structure that is
 * independent of symbols or a blank character. A blank character is used to
 * represent a blank in the string form of a puzzle, instead of a space
 * character to increase the readability. Symbols must not contain a blank
 * character.
 * </p>
 * <p>
 * This class is immutable, and subclasses of this class also must be
 * implemented as immutable.
 * </p>
 *
 * @author Chungmin Lee
 * @see net.lemonfactory.sudokusolver.SudokuTypes
 * @see SudokuTypeStructure
 */
public class SudokuType {

    protected final SudokuTypeStructure structure;
    protected final SymbolSet symbols;
    protected final char blank;

    private final Pattern verifyingPattern;

    public SudokuType(SudokuTypeStructure structure,
                      SymbolSet symbols,
                      char blank) {
        if (symbols.size() != structure.size())
            throw new IllegalArgumentException(
                    "symbols.size() != structure.size(): " + symbols.size()
                     + ", " + structure.size());
        if (symbols.contains(blank))
            throw new IllegalArgumentException(
                    blank + " is restricted: " + symbols);
        this.structure = structure;
        this.symbols = symbols;
        this.blank = blank;
        this.verifyingPattern = makeVerifyingPattern();
    }

    /**
     * Returns the associated structure of this Sudoku type.
     *
     * @return the associated structure of this Sudoku type
     * @see SudokuTypeStructure
     */
    public SudokuTypeStructure getStructure() {
        return structure;
    }

    /**
     * Returns the associated symbols of this Sudoku type.
     *
     * @return the associated symbols of this Sudoku type
     */
    public SymbolSet getSymbols() {
        return symbols;
    }

    /**
     * Returns the blank character of this Sudoku type.
     *
     * @return the blank character of this Sudoku type
     */
    public char getBlank() {
        return blank;
    }

    public SudokuType derive(SymbolSet symbols, char blank) {
        if (symbols == null)
            symbols = this.symbols;
        if (blank == '\0')
            blank = this.blank;
        if (symbols.size() != structure.size())
            throw new IllegalArgumentException(
                    "symbols.size() != structure.size(): " + symbols.size()
                     + ", " + structure.size());
        if (symbols.contains(blank))
            throw new IllegalArgumentException(
                    blank + " is restricted: " + symbols);
        return new SudokuType(structure, symbols, blank);
    }

    /**
     * Returns {@code false} if the given puzzle is utterly invalid.
     * It only checks for the surface; even when it returns {@code true},
     * the given puzzle may be invalid.
     *
     * @param puzzle puzzle to be tested
     * @return {@code false} if the given puzzle is utterly invalid; otherwise
     *         {@code true}
     */
    public boolean isValidPuzzle(String puzzle) {
        // check length
        if (puzzle.length() != structure.getTotalCells())
            return false;

        // check characters
        if (verifyingPattern.matcher(puzzle).find())
            return false;

        // check positions
        for (int i = 0; i < structure.getTotalCells(); ++i) {
            if (puzzle.charAt(i) == blank)
                continue;
            for (int j : structure.getNeighborCells(i))
                if (puzzle.charAt(i) == puzzle.charAt(j))
                    return false;
        }
        return true;
    }

    /**
     * Returns {@code true} if this object is equal to the given object.
     * Two {@link SudokuType} will be equal to each other, if and only if
     * the structures, the symbols and the blank characters of the two are
     * equal to each counter part.
     *
     * @param o object with which to compare
     * @return {@code true} if it is equal to the given object
     */
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SudokuType))
            return false;
        SudokuType t = (SudokuType) o;
        return structure.equals(t.structure) && t.symbols.equals(symbols)
                 && blank == t.blank;
    }

    /**
     * Returns the hash code value for this Sudoku type. While
     * {@code SudokuType} interface adds no stipulations to the general
     * contract for the {@link Object#hashCode} method, programmers should
     * take note that any class that overrides the {@link Object#equals}
     * method must also override the {@link Object#hashCode} method in order
     * to satisfy the general contract for the {@link Object#hashCode} method.
     * In particular, {@code o1.equals(o2)} implies that
     * {@code o1.hashCode()==o2.hashCode()}.
     *
     * @return the hash code value for this Sudoku type
     */
    public int hashCode() {
        int result = 19;
        result = 29 * result + structure.hashCode();
        result = 29 * result + symbols.hashCode();
        result = 29 * result + (int) blank;
        return result;
    }

    private Pattern makeVerifyingPattern() {
        StringBuilder buf = new StringBuilder();
        buf.append("[^\\\\").append(blank).append(symbols).append(']');
        return Pattern.compile(buf.toString());
    }
}
