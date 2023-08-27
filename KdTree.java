import java.io.File;
import java.util.LinkedList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

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

    private class NearestResult {
        Point2D p;
        double dSquared;

        NearestResult(Point2D p, double dSquared) {
            this.p = p;
            this.dSquared = dSquared;
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
        if (curr == null) {
            size++;
            return new KdTreeNode(p, null, null);
        }

        if (curr.value.equals(p)) 
            return curr;

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

        if (curr.value.equals(p))
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

        range(rect, root, list);

        return list;
    }

    private void range(RectHV rect, KdTreeNode curr, LinkedList<Point2D> list) {
        if (curr == null)
            return;

        if (rect.contains(curr.value))
            list.add(curr.value);

        range(rect, curr.left, list);
        range(rect, curr.right, list);
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new IllegalArgumentException();

        var result = nearest(p, root);

        return result.p;
    }

    private NearestResult nearest(Point2D p, KdTreeNode curr) {
        if (curr == null)
            return new NearestResult(null, Double.MAX_VALUE);

        var currSquared = curr.value.distanceSquaredTo(p);

        var nearLeft = nearest(p, curr.left);
        var nearRight = nearest(p, curr.right);

        // StdOut.println("Checking: " + curr.value + " with d^2 = " + currSquared);
        // StdOut.println("    Left: " + nearLeft.p + " with d^2 = " + nearLeft.dSquared);
        // StdOut.println("    Right: " + nearRight.p + " with d^2 = " + nearRight.dSquared);

        if (currSquared <= nearLeft.dSquared && currSquared <= nearRight.dSquared) {
            // StdOut.println("=== " + curr.value + "/" + nearLeft.p + "/" + nearRight.p  +   " ===> " + curr.value + " wins!");
            return new NearestResult(curr.value, currSquared);
        }
        else if (nearLeft.dSquared <= currSquared && nearLeft.dSquared <= nearRight.dSquared) {
            // StdOut.println("=== " + curr.value + "/" + nearLeft.p + "/" + nearRight.p  +   " ===> " + nearLeft.p + " wins!");
            return nearLeft;
        }
        else if (nearRight.dSquared <= currSquared && nearRight.dSquared <= nearLeft.dSquared) {
            // StdOut.println("=== " + curr.value + "/" + nearLeft.p + "/" + nearRight.p  +   " ===> " + nearRight.p + " wins!");
            return nearRight;
        }
        else {
            // StdOut.println("  currSquared <= nearLeft.dSquared " +  (currSquared <= nearLeft.dSquared));
            // StdOut.println("  currSquared <= nearRight.dSquared " +  (currSquared <= nearRight.dSquared));
            throw new RuntimeException("Argh!");
        }
    }

    public static void main(String[] args) { // unit testing of the methods (optional)
// create initial board from file
        var defaultFile = "input10.txt";

        In in = new In(new File(defaultFile));

        var tree = new KdTree();

        var lines = in.readAllLines();
        for (var line : lines) {
            var nums = line.split("\\s+");
            var point = new Point2D(Double.parseDouble(nums[0]), Double.parseDouble(nums[1]));
            tree.insert(point);
        }

        var target = new Point2D(0.25, 0.4375);

        StdOut.println("Size = " + tree.size());


        var nearest = tree.nearest(target);
        
        StdOut.println(nearest);
    }
}