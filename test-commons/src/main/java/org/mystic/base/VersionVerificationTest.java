package org.mystic.base;

import org.junit.Test;
import org.mystic.version.LuceneVersionDetector;

import static org.junit.Assert.*;

/**
 * Base test class that verifies the correct Lucene/Solr version is loaded.
 * Each module should extend this class and implement getExpectedVersion().
 *
 * Example usage in lucene4 module:
 * <pre>
 * public class Lucene4VersionTest extends VersionVerificationTest {
 *     {@literal @}Override
 *     protected String getExpectedVersion() {
 *         return "4.10.4";
 *     }
 * }
 * </pre>
 */
public abstract class VersionVerificationTest {

  /**
   * Subclasses must return the expected Lucene version for their module.
   * This should match the version defined in the module's pom.xml.
   *
   * @return Expected version string (e.g., "4.10.4", "8.10.1")
   */
  protected abstract String getExpectedVersion();

  /**
   * Verifies that the detected Lucene version matches the expected version.
   * At minimum, the major version must match.
   */
  @Test
  public void testLuceneVersionIsCorrect() {
    String expectedVersion = getExpectedVersion();
    String actualVersion = LuceneVersionDetector.detectLuceneVersion();

    System.out.println("Expected Lucene version: " + expectedVersion);
    System.out.println("Detected Lucene version: " + actualVersion);

    assertNotNull("Lucene version should not be null", actualVersion);
    assertNotEquals("Could not detect Lucene version", "UNKNOWN", actualVersion);

    int expectedMajor = LuceneVersionDetector.getMajorVersion(expectedVersion);
    int actualMajor = LuceneVersionDetector.getMajorVersion(actualVersion);

    assertEquals(
        String.format("Wrong Lucene major version! Expected %s but got %s",
            expectedVersion, actualVersion),
        expectedMajor,
        actualMajor);

    // Optionally check full version match (may need to be relaxed for patch versions)
    if (!actualVersion.startsWith(expectedVersion.substring(0, 3))) {
      System.out.println("WARNING: Full version mismatch. Expected starts with "
          + expectedVersion.substring(0, 3) + " but got " + actualVersion);
    }
  }

  /**
   * Test that prints all Lucene packages on classpath.
   * Useful for debugging dependency conflicts.
   */
  @Test
  public void printClasspathLucenePackages() {
    LuceneVersionDetector.printAllLucenePackages();
  }

  /**
   * Test that generates a detailed version report.
   */
  @Test
  public void printVersionReport() {
    System.out.println(LuceneVersionDetector.getVersionReport());
  }

  /**
   * Verify no conflicting Lucene versions are on classpath.
   * This test checks that all org.apache.lucene packages report the same version.
   */
  @Test
  public void testNoVersionConflicts() {
    Package[] packages = Package.getPackages();
    String firstLuceneVersion = null;

    for (Package pkg : packages) {
      if (pkg.getName().startsWith("org.apache.lucene")) {
        String pkgVersion = pkg.getImplementationVersion();
        if (pkgVersion != null) {
          if (firstLuceneVersion == null) {
            firstLuceneVersion = pkgVersion;
          } else if (!firstLuceneVersion.equals(pkgVersion)) {
            fail(String.format(
                "Version conflict detected! Package %s has version %s but expected %s",
                pkg.getName(), pkgVersion, firstLuceneVersion));
          }
        }
      }
    }
  }
}
