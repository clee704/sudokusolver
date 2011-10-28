package net.lemonfactory.sudokusolver.internal;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Stack implementation with more memory, less time.
 * Thread-safety is not guaranteed.
 *
 * @author Choongmin Lee
 */
public final class OperationStack {

    private static final int WIDTH = 4;

    private int[][] items;
    private int size;

    public OperationStack(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException(
                    "initialCapacity < 0: " + initialCapacity);
        items = new int[initialCapacity][WIDTH];
        size = 0;
    }

    public void push(int opCode) {
        ensureCapacity();
        items[size][0] = opCode;
        ++size;
    }

    public void push(int opCode, int arg1) {
        ensureCapacity();
        items[size][0] = opCode;
        items[size][1] = arg1;
        ++size;
    }

    public void push(int opCode, int arg1, int arg2) {
        ensureCapacity();
        items[size][0] = opCode;
        items[size][1] = arg1;
        items[size][2] = arg2;
        ++size;
    }

    public void push(int opCode, int arg1, int arg2, int arg3) {
        ensureCapacity();
        items[size][0] = opCode;
        items[size][1] = arg1;
        items[size][2] = arg2;
        items[size][3] = arg3;
        ++size;
    }

    public int[] pop() {
        if (size == 0)
            throw new EmptyStackException();
        return items[--size];
    }

    public int[] peek() {
        if (size == 0)
            throw new EmptyStackException();
        return items[size - 1];
    }

    public void clear() {
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (size == items.length) {
            items = Arrays.copyOf(items, items.length << 1);
            for (int i = 0; i < items.length; ++i)
                if (items[i] == null)
                    items[i] = new int[WIDTH];
        }
    }
}
