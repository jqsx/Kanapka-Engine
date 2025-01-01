package KanapkaEngine.Game;


import org.joml.*;

import java.lang.Math;

public final class Transformation {
    public Vector3d position;
    public Vector3d scale;
    public Vector3f rotation;

    private static final Matrix4f _projection = new Matrix4f().identity();

    private static final Matrix3f mat2d = new Matrix3f();
    private static final Matrix3f scaleMatrix = new Matrix3f();
    private static final Matrix3f positionMatrix = new Matrix3f();
    private static final Matrix3f rotationMatrix = new Matrix3f();

    public Transformation(Vector3d position, Vector3d scale, Vector3f rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Transformation(Vector2d position2d, Vector2d scale2d, float rotation) {
        this(new Vector3d(position2d.x, position2d.y, 0.0), new Vector3d(scale2d.x, scale2d.y, 1.0), new Vector3f(0, 0, rotation));
    }

    public void Update(Vector2d position2d, Vector2d scale2d, float rotation) {
        position.set(position2d.x, position2d.y, 0.0);
        scale.set(scale2d.x, scale2d.y, 1.0);
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
        _projection.set(projection);
        return _projection.mul(model);
    }

    public Matrix3f get2DMatrix() {
        mat2d.identity();
        positionMatrix.identity();
        rotationMatrix.identity();
        scaleMatrix.identity();
        positionMatrix.m20 = (float) position.x;
        positionMatrix.m21 = (float) position.y;

        rotationMatrix.m00 = (float) Math.cos(rotation.z);
        rotationMatrix.m11 = (float) Math.cos(rotation.z);

        rotationMatrix.m01 = (float) -Math.sin(rotation.z);
        rotationMatrix.m10 = (float) Math.sin(rotation.z);

        scaleMatrix.m00 = (float) scale.x;
        scaleMatrix.m11 = (float) scale.y;

        mat2d.mul(positionMatrix).mul(rotationMatrix).mul(scaleMatrix);

        return mat2d;
    }
}
