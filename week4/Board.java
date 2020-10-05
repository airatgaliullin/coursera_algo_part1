/* *****************************************************************************
 *  Compilation:  javac-algs4 Board.java
 *  Execution:    java-algs4 Board filename1.txt
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Board {

    private int[] boardRepr;  // internal representation of Board
    private final int gridSize;     // board is gridSize-by-gridSize
    private final int gridSizeSqr;

    public Board(int[][] tiles) {

        // null input should lead to exception
        if (tiles == null) throw new IllegalArgumentException();

        gridSize = tiles.length;
        gridSizeSqr = gridSize * gridSize;
        boardRepr = new int[gridSizeSqr];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (tiles[row][col] < 0 || tiles[row][col] >= gridSizeSqr) {
                    throw new IllegalArgumentException();
                }
                boardRepr[flatIndex(row, col)] = tiles[row][col];
            }
        }

    }

    // string representation of this board
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(gridSize + "\n");
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                s.append(String.format("%2d ", boardRepr[flatIndex(row, col)]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return gridSize;
    }

    // number of tiles out of place
    public int hamming() {
        int nbhamming = 0;
        for (int i = 0; i < gridSizeSqr - 1; i++) {
            if (boardRepr[i] != i + 1) nbhamming++;
        }
        return nbhamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int nbmanhattan = 0;
        for (int i = 0; i < gridSizeSqr; i++) {
            if (boardRepr[i] == 0) continue;
            int[] rowcolSolved = rowcolIndex(boardRepr[i] - 1);
            int[] rowcolActual = rowcolIndex(i);
            int multr = 1;
            int multc = 1;
            if (rowcolActual[0] < rowcolSolved[0]) multr = -1;
            if (rowcolActual[1] < rowcolSolved[1]) multc = -1;
            nbmanhattan += multr * (rowcolActual[0] - rowcolSolved[0]) + multc * (rowcolActual[1]
                    - rowcolSolved[1]);
        }
        return nbmanhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return this.hamming() == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return (this.gridSize == that.gridSize) && (this.gridSizeSqr == that.gridSizeSqr) &&
                Arrays.equals(this.boardRepr, that.boardRepr);
    }


    // return 1D index i from (row, col)
    // Convention:
    // - row and col are from [0, gridSize - 1]
    // - upper-left corner of the grid is associated with flattend index 0,
    // so i is from [0, gridSize*gridSize - 1]
    private int flatIndex(int row, int col) {
        if (!this.validRoCol(row, col)) throw new IllegalArgumentException();
        int i = gridSize * row + col;
        return i;
    }

    // return 2D index (row, col) from i
    private int[] rowcolIndex(int i) {
        if (i >= gridSizeSqr || i < 0) throw new IllegalArgumentException();
        int[] rowcol = new int[2];
        rowcol[0] = i / gridSize;
        rowcol[1] = i % gridSize;
        return rowcol;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neighborBoards = new Queue<Board>();

        // find zero element to determine the index configuration
        // of the neighbouring boards
        int izero = this.indexOfZero();

        // get 2d index of the zero element
        // this 2d index will be used to find the neighbours
        int[] rowcolZero = this.rowcolIndex(izero);
        int[][] drodcol = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        // loop over potential neighbours of the board: max number is 4
        // account only for +-1 increment of row and col indices
        for (int irc = 0; irc < drodcol.length; irc++) {
            if (this.validRoCol(rowcolZero[0] + drodcol[irc][0], rowcolZero[1] + drodcol[irc][1])) {
                // continue with this copyBoard function
                // though it's not the best name
                Board copyOfBoard = this.copyBoard();
                copyOfBoard.swap(izero, this.flatIndex(rowcolZero[0] + drodcol[irc][0],
                                                       rowcolZero[1] + drodcol[irc][1]));
                neighborBoards.enqueue(copyOfBoard);
            }
        }

        return neighborBoards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        Board copyOfBoard = this.copyBoard();
        int izero = copyOfBoard.indexOfZero();
        int index1 = izero - 1;
        int index2 = izero + 1;
        // corner cases
        if (izero == gridSizeSqr - 1) {
            index1 = izero - 1;
            index2 = izero - 2;
        }
        else if (izero == 0) {
            index1 = izero + 1;
            index2 = izero + 2;
        }
        copyOfBoard.swap(index1, index2);
        return copyOfBoard;
    }

    private Board copyBoard() {
        int[][] tiles = new int[this.gridSize][this.gridSize];
        for (int i = 0; i < gridSizeSqr; i++) {
            int[] rowcol = this.rowcolIndex(i);
            tiles[rowcol[0]][rowcol[1]] = this.boardRepr[i];
        }

        return new Board(tiles);
    }

    private boolean validRoCol(int row, int col) {
        return (row < gridSize) && (col < gridSize) && (row >= 0) && (col >= 0);
    }

    private void swap(int index1, int index2) {
        // exchange entries for index1 <--> index2
        int buff = this.boardRepr[index2];
        this.boardRepr[index2] = this.boardRepr[index1];
        this.boardRepr[index1] = buff;
    }

    private int indexOfZero() {
        // find zero element to determine the index configuration
        // of the neighbouring boards
        int izero = 0;
        for (int i = 0; i < gridSizeSqr; i++)
            if (boardRepr[i] == 0) {
                izero = i;
                break;
            }
        return izero;
    }

    public static void main(String[] args) {
        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            // solve the slider puzzle
            Board initial = new Board(tiles);

            int testFlatIndex = 4;
            int[] rowcol = initial.rowcolIndex(testFlatIndex);
            int fromRowCol = initial.flatIndex(rowcol[0], rowcol[1]);
            StdOut.print(String.format("get = %d produced = %d \n", testFlatIndex, fromRowCol));

            StdOut.print(initial.toString());
            // StdOut.print(String.format("rowcol = %d %d \n", rowcol[0], rowcol[1]));
            StdOut.print(String.format("hamming = %2d \n", initial.hamming()));
            StdOut.print(String.format("manhattan = %2d \n", initial.manhattan()));


            Board initial2 = new Board(tiles);
            StdOut.print(String.format("Equals = %b \n", initial.equals(initial2)));
            tiles[1][1] = tiles[2][2];
            Board initial3 = new Board(tiles);
            StdOut.print(String.format("Equals = %b \n", initial.equals(initial3)));


            for (Board b : initial.neighbors())
                StdOut.print(b.toString());

            StdOut.print(initial.twin());
        }
    }
}
