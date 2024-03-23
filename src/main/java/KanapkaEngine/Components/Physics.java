package KanapkaEngine.Components;

import KanapkaEngine.Editor.EditorActions;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.Scene;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Physics {
    public static Vector2D gravity = new Vector2D(0, -9.81);

    public static AudioClip hit = ResourceLoader.loadAudio("Audio/boxHit.wav");

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
            SceneManager.getSceneNodes().descendingIterator().forEachRemaining(node -> CheckCollisionFor(node, fixedDelta));
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

    private void VelocityUpdate(Node node, double fixedDelta) {
        node.getRigidbody().addVelocity(gravity.scalarMultiply(fixedDelta));
    }

    private void ApplyVelocity(Node node, double fixedDelta) {
        node.transform.setPosition(node.transform.getPosition().add(node.getRigidbody().getVelocity().scalarMultiply(fixedDelta)));
    }

    private void ProcessCollision(Node node, Node other, double fixedDelta) {
        // figure out collision logic

        Collider nodeCollider = node.getCollider();
        Collider otherCollider = other.getCollider();

        Vector2D nodeSize = nodeCollider.getScaledSize();
        Vector2D otherSize = otherCollider.getScaledSize();

        Vector2D nodeDiff = node.transform.getPosition().subtract(other.transform.getPosition());
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

        playSound(node.transform.getPosition(), position);

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
