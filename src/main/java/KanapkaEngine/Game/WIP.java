package KanapkaEngine.Game;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface WIP {
    String value() default "Work in progress feature";
}
