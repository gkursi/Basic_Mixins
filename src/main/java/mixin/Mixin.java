package mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The mixin market used to mark a class that should be
 * scanned for methods annotated with the @Injectable annotation.
 * These classes still have to be registered in the Mixin registry.
 * @Data targetClass - the class where the mixin target methods are
 * @see Injectable
 * @see Registry
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Mixin {
    Class<?> targetClass();
}
