package KanapkaEngine.Game;

import KanapkaEngine.Components.*;
import org.joml.Vector2d;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * Future plan: Create a system to add generic type based filter for collider resolving to allow for custom physics processing.
 */
public final class Physics {
    public static vec2d gravity = new vec2d(0, -9.81);

    public static AudioClip hit = ResourceLoader.loadAudio("Audio/boxHit.wav");

    private static Rectangle2D blockCollider = new Rectangle2D.Double(-8, -8, 16, 16);

    private static final Line2D.Double noAllocRay = new Line2D.Double();


    private static List<Vector2d> rayCastForNoAlloc(List<Vector2d> locations, Ray ray, Rectangle2D.Double rect) {
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y);
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, 0.0));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                locations.add(a);
        }
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, 0.0));
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, rect.height));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                locations.add(a);
        }
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, rect.height));
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(0.0, rect.height));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                locations.add(a);
        }
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y);
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(0.0, rect.height));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                locations.add(a);
        }

        return locations;
    }

    private static boolean rayCastNoAlloc(Ray ray, Rectangle2D.Double rect) {
        return rayCastNoAlloc(ray.origin, ray.direction, ray.length, rect);
    }

    private static boolean rayCastNoAlloc(Vector2d origin, Vector2d direction, double length, Rectangle2D.Double rect) {
        noAllocRay.setLine(origin.x, origin.y, origin.x + direction.x * length,origin.y + direction.y * length);

        return rect.intersectsLine(noAllocRay);
    }

    public static List<Vector2d> raycastAll(Vector2d origin, Vector2d direction, double length, Rectangle2D.Double rect) {
        List<Vector2d> intersections = new ArrayList<>();

        noAllocRay.setLine(origin.x, origin.y, origin.x + direction.x * length, origin.y + direction.y * length);
        return raycastAll(intersections, noAllocRay, rect);
    }

    public static List<Vector2d> raycastAll(Vector2d origin, Vector2d direction, double length) {
        List<Vector2d> intersections = new ArrayList<>();

        for (Node node : SceneManager.getSceneNodes())
            if (node.getCollider() != null)
                raycastAll(intersections, origin, direction, length, node.getCollider().getRectangle());

        return intersections;
    }

    public static List<Vector2d> raycastAll(List<Vector2d> intersections, Vector2d origin, Vector2d direction, double length, Rectangle2D.Double rect) {
        if (intersections == null)
            throw new NullPointerException("Intersections list is null");
        noAllocRay.setLine(origin.x, origin.y, origin.x + direction.x * length, origin.y + direction.y * length);
        return raycastAll(intersections, noAllocRay, rect);
    }

    private static List<Vector2d> raycastAll(List<Vector2d> intersections, Line2D.Double line, Rectangle2D.Double rect) {
        if (intersections == null)
            throw new NullPointerException("Intersections list is null");

        // Define the rectangle's edges as lines
        Line2D.Double topEdge = new Line2D.Double(rect.x, rect.y, rect.x + rect.width, rect.y);
        Line2D.Double bottomEdge = new Line2D.Double(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);
        Line2D.Double leftEdge = new Line2D.Double(rect.x, rect.y, rect.x, rect.y + rect.height);
        Line2D.Double rightEdge = new Line2D.Double(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

        // Check for intersections with each edge
        findIntersection(line, topEdge, intersections);
        findIntersection(line, bottomEdge, intersections);
        findIntersection(line, leftEdge, intersections);
        findIntersection(line, rightEdge, intersections);

        return intersections;
    }

    private static void findIntersection(Line2D.Double line1, Line2D.Double line2, List<Vector2d> intersections) {
        if (line1.intersectsLine(line2)) {
            Vector2d intersection = getIntersectionPoint(line1, line2);
            if (intersection != null) {
                intersections.add(intersection);
            }
        }
    }

    private static Vector2d getIntersectionPoint(Line2D.Double line1, Line2D.Double line2) {
        // Line 1's coordinates
        double x1 = line1.x1, y1 = line1.y1;
        double x2 = line1.x2, y2 = line1.y2;

        // Line 2's coordinates
        double x3 = line2.x1, y3 = line2.y1;
        double x4 = line2.x2, y4 = line2.y2;

        // Calculate the determinant
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        // If denom is zero, lines are parallel
        if (denom == 0) {
            return null;
        }

        // Calculate intersection point
        double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denom;
        double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denom;

        // Ensure the intersection point lies on both line segments
        if (px >= Math.min(x1, x2) && px <= Math.max(x1, x2) &&
            py >= Math.min(y1, y2) && py <= Math.max(y1, y2) &&
            px >= Math.min(x3, x4) && px <= Math.max(x3, x4) &&
            py >= Math.min(y3, y4) && py <= Math.max(y3, y4)) {
            return new Vector2d(px, py);
        }

        return null;
    }


    Physics() {

    }

    public static List<Vector2d> raycastNodesNoAlloc(List<Vector2d> locations, Vector2d from, Vector2d direction, double range, List<Node> exclude) {
        if (!SceneManager.hasScene())
            return new ArrayList<>();
        List<Vector2d> nodes = locations;
        Ray ray = new Ray(from, direction, range);
        SceneManager.getSceneNodes().forEach((other) -> {
            if (other.getCollider() == null) return;
            if (exclude != null && exclude.contains(other))
                return;

            rayCastForNoAlloc(nodes, ray, other.getCollider().getRectangle());
        });

        return locations;
    }

    public static Block[] castBlocks(Vector2d position, Vector2d size) {
        List<Block> blocks = new ArrayList<>();

        Vector2d chunkSize = Chunk.getSize();

        World world = World.getCurrent();

        int block_row = SceneManager.getCurrentlyLoaded().getChunkSize();

        if (world == null)
            return new Block[0];

        Point chunkPoint = new Point(
                (int) (Math.floor(position.x / chunkSize.x) + round(Mathf.Clamp01(-position.x))),
                (int) (Math.floor(position.y / chunkSize.y) + 1));

        for (int x = -2; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Chunk check = world.get(chunkPoint.x + x, chunkPoint.y + y);

                if (check != null) {

                    for (int i = 0; i < block_row; i++) {
                        for (int j = 0; j < block_row; j++) {
                            Point p = new Point( i, j);
                            Block block = check.getBlock(p);

                            if (block != null && !block.getBlockData().floor) {
                                if (getRect(position, size).intersects(getBlockCollider(block)))
                                    blocks.add(block);
                            }
                        }
                    }
                }
            }
        }

        Block[] arr = new Block[blocks.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = blocks.get(i);
        }

        return arr;
    }

    public static Node[] castNode(Vector2d position, Vector2d size) {
        if (!SceneManager.hasScene())
            return new Node[0];
        List<Node> nodes = new ArrayList<>();
        SceneManager.getSceneNodes().forEach((other) -> {
            if (other.getCollider() == null) return;
            if (other.getCollider().getRectangle().intersects(getRect(position, size)))
                nodes.add(other);
        });

        Node[] arr = new Node[nodes.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = nodes.get(i);
        }

        return arr;
    }

    private static Rectangle2D getRect(Vector2d position, Vector2d scale) {
        double w = scale.x;
        double h = scale.y;

        double x = (position.x - scale.x / 2.0);
        double y = (position.y - scale.y / 2.0);

        return new Rectangle2D.Double(x, y, w, h);
    }

    private static Vector2d checkSide(Ray ray, Vector2d A, Vector2d B) {
        Vector2d C = ray.origin;
        Vector2d D = ray.origin.add(ray.direction);

        double b = (D.x - C.x) * (B.y - A.y) - (B.x - A.x) * (D.y - C.y);

        double rT = (C.y - A.y) * (B.x - A.x) - (C.x - A.x) * (B.y - A.y);
        double r = rT / b;

        double sT = (A.x - C.x) * (D.y - C.y) - (D.x - C.x) * (A.y - C.y);
        double s = sT / b;

        Vector2d P = null;
        if (s >= 0 && s <= 1)
            P = B.sub(A).mul(s).add(A);
        else if (r >= 0) {
            P = D.sub(C).mul(r).add(C);
        }
        else return null;

        if (Mathf.distance(ray.origin, P) > ray.length)
            return null;

        return P;
    }

    private static class Ray {
        public Vector2d origin;
        public Vector2d direction;
        public double length;

        public Ray(Vector2d origin, Vector2d direction, double length) {
            this.origin = origin;
            this.direction = direction;
            this.length = length;
        }
    }

    void FixedUpdate(double fixedDelta) {
        if (SceneManager.hasScene()) {
            SceneManager.getSceneNodes().forEach(node -> {
                CheckCollisionFor(node, fixedDelta);
                CheckCollisionForChunk(node, fixedDelta);
            });
        }
    }

    private void CheckCollisionFor(Node node, double fixedDelta) {
        if (node.getRigidbody() == null) return;
        VelocityUpdate(node, fixedDelta);

        if (node.getCollider() == null) return;
        SceneManager.getSceneNodes().forEach((other) -> {
            if (other.getCollider() == null) return;
            if (other == node) return;
            if (other.getCollider().isColliding(node.getCollider()))
                ProcessCollision(node, other, fixedDelta);
        });
        ApplyVelocity(node, fixedDelta);
    }

    private void CheckCollisionForChunk(Node node, double fixedDelta) {

        if (node.getRigidbody() == null) return;

        if (node.getCollider() == null) return;

        Vector2d chunkSize = Chunk.getSize();

        World world = World.getCurrent();

        int block_row = SceneManager.getCurrentlyLoaded().getChunkSize();

        if (world == null)
            return;

        Point chunkPoint = new Point(
                (int) (Math.floor(node.transform.getPosition().x / chunkSize.x) + round(Mathf.Clamp01(-node.transform.getPosition().x))),
                (int) (Math.floor(node.transform.getPosition().y / chunkSize.y) + 1));

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Chunk check = world.get(chunkPoint.x + x, chunkPoint.y + y);

                if (check != null) {

                    for (int i = 0; i < block_row; i++) {
                        for (int j = 0; j < block_row; j++) {
                            Point p = new Point( i, j);
                            Block block = check.getBlock(p);

                            if (block != null && block.getBlockData().hasCollision)
                                ProcessCollision(node, block, fixedDelta);
                        }
                    }
                }
            }
        }
    }

    private static double round(double a) {
        return Math.floor(Math.abs(a) + 0.5) * Math.signum(a);
    }

    private static Rectangle2D getBlockCollider(Block block) {
        double blockScale = Chunk.BLOCK_SCALE;
        Vector2d blockPosition = block.getPosition();
        blockCollider.setRect(blockPosition.x, blockPosition.y - blockScale, blockScale, blockScale);
        return blockCollider;
    }

    private void VelocityUpdate(Node node, double fixedDelta) {
        node.getRigidbody().addVelocity(gravity.mul(fixedDelta));

        node.getRigidbody().setVelocity(Mathf.Lerp(node.getRigidbody().getVelocity(), new Vector2d(), fixedDelta / 15.0));
    }

    private void ApplyVelocity(Node node, double fixedDelta) {
        Vector2d old = node.transform.getPosition();

        assert node.getRigidbody() != null;

        Vector2d position = new Vector2d(old.x, old.y).add(node.getRigidbody().getVelocity().mul(fixedDelta));

        if (FailedCollisionTest(old, position, node))
            return;

        node.transform.setPosition(position);
    }

    private void ProcessCollision(Node node, Node other, double fixedDelta) {

        if (!node.isAlive() || !other.isAlive()) return;

        Collider nodeCollider = node.getCollider();
        Collider otherCollider = other.getCollider();

        if (nodeCollider == null || otherCollider == null) return;

        if (!(nodeCollider.collideNodes && otherCollider.collideNodes)) return;

        if (otherCollider.noMass && !nodeCollider.noMass) return;

//        Vector2d nodeSize = nodeCollider.getScaledSize();
//        Vector2d otherSize = otherCollider.getScaledSize();
//
//        Vector2d nodeDiff = node.transform.getPosition().sub(other.transform.getPosition());//.add(new Vector2d( node.transform.getSize().x / 2.0, 0)).sub(other.transform.getPosition().add(new Vector2d( other.transform.getSize().x / 2.0, 0)));
//        Vector2d combinedScale = nodeSize.add(otherSize);
//        Vector2d nodeDiffScaled = new Vector2d(nodeDiff.x / combinedScale.x, nodeDiff.y / combinedScale.y);

        Vector2d position = node.transform.getPosition();
        Vector2d velocity = node.getRigidbody().getVelocity();

        resolveCollision(position, velocity, nodeCollider.getRectangle(), otherCollider.getRectangle());

/*        {
            Rigidbody rb = other.getRigidbody();
            if (rb != null) {
                Vector2d v = velocity.mul(node.getRigidbody().getBounce() / rb.getMass());
                rb.addVelocity(v);
            }


        }*/

//        if (Math.abs(nodeDiffScaled.y) < Math.abs(nodeDiffScaled.x)) {
//            position = new Vector2d((position.x + (otherSize.x / 2.0 + nodeSize.x / 2.0 - Math.abs(nodeDiff.x)) * Math.signum(nodeDiff.x)), position.y);
//            velocity = new Vector2d((-velocity.x * node.getRigidbody().getBounce() * Math.signum(nodeDiff.x)), velocity.y);
//        }
//        else {
//            position = new Vector2d(position.x, (position.y + (otherSize.y / 2.0 + nodeSize.y / 2.0 - Math.abs(nodeDiff.y)) * Math.signum(nodeDiff.y)));
//            velocity = new Vector2d(velocity.x, (-velocity.y * node.getRigidbody().getBounce() * Math.signum(nodeDiff.y)));
//        }

        node.transform.setPosition(position);
        node.getRigidbody().setVelocity(velocity);
    }

    private boolean FailedCollisionTest(Vector2d old, Vector2d position, Node node) {
        double distance = Mathf.aDistance(old, position);
        Vector2d direction = new Vector2d(old.x, old.y).sub(position).mul(1.0 / distance);

        for (Node loop : SceneManager.getSceneNodes()) {
            if (loop.getCollider() == null)
                continue;
            if (loop == node)
                continue;
            if (rayCastNoAlloc(old, direction, distance, loop.getCollider().getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private void ProcessCollision(Node node, Block other, double fixedDelta) {

        if (!node.isAlive()) return;

        Rectangle2D otherCollider = getBlockCollider(other);

        Collider nodeCollider = node.getCollider();

        if (nodeCollider == null || otherCollider == null) return;

        if (!otherCollider.intersects(nodeCollider.getRectangle())) return;

//        Vector2d nodeSize = nodeCollider.getScaledSize();
//        Vector2d otherSize = new Vector2d(Chunk.BLOCK_SCALE, Chunk.BLOCK_SCALE);
//
//        Vector2d otherColliderPosition = new Vector2d(otherCollider.getX() + otherCollider.getWidth() / 2.0, otherCollider.getY() + otherCollider.getHeight() / 2.0);
//
////.add(new Vector2d( node.transform.getSize().x / 2.0, 0))
//        Vector2d nodeDiff = node.transform.getPosition().sub(otherColliderPosition);
//        Vector2d combinedScale = nodeSize.add(otherSize);
//        Vector2d nodeDiffScaled = new Vector2d(nodeDiff.x / combinedScale.x, nodeDiff.y / combinedScale.y);

        Vector2d position = node.transform.getPosition();
        Vector2d velocity = node.getRigidbody().getVelocity();

        resolveCollision(position, velocity, nodeCollider.getRectangle(), (Rectangle2D.Double) otherCollider);

//        if (Math.abs(nodeDiffScaled.y) < Math.abs(nodeDiffScaled.x)) {
//            position = new Vector2d((position.x + (otherSize.x / 2.0 + nodeSize.x / 2.0 - Math.abs(nodeDiff.x)) * Math.signum(nodeDiff.x)), position.y);
//            velocity = new Vector2d((-velocity.x * node.getRigidbody().getBounce() * Math.signum(nodeDiff.x)), velocity.y);
//        } else {
//            position = new Vector2d(position.x, (position.y + (otherSize.y / 2.0 + nodeSize.y / 2.0 - Math.abs(nodeDiff.y)) * Math.signum(nodeDiff.y)));
//            velocity = new Vector2d(velocity.x, (-velocity.y * node.getRigidbody().getBounce() * Math.signum(nodeDiff.y)));
//        }

//        playSound(node.transform.getPosition(), position);

        node.transform.setPosition(position);
        node.getRigidbody().setVelocity(velocity);
    }

    private void resolveCollision(Vector2d outPosition, Vector2d outVelocity, Rectangle2D.Double rect1, Rectangle2D.Double rect2) {
        if (rect1.intersects(rect2)) {
            double overlapX = Math.min(rect1.getMaxX() - rect2.getMinX(), rect2.getMaxX() - rect1.getMinX());
            double overlapY = Math.min(rect1.getMaxY() - rect2.getMinY(), rect2.getMaxY() - rect1.getMinY());

            // Resolve along the smaller overlap axis to minimize movement
            if (overlapX < overlapY) {
                // Move rect1 horizontally
                if (rect1.getCenterX() < rect2.getCenterX()) {
                    outPosition.x -= overlapX;
                } else {
                    outPosition.x += overlapX;
                }
                outVelocity.x *= -0.9;
            } else {
                // Move rect1 vertically
                if (rect1.getCenterY() < rect2.getCenterY()) {
                    outPosition.y -= overlapY;
                } else {
                    outPosition.y += overlapY;
                }
                outVelocity.y *= -0.9;
            }
        }
    }

    private void playSound(Vector2d old, Vector2d position) {
        double total = Math.abs(position.x - old.x) + Math.abs(position.y - old.y);

        if (total > 0.01) {
            hit.clip.setFramePosition(0);
            hit.setVolume((float)Math.random() / 4f);
            hit.clip.start();
        }
    }
}
