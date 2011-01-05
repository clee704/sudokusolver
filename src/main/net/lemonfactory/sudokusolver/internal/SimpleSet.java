package net.lemonfactory.sudokusolver.internal;

/**
 * <p>
 * Simple set with fixed capacity, integer elements, and rotational iterator
 * (see {@link #next()}).
 * </p>
 * <p>
 * {@code SimpleSet} is usually used to make a candidate set of a cell.
 * Thread-safety is implementation-dependent, but it is not recommended to
 * synchronize internally, since it may result in worse performance.
 * </p>
 *
 * @author Chungmin Lee
 */
public interface SimpleSet {

    /**
     * Adds the specified element to this set. No changes will occur if it is
     * already in this set.
     *
     * @param e element to be add to this set; must be zero or positive and
     *     must be less than the capacity of this set
     */
    public void add(int e);

    /**
     * Adds all of the elements in the specified {@code SimpleSet}.
     * If the capacity of the specified {@code SimpleSet} is larger than
     * this set, then the larger portion of the set will be ignored.
     *
     * @param s {@code SimpleSet} containing elements to be added to this set
     */
    public void addAll(SimpleSet s);

    /**
     * Removes the specified element from this set. No changes will occur if it
     * is not in this set.
     *
     * @param e element to be removed from this set; must be zero or positive
     *     and must be less than the capacity of this set
     */
    public void remove(int e);

    /**
     * Removes all elements in this set and resets the cursor.
     * After it returns, this set is in the same state as it is first created.
     */
    public void clear();

    /**
     * Complements this set.
     */
    public void complement();

    /**
     * Returns {@code true} if this set contains the specified element.
     *
     * @param e element whose presence in this set is to be tested
     * @return {@code true} if this set contains the specified element
     */
    public boolean contains(int e);

    /**
     * Returns the number of elements in this set.
     *
     * @return the number of elements in this set
     */
    public int cardinality();

    /**
     * Returns the maximum number of elements that this set can have.
     * This value is constant which is fixed at construction time of this set.
     *
     * @return the maximum number of elements that this set can have
     */
    public int capacity();

    /**
     * <p>
     * Returns an element in this set after the cursor if this set is not
     * empty, or -1 if this set is empty. If this set is not empty and this set
     * has no element after the cursor, then the first element of this set is
     * returned (rotation). After it returns, the cursor is set to the returned
     * element.
     * </p>
     * <p>
     * A cursor in this set is set to -1 when this set is created or after
     * {@link #resetCursor()} is called.
     * </p>
     *
     * @return an element after the cursor, or the first element if this set is
     *     not empty and this set has no element after the cursor, or -1 if
     *     this set is empty
     */
    public int next();

    /**
     * Decrements the cursor by 1, if it is not already negative.
     */
    public void withdrawCursor();

    /**
     * Sets the cursor to -1.
     */
    public void resetCursor();

    /**
     * Returns {@code true} if the object is not {@code null} and is a
     * {@code SimpleSet} object that has exactly the same elements
     * as this set and the same capacity as this set.
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are the same
     */
    public boolean equals(Object o);

    /**
     * Returns the hash code value for this set. While {@code SimpleSet}
     * interface adds no stipulations to the general contract for the
     * {@link Object#hashCode} method, programmers should take note that any
     * class that overrides the {@link Object#equals} method must also override
     * the {@link Object#hashCode} method in order to satisfy the general
     * contract for the {@link Object#hashCode} method.
     * In particular, {@code o1.equals(o2)} implies that
     * {@code o1.hashCode()==o2.hashCode()}.
     *
     * @return the hash code value for this set
     */
    public int hashCode();
}
