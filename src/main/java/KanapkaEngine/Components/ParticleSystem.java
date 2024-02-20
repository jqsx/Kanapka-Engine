package KanapkaEngine.Components;

import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Iterator;
import java.util.LinkedList;

public class ParticleSystem extends Renderer {
    private final LinkedList<Particle> particles = new LinkedList<>();
    private static final int UPDATE_RATE = 60;

    private double last_update = Time.time();

    public ParticleSystem() {

    }

    public Iterator<Particle> getIterator() {
        return particles.iterator();
    }

    public final void Spawn() {

    }

    public final void SpawnOffset(Vector2D offset) {

    }

    void onSpawn(Vector2D offset) {

    }

    @Override
    public void Update() {
        if (last_update + 1.0 / UPDATE_RATE < Time.time()) {
            double fixedDelta = Time.time() - last_update;

            Loop();
            CheckExpired();

            last_update = Time.time();
        }
    }

    private void Loop() {
        for (Particle particle : particles) {
            UpdateParticle(particle);
        }
    }

    private void CheckExpired() {
        particles.removeIf(particle -> particle.isDead(5.0));
    }

    private void UpdateParticle(Particle particle) {
        onParticleUpdate(particle);
    }

    public void onParticleUpdate(Particle particle) {

    }
}
