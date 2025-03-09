package KanapkaEngine.Game;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface WIP {
    String value() default "Work in progress feature";
}
