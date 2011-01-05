package net.lemonfactory.sudoku;

/**
 * Integer-based minimum priority queue.
 *
 * @author Chungmin Lee
 */
interface SimpleMinPriorityQueue {

    int capacity();
    int size();
    boolean isEmpty();

    /**
     * Returns {@code true} if this queue contains the specified element.
     * It may throw {@link ArrayIndexOutOfBoundsException} if
     * {@code element &gt;= this.capacity()}.
     * @param element element whose presence in this queue is to be tested
     * @return {@code true} if this queue contains the specified element
     */
    boolean contains(int element);

    /**
     * Adds the specified element into this queue with the specified priority.
     * It will throw {@link IllegalArgumentException} if this queue already
     * contains the element. It may throw {@link IllegalStateException}
     * if this queue is full, that is, {@code this.size() == this.capacity()},
     * depending on the implementation.
     *
     * @param element element to be added into this queue
     * @param priority priority to be used to rank the element
     */
    void push(int element, int priority);

    /**
     * Returns the element in this queue with the least priority. The returned
     * element will be removed from this queue. It will throw
     * {@link IllegalStateException} if this queue is empty.
     *
     * @return the element in this queue with the least priority
     */
    int pop();

    /**
     * Returns the element in this queue with the least priority. Unlike
     * {@link #pop()}, it maintains the returned element in this queue.
     * It will throw {@link IllegalStateException} if this queue is empty.
     *
     * @return the element in this queue with the least priority
     */
    int peek();

    /**
     * Removes the specified element from this queue. It may throw
     * {@link ArrayIndexOutOfBoundsException} if this queue does not contain
     * the element or {@code element &gt;= this.capacity()}.
     *
     * @param element element to be removed from this queue
     */
    void remove(int element);

    void clear();

    /**
     * Returns the priority of the specified element. It may throw
     * {@link ArrayIndexOutOfBoundsException} if this queue does not contain
     * the element or {@code element &gt;= this.capacity()}; otherwise the
     * returned value is undefined.
     *
     * @param element element whose priority is to be returned
     * @return the priority of the element
     */
    int getPriority(int element);

    /**
     * Updates the priority of the specified element. It may throw
     * {@link ArrayIndexOutOfBoundsException} if this queue does not contain
     * the element or {@code element &gt;= this.capacity()}.
     *
     * @param element element whose priority is to be updated
     * @param priority priority of the element
     */
    void updatePriority(int element, int priority);

    /**
     * Returns an array containing all of the elements in this queue. It may
     * return an array whose length is larger than the size of this queue.
     * In such cases, {@code returnedArray[this.size()] == -1} is always
     * guaranteed. It may also return the same array object with different
     * elements. If it is the case, the implementation must not rely on the
     * value of the array.
     *
     * @return an array containing all of the elements in this queue
     */
    int[] getElements();
}
