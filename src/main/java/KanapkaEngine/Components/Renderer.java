package KanapkaEngine.Components;

import KanapkaEngine.Editor.Attributes.Serialized;
import KanapkaEngine.Game.Component;
import KanapkaEngine.Game.Logger;
import org.joml.Matrix3f;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

/**
 * The renderer component allows for defining a texture for the node to be drawn to the screen. This is a component and has to be added to the node for it to be recognized. Only the last added renderer will be recognized on a node when rendering. <br><br>
 * The <strong><code>Renderer</code></strong> component allows for the <strong><code>getRender()</code></strong> function to be overriden and modified to create a custom render output. Here only a <strong><code>BufferedImage</code></strong> can be output using this function and in order to completely override the rendering of nodes, it can be done using the <strong><code>RenderLayer</code></strong> interface.
 */
public class Renderer extends Component {
    private static final Logger logger = new Logger("Renderer");
    private final Rectangle2D.Double m_Bounds = new Rectangle2D.Double();

    private static final vec2d min = new vec2d();
    private static final vec2d max = new vec2d();

    private static final Vector3f TOPRIGHT = new Vector3f(0.5f, 0.5f, 1.0f);
    private static final Vector3f BOTTOMLEFT = new Vector3f(-0.5f, -0.5f, 1.0f);

    private static final Vector3f topRight = new Vector3f();
    private static final Vector3f bottomLeft = new Vector3f();

    @Serialized
    private Material material;

    public final Material getMaterial() {
        return material;
    }
    public final void setMaterial(final Material material) {
        this.material = material;
    }

    public enum Stage {
        NOTSTARTED,
        RENDERING,
        FINISHED,
        READYTOBIND,
        BOUND
    }

    public Rectangle2D.Double bounds() {

        Matrix3f matrix3f = getParent().transform2D().getTransformation().get2DMatrix();

        matrix3f.transform(TOPRIGHT, topRight);
        matrix3f.transform(BOTTOMLEFT, bottomLeft);

        min.set(Math.min(topRight.x, bottomLeft.x), Math.min(topRight.y, bottomLeft.y));

        max.set(Math.max(topRight.x, bottomLeft.x), Math.max(topRight.y, bottomLeft.y));

        Vector2d ms = max.sub(min);

        ms.set(Math.abs(ms.x), Math.abs(ms.y));

        m_Bounds.setRect(min.x, min.y, ms.x, ms.y);

        return m_Bounds;
    }
}