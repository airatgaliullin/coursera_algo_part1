/******************************************************************************
 *  Compilation:  javac Deque.java
 *  Execution:    java Deque
 *  To enable execution with assertions: java -ea Deque
 *
 *  Doubly linked-list implementaion of a double-ended queue (deque).
 *
 *  Total memory usage for n items is 48*n + 40 bytes (without the content of an item).
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * "implements Iterable<Item>" means that class Deque<Item> implements the Iterator interface
 * Note a full anti-symmetry between addFirst() vs addLast and removeFirst() vs removeLast().
 */

public class Deque<Item> implements Iterable<Item> {
    private int n;       // size of the deque
    private Node first;  // front of deque (head in queue terms)
    private Node last;   // back of deque (tail in queue terms)

    // helper linked list class
    // memory uasge for an object of this class is 48 bytes
    private class Node {
        private Item item;
        private Node previous; // this field is needed specifically for removeLast()
        private Node next;
    }

    // construct an empty deque
    public Deque() {
        first = null;
        last = null;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front (push() for Stack)
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Attempt to add null in deque");
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        first.previous = null;
        if (last == null) last = first;     // first and last hold referce to the same object now
        else oldFirst.previous = first;     // link oldFirst to first Node: first <-- oldFirst
        n++;
    }

    // add the item to the back (enqueue() for Queue)
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Attempt to add null in deque");
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        last.previous = oldLast;
        if (isEmpty()) first = last;        // first and last hold referce to the same object now
        else oldLast.next = last;           // link oldLast to last Node: oldLast --> last
        n++;
    }

    // remove and return the item from the front (common: dequeue() for Queue and pop() for Stack)
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        Item oldFirstItem = first.item;
        first = first.next;
        if (isEmpty()) last = null;          // avoid loitering: last is no longer needed
        else first.previous = null;          // avoid loitering
        n--;
        return oldFirstItem;
    }

    // remove and return the item from the back (peculiar method not present in Queue and Stack)
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        Item oldLastItem = last.item;
        last = last.previous;
        if (last == null) first = null;     // avoid loitering: first is no longer needed
        else last.next = null;              // avoid loitering
        n--;
        return oldLastItem;
    }


    // Return an iterator over items in order from front to back.
    // This is a concrete implementation of the method Iterator<Item>
    // from the abstract class (interface) Iterable<Item>.
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    // This is a concrete implementation of the abstact class (interface)
    // Iterator<Item>
    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        // returns true if next() would return an element
        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    public static void main(String[] args) {
        Deque<String> objDeque = new Deque<String>();
        assert objDeque.isEmpty() : "Not Empty";
        assert (objDeque.size() == 0) : "Size not 0";


        String testString = "12345";
        for (int i = 0; i < testString.length(); i++) {
            char c = testString.charAt(i);
            objDeque.addLast(String.valueOf(c));
        }
        while (!objDeque.isEmpty()) {
            StdOut.println(objDeque.removeLast());
        }
        assert (objDeque.size() == 0) : "Size not 0";

        objDeque.addFirst("addFirstTest");
        StdOut.println(objDeque.removeFirst());
        assert objDeque.isEmpty() : "Not Empty";

        // test Iteration capability and foreach
        String testStringIterable = "ITERABLE";
        for (int i = 0; i < testStringIterable.length(); i++) {
            char c = testStringIterable.charAt(i);
            objDeque.addLast(String.valueOf(c));
        }
        for (String s : objDeque)
            StdOut.println(s);
    }

/*    private static void testSprint(boolean input, String testName) {
        System.out.print(String.format("%s: ", testName));
        if (input) {
            System.out.print("PASS\n");
        }
        else {
            System.out.print("FAIL\n");
        }
    }*/

}
