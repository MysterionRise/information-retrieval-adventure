package org.mystic.annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark experimental or exploratory code.
 * Helps distinguish between stable test infrastructure and experimental trials.
 *
 * Example usage:
 * <pre>
 * {@literal @}ExperimentalCode(
 *     purpose = "Testing custom BM25F implementation",
 *     status = "Work in Progress"
 * )
 * public class BM25FExperiment {
 *     public static void main(String[] args) {
 *         // experimental code
 *     }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface ExperimentalCode {

  /**
   * Purpose of the experiment
   */
  String purpose();

  /**
   * Current status (e.g., "Work in Progress", "Completed", "Abandoned")
   */
  String status() default "Active";

  /**
   * Additional notes
   */
  String notes() default "";

  /**
   * Author of the experiment
   */
  String author() default "";
}
