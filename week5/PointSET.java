/* *****************************************************************************
 *  Compilation:  javac PointSET.java
 *  Execution:    java PointSET filename.txt
 *
 * filename.txt contains normalized (in the range [0, 1])
 * 2d-coordinates of the input points in the format:
 * X1  Y1
 * .....
 * Xn  Yn
 *
 *
 *  PointSET is a mutable data type that represents a set of points
 *  in the unit square.
 *******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class PointSET {
    // final is only about the reference itself, and not about the contents of the referenced object
    // thus pointsBST can be made final even though .add is called from .insert
    private final SET<Point2D> pointsBST;     // BST which stores points in the set
    private int n;                      // size of the set

    public PointSET() {
        pointsBST = new SET<Point2D>();
        n = 0;
    }   // construct an empty set of points

    public boolean isEmpty() {
        return n == 0;
    }   // is the set empty?

    public int size() {
        return n;
    }   // number of points in the set

    public void insert(
            Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Attempt to add null in set");
        if (!pointsBST.contains(p)) {
            pointsBST.add(p);
            n++;
        }
    }   // add the point to the set (if it is not already in the set)
    // time ~log(N)

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException(
                "Attempt to apply contains operation to null");
        return pointsBST.contains(p);
    }   // does the set contain point p?
    // time ~log(N)

    public void draw() {
        double penRadius = 0.1;     // raidus for drawing
        for (Point2D point :
                pointsBST) {
            StdDraw.setPenRadius(penRadius);
            point.draw();
        }
    }   // draw all points to standard draw
    // time ~N

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException(
                "null rectangle given as input");
        Stack<Point2D> internalPoints = new Stack<Point2D>();
        for (Point2D point :
                pointsBST) {
            if (rect.contains(point)) {
                internalPoints.push(point);
            }
        }
        return internalPoints;
    }   // all points that are inside the rectangle (or on the boundary)
    // time ~N

    public Point2D nearest(
            Point2D p) {
        if (p == null) throw new IllegalArgumentException(
                "Attempt to find the nearest point to null");
        if (pointsBST.isEmpty()) return
                null;

        double distanceToNearest = Double.POSITIVE_INFINITY;
        Point2D nearestPoint = null;
        for (Point2D point :
                pointsBST) {
            double distanceToCandidate = p.distanceSquaredTo(point);
            if (distanceToCandidate < distanceToNearest) {
                nearestPoint = point;
                distanceToNearest = distanceToCandidate;
            }
        }

        return nearestPoint;
    }   // a nearest neighbor in the set to point p; null if the set is empty

    public static void main(
            String[] args) {
        // read the n points from a file
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }

        String format = "%s\n";  // define format for pringing the string
        for (Point2D point :
                brute.pointsBST) {
            StdOut.print(String.format(format, point.toString()));
        }
        StdOut.print(String.format("Totan N = %d\n", brute.size()));

        brute.draw();


    }   // unit testing of the methods (optional)
}
