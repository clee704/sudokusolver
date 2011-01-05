package net.lemonfactory.sudoku;

import java.awt.Color;

/**
 *
 * @author Chungmin Lee
 */
public final class XTypeStructure implements SudokuTypeStructure {

    private final SudokuTypeStructure backend;

    private XTypeStructure(SudokuTypeStructure structure) {
        backend = GeneralSudokuTypeStructure.getBuilder(structure)
                .addCellGroup(
                    Color.decode("#CCCCFF"),
                    makeDiagonalCellGroup(structure.size(), 0))
                .addCellGroup(
                    Color.decode("#CCCCFF"),
                    makeDiagonalCellGroup(structure.size(), 1))
                .build();
    }

    public static XTypeStructure getInstance(SudokuTypeStructure structure) {
        return new XTypeStructure(structure);
    }

    private static int[] makeDiagonalCellGroup(int size, int direction) {
        if (direction == 0)
            return range(0, size + 1, size);
        else
            return range(size - 1, size - 1, size);
    }

    private static int[] range(int start, int step, int length) {
        int[] result = new int[length];
        for (int i = 0; i < length; ++i) {
            result[i] = start;
            start += step;
        }
        return result;
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
        return o instanceof XTypeStructure
                 && backend.equals(((XTypeStructure) o).backend);
    }

    @Override
    public int hashCode() {
        return backend.hashCode();
    }
}
