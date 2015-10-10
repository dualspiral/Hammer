package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Signifies to the command processor that the marked command can run async, unless specifically set otherwise.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RunAsync {
    boolean isAsync() default true;
}
