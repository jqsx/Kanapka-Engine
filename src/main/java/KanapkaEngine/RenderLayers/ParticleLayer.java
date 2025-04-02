package KanapkaEngine.RenderLayers;

import KanapkaEngine.Game.Particle;
import KanapkaEngine.Game.ParticleSystem;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.*;
import org.joml.Matrix3f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ParticleLayer implements RenderLayer {

    private static final Logger logger = new Logger("ParticleLAyer");

    private static final List<Renderer> m_Renderers = new ArrayList<>();

    private static final Transformation transformation = new Transformation(new Vector3d(), new Vector3d(), new Vector3f());

    @Override
    public void Render() {
        if (SceneManager.hasScene()) {
            SceneManager.getSceneNodes().forEach(this::RenderForNode);
        }
    }

    private void RenderForNode(Node node) {
        if (node.getRenderer() != null) {
            node.getComponents(m_Renderers, Renderer.class);

            m_Renderers.forEach(renderer -> Render(node, renderer));
        }
    }

    private void Render(Node node, Renderer renderer) {
        if (renderer.getMaterial() == null)
            return;
        if (renderer.getMaterial().getShader() == null)
            return;

        if (renderer instanceof ParticleSystem<?>) {
            ParticleSystem<Particle> system = (ParticleSystem<Particle>) renderer;

            if (!Camera.main.isWithin(renderer))
                return;

            if (NodeLayer.DRAW_WIREFRAME) {
                Rectangle2D.Double rect = renderer.bounds();

                Graphics.WIREFRAME_COLOR.set(0.5f, 1.f, 0.f);
                Graphics.DrawWireframe(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0, rect.width, rect.height, 0.f);

                Graphics.WIREFRAME_COLOR.set(1f, 0.f, 0.f);
                Graphics.DrawWireframe(rect.x + rect.width * 1.5, rect.y + rect.height * 1.5, rect.width * 2.0, rect.height * 2.0, 0.f);

                Graphics.WIREFRAME_COLOR.set(1f, 0.f, 1.f);
            }

            if (system.isDrawInstanced()) {
                system.BufferParticleLocations();

                //Graphics.DrawInstancedSprite(renderer.getMaterial().getShader(), renderer.getTexture(), node.transform.getTransformation(), ParticleSystem.getInstancedMesh());
            }
            else {
                List<Particle> particles = system.getList();

                Matrix3f center = node.transform.getTransformation().get2DMatrix();

                Vector3f p = new Vector3f();

                Vector2d p1 = new Vector2d();
                Vector2d one = new Vector2d(1, 1);

                particles.forEach(particle -> {
                    p.set(particle.getPosition().x, particle.getPosition().y, 0.0);
                    p1.set(center.transform(p));

                    transformation.Update(p1, one, 0.f);

                    Graphics.DrawSpriteMesh(transformation, renderer.getMaterial());
                });
            }
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.PARTICLES;
    }
}
