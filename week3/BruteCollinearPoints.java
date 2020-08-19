/******************************************************************************
 *  Compilation:  javac BruteCollinearPoints.java
 *  Execution:    java BruteCollinearPoints
 *  Dependencies: none
 *
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class BruteCollinearPoints {
    private final int nbSegments;
    private final LineSegment[] detectedSegments;


    public BruteCollinearPoints(Point[] points) {

        // corner cases which should lead to exception
        if (points == null) throw new IllegalArgumentException();
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException();
            for (int j = i + 1; j < points.length; j++) {
                if (points[j] == null) throw new IllegalArgumentException();
                if (points[i].slopeTo(points[j]) == Double.NEGATIVE_INFINITY)
                    throw new IllegalArgumentException();
            }
        }

        // brute forse over 4-tuples
        int tmpnbSegments = 0;
        LineSegment[] tmpdetectedSegments = new LineSegment[1];
        for (int i = 0; i < points.length - 3; i++) {
            Comparator<Point> icompartor = points[i].slopeOrder();
            for (int j = i + 1; j < points.length - 2; j++) {
                for (int k = j + 1; k < points.length - 1; k++) {
                    if (icompartor.compare(points[j], points[k])
                            == 0) {         // carry on only if 3 points are collinear
                        for (int m = k + 1; m < points.length; m++) {
                            if (icompartor.compare(points[j], points[m]) == 0) {

                                if (tmpnbSegments == tmpdetectedSegments.length)
                                    tmpdetectedSegments = resize(tmpnbSegments * 2, tmpnbSegments,
                                                                 tmpdetectedSegments);

                                Point[] pointsToSort = {
                                        points[i], points[j], points[k], points[m]
                                };
                                Arrays.sort(pointsToSort);
                                tmpdetectedSegments[tmpnbSegments++] = new LineSegment(
                                        pointsToSort[0],
                                        pointsToSort[3]);
                            }
                        }
                    }
                }

            }
        }

        // set immutable properties
        detectedSegments = resize(tmpnbSegments, tmpnbSegments, tmpdetectedSegments);
        nbSegments = tmpnbSegments;

    }

    public int numberOfSegments() {
        return nbSegments;
    }

    public LineSegment[] segments() {
        return copyDetectedSegments();
    }

    private LineSegment[] resize(int capacity, int tmpnbSegments,
                                 LineSegment[] tmpdetectedSegments) {
        LineSegment[] copy = new LineSegment[capacity];
        for (int ind = 0; ind < tmpnbSegments; ind++)
            copy[ind] = tmpdetectedSegments[ind];

        return copy;
    }

    private LineSegment[] copyDetectedSegments() {
        LineSegment[] copy = new LineSegment[detectedSegments.length];
        for (int ind = 0; ind < detectedSegments.length; ind++)
            copy[ind] = detectedSegments[ind];
        return copy;
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
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        StdOut.println(collinear.numberOfSegments());
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
