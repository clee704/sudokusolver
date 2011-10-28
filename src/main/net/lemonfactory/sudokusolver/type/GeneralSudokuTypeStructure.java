package net.lemonfactory.sudokusolver.type;

import java.awt.Color;
import java.util.*;

/**
 *
 * @author Choongmin Lee
 */
public class GeneralSudokuTypeStructure implements SudokuTypeStructure {

    private static final Comparator<ColoredCellGroup> CCGCOMP;

    static {
        CCGCOMP = new Comparator<ColoredCellGroup>() {
            @Override
            public int compare(ColoredCellGroup o1, ColoredCellGroup o2) {
                int[] i1 = convert(o1.getCellGroup());
                int[] i2 = convert(o1.getCellGroup());
                for (int i = 0; i < i1.length && i < i2.length; ++i) {
                    int j = i1[i] - i2[i];
                    if (j != 0)
                        return j;
                }
                return i1.length - i2.length;
            }
        };
    }

    private final int size;
    private final int boxWidth;
    private final int boxHeight;
    private final int totalCells;

    private final int[][] cellGroups;          // index: cell group index
    private final int[][] cellGroupIndexes;    // index: cell
    private final int[][] neighborCellGroups;  // index: cell
    private final int[][] intersections;       // index: two cell group indexes
    private final int[][][] cellGroupDiffs;

    private final Color[] cellGroupColors;

    private GeneralSudokuTypeStructure(
            int size,
            int boxWidth,
            int boxHeight,
            List<ColoredCellGroup> coloredCellGroups) {
        this.size = size;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.totalCells = countCells(coloredCellGroups);

        // Simlify equals() and hashCode()
        Collections.sort(coloredCellGroups, CCGCOMP);
        this.cellGroups = getCellGroups(coloredCellGroups);
        this.cellGroupColors = getCellGroupColors(coloredCellGroups);

        this.cellGroupIndexes = evalCellGroupIndexes();
        this.neighborCellGroups = evalNeighborCellGroups();
        this.intersections = evalIntersections();
        this.cellGroupDiffs = evalCellGroupDiffs();
    }

    public static Builder getBuilder(int size) {
        return new Builder(size);
    }

