package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to document design patterns used in classes.
 * Used by ReflectionInspector to analyze and validate design pattern implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PatternInfo {
    
    /**
     * The name of the design pattern
     */
    String patternName();
    
    /**
     * Role of this class/method in the pattern
     */
    String role() default "";
    
    /**
     * Description of why this pattern was chosen
     */
    String description() default "";
}
