package net.lemonfactory.sudokusolver.type;

import java.awt.Color;
import net.lemonfactory.sudokusolver.type.GeneralSudokuTypeStructure.Builder;

/**
 *
 * @author Chungmin Lee
 */
public final class RowColumnTypeStructure implements SudokuTypeStructure {

    private final SudokuTypeStructure backend;

    private RowColumnTypeStructure(SudokuTypeStructure structure) {
        Builder builder = GeneralSudokuTypeStructure.getBuilder(structure);
        int size = structure.size();
        for (int i = 0; i < size; ++i) {
            builder.addCellGroup(range(i * size, i * size + size));  // rows
            builder.addCellGroup(range(i, size, size));              // columns
        }
        backend = builder.build();
    }

    public static RowColumnTypeStructure getInstance(int size) {
        return getInstance(
                GeneralSudokuTypeStructure.getBuilder(size).build());
    }

    public static RowColumnTypeStructure
    getInstance(SudokuTypeStructure structure) {
        return new RowColumnTypeStructure(structure);
    }

    private static int[] range(int start, int end) {
        return range(start, 1, end - start);
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
        return o instanceof RowColumnTypeStructure
                 && backend.equals(((RowColumnTypeStructure) o).backend);
    }

    @Override
    public int hashCode() {
        return backend.hashCode();
    }
}
