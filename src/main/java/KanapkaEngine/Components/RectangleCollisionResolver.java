package KanapkaEngine.Components;

import KanapkaEngine.Game.*;
import org.joml.Vector2d;

import java.awt.geom.Rectangle2D;

import static KanapkaEngine.Game.Physics.rayCastNoAlloc;

public class RectangleCollisionResolver implements ICollisionResolver {
    private static final CollisionData one = new CollisionData();
    private static final CollisionData two = new CollisionData();

    @Override
    public boolean hasCorrectCollider(ICollider node, ICollider other) {
        return node instanceof RectangleCollider && other instanceof RectangleCollider;
    }

    @Override
    public boolean supportsBlocks(ICollider node) {
        return node instanceof RectangleCollider;
    }

    @Override
    public boolean isInRange(CollisionData node, CollisionData other) {
        RectangleCollider nodeRectangleCollider = (RectangleCollider) node.collider;
        RectangleCollider otherRectangleCollider = (RectangleCollider) other.collider;

        return nodeRectangleCollider.isColliding(otherRectangleCollider);
    }

    @Override
    public void resolve(CollisionData node, CollisionData other) {
        RectangleCollider nodeRectangleCollider = (RectangleCollider) node.collider;
        RectangleCollider otherRectangleCollider = (RectangleCollider) other.collider;

        if (nodeRectangleCollider == null || otherRectangleCollider == null) return;

        if (!(nodeRectangleCollider.collideNodes && otherRectangleCollider.collideNodes)) return;

        if (otherRectangleCollider.noMass && !nodeRectangleCollider.noMass) return;

        assert node.node.getRigidbody() != null;

        Vector2d position = node.node.transform.getPosition();
        Vector2d velocity = node.node.getRigidbody().getVelocity();

        resolveCollision(position, velocity, nodeRectangleCollider.getRectangle(), otherRectangleCollider.getRectangle());

        node.node.transform.setPosition(position);
        node.node.getRigidbody().setVelocity(velocity);
    }

    @Override
    public void resolve(CollisionData node, Block other) {
        Rectangle2D otherCollider = Physics.getBlockCollider(other);

        RectangleCollider nodeRectangleCollider = (RectangleCollider) node.collider;

        if (nodeRectangleCollider == null || otherCollider == null) return;

        if (!otherCollider.intersects(nodeRectangleCollider.getRectangle())) return;

        assert node.node.getRigidbody() != null;

        Vector2d position = node.node.transform.getPosition();
        Vector2d velocity = node.node.getRigidbody().getVelocity();

        resolveCollision(position, velocity, nodeRectangleCollider.getRectangle(), (Rectangle2D.Double) otherCollider);

        node.node.transform.setPosition(position);
        node.node.getRigidbody().setVelocity(velocity);
    }

    @Override
    public int priority() {
        return -1;
    }

    @Override
    public boolean Interpolation(Vector2d position, Vector2d old, CollisionData node) {
        double distance = Mathd.aDistance(old, position);
        Vector2d direction = new Vector2d(old.x, old.y).sub(position).mul(1.0 / distance);

        RectangleCollider collider = (RectangleCollider) node.collider;

        one.node = node.node;
        one.collider = node.collider;

        for (Node loop : SceneManager.getSceneNodes()) {

            if (!hasCorrectCollider(loop.getCollider(), collider))
                continue;
            two.node = loop;
            two.collider = loop.getCollider();
            if (!isInRange(one, two))
                continue;
            if (loop.getCollider() == null)
                continue;
            if (loop == node.node)
                continue;
            if (rayCastNoAlloc(old, direction, distance, ((RectangleCollider) two.collider).getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private void resolveCollision(Vector2d outPosition, Vector2d outVelocity, Rectangle2D.Double rect1, Rectangle2D.Double rect2) {
        if (rect1.intersects(rect2)) {
            double overlapX = Math.min(rect1.getMaxX() - rect2.getMinX(), rect2.getMaxX() - rect1.getMinX());
            double overlapY = Math.min(rect1.getMaxY() - rect2.getMinY(), rect2.getMaxY() - rect1.getMinY());

            if (overlapX < overlapY) {
                if (rect1.getCenterX() < rect2.getCenterX()) {
                    outPosition.x -= overlapX;
                } else {
                    outPosition.x += overlapX;
                }
                outVelocity.x *= -0.3;
            }
            else {
                if (rect1.getCenterY() < rect2.getCenterY()) {
                    outPosition.y -= overlapY;
                } else {
                    outPosition.y += overlapY;
                }
                outVelocity.y *= -0.3;
            }
        }
    }
}
