/******************************************************************************
 *  Compilation:  javac RandomizedQueue.java
 *  Execution with assetrions:    java -ea RandomizedQueue
 *
 *  Array based implementation of the randomized queue.
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] a;         // array of items
    private int idtail;       // index of the first null item

    // construct an empty randomized queue
    public RandomizedQueue() {
        a = (Item[]) new Object[1];
        idtail = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return idtail == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return idtail;
    }

    // always add the item to the tail
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Attempt to add null in deque");
        if (idtail == a.length)
            resize(2 * a.length);     // double size of array if cannot fit another item
        a[idtail++] = item;           // add item and then increment tail
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        int idrand = getRandomIndex();
        Item randOutput = a[idrand];
        a[idrand] = a[--idtail];         // decrease idtail and use it as index
        a[idtail] = null;                // avoid loitering
        if (idtail > 0 && idtail == a.length / 4)
            resize(a.length / 2);     // shirnk size of the array
        return randOutput;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        return a[getRandomIndex()];
    }

    // return a random index from [0, tail)
    private int getRandomIndex() {
        return StdRandom.uniform(size());
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < idtail; i++) {
            copy[i] = a[i];
        }
        a = copy;
    }

    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    // This is a concrete implementation of the abstact class (interface)
    // Iterator<Item>
    private class ListIterator implements Iterator<Item> {
        private int id = 0;
        // final fields can be changed only upon initialization, i.e. in constructor
        private final int[] shuffledIndices = new int[idtail];

        private ListIterator() {
            for (int i = 0; i < idtail; i++) {
                shuffledIndices[i] = i;
            }
            StdRandom.shuffle(shuffledIndices);
        }

        // returns true if next() would return an element
        public boolean hasNext() {
            return id < idtail;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return a[shuffledIndices[id++]];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> objRQueue = new RandomizedQueue<String>();
        assert objRQueue.isEmpty() : "Not Empty";
        assert (objRQueue.size() == 0) : "Size not 0";

        objRQueue.enqueue("1");
        objRQueue.enqueue("2");
        objRQueue.enqueue("3");
        StdOut.println(objRQueue.dequeue());
        StdOut.println(objRQueue.dequeue());
        StdOut.println(String.format("size = %d ", objRQueue.size()));
        StdOut.println(objRQueue.dequeue());
        StdOut.println(String.format("size = %d: ", objRQueue.size()));
        objRQueue.enqueue("443343");
        StdOut.println(String.format("size = %d: ", objRQueue.size()));

        RandomizedQueue<String> objRQueueTest2 = new RandomizedQueue<String>();
        String testStringIterable = "ITERABL";
        for (int i = 0; i < testStringIterable.length(); i++) {
            char c = testStringIterable.charAt(i);
            objRQueueTest2.enqueue(String.valueOf(c));
        }

        for (String s : objRQueueTest2)
            StdOut.println(s);
        StdOut.println("    ");
        for (String s : objRQueueTest2)
            StdOut.println(s);
    }
}
