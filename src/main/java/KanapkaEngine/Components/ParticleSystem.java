package KanapkaEngine.Components;

import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

public class ParticleSystem extends Renderer {
    private final LinkedList<Particle> particles = new LinkedList<>();
    private static final int UPDATE_RATE = 60;

    private double last_update = Time.time();

    public ParticleSystem() {

    }

    public final Iterator<Particle> getIterator() {
        return particles.iterator();
    }

    public final void Spawn() {
        SpawnOffset(new Vector2D(0, 0));
    }

    public final void SpawnOffset(Vector2D offset) {
        Particle particle = new Particle(getParent().transform.getPosition().add(offset));
        particles.add(particle);
        onSpawn(particle);
    }

    void onSpawn(Particle instance) {

    }

    @Override
    public BufferedImage getRender() {
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

            last_update = Time.time();
        }
    }

    private void Loop(double fixedDelta) {
        for (Particle particle : particles) {
            UpdateParticle(particle, fixedDelta);
        }
    }

    private void CheckExpired() {
        particles.removeIf(particle -> particle.isDead(getLifeTime()));
    }
    private void UpdateParticle(Particle particle, double fixedDelta) {
        onParticleUpdate(particle, fixedDelta);
    }

    public void onParticleUpdate(Particle particle, double fixedDelta) {

    }
}
