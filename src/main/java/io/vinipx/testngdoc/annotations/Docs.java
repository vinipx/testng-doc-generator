package io.vinipx.testngdoc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify tags/features/capabilities for a test method.
 * These will be displayed in the generated documentation to provide additional
 * context about what the test is validating.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Docs {
    /**
     * Array of tags/features/capabilities that this test method is validating.
     * @return array of tag strings
     */
    String[] tags() default {};
}