    public static Builder getBuilder(SudokuTypeStructure s) {
        Builder builder = new Builder(s.size());
        for (int i = 0; i < s.getNumCellGroups(); ++i)
            builder.addCellGroup(s.getCellGroupColor(i), s.getCellGroup(i));
        builder.setBox(s.boxWidth(), s.boxHeight());
        return builder;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int boxHeight() {
        return boxHeight;
    }

    @Override
    public int boxWidth() {
        return boxWidth;
    }

    @Override
    public int getTotalCells() {
        return totalCells;
    }

    @Override
    public int getNumCellGroups() {
        return cellGroups.length;
    }

    @Override
    public int[] getCellGroupIndexes(int cell) {
        return cellGroupIndexes[cell];
    }

    @Override
    public int[] getCellGroup(int cellGroupIndex) {
        return cellGroups[cellGroupIndex];
    }

    @Override
    public int[] getCellGroupDiff(int cgIndex1, int cgIndex2) {
        return cellGroupDiffs[cgIndex1][cgIndex2];
    }

    @Override
    public int[] getNeighborCells(int cell) {
        return neighborCellGroups[cell];
    }

    @Override
    public int getNumIntersections(int cellGroupIndex1, int cellGroupIndex2) {
        return intersections[cellGroupIndex1][cellGroupIndex2];
    }

    @Override
    public Color getCellGroupColor(int cellGroupIndex) {
        return cellGroupColors[cellGroupIndex];
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GeneralSudokuTypeStructure) {
            GeneralSudokuTypeStructure s = (GeneralSudokuTypeStructure) o;
            return size == s.size && Arrays.deepEquals(cellGroups, s.cellGroups);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 23;
        result = 41 * result + size;
        result = 41 * result + Arrays.deepHashCode(cellGroups);
        return result;
    }

    private static int[] convert(TreeSet<Integer> from) {
        int[] to = new int[from.size()];
        int i = 0;
        for (Integer j : from)
            to[i++] = j.intValue();
        return to;
    }

    private int countCells(List<ColoredCellGroup> cellGroups) {
        Set<Integer> set = new HashSet<Integer>();
        for (ColoredCellGroup cellGroup : cellGroups)
            set.addAll(cellGroup.getCellGroup());
        return set.size();
    }

    private int[][] getCellGroups(List<ColoredCellGroup> cellGroupList) {
        int[][] cellGroups = new int[cellGroupList.size()][size];
        for (int i = 0; i < cellGroupList.size(); ++i)
            cellGroups[i] = convert(cellGroupList.get(i).getCellGroup());
        return cellGroups;
    }

    private Color[] getCellGroupColors(List<ColoredCellGroup> cellGroupList) {
        Color[] colors = new Color[cellGroupList.size()];
        for (int i = 0; i < cellGroupList.size(); ++i)
            colors[i] = cellGroupList.get(i).getColor();
        return colors;
    }

    private int[][] evalCellGroupIndexes() {
        Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
        for (int cgIndex = 0; cgIndex < cellGroups.length; ++cgIndex)
            for (int cell: cellGroups[cgIndex])
                if (map.containsKey(cell)) {
                    map.get(cell).add(cgIndex);
                } else {
                    List<Integer> cellGroup = new ArrayList<Integer>();
                    cellGroup.add(cgIndex);
                    map.put(cell, cellGroup);
                }
        int[][] cellGroupIndexes = new int[totalCells][];
        for (int cell = 0; cell < totalCells; ++cell)
            cellGroupIndexes[cell] = toArray(map.get(cell));
        return cellGroupIndexes;
    }

    private int[] toArray(Collection<Integer> collection) {
        if (collection == null)
            return new int[0];
        int[] array = new int[collection.size()];
        int i = 0;
        for (int e : collection)
            array[i++] = e;
        return array;
    }

    private int[][] evalNeighborCellGroups() {
        int[][] neighborCellGroups = new int[totalCells][];
        TreeSet<Integer> set = new TreeSet<Integer>();
        for (int cell = 0; cell < totalCells; ++cell) {
            set.clear();
            for (int cellGroupIndex : cellGroupIndexes[cell])
                for (int otherCell : cellGroups[cellGroupIndex])
                    set.add(otherCell);
            set.remove(cell);
            neighborCellGroups[cell] = toArray(set);
        }
        return neighborCellGroups;
    }

    private int[][] evalIntersections() {
        int[][] intersections = new int[cellGroups.length][cellGroups.length];
        for (int cgIndex1 = 0; cgIndex1 < cellGroups.length; ++cgIndex1)
            for (int cgIndex2 = 0; cgIndex2 < cellGroups.length; ++cgIndex2)
                intersections[cgIndex1][cgIndex2] = intersect(
                        cellGroups[cgIndex1],
                        cellGroups[cgIndex2]);
        return intersections;
    }

    // Precondition: a and b are sorted in ascending order
    private int intersect(int[] a, int[] b) {
        int result = 0;
        int i = 0;
        int j = 0;
        while (i < a.length && j < b.length) {
            if (a[i] == b[j]) {
                ++result;
                ++i;
                ++j;
            } else if (a[i] < b[j]) {
                ++i;
            } else {
                ++j;
            }
        }
        return result;
    }

    private int[][][] evalCellGroupDiffs() {
        int[][][] cellGroupDiffs = new int[cellGroups.length][cellGroups.length][];
        for (int cgIndex1 = 0; cgIndex1 < cellGroups.length; ++cgIndex1)
            for (int cgIndex2 = 0; cgIndex2 < cellGroups.length; ++cgIndex2)
                cellGroupDiffs[cgIndex1][cgIndex2] = diff(cellGroups[cgIndex1],
                        cellGroups[cgIndex2]);
        return cellGroupDiffs;
    }

    private int[] diff(int[] a, int[] b) {
        int[] diff = new int[a.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] < b[j]) {
                diff[k++] = a[i++];
            } else if (a[i] > b[j]) {
                ++j;
            } else {
                ++i;
                ++j;
            }
        }
        while (i < a.length)
            diff[k++] = a[i++];
        return Arrays.copyOf(diff, k);
    }

    public static class Builder {

        private final int size;
        private final List<ColoredCellGroup> cellGroups;
        private final Set<Integer> allCells;

        private int bw;
        private int bh;

        private Builder(int size) {
            if (size < 1)
                throw new IllegalArgumentException("size < 1: " + size);
            this.size = size;
            this.cellGroups = new ArrayList<ColoredCellGroup>();
            this.allCells = new HashSet<Integer>();
        }

        public Builder setBox(int boxWidth, int boxHeight) {
            this.bw = boxWidth;
            this.bh = boxHeight;
            return this;
        }

        public Builder addCellGroup(int... cellGroup) {
            return addCellGroup(null, cellGroup);
        }

        public Builder addCellGroup(Color cellGroupColor, int... cellGroup) {
            TreeSet<Integer> s = new TreeSet<Integer>();
            for (int c : cellGroup)
                s.add(c);
            if (s.size() != size)
                throw new IllegalArgumentException(
                        "(cellGroup as Set).size() != size: "
                         + "(cellGroup as Set).size() = " + s.size()
                         + ", size = " + size);
            cellGroups.add(new ColoredCellGroup(s, cellGroupColor));
            allCells.addAll(s);
            return this;
        }

        public GeneralSudokuTypeStructure build() {
            for (int i = 0; i < allCells.size(); ++i)
                if (!allCells.contains(i))
                    throw new IllegalStateException("cell hole at " + i);
            return new GeneralSudokuTypeStructure(size, bw, bh, cellGroups);
        }
    }

    private static class ColoredCellGroup {

        private final TreeSet<Integer> cellGroup;
        private final Color color;

        public ColoredCellGroup(TreeSet<Integer> cellGroup, Color color) {
            this.cellGroup = cellGroup;
            this.color = color;
        }

        public TreeSet<Integer> getCellGroup() {
            return cellGroup;
        }

        public Color getColor() {
            return color;
        }
    }
}
