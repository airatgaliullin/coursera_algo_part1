/******************************************************************************
 *  Compilation:  javac Permutation.java
 *  Execution:    java Permutation 3 < file.txt
 *
 * The sized of RandomizedQueue object is determined from the command line argument k.
 * For that, Reservoir sampling with Algorithm R is added.
 ******************************************************************************/

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        // Input
        // k - number of string to read from the STDIN (in fact, the redirected file)
        RandomizedQueue<String> objRQ = new RandomizedQueue<String>();
        int k = Integer.parseInt(args[0]);
        int counter = 1;
        // implement Reservoir sampling with Algorithm R
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (objRQ.size() < k)
                objRQ.enqueue(item);
            else {
                int j = StdRandom.uniform(k + counter++);
                if (j < k) {
                    objRQ.dequeue();
                    objRQ.enqueue(item);
                }
            }
        }

        for (String s : objRQ) {
            StdOut.println(s);
        }
    }
}
