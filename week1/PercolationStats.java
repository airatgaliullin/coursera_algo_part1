/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private final double[] rawResults;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) throw new IllegalArgumentException();
        if (trials <= 0) throw new IllegalArgumentException();

        rawResults = new double[trials];
        double totalNbSites = n * n;
        for (int itr = 1; itr <= trials; itr++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                // +1 since row and col are within [1, n]
                int openRow = StdRandom.uniform(n) + 1;
                int openCol = StdRandom.uniform(n) + 1;
                perc.open(openRow, openCol);
            }
            rawResults[itr - 1] = perc.numberOfOpenSites() / totalNbSites;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(rawResults);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(rawResults);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - ci();
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + ci();
    }

    private double ci() {
        return 1.96 * stddev() / Math.sqrt(rawResults.length);
    }

    // test client (see below)
    public static void main(String[] args) {
        // Input
        // n - grid size
        // trials - the number of repetitions
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats objPrcStats = new PercolationStats(n, trials);
        String meanStr = String.format("mean\t = %f\n", objPrcStats.mean());
        String stdStr = String.format("stddev\t = %f\n", objPrcStats.stddev());
        String stdCI = String
                .format("95%% confidence interval = [%f, %f]\n", objPrcStats.confidenceLo(),
                        objPrcStats.confidenceHi());

        System.out.print(meanStr);
        System.out.print(stdStr);
        System.out.print(stdCI);
    }
}
