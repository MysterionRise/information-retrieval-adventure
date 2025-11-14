package org.mystic.version;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Utility class to detect and verify Lucene/Solr version at runtime.
 * This helps ensure that tests are actually running against the expected version.
 */
public class LuceneVersionDetector {

  /**
   * Gets the Lucene version from the runtime classpath.
   * Tries multiple methods to ensure accurate detection.
   *
   * @return The detected Lucene version string, or "UNKNOWN" if not detectable
   */
  public static String detectLuceneVersion() {
    String version = null;

    // Method 1: Try to get version from org.apache.lucene.LucenePackage (Lucene 4+)
    try {
      Class<?> lucenePackageClass = Class.forName("org.apache.lucene.LucenePackage");
      Object packageInstance = lucenePackageClass.getMethod("get").invoke(null);
      version =
          (String)
              packageInstance
                  .getClass()
                  .getMethod("getImplementationVersion")
                  .invoke(packageInstance);
      if (version != null && !version.isEmpty()) {
        return version;
      }
    } catch (Exception e) {
      // Fall through to next method
    }

    // Method 2: Try to get from org.apache.lucene.util.Version constants
    try {
      Class<?> versionClass = Class.forName("org.apache.lucene.util.Version");
      Object latestVersion = versionClass.getField("LATEST").get(null);
      version = latestVersion.toString();
      if (version != null && !version.isEmpty()) {
        return version;
      }
    } catch (Exception e) {
      // Fall through to next method
    }

    // Method 3: Parse from MANIFEST.MF
    version = getVersionFromManifest();
    if (version != null && !version.isEmpty()) {
      return version;
    }

    return "UNKNOWN";
  }

  /**
   * Reads version from JAR manifest files on classpath.
   */
  private static String getVersionFromManifest() {
    try {
      Enumeration<URL> resources = Thread.currentThread().getContextClassLoader()
          .getResources("META-INF/MANIFEST.MF");

      while (resources.hasMoreElements()) {
        URL url = resources.nextElement();
        try (InputStream is = url.openStream()) {
          Manifest manifest = new Manifest(is);
          Attributes attributes = manifest.getMainAttributes();

          String implTitle = attributes.getValue("Implementation-Title");
          if (implTitle != null && implTitle.contains("Lucene")) {
            String version = attributes.getValue("Implementation-Version");
            if (version != null) {
              return version;
            }
          }
        }
      }
    } catch (IOException e) {
      // Ignore
    }
    return null;
  }

  /**
   * Gets the major version number (e.g., "4", "5", "8")
   */
  public static int getMajorVersion(String fullVersion) {
    if (fullVersion == null || fullVersion.equals("UNKNOWN")) {
      return -1;
    }
    try {
      String[] parts = fullVersion.split("\\.");
      return Integer.parseInt(parts[0]);
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * Checks if the detected version matches the expected version (at least major version)
   */
  public static boolean verifyVersion(String expectedVersion) {
    String actualVersion = detectLuceneVersion();

    if (actualVersion.equals("UNKNOWN")) {
      System.err.println("WARNING: Could not detect Lucene version");
      return false;
    }

    int expectedMajor = getMajorVersion(expectedVersion);
    int actualMajor = getMajorVersion(actualVersion);

    return expectedMajor == actualMajor;
  }

  /**
   * Prints all Lucene-related packages and their versions found on classpath.
   * Useful for debugging classpath conflicts.
   */
  public static void printAllLucenePackages() {
    System.out.println("=== Lucene Packages on Classpath ===");
    Package[] packages = Package.getPackages();
    for (Package pkg : packages) {
      if (pkg.getName().startsWith("org.apache.lucene")
          || pkg.getName().startsWith("org.apache.solr")) {
        System.out.printf("%-50s : %s%n",
            pkg.getName(),
            pkg.getImplementationVersion() != null
                ? pkg.getImplementationVersion()
                : "unknown");
      }
    }
    System.out.println("====================================");
  }

  /**
   * Gets a detailed version report for debugging
   */
  public static String getVersionReport() {
    StringBuilder report = new StringBuilder();
    report.append("Lucene Version Detection Report\n");
    report.append("================================\n");
    report.append("Detected Version: ").append(detectLuceneVersion()).append("\n");
    report.append("Major Version: ").append(getMajorVersion(detectLuceneVersion())).append("\n");
    report.append("\nClasspath Lucene Packages:\n");

    Package[] packages = Package.getPackages();
    for (Package pkg : packages) {
      if (pkg.getName().startsWith("org.apache.lucene")) {
        report.append("  - ").append(pkg.getName())
              .append(" : ").append(pkg.getImplementationVersion())
              .append("\n");
      }
    }

    return report.toString();
  }
}
