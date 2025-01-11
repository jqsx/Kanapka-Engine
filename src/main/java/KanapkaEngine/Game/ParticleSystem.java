package KanapkaEngine.Game;

import KanapkaEngine.Components.IUpdate;
import KanapkaEngine.Components.Material;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Components.TextureMaterial;
import KanapkaEngine.Editor.Attributes.ReadOnly;
import KanapkaEngine.Editor.Attributes.Serialized;
import KanapkaEngine.Editor.Attributes.ShowMethods;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticleSystem<T extends Particle> extends Renderer implements IUpdate {

    private static final Logger logger = new Logger("ParticleSystem");
    private final Rectangle2D.Double m_Bounds = new Rectangle2D.Double();

    private final List<T> particles = new ArrayList<>();
    @Serialized
    private int UPDATE_RATE = 60;

    @ReadOnly
    private double last_update = Time.time();

    @Serialized
    private static AttributeElementBuffer instancedMesh;

    private boolean isDrawInstanced = false;

    private float[] bufferedFloats = new float[0];

    public ParticleSystem() {
        if (instancedMesh == null) {
            instancedMesh = new AttributeElementBuffer();

            instancedMesh.createAttribute("vertices");

            instancedMesh.createAttribute("inst_locations");

            instancedMesh.BufferVec3("vertices",
                    new Vector3f(-0.5f, -0.5f, 0.f),
                    new Vector3f(-0.5f, 0.5f, 0.f),
                    new Vector3f(0.5f, -0.5f, 0.f),
                    new Vector3f(.5f, .5f, 0.f)
            );

            instancedMesh.BufferTriangles(0,2,1,2,3,1);
        }

        setMaterial(new TextureMaterial());
    }

    public final void setInstanced() {
        Material material = new Material();

        material.setShader(Shader.findOrCreate("builtIn:particle", "Shader/standard/Particle/particle"));

        setMaterial(material);

        isDrawInstanced = true;
    }

    public final int getFPS() {
        return UPDATE_RATE;
    }

    public final void setFPS(int f) {
        this.UPDATE_RATE = f;
    }

    public final List<T> getList() {
        return particles;
    }

    public final T Spawn() {
        return SpawnOffset(new Vector2d(0, 0));
    }

    public final T SpawnOffset(Vector2d offset) {
        T t = createParticle(offset);
        particles.add(t);
        onSpawn(t);

        return t;
    }

    public T createParticle(Vector2d offset) {
        return (T) new Particle(offset);
    }

    public void onSpawn(T instance) {

    }

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
            FixedUpdate(fixedDelta);
        }
    }

    public void FixedUpdate(final double fixedDelta) {

    }

    private void Loop(double fixedDelta) {
        particles.forEach((particle) -> {
            UpdateParticle(particle, fixedDelta);
        });
    }

    @Override
    public Rectangle2D.Double bounds() {
        return m_Bounds;
    }

    private void CheckExpired() {
        int count = particles.size();
        particles.removeIf(particle -> particle.isDead(getLifeTime()));

        if (isDrawInstanced)
            if (particles.size() != count) {
                regenerateArray();
            }
    }

    private void regenerateArray() {
        bufferedFloats = new float[particles.size() * 2];
    }

    public void BufferParticleLocations() {
        if (!isDrawInstanced)
            return;

        if (particles.size() != bufferedFloats.length * 2)
            regenerateArray();

        AtomicInteger index = new AtomicInteger();
        particles.forEach(particle -> {
            bufferedFloats[index.get()] = (float) particle.getPosition().x;
            bufferedFloats[index.get() + 1] = (float) particle.getPosition().y;
            index.getAndAdd(2);
        });

        instancedMesh.BufferFloatsInstancedc("inst_locations", bufferedFloats, 2);
    }

    public final boolean isDrawInstanced() {
        return isDrawInstanced;
    }

    private void UpdateParticle(T particle, double fixedDelta) {
        particle.addPosition(particle.getVelocity().mul(fixedDelta));

        ParticleUpdate(particle, fixedDelta);
    }

    public void ParticleUpdate(T particle, final double fixedDelta) {

    }

    public static AttributeElementBuffer getInstancedMesh() {
        return instancedMesh;
    }

    @Serialized
    public void logBufferedFloatsLength() {
        logger.log("Buffered: " + bufferedFloats.length);
        logger.log("Particle List: " + particles.size());
    }
}
