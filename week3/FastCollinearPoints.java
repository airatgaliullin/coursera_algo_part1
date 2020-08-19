/******************************************************************************
 *  Compilation:  javac  FastCollinearPoints.java
 *  Execution:    java  FastCollinearPoints
 *  Dependencies: none
 *
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {
    private final int nbSegments;
    private final LineSegment[] detectedSegments;
    private LineSegment[] buffDetectedSegments = new LineSegment[1];
    private int tmpnbSegments = 0;

    public FastCollinearPoints(
            Point[] points) {

        // corner cases which should lead to exception
        if (points == null) throw new IllegalArgumentException();
        Point[] pointsSorted = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException();
            for (int j = i + 1; j < points.length; j++) {
                if (points[j] == null) throw new IllegalArgumentException();
                if (points[i].slopeTo(points[j]) == Double.NEGATIVE_INFINITY)
                    throw new IllegalArgumentException();
            }
            pointsSorted[i] = points[i];
        }
        boolean ENDLOOP = false;
        // unless ENDLOOP becomes false, test if a given point is collinear to
        // any other 3 or more points
        // ENDLOOP becomes true if all of points are collinear.
        for (int i = 0; i < points.length; i++) {
            Comparator<Point> icompartor = points[i].slopeOrder();
            Arrays.sort(pointsSorted, icompartor);

            int totalStepCounter
                    = 1; // this variable keeps track of the absolute index of the loop over all points

            while (totalStepCounter < points.length - 1) {
                int localCounter
                        = 1; // localCounter stores the number of points which are collinear to points[i]
                while (icompartor
                        .compare(pointsSorted[totalStepCounter],
                                 pointsSorted[totalStepCounter + 1]) == 0) {
                    localCounter++;
                    totalStepCounter++;
                    if (totalStepCounter == points.length - 1) break;
                }
                if (localCounter > 2) {

                    // create an array of collinear points to be sorted by natural order
                    // in order to determine head-tail points of the segment
                    Point[] subPointsToSort = new Point[localCounter + 1];
                    subPointsToSort[0] = points[i];
                    for (int si = 1; si < subPointsToSort.length; si++) {
                        subPointsToSort[si] = pointsSorted[totalStepCounter - localCounter + si];
                    }
                    Arrays.sort(subPointsToSort);
                    LineSegment tentativeLineSegmanet = new LineSegment(subPointsToSort[0],
                                                                        subPointsToSort[localCounter]);
                    if (localCounter + 1 == points.length) {
                        this.addLineSegment(tentativeLineSegmanet);
                        ENDLOOP = true;
                    }
                    // Note that it can is inneficient to add the line segment 
                    // under the condition that the head == points[i]
                    // for very long ~n segments. Reason: ~n redundant
                    // sorting calls requiring o(nlog(n)) each.
                    // This is actually the motivation for introducing
                    // ENDLOOP variable, even so it's not general enough.
                    else if (subPointsToSort[0].compareTo(points[i]) == 0) {
                        this.addLineSegment(tentativeLineSegmanet);
                    }
                }
                totalStepCounter++;
            }
            if (ENDLOOP) break;
        }

        detectedSegments = resize(this.tmpnbSegments, this.buffDetectedSegments);
        nbSegments = this.tmpnbSegments;

    }    // finds all line segments containing 4 or more points

    public int numberOfSegments() {
        return nbSegments;
    }      // the number of line segments

    public LineSegment[] segments() {
        return copyDetectedSegments();
    }

    private LineSegment[] resize(int capacity,
                                 LineSegment[] tmpdetectedSegments) {
        LineSegment[] copy = new LineSegment[capacity];
        for (int ind = 0; ind < this.tmpnbSegments; ind++)
            copy[ind] = tmpdetectedSegments[ind];

        return copy;
    }

    private LineSegment[] copyDetectedSegments() {
        LineSegment[] copy = new LineSegment[detectedSegments.length];
        for (int ind = 0; ind < detectedSegments.length; ind++)
            copy[ind] = detectedSegments[ind];
        return copy;
    }

    private void addLineSegment(LineSegment newLineSegmanet) {
        if (this.tmpnbSegments == this.buffDetectedSegments.length)
            this.buffDetectedSegments = resize(this.tmpnbSegments * 2,
                                               this.buffDetectedSegments);
        buffDetectedSegments[this.tmpnbSegments++] = newLineSegmanet;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        // StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.println(collinear.numberOfSegments());
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
