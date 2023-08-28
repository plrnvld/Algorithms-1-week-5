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

        range(rect, root, list, true);

        return list;
    }

    private void range(RectHV rect, KdTreeNode curr, LinkedList<Point2D> list, boolean useX) {
        if (curr == null)
            return;

        if (rect.contains(curr.value))
            list.add(curr.value);

        if (checkLeft(rect, curr.value, useX))
            range(rect, curr.left, list, !useX);

        if (checkRight(rect, curr.value, useX))
            range(rect, curr.right, list, !useX);
    }

    private boolean checkLeft(RectHV rect, Point2D currPoint, boolean useX) {
        return useX
                ? rect.xmin() <= currPoint.x()
                : rect.ymin() <= currPoint.y();
    }

    private boolean checkRight(RectHV rect, Point2D currPoint, boolean useX) {
        return useX
                ? rect.xmax() >= currPoint.x()
                : rect.ymax() >= currPoint.y();
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new IllegalArgumentException();

        var result = nearest(p, root, true);

        return result.p;
    }

    private NearestResult nearest(Point2D p, KdTreeNode curr, boolean useX) {
        if (curr == null)
            return new NearestResult(null, Double.POSITIVE_INFINITY);

        var bestResult = new NearestResult(curr.value, curr.value.distanceSquaredTo(p));

        var leftFirst = useX
                ? curr.value.x() > p.x()
                : curr.value.y() > p.y();

        var checkFirst = leftFirst ? curr.left : curr.right;
        var checkLast = leftFirst ? curr.right : curr.left;

        var firstChildResult = nearest(p, checkFirst, !useX);
        if (isBetter(firstChildResult.dSquared, bestResult.dSquared))
            bestResult = firstChildResult;

        if (canBeatBest(p, curr.value, bestResult.dSquared, useX)) {

            var lastChildResult = nearest(p, checkLast, !useX);
            if (isBetter(lastChildResult.dSquared, bestResult.dSquared))
                bestResult = lastChildResult;
        }

        return bestResult;
    }

    private boolean canBeatBest(Point2D target, Point2D curr, double currentBest, boolean useX) {
        var diff = useX ? curr.x() - target.x() : curr.y() - target.y();
        var newMinDistSquared = diff * diff;

        if (Double.isInfinite(currentBest))
            return true;

        return newMinDistSquared < currentBest;
    }

    private boolean isBetter(double distNew, double distPrev) {
        if (Double.isInfinite(distNew) && Double.isInfinite(distPrev))
            return false;

        if (Double.isInfinite(distPrev))
            return true;

        return distNew < distPrev;
    }

    public static void main(String[] args) { // unit testing of the methods (optional)
        // create initial board from file
        var defaultFile = "input5.txt";

        In in = new In(new File(defaultFile));

        var tree = new KdTree();

        var lines = in.readAllLines();
        for (var line : lines) {
            var nums = line.split("\\s+");
            var point = new Point2D(Double.parseDouble(nums[0]), Double.parseDouble(nums[1]));
            tree.insert(point);
        }

        var target = new Point2D(0.5, 0.954);

        StdOut.println("Size = " + tree.size());

        var nearest = tree.nearest(target);

        StdOut.println(nearest);
    }
}