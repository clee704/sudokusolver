package net.lemonfactory.sudokusolver.internal;

import static org.junit.Assert.*;
import org.junit.Test;

public class BitSetSimpleSetTest {

    @Test
    public void overall() {
        SimpleSet s = new BitSetSimpleSet(9);
        assertEquals(0, s.cardinality());
        assertEquals(9, s.capacity());
        s.add(0);
        s.add(8);
        assertTrue(s.contains(0));
        assertFalse(s.contains(1));
        assertFalse(s.contains(7));
        assertTrue(s.contains(8));
        assertEquals(2, s.cardinality());
        assertEquals(9, s.capacity());
        s.clear();
        assertEquals(0, s.cardinality());
        assertEquals(9, s.capacity());
        assertEquals(-1, s.next());
        assertEquals(-1, s.next());
        s.add(1);
        s.add(6);
        s.add(7);
        assertEquals(1, s.next());
        assertEquals(6, s.next());
        assertEquals(7, s.next());
        assertEquals(1, s.next());
        assertEquals(6, s.next());
        assertEquals(7, s.next());
        assertEquals(1, s.next());
        s.resetCursor();
        assertEquals(1, s.next());
        SimpleSet s2 = new BitSetSimpleSet(9);
        s2.add(0);
        s2.add(1);
        s2.add(5);
        s.addAll(s2);
        assertEquals(5, s.cardinality());
        assertEquals(9, s.capacity());
        s.complement();
        assertEquals(4, s.cardinality());
        assertEquals(9, s.capacity());
    }
}
