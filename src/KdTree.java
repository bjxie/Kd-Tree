import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;
import java.util.List;

public class KdTree {
    private Node root;
    private int size = 0;

    private enum Direction {HORIZONTAL, VERTICAL}

    private static class Node {
        private final Point2D point;
        private RectHV rect;
        private Node left;
        private Node right;
        private final Direction direct;

        // every node has a point, a rectangle within which the point resides,
        // and a virtual vertical/horizontal separator
        private Node(Point2D p, RectHV r, Direction d) {
            if (r == null) {
                this.rect = new RectHV(0, 0, 1, 1);
            }
            this.point = p;
            this.rect = r;
            this.direct = d;
        }

        // switches the direction of the next node depth
        public Direction nextDir() {
            if (direct == Direction.VERTICAL) {
                return Direction.HORIZONTAL;
            } else {
                return Direction.VERTICAL;
            }
        }
    }

    // construct an empty set of points
    public KdTree() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // insert a point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        root = insert(p, root, Direction.VERTICAL, new RectHV(0, 0, 1, 1));
    }

    // insert helper function
    private Node insert(Point2D p, Node parent, Direction d, RectHV rect) {
        if (parent == null) {
            parent = new Node(p, rect, d);
            size++;
            return parent;
        }

        if (parent.direct == Direction.VERTICAL) {
            int compare = Point2D.X_ORDER.compare(p, parent.point);
            if (compare > 0) { // if new point is to the right of the vertical separator
                parent.right = insert(p, parent.right, parent.nextDir(), // recursively insert the point on the right
                        new RectHV(parent.point.x(), rect.ymin(), rect.xmax(), rect.ymax())); // update the input rectangle
            } else if (compare < 0) { // else if new point is to the left
                parent.left = insert(p, parent.left, parent.nextDir(), // recursively insert the point on the left
                        new RectHV(rect.xmin(), rect.ymin(), parent.point.x(), rect.ymax())); // update rectangle
            } else if (parent.point.y() != p.y()) { // else if x-coord are equal, check if y-coord are different
                parent.right = insert(p, parent.right, parent.nextDir(), // insert on right side
                        new RectHV(parent.point.x(), rect.ymin(), rect.xmax(), rect.ymax())); // update rectangle
            }
        }

        if (parent.direct == Direction.HORIZONTAL) { // same logic as vertical separator, except intuition is above/below
            int compare = Point2D.Y_ORDER.compare(p, parent.point);
            if (compare > 0) {
                parent.right = insert(p, parent.right, parent.nextDir(),
                        new RectHV(rect.xmin(), parent.point.y(), rect.xmax(), rect.ymax()));
            } else if (compare < 0) {
                parent.left = insert(p, parent.left, parent.nextDir(),
                        new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), parent.point.y()));
            } else if (parent.point.x() != p.x()) {
                parent.right = insert(p, parent.right, parent.nextDir(),
                        new RectHV(rect.xmin(), parent.point.y(), rect.xmax(), rect.ymax()));
            }

        }
        return parent;
    }

    // check if point is in the tree
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return contains(p, root);
    }

    // private search helper
    private boolean contains(Point2D p, Node parent) {
        if (parent == null) {
            return false;
        }
        if (parent.direct == Direction.VERTICAL) {
            int compare = Point2D.X_ORDER.compare(p, parent.point);
            if (compare > 0) { // search the right subtree if the point is to the right of separator
                return contains(p, parent.right);
            } else if (compare < 0) { // else search left if is to the left
                return contains(p, parent.left);
            } else if (parent.point.y() != p.y()) { // if on the same x-coord, search the right side
                return contains(p, parent.right);
            }
        }
        if (parent.direct == Direction.HORIZONTAL) { // same as above but with horizontal separators
            int compare = Point2D.Y_ORDER.compare(p, parent.point);
            if (compare > 0) {
                return contains(p, parent.right);
            }
            if (compare < 0) {
                return contains(p, parent.left);
            } else if (parent.point.x() != p.x()) {
                return contains(p, parent.right);
            }
        }
        return true;
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, 0.0, 1.0, 0.0, 1.0);

    }

    // recursively search the tree using the same logic as previous methods
    private void draw(Node current, double xmin, double xmax, double ymin, double ymax) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(current.point.x(), current.point.y());
        if (current.direct == Direction.VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            RectHV rect = new RectHV(current.point.x(), ymin, current.point.x(), ymax);
            rect.draw();
            draw(current.right, current.point.x(), ymin, xmax, ymax);
            draw(current.left, xmin, ymin, current.point.x(), ymax);
        }

        if (root.direct == Direction.HORIZONTAL) {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            RectHV rect = new RectHV(xmin, current.point.y(), xmax, current.point.y());
            rect.draw();
            draw(current.right, xmin, current.point.y(), xmax, ymax);
            draw(current.left, xmin, ymin, xmax, ymax);
        }
    }

    // return all points within a rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> inside = new LinkedList<>();
        range(this.root, rect, inside);
        return inside;
    }

    // helper method to recursively search if the point is in the rectangle
    private void range(Node parent, RectHV rect, List<Point2D> points) {
        if (parent == null) {
            return;
        }
        if (rect.contains(parent.point)) {
            points.add(parent.point);
        }
        if (parent.direct == Direction.VERTICAL) {
            if (parent.point.x() <= rect.xmax() && parent.point.x() >= rect.xmin()) {
                range(parent.left, rect, points); // if point x-coord is within the bounds of the rectangle x-coords,
                range(parent.right, rect, points); // search both sides
            } else if (parent.point.x() > rect.xmax()) { // if point x-coord is to the right of the rectangle x-max
                range(parent.left, rect, points); // search left
            } else {
                range(parent.right, rect, points); // else search right
            }
        }

        if (parent.direct == Direction.HORIZONTAL) { // same logic as above, but with horizontal separators
            if (parent.point.y() <= rect.ymax() && parent.point.y() >= rect.ymin()) {
                range(parent.left, rect, points);
                range(parent.right, rect, points);
            } else if (parent.point.y() > rect.ymax()) {
                range(parent.left, rect, points);
            } else {
                range(parent.right, rect, points);
            }
        }
    }

    // the nearest point to input point p
    public Point2D nearest(Point2D p) {
        if (root == null) {
            return null;
        }
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return nearest(root, p, root.point);
    }

    // private nearest helper
    private Point2D nearest(Node parent, Point2D target, Point2D closest) {
        if (parent == null) {
            return closest;
        }
        if (parent.point.distanceSquaredTo(target) < closest.distanceSquaredTo(target)) {
            closest = parent.point; // update the current closest point
        }
        if (parent.rect.distanceSquaredTo(target) < closest.distanceSquaredTo(target)) {
            if (parent.direct == Direction.VERTICAL) {
                if (Point2D.X_ORDER.compare(target, parent.point) > 0) { // if point is to the right of current closest point,
                    closest = nearest(parent.right, target, closest); // search right node
                    if (parent.left != null && closest.distanceSquaredTo(target) // check the left rectangle if it could
                            > parent.left.rect.distanceSquaredTo(target)) { // be closer to the target point
                        closest = nearest(parent.left, target, closest);
                    }
                } else {
                    closest = nearest(parent.left, target, closest); // else search the left node
                    if (parent.right != null && closest.distanceSquaredTo(target)
                            > parent.right.rect.distanceSquaredTo(target)) { // check the right rectangle if it is closer
                        closest = nearest(parent.right, target, closest);
                    }
                }
            }
            if (parent.direct == Direction.HORIZONTAL) { // same logic as above
                if (Point2D.Y_ORDER.compare(target, parent.point) > 0) {
                    closest = nearest(parent.right, target, closest);
                    if (parent.left != null && closest.distanceSquaredTo(target)
                            > parent.left.rect.distanceSquaredTo(target)) {
                        closest = nearest(parent.left, target, closest);
                    }
                } else {
                    closest = nearest(parent.left, target, closest);
                    if (parent.right != null && closest.distanceSquaredTo(target)
                            > parent.right.rect.distanceSquaredTo(target)) {
                        closest = nearest(parent.right, target, closest);
                    }
                }
            }
        }
        return closest;
    }

    // a nearest neighbor in the set to point p; null if the set is empty

    public static void main(String[] args) {
       /* KdTree testTree = new KdTree();
        Point2D A = new Point2D(0.7, 0.2);
        Point2D B = new Point2D(0.5, 0.4);
        Point2D C = new Point2D(0.2, 0.3);
        Point2D D = new Point2D(0.4, 0.7);
        Point2D E = new Point2D(0.9, 0.6);


        testTree.insert(A);
        testTree.insert(B);
        testTree.insert(C);
        testTree.insert(D);
        testTree.insert(E);
        // testTree.insert(G);
        System.out.println("size of tree: " + testTree.size())
        System.out.println("contains A: " + testTree.contains(A));
        System.out.println("contains B: " + testTree.contains(B));
        System.out.println("contains C: " + testTree.contains(C));

        System.out.print("closest Point to D: " + testTree.nearest(new Point2D(0.72, 0.18)));
*/
    }
}
