package org.mystic.annotations;

import java.lang.annotation.*;

/**
 * Annotation to document what feature a test class or method is testing.
 * Helps with test discovery and documentation generation.
 *
 * Example usage:
 * <pre>
 * {@literal @}TestedFeature("Phonetic Search with ICU Transliteration")
 * public class PhoneticSearchTest {
 *     // test methods
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface TestedFeature {

  /**
   * Description of the feature being tested
   */
  String value();

  /**
   * Category of the feature (e.g., "Analyzers", "Scoring", "Indexing")
   */
  String category() default "";

  /**
   * Additional notes about the test
   */
  String notes() default "";
}
