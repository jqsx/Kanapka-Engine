package KanapkaEngine.Game;

import KanapkaEngine.Components.*;
import org.joml.Vector2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class Physics {
    public static Vector2d gravity = new Vector2d(0, -9.81);

    public static AudioClip hit = ResourceLoader.loadAudio("Audio/boxHit.wav");

    private static Rectangle2D blockCollider = new Rectangle2D.Double(-8, -8, 16, 16);

    private static List<Vector2d> rayCastFor(Ray ray, Rectangle rect) {
        List<Vector2d> intersections = new ArrayList<>();
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y);
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, 0.0));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, 0.0));
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, rect.height));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y).add(new Vector2d(rect.width, rect.height));
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(0.0, rect.height));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }
        {
            Vector2d p1 = new Vector2d(rect.x, rect.y);
            Vector2d p2 = new Vector2d(rect.x, rect.y).add(new Vector2d(0.0, rect.height));
            Vector2d a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }

        return intersections;
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
        List<Node> nodes = new ArrayList<>();
        SceneManager.getSceneNodes().foreach((other) -> {
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

    private record Ray(Vector2d origin, Vector2d direction, double length) {

    }

    protected final void FixedUpdate(double fixedDelta) {
        if (SceneManager.hasScene()) {
            try {
                TSLinkedList<Node>.Element last = SceneManager.getSceneNodes().getRoot();

                while (last != null) {
                    Node node = last.getValue();

                    CheckCollisionFor(node, fixedDelta);

                    _CheckCollisionForChunk(node, fixedDelta);

                    last = last.getNext();
                }
            } catch (ConcurrentModificationException ignore) {

            }
        }
    }

    private void CheckCollisionFor(Node node, double fixedDelta) {
        if (node.getRigidbody() == null) return;
        VelocityUpdate(node, fixedDelta);

        if (node.getCollider() == null) return;
        SceneManager.getSceneNodes().foreach((other) -> {
            if (other.getCollider() == null) return;
            if (other == node) return;
            if (other.getCollider().isColliding(node.getCollider()))
                ProcessCollision(node, other, fixedDelta);
        });
        ApplyVelocity(node, fixedDelta);
    }

    private void _CheckCollisionForChunk(Node node, double fixedDelta) {

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
        node.transform.setPosition(node.transform.getPosition().add(node.getRigidbody().getVelocity().mul(fixedDelta)));
    }

    private void ProcessCollision(Node node, Node other, double fixedDelta) {

        if (!node.isAlive() || !other.isAlive()) return;

        Collider nodeCollider = node.getCollider();
        Collider otherCollider = other.getCollider();

        if (nodeCollider == null || otherCollider == null) return;

        if (!(nodeCollider.collideNodes && otherCollider.collideNodes)) return;

        if (otherCollider.noMass && !nodeCollider.noMass) return;

        Vector2d nodeSize = nodeCollider.getScaledSize();
        Vector2d otherSize = otherCollider.getScaledSize();

        Vector2d nodeDiff = node.transform.getPosition().sub(other.transform.getPosition());//.add(new Vector2d( node.transform.getSize().x / 2.0, 0)).sub(other.transform.getPosition().add(new Vector2d( other.transform.getSize().x / 2.0, 0)));
        Vector2d combinedScale = nodeSize.add(otherSize);
        Vector2d nodeDiffScaled = new Vector2d(nodeDiff.x / combinedScale.x, nodeDiff.y / combinedScale.y);

        Vector2d position = node.transform.getPosition();
        Vector2d velocity = node.getRigidbody().getVelocity();

/*        {
            Rigidbody rb = other.getRigidbody();
            if (rb != null) {
                Vector2d v = velocity.mul(node.getRigidbody().getBounce() / rb.getMass());
                rb.addVelocity(v);
            }


        }*/

        if (Math.abs(nodeDiffScaled.y) < Math.abs(nodeDiffScaled.x)) {
            position = new Vector2d((position.x + (otherSize.x / 2.0 + nodeSize.x / 2.0 - Math.abs(nodeDiff.x)) * Math.signum(nodeDiff.x)), position.y);
            velocity = new Vector2d((-velocity.x * node.getRigidbody().getBounce() * Math.signum(nodeDiff.x)), velocity.y);
        }
        else {
            position = new Vector2d(position.x, (position.y + (otherSize.y / 2.0 + nodeSize.y / 2.0 - Math.abs(nodeDiff.y)) * Math.signum(nodeDiff.y)));
            velocity = new Vector2d(velocity.x, (-velocity.y * node.getRigidbody().getBounce() * Math.signum(nodeDiff.y)));
        }

        node.transform.setPosition(position);
        node.getRigidbody().setVelocity(velocity);
    }

    private void ProcessCollision(Node node, Block other, double fixedDelta) {

        if (!node.isAlive()) return;

        Rectangle2D otherCollider = getBlockCollider(other);

        Collider nodeCollider = node.getCollider();

        if (nodeCollider == null || otherCollider == null) return;

        if (!otherCollider.intersects(nodeCollider.getRectangle())) return;

        Vector2d nodeSize = nodeCollider.getScaledSize();
        Vector2d otherSize = new Vector2d(Chunk.BLOCK_SCALE, Chunk.BLOCK_SCALE);

        Vector2d otherColliderPosition = new Vector2d(otherCollider.getX() + otherCollider.getWidth() / 2.0, otherCollider.getY() + otherCollider.getHeight() / 2.0);

//.add(new Vector2d( node.transform.getSize().x / 2.0, 0))
        Vector2d nodeDiff = node.transform.getPosition().sub(otherColliderPosition);
        Vector2d combinedScale = nodeSize.add(otherSize);
        Vector2d nodeDiffScaled = new Vector2d(nodeDiff.x / combinedScale.x, nodeDiff.y / combinedScale.y);

        Vector2d position = node.transform.getPosition();
        Vector2d velocity = node.getRigidbody().getVelocity();

        if (Math.abs(nodeDiffScaled.y) < Math.abs(nodeDiffScaled.x)) {
            position = new Vector2d((position.x + (otherSize.x / 2.0 + nodeSize.x / 2.0 - Math.abs(nodeDiff.x)) * Math.signum(nodeDiff.x)), position.y);
            velocity = new Vector2d((-velocity.x * node.getRigidbody().getBounce() * Math.signum(nodeDiff.x)), velocity.y);
        }
        else {
            position = new Vector2d(position.x, (position.y + (otherSize.y / 2.0 + nodeSize.y / 2.0 - Math.abs(nodeDiff.y)) * Math.signum(nodeDiff.y)));
            velocity = new Vector2d(velocity.x, (-velocity.y * node.getRigidbody().getBounce() * Math.signum(nodeDiff.y)));
        }

//        playSound(node.transform.getPosition(), position);

        node.transform.setPosition(position);
        node.getRigidbody().setVelocity(velocity);
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
