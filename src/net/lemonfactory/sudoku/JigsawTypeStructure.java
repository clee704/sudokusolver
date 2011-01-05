package net.lemonfactory.sudoku;

import java.awt.Color;
import java.util.*;
import net.lemonfactory.sudoku.GeneralSudokuTypeStructure.Builder;

public class JigsawTypeStructure implements SudokuTypeStructure {

    private final SudokuTypeStructure backend;

    private JigsawTypeStructure(SudokuTypeStructure structure) {
        backend = structure;
    }

    public static JigsawTypeStructure fromEncodedString(
            int size, String encodedString, char excludeChar) {
        return fromEncodedString(
                RowColumnTypeStructure.getInstance(size),
                encodedString,
                excludeChar);
    }

    public static JigsawTypeStructure fromEncodedString(
            SudokuTypeStructure structure,
            String encodedString,
            char excludeChar) {
        int size = structure.size();
        Map<Character, List<Integer>> map;
        map = new LinkedHashMap<Character, List<Integer>>();
        for (int i = 0; i < encodedString.length(); ++i) {
            char c = encodedString.charAt(i);
            if (c == excludeChar)
                continue;
            if (!map.containsKey(c))
                map.put(c, new ArrayList<Integer>());
            map.get(c).add(i);
        }
        Builder builder = GeneralSudokuTypeStructure.getBuilder(structure);
        int i = 0;
        for (char c : map.keySet()) {
            List<Integer> cellGroup = map.get(c);
            if (cellGroup.size() != size)
                throw new IllegalArgumentException(
                        "Group " + c + " has " + cellGroup.size()
                         + " cells, but " + size + " cells are required.");
            builder.addCellGroup(
                    Color.getHSBColor((float) i++ / map.size(), 0.5f, 1.0f),
                    toArray(cellGroup));
        }
        return new JigsawTypeStructure(builder.build());
    }

    private static int[] toArray(List<Integer> lst) {
        int[] a = new int[lst.size()];
        int i = 0;
        for (int e : lst)
            a[i++] = e;
        return a;
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
        return o instanceof JigsawTypeStructure
                 && backend.equals(((JigsawTypeStructure) o).backend);
    }

    @Override
    public int hashCode() {
        return backend.hashCode();
    }
}
