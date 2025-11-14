package org.mystic.annotations;

import java.lang.annotation.*;

/**
 * Annotation to link test/code to a Stack Overflow question.
 * Useful for tracking which code was written to answer SO questions.
 *
 * Example usage:
 * <pre>
 * {@literal @}RelatedStackOverflowQuestion(
 *     questionId = "12345678",
 *     url = "https://stackoverflow.com/questions/12345678/..."
 * )
 * public class CustomAnalyzerTest {
 *     // test related to SO question
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface RelatedStackOverflowQuestion {

  /**
   * Stack Overflow question ID
   */
  String questionId() default "";

  /**
   * Full URL to the Stack Overflow question
   */
  String url() default "";

  /**
   * Brief description of what the question was about
   */
  String description() default "";
}
