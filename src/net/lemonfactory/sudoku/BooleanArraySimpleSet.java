package net.lemonfactory.sudoku;

/**
 * BooleanArray implementation of the {@link SimpleSet} interface.
 *
 * @author Chungmin Lee
 */
final class BooleanArraySimpleSet implements SimpleSet {

    private final boolean[] table;
    private final int capacity;
    private int cardinality;
    private int cursor;

    public BooleanArraySimpleSet(int capacity) {
        if (capacity < 1)
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        this.capacity = capacity;
        this.table = new boolean[this.capacity];
        this.cardinality = 0;
        this.cursor = -1;
    }

    public void add(int e) {
        if (!table[e]) {
            table[e] = true;
            ++cardinality;
        }
    }

    public void remove(int e) {
        if (table[e]) {
            table[e] = false;
            --cardinality;
        }
    }

    public void clear() {
        for (int i = 0; i < capacity; ++i)
            table[i] = false;
        cardinality = 0;
        cursor = -1;
    }

    public void complement() {
        for (int i = 0; i < capacity; ++i)
            table[i] = !table[i];
        cardinality = capacity - cardinality;
    }

    public void addAll(SimpleSet s) {
        for (int i = 0, n = Math.min(capacity, s.capacity()); i < n; ++i)
            if (!table[i] && s.contains(i)) {
                table[i] = true;
                ++cardinality;
            }
    }

    public boolean contains(int e) {
        return table[e];
    }

    public int cardinality() {
        return cardinality;
    }

    public int capacity() {
        return table.length;
    }

    public int next() {
        if (cardinality == 0)
            return -1;
        while (++cursor < capacity && !table[cursor])
            ;
        if (cursor == capacity) {
            cursor = -1;
            while (++cursor < capacity && !table[cursor])
                ;
        }
        return cursor;
    }

    public void withdrawCursor() {
        if (cursor >= 0)
            --cursor;
    }

    public void resetCursor() {
        cursor = -1;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SimpleSet))
            return false;
        SimpleSet s = (SimpleSet) o;
        if (!(capacity == s.capacity() && cardinality == s.cardinality()))
            return false;
        for (int i = 0; i < capacity; ++i)
            if (table[i] != s.contains(i))
                return false;
        return true;
    }

    public int hashCode() {
        int result = 53;
        result = 73 * result + table.hashCode();
        result+= 73 * result + capacity;
        return result;
    }
}
