import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> pointSET;

    // construct an empty set of points
    public PointSET() {
        pointSET = new TreeSet<>();
    }

    // checks if set is empty
    public boolean isEmpty() {
        return pointSET.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointSET.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        checkNull(p);
        if (!pointSET.contains(p)) {
            pointSET.add(p);
        }
    }

    // private null checker
    private void checkNull(Object in) {
        if (in == null) {
            throw new IllegalArgumentException();
        }
    }

    // is the point in the set?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return pointSET.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);

        for (Point2D point : pointSET)
            StdDraw.point(point.x(), point.y());
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        checkNull(rect);
        ArrayList<Point2D> insideRect = new ArrayList<>();
        for (Point2D point : pointSET) {
            if (rect.contains(point)) {
                insideRect.add(point);
            }
        }
        return insideRect;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (pointSET.isEmpty()) {
            return null;
        }
        double minDistance = Double.MAX_VALUE;
        double distance;
        Point2D nearest = null;
        for (Point2D compare : pointSET) {
            distance = p.distanceSquaredTo(compare);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = compare;
            }
        }
        return nearest;
    }

    public static void main(String[] args) {
        // unit testing of the methods (optional)
    }
}
