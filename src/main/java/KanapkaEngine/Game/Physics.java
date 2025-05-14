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
    private static final Logger logger = new Logger("physics");
    public static vec2d gravity = new vec2d(0, -9.81);

    private static final ICollisionResolver.CollisionData one = new ICollisionResolver.CollisionData();
    private static final ICollisionResolver.CollisionData two = new ICollisionResolver.CollisionData();

    private static List<ICollisionResolver> resolvers = new ArrayList<>();

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

    public static boolean rayCastNoAlloc(Ray ray, Rectangle2D.Double rect) {
        return rayCastNoAlloc(ray.origin, ray.direction, ray.length, rect);
    }

    public static boolean rayCastNoAlloc(Vector2d origin, Vector2d direction, double length, Rectangle2D.Double rect) {
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
            if (node.getCollider() != null && node.getCollider() instanceof RectangleCollider)
                raycastAll(intersections, origin, direction, length, ((RectangleCollider)node.getCollider()).getRectangle());

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
        registerCollisionResolver(new RectangleCollisionResolver());
    }

    public static List<Vector2d> raycastNodesNoAlloc(List<Vector2d> locations, Vector2d from, Vector2d direction, double range, List<Node> exclude) {
        if (!SceneManager.hasScene())
            return new ArrayList<>();
        List<Vector2d> nodes = locations;
        Ray ray = new Ray(from, direction, range);
        SceneManager.getSceneNodes().forEach((other) -> {
            if (other.getCollider() == null || !(other.getCollider() instanceof RectangleCollider)) return;
            if (exclude != null && exclude.contains(other))
                return;

            rayCastForNoAlloc(nodes, ray, ((RectangleCollider)other.getCollider()).getRectangle());
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
                (int) (Math.floor(position.x / chunkSize.x) + round(Mathd.Clamp01(-position.x))),
                (int) (Math.floor(position.y / chunkSize.y) + 1));

        for (int x = -2; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Chunk check = world.get(chunkPoint.x + x, chunkPoint.y + y);

                if (check != null) {

                    for (int i = 0; i < block_row; i++) {
                        for (int j = 0; j < block_row; j++) {
                            Point p = new Point( i, j);
                            Block block = check.getBlock(p);

                            if (block != null && !block.getBlockData().isFloor()) {
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
            if (other.getCollider() == null || !(other.getCollider() instanceof RectangleCollider)) return;
            if (((RectangleCollider)other.getCollider()).getRectangle().intersects(getRect(position, size)))
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

        if (Mathd.distance(ray.origin, P) > ray.length)
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

    public static void registerCollisionResolver(ICollisionResolver resolver) {
        resolvers.add(resolver);

        Collections.sort(resolvers, new Comparator<ICollisionResolver>() {
            @Override
            public int compare(ICollisionResolver o1, ICollisionResolver o2) {
                return o1.priority() > o2.priority() ? -1 : (o1.priority() == o2.priority() ? 0 : -1);
            }
        });
    }

    void FixedUpdate(double fixedDelta) {
        if (SceneManager.hasScene()) {
            SceneManager.getSceneNodes().forEach(node -> {
                if (!node.isAlive()) return;
                CheckCollisionFor(node, fixedDelta);
                CheckCollisionForChunk(node, fixedDelta);
            });
        }
    }

    private void CheckCollisionFor(Node node, double fixedDelta) {
        if (node.getRigidbody() == null) return;
        Vector2d oldposition = node.transform2D().getPosition();
        VelocityUpdate(node, fixedDelta);
        ApplyVelocity(node, oldposition, fixedDelta);

        if (node.getCollider() == null) return;
        SceneManager.getSceneNodes().forEach((other) -> {
            if (!other.isAlive()) return;
            if (other.getCollider() == null) return;
            if (other == node) return;

            one.node = node;
            one.collider = node.getCollider();
            two.node = other;
            two.collider = other.getCollider();

            for (ICollisionResolver resolver : resolvers) {
                if (resolver.hasCorrectCollider(node.getCollider(), other.getCollider())) {
                    if (resolver.isInRange(one, two)) {
                        resolver.resolve(one, two);
                    }
                    break;
                }
            }
        });
    }

    private void CheckCollisionForChunk(Node node, double fixedDelta) {

        if (node.getRigidbody() == null) return;

        if (node.getCollider() == null) return;

        ICollisionResolver selected = null;

        for (ICollisionResolver resolver : resolvers) {
            if (resolver.supportsBlocks(node.getCollider())) {
                selected = resolver;
                break;
            }
        }

        if (selected == null)
            return;

        one.node = node;
        one.collider = node.getCollider();

        Vector2d chunkSize = Chunk.getSize();

        World world = World.getCurrent();

        int block_row = SceneManager.getCurrentlyLoaded().getChunkSize();

        if (world == null)
            return;

        Point chunkPoint = new Point(
                (int) (Math.floor(node.transform2D().getPosition().x / chunkSize.x) + round(Mathd.Clamp01(-node.transform2D().getPosition().x))),
                (int) (Math.floor(node.transform2D().getPosition().y / chunkSize.y) + 1));

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Chunk check = world.get(chunkPoint.x + x, chunkPoint.y + y);

                if (check != null) {

                    for (int i = 0; i < block_row; i++) {
                        for (int j = 0; j < block_row; j++) {
                            Point p = new Point( i, j);
                            Block block = check.getBlock(p);

                            if (block != null && block.getBlockData().hasCollision())
                                selected.resolve(one, block);
                        }
                    }
                }
            }
        }
    }

    private static double round(double a) {
        return Math.floor(Math.abs(a) + 0.5) * Math.signum(a);
    }

    public static Rectangle2D getBlockCollider(Block block) {
        double blockScale = Chunk.BLOCK_SCALE;
        Vector2d blockPosition = block.getPosition();
        blockCollider.setRect(blockPosition.x, blockPosition.y - blockScale, blockScale, blockScale);
        return blockCollider;
    }

    private void VelocityUpdate(Node node, double fixedDelta) {
        assert node.getRigidbody() != null;
        node.getRigidbody().addVelocity(gravity.mul(fixedDelta));

        node.getRigidbody().setVelocity(Mathd.Lerp(node.getRigidbody().getVelocity(), new Vector2d(), fixedDelta / 15.0));
    }

    private void ApplyVelocity(Node node, Vector2d old, double fixedDelta) {
        assert node.getRigidbody() != null;

        Vector2d position = node.transform2D().getPosition().add(node.getRigidbody().getVelocity().mul(fixedDelta));

        if (node.getCollider() != null) {
            one.node = node;
            one.collider = node.getCollider();

            for (ICollisionResolver resolver : resolvers) {
                if (resolver.hasCorrectCollider(node.getCollider(), node.getCollider())) {
                    if (resolver.Interpolation(position, old, one))
                        return;
                    break;
                }
            }
        }

        node.transform2D().setPosition(position);
    }
}
