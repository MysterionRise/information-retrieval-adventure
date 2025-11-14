package org.mystic.base;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.mystic.version.LuceneVersionDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class for all Lucene tests across versions.
 * Provides common setup/teardown, temp directory management, and version detection.
 *
 * Subclasses should override:
 * - getExpectedLuceneVersion() - to specify which version they're testing
 * - Additional setup/teardown as needed
 */
public abstract class LuceneVersionTestBase {

  @Rule
  public TestName testName = new TestName();

  protected Path tempDir;
  private long startTime;

  /**
   * Returns the expected Lucene version for this test module.
   * Example: "4.10.4", "8.10.1"
   */
  protected abstract String getExpectedLuceneVersion();

  @Before
  public void baseSetUp() throws IOException {
    startTime = System.currentTimeMillis();
    System.out.println("\n=== Starting Test: " + testName.getMethodName() + " ===");
    System.out.println("Expected Lucene Version: " + getExpectedLuceneVersion());
    System.out.println("Detected Lucene Version: " + LuceneVersionDetector.detectLuceneVersion());

    // Create temp directory for this test
    tempDir = Files.createTempDirectory("lucene-test-" + testName.getMethodName());
    System.out.println("Temp directory: " + tempDir);
  }

  @After
  public void baseTearDown() throws IOException {
    // Clean up temp directory
    if (tempDir != null && Files.exists(tempDir)) {
      deleteRecursively(tempDir.toFile());
    }

    long duration = System.currentTimeMillis() - startTime;
    System.out.println("=== Test Completed in " + duration + "ms ===\n");
  }

  /**
   * Gets the major version number of the Lucene version being tested
   */
  protected int getLuceneMajorVersion() {
    return LuceneVersionDetector.getMajorVersion(getExpectedLuceneVersion());
  }

  /**
   * Checks if the current version supports a specific feature.
   * Subclasses can override to provide version-specific feature detection.
   */
  protected boolean supportsFeature(String featureName) {
    // Default implementation - subclasses should override
    return true;
  }

  /**
   * Recursively delete a directory and all its contents
   */
  protected void deleteRecursively(File file) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File child : files) {
          deleteRecursively(child);
        }
      }
    }
    file.delete();
  }

  /**
   * Asserts that the test is running on an expected version range
   */
  protected void requireMinimumVersion(int minMajorVersion) {
    int actualMajor = getLuceneMajorVersion();
    if (actualMajor < minMajorVersion) {
      throw new UnsupportedOperationException(
          String.format("This test requires Lucene %d or higher, but running on %d",
              minMajorVersion, actualMajor));
    }
  }

  /**
   * Asserts that the test is running on a maximum version
   */
  protected void requireMaximumVersion(int maxMajorVersion) {
    int actualMajor = getLuceneMajorVersion();
    if (actualMajor > maxMajorVersion) {
      throw new UnsupportedOperationException(
          String.format("This test requires Lucene %d or lower, but running on %d",
              maxMajorVersion, actualMajor));
    }
  }
}
