package net.lemonfactory.sudokusolver;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.lemonfactory.sudokusolver.type.*;

/**
 * This class consists of frequently used Sudoku types and related methods.
 *
 * @author Choongmin Lee
 * @see SudokuType
 */
public class SudokuTypes {

    /** Regular Sudoku type with 3×3 boxes and symbols of 1-9. */
    public static final SudokuType BOX_9 = new SudokuType(
            BoxTypeStructure.getInstance(3, 3),
            new SymbolSet("123456789"),
            '.');

    /** Regular Sudoku type with 4×4 boxes and symbols of A-P. */
    public static final SudokuType BOX_16 = new SudokuType(
            BoxTypeStructure.getInstance(4, 4),
            new SymbolSet("ABCDEFGHIJKLMNOP"),
            '.');

    /** Regular Sudoku type with 5×5 boxes and symbols of A-Y. */
    public static final SudokuType BOX_25 = new SudokuType(
            BoxTypeStructure.getInstance(5, 5),
            new SymbolSet("ABCDEFGHIJKLMNOPQRSTUVWXY"),
            '.');

    /** Specific Jigsaw Sudoku type with 9×9 cells and symbols of 1-9. */
    public static final SudokuType JIGSAW_9 = new SudokuType(
            JigsawTypeStructure.fromEncodedString(
                9,
                "111233333111222333144442223114555522444456666775555688977766668"
                 + "999777888999997888",
                '.'),
            new SymbolSet("123456789"),
            '.');

    public static final SudokuType X_9 = new SudokuType(
            XTypeStructure.getInstance(BoxTypeStructure.getInstance(3, 3)),
            new SymbolSet("123456789"),
            '.');

    public static final SudokuType HYPER_9 = new SudokuType(
            GeneralSudokuTypeStructure.getBuilder(BoxTypeStructure.getInstance(3, 3))
                .addCellGroup(
                    Color.decode("#CCCCFF"),
                    10, 11, 12, 19, 20, 21, 28, 29, 30)
                .addCellGroup(
                    Color.decode("#CCCCFF"),
                    14, 15, 16, 23, 24, 25, 32, 33, 34)
                .addCellGroup(
                    Color.decode("#CCCCFF"),
                    46, 47, 48, 55, 56, 57, 64, 65, 66)
                .addCellGroup(
                    Color.decode("#CCCCFF"),
                    50, 51, 52, 59, 60, 61, 68, 69, 70)
                .build(),
            new SymbolSet("123456789"),
            '.');

    public static final List<SudokuType> PRESET_TYPES =
            Collections.unmodifiableList(Arrays.asList(
                BOX_9,
                BOX_16,
                BOX_25,
                JIGSAW_9,
                X_9,
                HYPER_9));

    /**
     * Utility class.
     */
    private SudokuTypes() {}
}
