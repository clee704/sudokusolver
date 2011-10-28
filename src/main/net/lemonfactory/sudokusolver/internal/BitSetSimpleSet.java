package net.lemonfactory.sudokusolver.internal;

import java.util.BitSet;

/**
 * BitSet implementation of the {@link SimpleSet} interface.
 *
 * @author Choongmin Lee
 */
public final class BitSetSimpleSet implements SimpleSet {

    private final BitSet set;
    private final int capacity;
    private int cursor;

    public BitSetSimpleSet(int capacity) {
        if (capacity < 1)
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        this.set = new BitSet(capacity);
        this.capacity = capacity;
        this.cursor = -1;
    }

    @Override
    public void add(int e) {
        set.set(e);
    }

    @Override
    public void remove(int e) {
        set.clear(e);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public void complement() {
        set.flip(0, capacity);
    }

    @Override
    public void addAll(SimpleSet s) {
        if (s instanceof BitSetSimpleSet) {
            set.or(((BitSetSimpleSet) s).set);
        } else {
            int n = Math.min(capacity, s.capacity());
            for (int i = 0; i < n; ++i)
                if (s.contains(i))
                    set.set(i);
        }
    }

    @Override
    public boolean contains(int e) {
        return set.get(e);
    }

    @Override
    public int cardinality() {
        return set.cardinality();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int next() {
        cursor = set.nextSetBit(cursor + 1);
        if (cursor == -1)
            cursor = set.nextSetBit(0);
        return cursor;
    }

    @Override
    public void withdrawCursor() {
        if (cursor >= 0)
            --cursor;
    }

    @Override
    public void resetCursor() {
        cursor = -1;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleSet))
            return false;
        if (o instanceof BitSetSimpleSet) {
            BitSetSimpleSet s = (BitSetSimpleSet) o;
            return capacity == s.capacity && set.equals(s.set);
        } else {
            SimpleSet s = (SimpleSet) o;
            if (set.cardinality() != s.cardinality() || capacity != s.capacity())
                return false;
            for (int i = 0; i < capacity; ++i)
                if (set.get(i) != s.contains(i))
                    return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = 101;
        result = 71 * result + set.hashCode();
        result = 71 * result + capacity;
        return result;
    }
}
