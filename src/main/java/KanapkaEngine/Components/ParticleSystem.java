package KanapkaEngine.Components;

import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

public class ParticleSystem<T extends Particle> extends Renderer {
    private final TSLinkedList<T> particles = new TSLinkedList<>();
    private static final int UPDATE_RATE = 60;

    private double last_update = Time.time();

    private Iterator<T> _iterator;

    public ParticleSystem() {

    }

    public final TSLinkedList<T> getList() {
        return particles;
    }

    public final void Spawn() {
        SpawnOffset(new Vector2D(0, 0));
    }

    public final void SpawnOffset(Vector2D offset) {
        T t = createParticle(offset);
        particles.addEnd(t);
        onSpawn(t);
    }

    public T createParticle(Vector2D offset) {
        return null;
    }

    public void onSpawn(T instance) {

    }

    public BufferedImage getRender(Particle particle) {
        return rendered_visual;
    }

    public double getLifeTime() {
        return 5.0;
    }

    @Override
    public final void Update() {
        if (last_update + 1.0 / UPDATE_RATE < Time.time()) {
            double fixedDelta = Time.time() - last_update;

            Loop(fixedDelta);
            CheckExpired();
            onUpdate(fixedDelta);

            last_update = Time.time();
        }
    }

    public void onUpdate(double fixedDelta) {

    }

    private void Loop(double fixedDelta) {
        particles.foreach((particle) -> {
            UpdateParticle(particle, fixedDelta);
        });
    }

    private void CheckExpired() {
        particles.removeIf(particle -> particle.isDead(getLifeTime()));
    }
    private void UpdateParticle(T particle, double fixedDelta) {
        onParticleUpdate(particle, fixedDelta);
    }

    public void onParticleUpdate(T particle, final double fixedDelta) {

    }
}
