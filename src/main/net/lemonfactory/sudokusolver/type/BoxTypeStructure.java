package net.lemonfactory.sudokusolver.type;

import java.awt.Color;
import net.lemonfactory.sudokusolver.type.GeneralSudokuTypeStructure.Builder;

/**
 *
 * @author Chungmin Lee
 */
public final class BoxTypeStructure implements SudokuTypeStructure {

    private final SudokuTypeStructure backend;

    private BoxTypeStructure(
            SudokuTypeStructure structure, int boxWidth, int boxHeight) {
        int size = structure.size();
        if (size != boxWidth * boxHeight)
            throw new IllegalArgumentException(
                    "structure.size() != boxWidth * boxHeight: " + size
                     + " != " + boxWidth * boxHeight);
        Builder builder = GeneralSudokuTypeStructure.getBuilder(structure);
        for (int i = 0; i < boxWidth * boxHeight; ++i)
            builder.addCellGroup(makeBoxCellGroup(i, boxWidth, boxHeight));
        builder.setBox(boxWidth, boxHeight);
        backend = builder.build();
    }

    public static BoxTypeStructure getInstance(int boxWidth, int boxHeight) {
        return getInstance(
                RowColumnTypeStructure.getInstance(boxWidth * boxHeight),
                boxWidth,
                boxHeight);
    }

    public static BoxTypeStructure getInstance(
            SudokuTypeStructure structure,
            int boxWidth,
            int boxHeight) {
        return new BoxTypeStructure(structure, boxWidth, boxHeight);
    }

    private static int[] makeBoxCellGroup(
            int order, int boxWidth, int boxHeight) {
        int size = boxWidth * boxHeight;
        int[] boxToCells = new int[size];
        int rb = order / boxHeight * boxHeight;
        int re = rb + boxHeight;
        int cb = order % boxHeight * boxWidth;
        int ce = cb + boxWidth;
        int i = 0;
        for (int r = rb; r < re; ++r)
            for (int c = cb; c < ce; ++c)
                boxToCells[i++] = r * size + c;
        return boxToCells;
    }

    @Override
    public int size() {
        return backend.size();
    }

    @Override
    public int boxWidth() {
        return backend.boxWidth();
    }

    @Override
    public int boxHeight() {
        return backend.boxHeight();
    }

    @Override
    public int getTotalCells() {
        return backend.getTotalCells();
    }

    @Override
    public int getNumCellGroups() {
        return backend.getNumCellGroups();
    }

    @Override
    public int[] getCellGroupIndexes(int cell) {
        return backend.getCellGroupIndexes(cell);
    }

    @Override
    public int[] getCellGroup(int cellGroupIndex) {
        return backend.getCellGroup(cellGroupIndex);
    }

    @Override
    public int[] getCellGroupDiff(int cgIndex1, int cgIndex2) {
        return backend.getCellGroupDiff(cgIndex1, cgIndex2);
    }

    @Override
    public int[] getNeighborCells(int cell) {
        return backend.getNeighborCells(cell);
    }

    @Override
    public int getNumIntersections(int cellGroupIndex1, int cellGroupIndex2) {
        return backend.getNumIntersections(cellGroupIndex1, cellGroupIndex2);
    }

    @Override
    public Color getCellGroupColor(int cellGroupIndex) {
        return backend.getCellGroupColor(cellGroupIndex);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BoxTypeStructure
                 && backend.equals(((BoxTypeStructure) o).backend);
    }

    @Override
    public int hashCode() {
        return backend.hashCode();
    }
}
