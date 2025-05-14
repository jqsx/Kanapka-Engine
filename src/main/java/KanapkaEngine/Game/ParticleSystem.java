package KanapkaEngine.Game;

import KanapkaEngine.Components.IUpdate;
import KanapkaEngine.Components.Material;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Components.TextureMaterial;
import KanapkaEngine.Editor.Attributes.ReadOnly;
import KanapkaEngine.Editor.Attributes.Serialized;
import KanapkaEngine.Editor.Attributes.ShowMethods;
import org.joml.*;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

@WIP("Allow for allocation of objects into an arraylist and setting of maximum particles in the system")
public class ParticleSystem<T extends Particle> extends Renderer implements IUpdate {

    private static final Logger logger = new Logger("ParticleSystem");
    private final Rectangle2D.Double m_Bounds = new Rectangle2D.Double();

    private static final Transformation transformation = new Transformation(new Vector3d(), new Vector3d(1,1,1), new Vector3f());

    private static final Vector3f TOPRIGHT = new Vector3f(0.5f, 0.5f, 1.0f);
    private static final Vector3f BOTTOMLEFT = new Vector3f(-0.5f, -0.5f, 1.0f);

    private static final Vector2d max = new Vector2d();
    private static final Vector2d min = new Vector2d();

    private static final Vector3f topRight = new Vector3f();
    private static final Vector3f bottomLeft = new Vector3f();

    private static final Matrix3f output_mat = new Matrix3f().identity();

    private final List<T> particles = new ArrayList<>();

    public boolean isLoop = false;
    public double loopEmitDelay = 1.0;

    private double lastEmit = Time.time();

    @Serialized
    private int UPDATE_RATE = 60;

    private int MAX_PARTICLES = 500;

    @Serialized
    public double LIFETIME = 5.0;

    @ReadOnly
    private double last_update = Time.time();

    @Serialized
    private static AttributeElementBuffer instancedMesh;

    private boolean isDrawInstanced = false;

    private float[] bufferedFloats = new float[0];

    public ParticleSystem(int max_particles) {
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

        setMaxParticles(max_particles);
    }

    public ParticleSystem() {
        this(500);
    }

    public final void setMaxParticles(int count) {
        if (particles.size() < count) {
            int add_particles = count - particles.size();
            for (int index = 0; index < add_particles; index++) {
                particles.add(createParticle());
            }
        }
        else {
            AtomicInteger remainingCount = new AtomicInteger(particles.size() - count);

            particles.removeIf(particle -> {
                boolean v = particle.isDead(getLifeTime(particle)) && remainingCount.get() > 0;

                if (v)
                    remainingCount.decrementAndGet();
                return v;
            });
        }

        if (isDrawInstanced)
            regenerateArray();

        MAX_PARTICLES = count;
    }

    public final void setInstanced() {
        Material material = new Material();

        material.setShader(Shader.findOrCreate("builtIn:particle", "Shader/standard/Particle/particle"));

        setMaterial(material);

        isDrawInstanced = true;

        regenerateArray();
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

    /**
     * You need to override this method in order to implement your own particle class instantiation
     * @return A new particle object
     */
    public T createParticle() {
        return (T) new Particle();
    }

    public final T emit(Vector2d worldSpace, Vector2d v) {
        Objects.requireNonNull(worldSpace);
        T particle = fetchDeadParticle();

        if (particle == null)
            return null;

        if (v != null)
            particle.reset(worldSpace,v);
        else particle.reset(worldSpace);

        onSpawn(particle);

        return particle;
    }

    public final T emit(Vector2d worldSpace) {
        return emit(worldSpace, null);
    }

    public void onSpawn(T instance) {

    }

    public double getLifeTime(T particle) {
        return LIFETIME;
    }

    @Override
    public final void Update() {
        if (last_update + 1.0 / UPDATE_RATE < Time.time()) {
            double fixedDelta = Time.time() - last_update;
            last_update = Time.time();

            Loop(fixedDelta);
            FixedUpdate(fixedDelta);
        }

        if (isLoop && lastEmit + loopEmitDelay < Time.time()) {
            lastEmit = Time.time();

            emit(getParent().transform2D().getPosition());
        }
    }

    public void FixedUpdate(final double fixedDelta) {

    }

    private void Loop(double fixedDelta) {
        max.set(-Double.MAX_VALUE, -Double.MAX_VALUE);
        min.set(Double.MAX_VALUE, Double.MAX_VALUE);
        particles.forEach((particle) -> {
            UpdateParticle(particle, fixedDelta);

            transformation.Update(particle.getPosition(), getParent().transform2D().getPosition().set(1,1), 0.f);
            Matrix3f mat = transformation.get2DMatrix();

            mat.transform(TOPRIGHT, topRight);
            mat.transform(BOTTOMLEFT, bottomLeft);

            if (min.x > bottomLeft.x)
                min.x = bottomLeft.x;
            if (min.y > bottomLeft.y)
                min.y = bottomLeft.y;

            if (max.x < topRight.x)
                max.x = topRight.x;
            if (max.y < topRight.y)
                max.y = topRight.y;
        });
    }

    @Override
    public Rectangle2D.Double bounds() {
        m_Bounds.setRect(min.x, min.y, max.x - min.x, max.y - min.y);

        return m_Bounds;
    }

    private void regenerateArray() {
        bufferedFloats = new float[particles.size() * 2];
    }

    public void BufferParticleLocations() {
        if (!isDrawInstanced)
            return;

        if (particles.size() != bufferedFloats.length * 2)
            regenerateArray();

        for (int index = 0; index < particles.size(); index++) {
            T particle = particles.get(index);

            int bufferedIndex = index * 2;

            bufferedFloats[bufferedIndex] = (float) particle.getPosition().x;
            bufferedFloats[bufferedIndex + 1] = (float) particle.getPosition().y;
        }

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

    private T fetchDeadParticle() {
        for (T particle : particles) {
            if (particle.isDead(getLifeTime(particle))) {
                return particle;
            }
        }
        return null;
    }

    public static AttributeElementBuffer getInstancedMesh() {
        return instancedMesh;
    }
}
