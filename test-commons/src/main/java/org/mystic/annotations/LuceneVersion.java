package org.mystic.annotations;

import java.lang.annotation.*;

/**
 * Annotation to specify which Lucene versions a test applies to.
 *
 * Example usage:
 * <pre>
 * {@literal @}LuceneVersion(min = "4.0", max = "8.x")
 * public class BM25Test {
 *     // test that works across all versions
 * }
 *
 * {@literal @}LuceneVersion(min = "6.0")
 * public class PointValuesTest {
 *     // test for features introduced in Lucene 6
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface LuceneVersion {

  /**
   * Minimum Lucene version required (e.g., "4.0", "6.0")
   */
  String min() default "4.0";

  /**
   * Maximum Lucene version supported (e.g., "8.x", "7.99")
   */
  String max() default "99.x";

  /**
   * Specific versions this test applies to
   * If specified, overrides min/max
   */
  String[] versions() default {};
}
