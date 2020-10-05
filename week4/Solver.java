/* *****************************************************************************
 *  Compilation:  javac-algs4 Solver.java
 *  Execution:    java-algs4 Solver filename1.txt
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {

    private final Stack<Board> refinedSolSequence; // = new Stack<Board>();
    private int buffTotalMoves = 0;
    private final int totalMoves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {

        if (initial == null) throw new IllegalArgumentException();

        // initialize stack with node-candidates for the shortest soltion path
        Stack<Solver.SearchNode> rawSolSequence = new Stack<Solver.SearchNode>();

        // initialize Priority Queue with a comparator
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>(new ByPriourity());
        pq.insert(new SearchNode(initial, null));
        MinPQ<SearchNode> twinpq = new MinPQ<SearchNode>(new ByPriourity());
        twinpq.insert(new SearchNode(pq.min().getBoard().twin(), null));

        int buffprio = 0;
        while (true) {

            SearchNode delNode = pq.delMin();

            // use invariant as a debugging clue: priopity is non-decresing
            if (buffprio > delNode.getPriority()) throw new IllegalArgumentException();
            buffprio = delNode.getPriority();

            // debug output
            // StdOut.println(String.format("priority = %d (manh = %d moves = %d) \n", buffprio,
            //                             delNode.getCacheManhattan(), delNode.getMoves()));
            // StdOut.println(delNode.board);

            rawSolSequence.push(delNode);

            // stop loop if the puzzle is solved
            if (delNode.getBoard().isGoal()) break;

            // include neighbours of the removed board to PQ
            Iterable<Board> toInclude = delNode.getBoard().neighbors();
            for (Board b : toInclude) {

                if (delNode.getPrevious() != null && b.equals(delNode.getPrevious().getBoard())) {
                    continue;
                }
                pq.insert(new SearchNode(b, delNode));

            }

            // repeat for twin
            SearchNode twindelNode = twinpq.delMin();
            // stop loop if the puzzle is solved
            if (twindelNode.getBoard().isGoal()) {
                buffTotalMoves = -1;
                break;
            }
            // include neighbours of the removed board to PQ
            Iterable<Board> twintoInclude = twindelNode.getBoard().neighbors();
            for (Board b : twintoInclude) {

                if (twindelNode.getPrevious() != null && b
                        .equals(twindelNode.getPrevious().getBoard())) {
                    continue;
                }
                twinpq.insert(new SearchNode(b, twindelNode));

            }
            // end of repeat for twin

        }

        // unwind from Solved board -> ...-> Initial board in case
        // the board is solvable
        if (buffTotalMoves == 0) {
            refinedSolSequence = backpropagateSol(rawSolSequence);
        }
        else {
            refinedSolSequence = null;
        }
        totalMoves = buffTotalMoves;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return totalMoves > -1;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return totalMoves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return refinedSolSequence;
    }

    // retrieve shortest path by going back starting from the solution
    private Stack<Board> backpropagateSol(Stack<Solver.SearchNode> rawSolSequence) {
        Stack<Board> buffRefinedSolSequence = new Stack<Board>();
        SearchNode preNode = rawSolSequence.pop();
        while (preNode != null) {
            buffRefinedSolSequence.push(preNode.getBoard());
            buffTotalMoves++;
            preNode = preNode.getPrevious();
        }
        buffTotalMoves--;
        return buffRefinedSolSequence;

    }


    private class SearchNode {

        private final SearchNode previous;
        private final Board board;
        private final int moves;
        private final int cacheManhattan;
        private final int priority;

        public SearchNode(Board boardInit, SearchNode previousInit) {
            board = boardInit;
            previous = previousInit;
            cacheManhattan = boardInit.manhattan();
            if (previousInit != null) moves = previousInit.getMoves() + 1;
            else moves = 0;
            priority = moves + cacheManhattan;

        }

        public int getMoves() {
            return moves;
        }

        public int getCacheManhattan() {
            return cacheManhattan;
        }

        public int getPriority() {
            return priority;
        }

        public SearchNode getPrevious() {
            return previous;
        }

        public Board getBoard() {
            return board;
        }

    }

    // concrete implementation of Comparator interface
    private static class ByPriourity implements Comparator<SearchNode> {
        public int compare(SearchNode node1, SearchNode node2) {
            int priority = Integer.compare(node1.getPriority(),
                                           node2.getPriority());
            if (priority != 0) {
                return priority;
            }
            // break ties by comparing Manhattan distances
            else {
                return Integer.compare(node1.getCacheManhattan(),
                                       node2.getCacheManhattan());
            }

        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output

        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }


        StdOut.println("Minimum number of moves = " + solver.moves());


    }

}
