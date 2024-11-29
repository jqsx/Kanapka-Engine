package KanapkaEngine.Components;

import KanapkaEngine.Game.Time;
import org.joml.Vector2d;

import java.awt.image.BufferedImage;

public class ParticleSystem<T extends Particle> extends Renderer {
    private final TSLinkedList<T> particles = new TSLinkedList<>();
    private int UPDATE_RATE = 60;

    private double last_update = Time.time();

    public ParticleSystem() {

    }

    public final int getFPS() {
        return UPDATE_RATE;
    }

    public final void setFPS(int f) {
        this.UPDATE_RATE = f;
    }

    public final TSLinkedList<T> getList() {
        return particles;
    }

    public final T Spawn() {
        return SpawnOffset(new Vector2d(0, 0));
    }

    public final T SpawnOffset(Vector2d offset) {
        T t = createParticle(offset);
        particles.addStart(t);
        onSpawn(t);

        return t;
    }

    public T createParticle(Vector2d offset) {
        return (T) new Particle(offset);
    }

    public void onSpawn(T instance) {

    }
// Todo
//    public BufferedImage getRender(Particle particle) {
//        return rendered_visual;
//    }

    public double getLifeTime() {
        return 5.0;
    }

    @Override
    public final void Update() {
        if (last_update + 1.0 / UPDATE_RATE < Time.time()) {
            double fixedDelta = Time.time() - last_update;
            last_update = Time.time();

            CheckExpired();
            Loop(fixedDelta);
            onUpdate(fixedDelta);
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

        particle.addPosition(particle.getVelocity().mul(fixedDelta));

        onParticleUpdate(particle, fixedDelta);
    }

    public void onParticleUpdate(T particle, final double fixedDelta) {

    }
}
