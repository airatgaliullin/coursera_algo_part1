/* *****************************************************************************
 *  Name:              Airat
 *  Coursera User ID:  123456
 *  Last modified:     13/06/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int gridSize;
    // Declaration: WeightedQuickUnionUF associates a variable name with an object type.
    // Note that UF object is indexed starting from 0
    private final WeightedQuickUnionUF ufObject;
    private boolean[][] conductMap;
    private int nbOpenSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        // Instantiation: The new keyword is a Java operator that creates the object.
        // Initialization: The new operator is followed by a call to a constructor, which initializes the new object.
        // Input:
        // n - integer defining the grid size: n-by-n
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        gridSize = n;
        conductMap = new boolean[n][n];
        nbOpenSites = 0;

        int maxId = n * n;
        ufObject = new WeightedQuickUnionUF(maxId);
        for (int ui = 1; ui <= n - 1; ui++) {
            ufObject.union(0, ui);
            // System.out.println(ufObject.count());
            // Put maxId - 1 because the indexing of ufObject starts from 0
            // thus its from 0 to n^2-1 but NOT from 1 to n^2
            ufObject.union(maxId - 1, maxId - 1 - ui);
            // System.out.println(ufObject.count());
        }
    }

    // opens the site (row, col) if it is not open already
    // by convention row and col are integrers from 1 to n where (1, 1) is upper left site
    public void open(int row, int col) {
        if (!isValidRange(row, col)) {
            throw new IllegalArgumentException();
        }
        if (!isOpen(row, col)) {
            conductMap[row - 1][col - 1] = true;
            nbOpenSites = ++nbOpenSites;
            connectToNeighbours(row, col);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!isValidRange(row, col)) {
            throw new IllegalArgumentException();
        }
        return conductMap[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isValidRange(row, col)) {
            throw new IllegalArgumentException();
        }
        boolean flagFull;
        if (row > 1 && row < gridSize)
            flagFull = ufObject.find(xyTo1D(row, col)) == ufObject.find(0);
        else if (row == 1) flagFull = isOpen(row, col);
        else flagFull = isOpen(row, col) && ufObject.find(xyTo1D(row, col)) == ufObject.find(0);
        return flagFull;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return nbOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        if (gridSize > 1) return ufObject.find(gridSize * gridSize - 1) == ufObject.find(0);
        else return isOpen(1, 1);
    }

    // transforms (row,col) of [n x n] grid to 1D indices from 0 to Math.pow(n, 2)-1
    // this is needed because UF object is 1D
    private int xyTo1D(int row, int col) {
        int idx = gridSize * (row - 1) + col - 1;
        return idx;
    }

    private boolean isValidRange(int row, int col) {
        boolean validRange = false;
        if (row > 0 && row <= gridSize && col > 0 && col <= gridSize) {
            validRange = true;
        }
        return validRange;
    }

    //    n
    //  n-n-n
    //    n
    private void connectToNeighbours(int row, int col) {
        if (isValidRange(row - 1, col)) {
            if (isOpen(row - 1, col)) {
                ufObject.union(xyTo1D(row, col), xyTo1D(row - 1, col));
            }
        }
        if (isValidRange(row + 1, col)) {
            if (isOpen(row + 1, col)) {
                ufObject.union(xyTo1D(row, col), xyTo1D(row + 1, col));
            }
        }
        if (isValidRange(row, col - 1)) {
            if (isOpen(row, col - 1)) {
                ufObject.union(xyTo1D(row, col), xyTo1D(row, col - 1));
            }
        }
        if (isValidRange(row, col + 1)) {
            if (isOpen(row, col + 1)) {
                ufObject.union(xyTo1D(row, col), xyTo1D(row, col + 1));
            }
        }
    }


    // test client (optional)
    public static void main(String[] args) {
        // int n = Integer.parseInt(args[0]);
        // Percolation objPrc = new Percolation(n);
        // objPrc.numberOfOpenSites();
        // test output
        // System.out.println(objPrc.xyTo1D(n, n));
/*        objPrc.open(2, 3);
        System.out.println(Arrays.deepToString(objPrc.conductMap));
        System.out.println(objPrc.isFull(2, 3));
        objPrc.open(1, 3);
        System.out.println(Arrays.deepToString(objPrc.conductMap));
        System.out.println(objPrc.ufObject.find(n * n - 1));
        System.out.println(objPrc.percolates());
        objPrc.open(3, 3);
        System.out.println(objPrc.percolates());*/
    }
}
