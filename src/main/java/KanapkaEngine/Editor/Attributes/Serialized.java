package KanapkaEngine.Editor.Attributes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Serialized {
    String methodName() default "";
}
