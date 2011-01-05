package net.lemonfactory.sudoku;

/**
 * Binary min-heap implementation of {@link SimpleMinPriorityQueue}.
 *
 * @author Chungmin Lee
 */
class BinaryMinHeap implements SimpleMinPriorityQueue {

    private static final int ELEMENT = 0;
    private static final int PRIORITY = 1;

    private final int[][] entries;
    private final int[] elements;
    private final int[] indexes;

    private final int capacity;
    private int size;

    public BinaryMinHeap(int capacity) {
        this.entries = new int[capacity << 1][2];
        this.elements = new int[capacity];
        this.indexes = new int[capacity];
        this.capacity = capacity;
        this.size = 0;
        for (int i = 0; i < capacity; ++i) {
            elements[i] = -1;
            indexes[i] = -1;
        }
        for (int i = 0; i < capacity << 1; ++i)
            entries[i][PRIORITY] = Integer.MAX_VALUE;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(int element) {
        return indexes[element] != -1;
    }

    @Override
    public void push(int element, int priority) {
        if (size == capacity)
            throw new IllegalStateException("queue is full");
        if (indexes[element] != -1)
            throw new IllegalArgumentException("element already exists");
        elements[size] = element;
        indexes[element] = size;
        entries[size][ELEMENT] = element;
        entries[size][PRIORITY] = priority;
        ++size;
        siftUp(size - 1);
    }

    @Override
    public int pop() {
        if (size == 0)
            throw new IllegalStateException("queue is empty");
        final int e = entries[0][ELEMENT];
        entries[0][PRIORITY] = Integer.MAX_VALUE;
        elements[indexes[e]] = -1;
        indexes[e] = -1;
        --size;
        if (size != 0) {
            elements[0] = entries[size][ELEMENT];
            indexes[entries[size][ELEMENT]] = 0;
            entries[0][ELEMENT] = entries[size][ELEMENT];
            entries[0][PRIORITY] = entries[size][PRIORITY];
            entries[size][PRIORITY] = Integer.MAX_VALUE;
            siftDown(0);
        }
        return e;
    }

    @Override
    public int peek() {
        if (size == 0)
            throw new IllegalStateException("empty queue");
        return entries[0][ELEMENT];
    }

    @Override
    public void remove(int element) {
        final int i = indexes[element];
        entries[i][PRIORITY] = Integer.MAX_VALUE;
        elements[indexes[element]] = -1;
        indexes[element] = -1;
        --size;
        if (size != 0 && size != i) {
            elements[i] = entries[size][ELEMENT];
            indexes[entries[size][ELEMENT]] = i;
            entries[i][ELEMENT] = entries[size][ELEMENT];
            entries[i][PRIORITY] = entries[size][PRIORITY];
            entries[size][PRIORITY] = Integer.MAX_VALUE;
            siftDown(i);
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < capacity; ++i) {
            elements[i] = -1;
            indexes[i] = -1;
        }
        for (int i = 0; i < capacity << 1; ++i)
            entries[i][PRIORITY] = Integer.MAX_VALUE;
        size = 0;
    }

    @Override
    public int getPriority(int element) {
        return entries[indexes[element]][PRIORITY];
    }

    @Override
    public void updatePriority(int element, int priority) {
        final int i = indexes[element];
        final int oldp = entries[i][PRIORITY];
        entries[i][PRIORITY] = priority;
        if (oldp > priority)
            siftUp(i);
        else
            siftDown(i);
    }

    @Override
    public int[] getElements() {
        if (size < capacity)
            elements[size] = -1;
        return elements;
    }

    private void siftUp(int i) {
        final int e = entries[i][ELEMENT];
        final int p = entries[i][PRIORITY];
        int parent = i >> 1;
        int pe = entries[parent][ELEMENT];
        int pp = entries[parent][PRIORITY];
        while (i != 0 && (pp > p || pp == p && pe > e)) {
            elements[i] = pe;
            indexes[pe] = i;
            entries[i][ELEMENT] = pe;
            entries[i][PRIORITY] = pp;
            i = parent;
            parent = i >> 1;
            pe = entries[parent][ELEMENT];
            pp = entries[parent][PRIORITY];
        }
        elements[i] = e;
        indexes[e] = i;
        entries[i][ELEMENT] = e;
        entries[i][PRIORITY] = p;
    }

    private void siftDown(int i) {
        final int e = entries[i][ELEMENT];
        final int p = entries[i][PRIORITY];
        int lchild = (i << 1) + 1;
        int rchild = (i << 1) + 2;
        int mchild;
        if (entries[lchild][PRIORITY] > entries[rchild][PRIORITY])
            mchild = rchild;
        else
            mchild = lchild;
        int ce = entries[mchild][ELEMENT];
        int cp = entries[mchild][PRIORITY];
        while (mchild < size && (cp < p || cp == p && ce < e)) {
            elements[i] = ce;
            indexes[ce] = i;
            entries[i][ELEMENT] = ce;
            entries[i][PRIORITY] = cp;
            i = mchild;
            lchild = (i << 1) + 1;
            rchild = (i << 1) + 2;
            if (entries[lchild][PRIORITY] > entries[rchild][PRIORITY])
                mchild = rchild;
            else
                mchild = lchild;
            ce = entries[mchild][ELEMENT];
            cp = entries[mchild][PRIORITY];
        }
        elements[i] = e;
        indexes[e] = i;
        entries[i][ELEMENT] = e;
        entries[i][PRIORITY] = p;
    }
}
