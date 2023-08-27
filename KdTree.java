import java.util.LinkedList;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class KdTree {
    private KdTreeNode root;
    private int size;

    private class KdTreeNode {
        Point2D value;
        KdTreeNode left;
        KdTreeNode right;

        KdTreeNode(Point2D value, KdTreeNode left, KdTreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }
    }

    public KdTree() { // construct an empty set of points
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() { // number of points in the set
        return size;
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        if (p == null)
            throw new IllegalArgumentException();

        root = insert(p, root, true);
    }

    private KdTreeNode insert(Point2D p, KdTreeNode curr, boolean useX) {
        if (curr == null)
            return new KdTreeNode(p, null, null);

        var newLeft = curr.left;
        var newRight = curr.right;

        if (less(p, curr.value, useX))
            newLeft = insert(p, curr.left, !useX);
        else
            newRight = insert(p, curr.right, !useX);

        return new KdTreeNode(curr.value, newLeft, newRight);
    }

    private boolean less(Point2D newPoint, Point2D nodePoint, boolean useX) {
        return useX ? newPoint.x() < nodePoint.x() : newPoint.y() < nodePoint.y();
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        if (p == null)
            throw new IllegalArgumentException();

        return contains(p, root, true);
    }

    private boolean contains(Point2D p, KdTreeNode curr, boolean useX) {
        if (curr == null)
            return false;

        // ############################### Continue here

        if (curr.value == p)
            return true;

        var nextNode = less(p, curr.value, useX) ? curr.left : curr.right;

        return contains(p, nextNode, !useX);
    }

    public void draw() { // draw all points to standard draw
        draw(root);
    }

    private void draw(KdTreeNode curr) {
        if (curr == null)
            return;

        curr.value.draw();
        draw(curr.left);
        draw(curr.right);
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

        var minDist = Double.MAX_VALUE;
        Point2D champion = null;

        for (var point : set) {
            var dist = p.distanceTo(point);
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