package KanapkaEngine.Components;

import KanapkaEngine.Editor.EditorActions;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.Scene;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class Physics {
    public static Vector2D gravity = new Vector2D(0, -9.81);

    public static AudioClip hit = ResourceLoader.loadAudio("Audio/boxHit.wav");

    private static Rectangle2D blockCollider = new Rectangle2D.Double(-8, -8, 16, 16);

    private static List<Vector2D> rayCastFor(Ray ray, Rectangle rect) {
        List<Vector2D> intersections = new ArrayList<>();
        {
            Vector2D p1 = new Vector2D(rect.x, rect.y);
            Vector2D p2 = new Vector2D(rect.x, rect.y).add(new Vector2D(rect.width, 0.0));
            Vector2D a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }
        {
            Vector2D p1 = new Vector2D(rect.x, rect.y).add(new Vector2D(rect.width, 0.0));
            Vector2D p2 = new Vector2D(rect.x, rect.y).add(new Vector2D(rect.width, rect.height));
            Vector2D a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }
        {
            Vector2D p1 = new Vector2D(rect.x, rect.y).add(new Vector2D(rect.width, rect.height));
            Vector2D p2 = new Vector2D(rect.x, rect.y).add(new Vector2D(0.0, rect.height));
            Vector2D a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }
        {
            Vector2D p1 = new Vector2D(rect.x, rect.y);
            Vector2D p2 = new Vector2D(rect.x, rect.y).add(new Vector2D(0.0, rect.height));
            Vector2D a = checkSide(ray, p1, p2);
            if (a != null)
                intersections.add(a);
        }

        return intersections;
    }

    private static Vector2D checkSide(Ray ray, Vector2D A, Vector2D B) {
        Vector2D C = ray.origin;
        Vector2D D = ray.origin.add(ray.direction);

        double b = (D.getX() - C.getX()) * (B.getY() - A.getY()) - (B.getX() - A.getX()) * (D.getY() - C.getY());

        double rT = (C.getY() - A.getY()) * (B.getX() - A.getX()) - (C.getX() - A.getX()) * (B.getY() - A.getY());
        double r = rT / b;

        double sT = (A.getX() - C.getX()) * (D.getY() - C.getY()) - (D.getX() - C.getX()) * (A.getY() - C.getY());
        double s = sT / b;

        Vector2D P = null;
        if (s >= 0 && s <= 1)
            P = B.subtract(A).scalarMultiply(s).add(A);
        else if (r >= 0) {
            P = D.subtract(C).scalarMultiply(r).add(C);
        }
        else return null;

        if (Mathf.distance(ray.origin, P) > ray.length)
            return null;

        return P;
    }

    private record Ray(Vector2D origin, Vector2D direction, double length) {

    }

    public final void FixedUpdate(double fixedDelta) {
        if (SceneManager.hasScene()) {
            try {
                LinkedList<Node> temp_nodes = new LinkedList<>(SceneManager.getSceneNodes());
                temp_nodes.forEach(node -> { CheckCollisionFor(node, fixedDelta); CheckCollisionForChunk(node, fixedDelta);});
                temp_nodes.clear();
            } catch (ConcurrentModificationException ignore) {

            }
        }
    }

    private void CheckCollisionFor(Node node, double fixedDelta) {
        if (node.getRigidbody() == null) return;
        VelocityUpdate(node, fixedDelta);
        for (Node other : SceneManager.getSceneNodes()) {
            if (other.getCollider() == null) continue;
            if (other == node) continue;
            if (other.getCollider().isColliding(node.getCollider()))
                ProcessCollision(node, other, fixedDelta);
        }
        ApplyVelocity(node, fixedDelta);
    }

    private void CheckCollisionForChunk(Node node, double fixedDelta) {
        Vector2D chunkSize = Chunk.getSize();
        int blockCount = SceneManager.getCurrentlyLoaded().getChunkSize();
        Point chunkPoint = new Point(
                (int) (Math.floor(node.transform.getPosition().getX() / chunkSize.getX()) + Math.round(Mathf.Clamp01(-node.transform.getPosition().getX()))),
                (int) (Math.floor(node.transform.getPosition().getY() / chunkSize.getY()) + 1));

        Vector2D chunkCenter = new Vector2D(
                chunkPoint.x * chunkSize.getX() + chunkSize.getX() / 2.0,
                chunkPoint.y * chunkSize.getY() + chunkSize.getY() / 2.0
                );

        Vector2D _diff = node.transform.getPosition().subtract(chunkCenter).scalarMultiply(1.0 / chunkSize.getX());
        _diff = new Vector2D(Math.round(_diff.getX()), Math.round(_diff.getY()));

        Point checks = new Point((int) Math.round(_diff.getX()), (int) Math.round(_diff.getY()));

        for (int i = 0; i < 1 + Math.abs(checks.x); i++) {
            for (int j = 0; j < 1 + Math.abs(checks.y); j++) {
                Chunk chunk = World.getCurrent().get(chunkPoint.x + i * Mathf.Clamp(checks.x, -1, 1), chunkPoint.y + j * Mathf.Clamp(checks.y, -1, 1));

                if (chunk != null) {
                    Vector2D chunkPosition = chunk.getPosition();
                    Point blockPoint = new Point(
                            (int)Math.abs(Math.floor((node.transform.getPosition().getX() - chunkPosition.getX()) / Chunk.BLOCK_SCALE)),
                            (int)Math.abs(Math.floor((node.transform.getPosition().getY() - chunkPosition.getY()) / Chunk.BLOCK_SCALE)));


                    for (int k = -1; k < 1; k++) {
                        for (int h = -1; h < 1; h++) {
                            Point p = new Point(blockPoint.x + k, blockPoint.y + h);
                            Block block = chunk.getBlock(p);

                            if (block != null && block.getBlockData().hasCollision)
                                ProcessCollision(node, block, fixedDelta);
                            else if (block == null)
                                chunk.createBlock(1, p);
                        }
                    }
                }
            }
        }
    }
    private Rectangle2D getBlockCollider(Block block) {
        double blockScale = Chunk.BLOCK_SCALE;
        Vector2D blockPosition = block.getPosition();
        blockCollider.setRect(blockPosition.getX() - blockScale, blockPosition.getY() - blockScale, blockScale * 2.0, blockScale * 2.0);
        return blockCollider;
    }

    private void VelocityUpdate(Node node, double fixedDelta) {
        node.getRigidbody().addVelocity(gravity.scalarMultiply(fixedDelta));
    }

    private void ApplyVelocity(Node node, double fixedDelta) {
        node.transform.setPosition(node.transform.getPosition().add(node.getRigidbody().getVelocity().scalarMultiply(fixedDelta)));
    }

    private void ProcessCollision(Node node, Node other, double fixedDelta) {

        if (!node.isAlive() || !other.isAlive()) return;

        Collider nodeCollider = node.getCollider();
        Collider otherCollider = other.getCollider();

        Vector2D nodeSize = nodeCollider.getScaledSize();
        Vector2D otherSize = otherCollider.getScaledSize();

        Vector2D nodeDiff = node.transform.getPosition().add(new Vector2D( node.transform.getSize().getX() / 2.0, 0)).subtract(other.transform.getPosition().add(new Vector2D( other.transform.getSize().getX() / 2.0, 0)));
        Vector2D combinedScale = nodeSize.add(otherSize);
        Vector2D nodeDiffScaled = new Vector2D(nodeDiff.getX() / combinedScale.getX(), nodeDiff.getY() / combinedScale.getY());

        Vector2D position = node.transform.getPosition();
        Vector2D velocity = node.getRigidbody().getVelocity();

        if (Math.abs(nodeDiffScaled.getY()) < Math.abs(nodeDiffScaled.getX())) {
            position = new Vector2D((position.getX() + (otherSize.getX() / 2.0 + nodeSize.getX() / 2.0 - Math.abs(nodeDiff.getX())) * Math.signum(nodeDiff.getX())), position.getY());
            velocity = new Vector2D((-velocity.getX() * node.getRigidbody().getBounce() * Math.signum(nodeDiff.getX())), velocity.getY());
        }
        else {
            position = new Vector2D(position.getX(), (position.getY() + (otherSize.getY() / 2.0 + nodeSize.getY() / 2.0 - Math.abs(nodeDiff.getY())) * Math.signum(nodeDiff.getY())));
            velocity = new Vector2D(velocity.getX(), (-velocity.getY() * node.getRigidbody().getBounce() * Math.signum(nodeDiff.getY())));
        }

        node.transform.setPosition(position);
        node.getRigidbody().setVelocity(velocity);
    }

    private void ProcessCollision(Node node, Block other, double fixedDelta) {

        if (!node.isAlive()) return;

        Rectangle2D otherCollider = getBlockCollider(other);

        Collider nodeCollider = node.getCollider();

        if (!otherCollider.intersects(nodeCollider.getRectangle())) return;

        Vector2D nodeSize = nodeCollider.getScaledSize();
        Vector2D otherSize = new Vector2D(Chunk.BLOCK_SCALE, Chunk.BLOCK_SCALE);

        Vector2D otherColliderPosition = new Vector2D(otherCollider.getX() + otherCollider.getWidth() / 2.0, otherCollider.getY() + otherCollider.getHeight() / 2.0);

        Vector2D nodeDiff = node.transform.getPosition().add(new Vector2D( node.transform.getSize().getX() / 2.0, 0)).subtract(new Vector2D(otherColliderPosition.getX(), otherColliderPosition.getY()).add(new Vector2D( otherSize.getX() / 2.0, 0)));
        Vector2D combinedScale = nodeSize.add(otherSize);
        Vector2D nodeDiffScaled = new Vector2D(nodeDiff.getX() / combinedScale.getX(), nodeDiff.getY() / combinedScale.getY());

        Vector2D position = node.transform.getPosition();
        Vector2D velocity = node.getRigidbody().getVelocity();

        if (Math.abs(nodeDiffScaled.getY()) < Math.abs(nodeDiffScaled.getX())) {
            position = new Vector2D((position.getX() + (otherSize.getX() / 2.0 + nodeSize.getX() / 2.0 - Math.abs(nodeDiff.getX())) * Math.signum(nodeDiff.getX())), position.getY());
            velocity = new Vector2D((-velocity.getX() * node.getRigidbody().getBounce() * Math.signum(nodeDiff.getX())), velocity.getY());
        }
        else {
            position = new Vector2D(position.getX(), (position.getY() + (otherSize.getY() / 2.0 + nodeSize.getY() / 2.0 - Math.abs(nodeDiff.getY())) * Math.signum(nodeDiff.getY())));
            velocity = new Vector2D(velocity.getX(), (-velocity.getY() * node.getRigidbody().getBounce() * Math.signum(nodeDiff.getY())));
        }

//        playSound(node.transform.getPosition(), position);

        node.transform.setPosition(position);
        node.getRigidbody().setVelocity(velocity);
    }

    private void playSound(Vector2D old, Vector2D position) {
        double total = Math.abs(position.getX() - old.getX()) + Math.abs(position.getY() - old.getY());

        if (total > 0.01) {
            hit.clip.setFramePosition(0);
            hit.fc.setValue((float)Math.random() / 4f);
            hit.clip.start();
        }
    }
}
