package KanapkaEngine.Game;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public final class Transformation {
    public Vector3d position;
    public Vector3d scale;
    public Vector3f rotation;

    public Transformation(Vector3d position, Vector3d scale, Vector3f rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Transformation(Vector2D position2d, Vector2D scale2d, float rotation) {
        this(new Vector3d(position2d.getX(), position2d.getY(), 0.0), new Vector3d(scale2d.getX(), scale2d.getY(), 1.0), new Vector3f(0, 0, rotation));
    }

    public void Update(Vector2D position2d, Vector2D scale2d, float rotation) {
        position.set(position2d.getX(), position2d.getY(), 0.0);
        scale.set(scale2d.getX(), scale2d.getY(), 1.0);
        this.rotation.set(0, 0, rotation);
    }

    public void Update(Vector3d position, Vector3d scale, Vector3f rotation) {
        this.position.set(position);
        this.scale.set(scale);
        this.rotation.set(rotation);
    }

    public Matrix4f getFinalMat(Matrix4f model, Vector3d cameraLocation, Matrix4f projection) {
        model = model.identity()
                .translate(new Vector3f((float)(position.x - cameraLocation.x),(float)(position.y - cameraLocation.y),(float)(position.z - cameraLocation.z)))
                .rotateAffineXYZ(rotation.x, rotation.y, rotation.z)
                .scale(new Vector3f((float) scale.x, (float) scale.y, (float) scale.z));
        return model.mul(projection);
    }
}
