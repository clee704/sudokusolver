package net.lemonfactory.sudokusolver.internal;

import static org.junit.Assert.*;
import org.junit.Test;

public class BinaryMinHeapTest {

    @Test
    public void testOverall() {
        SimpleMinPriorityQueue h = new BinaryMinHeap(125);
        assertEquals(0, h.size());
        assertEquals(125, h.capacity());
        h.push(17, 7);
        assertEquals(17, h.pop());
        h.push(17, 11);
        h.push(25, 17);
        h.push(19, 9);
        assertEquals(3, h.size());
        assertEquals(19, h.peek());
        assertEquals(19, h.pop());
        assertEquals(17, h.pop());
        assertEquals(25, h.pop());
        h.push(18, 10);
        h.push(19, 9);
        h.push(20, 22);
        h.push(25, 13);
        h.push(26, 13);
        h.push(5, 5);
        h.push(6, 25);
        h.push(7, 24);
        h.push(99, 9);
        h.push(33, 9);
        h.push(45, 1);
        assertEquals(45, h.peek());
        h.updatePriority(33, -1);
        assertEquals(33, h.pop());
        assertEquals(45, h.pop());
        h.updatePriority(5, 30);
        h.remove(19);
        assertEquals(99, h.pop());
        h.remove(25);
        assertEquals(18, h.pop());
        assertEquals(5, h.size());
        h.updatePriority(26, -5);
        assertEquals(26, h.pop());
        assertEquals(20, h.pop());
        assertEquals(7, h.pop());
        assertEquals(6, h.pop());
        assertEquals(5, h.pop());
        assertTrue(h.isEmpty());
    }

    @Test
    public void testPushOverflow() {
        final int c = 8;
        SimpleMinPriorityQueue h = new BinaryMinHeap(c);
        int i = 0;
        try {
            for (i = 0; i < c + 1; ++i)
                h.push(i, 0);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(c, i);
        }
    }

    @Test
    public void testPopUnderflow() {
        final int c = 9;
        SimpleMinPriorityQueue h = new BinaryMinHeap(c);
        for (int i = 0; i < c; ++i)
            h.push(i, 0);
        int i = 0;
        try {
            for (i = 0; i < c + 1; ++i)
                h.pop();
            fail();
        } catch (IllegalStateException e) {
            assertEquals(c, i);
        }
    }

    @Test
    public void testPeekUnderflow() {
        SimpleMinPriorityQueue h = new BinaryMinHeap(5);
        try {
            h.peek();
            fail();
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }
}
