package mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The marker for inject methods.
 * Inject methods must be similar in all ways
 * (other than the name and return value) to the target method.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Injectable {
    String targetMethod();
    String position();
}
