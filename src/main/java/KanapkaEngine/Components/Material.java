package KanapkaEngine.Components;

import KanapkaEngine.Game.Shader;
import KanapkaEngine.Game.Texture;
import org.joml.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Extend this class inorder to quickly add your uniforms to a shader during draw time.
 */
public class Material {
    private static final List<Class> permittedClassTypes = List.of(new Class[]{Float.class, Vector2f.class, Vector3f.class, Vector4f.class, Texture.class, Matrix4f.class});

    private List<ValidUniform> ValidatedUniforms = new ArrayList<>();

    private Shader target;

    private boolean WasLocated = false;

    public Material() {
        LocateUniforms();
    }

    private void LocateUniforms() {
        if (WasLocated)
            return;

        Field[] fields = getClass().getFields();

        for (Field field : fields) {
            if ((field.getModifiers() & (Modifier.PROTECTED | Modifier.PRIVATE)) != 0)
                continue;

            Uniform uniform = field.getAnnotation(Uniform.class);

            if (uniform != null && permittedClassTypes.contains(field.getType()) && uniform.isEnabled()) {
                ValidatedUniforms.add(new ValidUniform(uniform.uniformName(), field.getType(), field));
            }
        }

        WasLocated = true;
    }

    /**
     * Sets the located uniform values before rendering (Executed by the renderer before drawing elements)
     */
    public final void Set() {
        if (target == null)
            return;

        for (ValidUniform uniform : ValidatedUniforms) {
            target.bind();
            try {
                if (uniform.target == Float.class) {
                    Float result = uniform.field.getFloat(this);
                    target.setUniform(uniform.targetUniform, result);
                }
                else if (uniform.target == Vector2f.class) {
                    Vector2f result = (Vector2f) uniform.field.get(this);
                    if (result != null)
                        target.setUniform(uniform.targetUniform, result);
                }
                else if (uniform.target == Vector3f.class) {
                    Vector3f result = (Vector3f) uniform.field.get(this);
                    if (result != null)
                        target.setUniform(uniform.targetUniform, result);
                }
                else if (uniform.target == Vector4f.class) {
                    Vector4f result = (Vector4f) uniform.field.get(this);
                    if (result != null)
                        target.setUniform(uniform.targetUniform, result);
                }
                else if (uniform.target == Texture.class) {
                    Texture result = (Texture) uniform.field.get(this);
                    if (result != null)
                        target.setUniform(uniform.targetUniform, result);
                }
                else if (uniform.target == Matrix4f.class) {
                    Matrix4f result = (Matrix4f) uniform.field.get(this);
                    if (result != null)
                        target.setUniform(uniform.targetUniform, result);
                }
            } catch (IllegalAccessException e) {

            }
        }
        target.unbind();
    }

    public void setShader(Shader target) {
        Objects.requireNonNull(target);

        this.target = target;

        for (ValidUniform uniform : ValidatedUniforms) {
            target.createUniform(uniform.targetUniform);
        }
    }

    public Shader getShader() {
        return target;
    }

    private static class ValidUniform {
        private String targetUniform;
        private Class target;
        private Field field;

        ValidUniform(String targetUniform, Class target, Field field) {
            this.targetUniform = targetUniform;
            this.target = target;
            this.field = field;
        }
    }
}
