package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that follow functional programming principles.
 * - Pure functions (no side effects)
 * - Immutable data handling
 * - Higher-order functions
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FunctionalComponent {
    
    /**
     * Indicates if the method is a pure function
     */
    boolean isPure() default true;
    
    /**
     * Description of the functional aspect
     */
    String description() default "";
}
