/* *****************************************************************************
 *  Compilation:  javac KdTree.java
 *  Execution:    java KdTree filename.txt
 *
 * filename.txt contains normalized (in the range [0, 1])
 * 2d-coordinates of the input points in the format:
 * X1  Y1
 * .....
 * Xn  Yn
 *
 *
 *  KdTree is a mutable data type that uses 2d-tree to represent
 *  a set of points in the unit square.
 *******************************************************************************/

// Pick-up from here:
// 1. Finish up with nearest()

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;

public class KdTree {
    private Node root;          // root of BST
    private int redblue;
    // color-code for the comparison 1: red (x-ccordinate), -1: blue (y-coodrinate)
    private int n;              // size of the set

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
    } // node of 2d-tree

    public KdTree() {
    }   // construct an empty set of points

    public boolean isEmpty() {
        return n == 0;
    }   // is the set empty?

    public int size() {
        return n;
    }   // number of points in the set

    /**
     * Inserts a point into the KdTree, unless it is already present.
     *
     * @param p given point
     * @throws IllegalArgumentException if {@code p} is {@code null}
     */
    public void insert(
            Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("call insert() with null");
        if (!contains(p)) {
            initializeredblue();           // initialize as blue
            root = insert(root, p, null, 1);
            n++;
        }

    }

    private Node insert(Node node, Point2D p, Node neighborPar, int prevcmp) {
        // call recursively until null leaf node is reached
        if (node == null) {
            Node newNode = new Node();
            newNode.p = p;
            if (neighborPar == null) {
                newNode.rect = new RectHV(0, 0, 1, 1);
                return newNode;
            } // execute this only when the Node to set up is the root
            double xmin = neighborPar.rect.xmin();
            double ymin = neighborPar.rect.ymin();
            double xmax = neighborPar.rect.xmax();
            double ymax = neighborPar.rect.ymax();
            // Consider 4 scenarios:
            // isred() AND prevcmp == 1: went right for X(red)-comparison
            // isred() AND prevcmp == -1: went left for X(red)-comparison
            // not isred() AND prevcmp == 1: went up for Y(blue)-comparison
            // not isred() AND prevcmp == -1: went down for Y(blue)-comparison
            if (isred()) {
                if (prevcmp == 1) xmin = neighborPar.p.x();
                else xmax = neighborPar.p.x();
            }
            else {
                if (prevcmp == 1) ymin = neighborPar.p.y();
                else ymax = neighborPar.p.y();
            }
            newNode.rect = new RectHV(xmin, ymin, xmax, ymax);
            return newNode;
        }

        toggleredblue();
        int cmp = doComparison(node, p);
        /*
        cmp = 0 will not be reached anyhow due to contains-check in public insert
        */
        if (cmp < 0) node.lb = insert(node.lb, p, node, cmp);
        else if (cmp > 0) node.rt = insert(node.rt, p, node, cmp);
        return node;
    }

    /**
     * Does this KdTree contain the given point?
     *
     * @param p point to check
     * @return {@code true} if this KdTree contains {@code p} and
     * {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException(
                "Attempt to apply contains operation to null");
        initializeredblue();
        return contains(root, p);
    }

    private boolean contains(Node node, Point2D p) {
        // call recursively until null leaf node or the same point is reached
        if (node == null) {
            return false;
        }
        toggleredblue();
        int cmp = doComparison(node, p);
        if (cmp < 0) return contains(node.lb, p);
        else if (cmp > 0) return contains(node.rt, p);
        else return true;
    }

    /**
     * Find all points that are inside the rectangle (or on the boundary)
     *
     * @param rect a query rectangle
     * @return {@code Iterable<Point2D>} contains all points within a give query rectangele
     * @throws IllegalArgumentException if {@code rect} is {@code null}
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException(
                "null rectangle given as input");
        initializeredblue();
        Stack<Point2D> internalPoints = new Stack<Point2D>();
        return range(root, rect, internalPoints);
    }

    private Stack<Point2D> range(Node node, RectHV rect, Stack<Point2D> internalPoints) {
        // call recursively until null leaf node or the same point is reached
        if (node == null) {
            return internalPoints;
        }
        if (rect.contains(node.p)) internalPoints.push(node.p);

        toggleredblue();
        // record the color of the node at this level
        int currentColor = redblue;
        double nodeCoord = node.p.x();
        double minCoord = rect.xmin();
        double maxCoord = rect.xmax();
        if (!isred()) {
            nodeCoord = node.p.y();
            minCoord = rect.ymin();
            maxCoord = rect.ymax();
        }
        if (nodeCoord > maxCoord) internalPoints = range(node.lb, rect, internalPoints);
        else if (nodeCoord < minCoord) internalPoints = range(node.rt, rect, internalPoints);
        else {
            internalPoints = range(node.rt, rect, internalPoints);
            redblue = currentColor;
            internalPoints = range(node.lb, rect, internalPoints);
        }
        return internalPoints;
    }

    /**
     * Find find a closest point to a given query point.
     *
     * @param p a query rectangle
     * @return {@code Point2D} the nearest point from KdTree and
     * {@code null} if the set is empty.
     * @throws IllegalArgumentException if {@code p} is {@code null}
     */
    public Point2D nearest(
            Point2D p) {
        if (p == null) throw new IllegalArgumentException(
                "Attempt to find the nearest point to null");
        if (isEmpty()) return null;
        initializeredblue();
        return nearest(root, p, root.p);
    }

    private Point2D nearest(Node node, Point2D p, Point2D nearestPoint) {
        if (node == null) return nearestPoint;
        if (p.distanceSquaredTo(nearestPoint) < node.rect.distanceSquaredTo(p)) return nearestPoint;

        if (p.distanceSquaredTo(node.p) < p.distanceSquaredTo(nearestPoint)) {
            nearestPoint = node.p;
        }

        toggleredblue();
        // record the color of the node at this level
        int currentColor = redblue;
        double nodeCoord = node.p.x();
        double queryPointCoord = p.x();
        if (!isred()) {
            nodeCoord = node.p.y();
            queryPointCoord = p.y();
        }

        if (nodeCoord > queryPointCoord) {
            nearestPoint = nearest(node.lb, p, nearestPoint);
            redblue = currentColor;
            nearestPoint = nearest(node.rt, p, nearestPoint);
        }
        else {
            nearestPoint = nearest(node.rt, p, nearestPoint);
            redblue = currentColor;
            nearestPoint = nearest(node.lb, p, nearestPoint);
        }
        return nearestPoint;

    }

    public void draw() {
        initializeredblue();
        draw(root);
    }   // draw all points to standard draw

    private void draw(Node x) {
        // call recursively until null leaf node or the same point is reached
        if (x == null) {
            return;
        }
        toggleredblue();
        // record the color of the node at this level
        int currentColor = redblue;
        Color color = StdDraw.RED;
        double x0 = x.p.x();
        double x1 = x.p.x();
        double y0 = x.rect.ymin();
        double y1 = x.rect.ymax();
        if (!isred()) {
            color = StdDraw.BLUE;
            x0 = x.rect.xmin();
            x1 = x.rect.xmax();
            y0 = x.p.y();
            y1 = x.p.y();
        }

        // draw point
        double penRadius = 0.01;     // raidus for drawing
        StdDraw.setPenRadius(penRadius);
        StdDraw.setPenColor(StdDraw.BLACK);
        x.p.draw();

        // draw line
        StdDraw.setPenRadius();
        StdDraw.setPenColor(color);
        StdDraw.line(x0, y0, x1, y1);

        draw(x.lb);
        // restore the same color before going to neghbout node down the tree
        redblue = currentColor;
        draw(x.rt);
    }


    private int doComparison(Node x, Point2D p) {
        // compare point taking into account the color (based on x- or y- coordinate)
        int cmp = p.compareTo(x.p);
        if (cmp == 0) return cmp;
        if (isred()) {
            return p.x() < x.p.x() ? -1 : 1;
        }
        else {
            return p.y() < x.p.y() ? -1 : 1;
        }
    }

    private void initializeredblue() {
        redblue = -1;
    }

    private void toggleredblue() {
        redblue = redblue * -1;
    }

    private boolean isred() {
        return redblue == 1;
    }


    public static void main(
            String[] args) {
        // read the n points from a file
        // initialize the two data structures with point from file
        KdTree bruteT = new KdTree();
        StdOut.print(String.format("Total N = %d\n", bruteT.size()));

        String filename = args[0];
        In in = new In(filename);
        KdTree brute = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }
        Point2D nearest = brute.nearest(new Point2D(0.7, 0.484));
        StdOut.print(String.format("Total N = %d\n", brute.size()));
        StdOut.print(String.format("Expect true = Get %b\n", brute.contains(brute.root.p)));
        Point2D p = new Point2D(0, 0);
        StdOut.print(String.format("Expect false = Get %b\n", brute.contains(p)));
        brute.draw();

        /*
        String format = "%s\n";  // define format for pringing the string
        for (Point2D point :
                brute.pointsBST) {
            StdOut.print(String.format(format, point.toString()));
        }
        StdOut.print(String.format("Totan N = %d\n", brute.size()));

        brute.draw();
        */

    }   // unit testing of the methods (optional)
}

