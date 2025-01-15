package KanapkaEngine.Game;

import KanapkaEngine.Components.Block;
import org.joml.Vector2d;

public interface ICollisionResolver {
    /**
     *
     * @return Tests if the Node object has a collider of selected and if the resolver is able to process the colliders.
     */
    boolean hasCorrectCollider(ICollider node, ICollider other);
    boolean supportsBlocks(ICollider node);

    /**
     * Optimization function to test if the object is in range and for it to be
     * allowed to be forwarded to the resolve method.
     * @return If the Node object is in range.
     */
    boolean isInRange(CollisionData node, CollisionData other);

    /**
     *
     * @param node Has both a collider and a rigidbody component attached
     * @param other Has a collider object
     * @return If the resolve was successful so that it doesn't get forwarded to the rest of prepared collision resolvers
     */
    void resolve(CollisionData node, CollisionData other);
    void resolve(CollisionData node, Block other);

    /**
     * @return The resolver priority
     */
    int priority();

    /**
     * If true then the current velocity update doesn't happen.
     * @param position
     * @param old
     * @param node
     * @return
     */
    boolean Interpolation(Vector2d position, Vector2d old, CollisionData node);

    class CollisionData {
        public Node node;
        public ICollider collider;
    }
}
