import java.util.LinkedList;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private SET<Point2D> set;

    public PointSET() { // construct an empty set of points
        set = new SET<Point2D>();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() { // number of points in the set
        return set.size();
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        if (p == null)
            throw new IllegalArgumentException();

        set.add(p);
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        if (p == null)
            throw new IllegalArgumentException();

        return set.contains(p);
    }

    public void draw() { // draw all points to standard draw
        for (var point : set) {
            point.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        if (rect == null)
            throw new IllegalArgumentException();

        var list = new LinkedList<Point2D>();

        for (var point : set) {
            if (rect.contains(point)) {
                list.add(point);
            }
        }

        return list;
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new IllegalArgumentException();

        var minDist = Double.POSITIVE_INFINITY;
        Point2D champion = null;

        for (var point : set) {
            var dist = p.distanceSquaredTo(point);
            if (dist < minDist) {
                minDist = dist;
                champion = point;
            }
        }

        return champion;
    }

    public static void main(String[] args) { // unit testing of the methods (optional)
    }
}