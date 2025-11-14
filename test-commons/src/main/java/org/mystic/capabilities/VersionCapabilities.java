package org.mystic.capabilities;

import org.mystic.version.LuceneVersionDetector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumerates Lucene/Solr features and which versions support them.
 * Useful for conditional test execution and capability detection.
 *
 * Example usage:
 * <pre>
 * if (VersionCapabilities.POINT_VALUES.isSupportedIn("8.10.1")) {
 *     // Use IntPoint, LongPoint, etc.
 * }
 * </pre>
 */
public enum VersionCapabilities {

  // Core features
  BM25_SIMILARITY(4, 5, 6, 7, 8),
  CLASSIC_SIMILARITY(4, 5, 6, 7, 8),

  // Field types
  NUMERIC_FIELD(4, 5, 6), // Deprecated in Lucene 7, removed in 8
  INT_FIELD(4, 5, 6), // Deprecated in Lucene 7, removed in 8
  LONG_FIELD(4, 5, 6), // Deprecated in Lucene 7, removed in 8
  POINT_VALUES(6, 7, 8), // IntPoint, LongPoint, etc. introduced in Lucene 6
  INT_POINT(6, 7, 8),
  LONG_POINT(6, 7, 8),
  FLOAT_POINT(6, 7, 8),
  DOUBLE_POINT(6, 7, 8),

  // Document values
  SORTED_NUMERIC_DOC_VALUES(4, 5, 6, 7, 8),
  SORTED_SET_DOC_VALUES(4, 5, 6, 7, 8),
  NUMERIC_DOC_VALUES(4, 5, 6, 7, 8),

  // Query types
  BOOLEAN_QUERY(4, 5, 6, 7, 8),
  PHRASE_QUERY(4, 5, 6, 7, 8),
  FUZZY_QUERY(4, 5, 6, 7, 8),
  WILDCARD_QUERY(4, 5, 6, 7, 8),
  TERM_QUERY(4, 5, 6, 7, 8),
  SPAN_QUERY(4, 5, 6, 7, 8),

  // Analyzers
  STANDARD_ANALYZER(4, 5, 6, 7, 8),
  WHITESPACE_ANALYZER(4, 5, 6, 7, 8),
  SIMPLE_ANALYZER(4, 5, 6, 7, 8),
  KEYWORD_ANALYZER(4, 5, 6, 7, 8),

  // Token filters
  SYNONYM_FILTER(4, 5, 6, 7, 8),
  SYNONYM_GRAPH_FILTER(6, 7, 8), // Introduced in Lucene 6
  STOPWORD_FILTER(4, 5, 6, 7, 8),
  LOWERCASE_FILTER(4, 5, 6, 7, 8),
  PHONETIC_FILTER(4, 5, 6, 7, 8),

  // Faceting
  TAXONOMY_FACETS(4, 5, 6, 7, 8),
  SORTED_SET_DOC_VALUES_FACETS(4, 5, 6, 7, 8),

  // Index features
  SEGMENTS_API(4, 5, 6, 7, 8),
  MERGE_POLICY(4, 5, 6, 7, 8),
  CODEC_API(4, 5, 6, 7, 8),

  // Version parameter requirement
  VERSION_PARAMETER_REQUIRED(4, 5, 6), // Version parameter removed in Lucene 7+

  // Solr-specific
  SOLR_CLOUD(4, 5, 6, 7, 8),
  MANAGED_SCHEMA(5, 6, 7, 8), // Introduced in Solr 5

  // Search features
  MULTI_TERM_QUERY(4, 5, 6, 7, 8),
  CONSTANT_SCORE_QUERY(4, 5, 6, 7, 8),

  // Highlighters
  UNIFIED_HIGHLIGHTER(6, 7, 8), // Introduced in Lucene 6
  FAST_VECTOR_HIGHLIGHTER(4, 5, 6, 7, 8),
  POSTINGS_HIGHLIGHTER(4, 5, 6, 7, 8);

  private final Set<Integer> supportedMajorVersions;

  VersionCapabilities(Integer... versions) {
    this.supportedMajorVersions = new HashSet<>(Arrays.asList(versions));
  }

  /**
   * Checks if this feature is supported in the given version string
   *
   * @param version Full version string (e.g., "4.10.4", "8.10.1")
   * @return true if supported, false otherwise
   */
  public boolean isSupportedIn(String version) {
    int majorVersion = LuceneVersionDetector.getMajorVersion(version);
    return supportedMajorVersions.contains(majorVersion);
  }

  /**
   * Checks if this feature is supported in the current runtime version
   */
  public boolean isSupportedInCurrentVersion() {
    String version = LuceneVersionDetector.detectLuceneVersion();
    return isSupportedIn(version);
  }

  /**
   * Gets the minimum version that supports this feature
   */
  public int getMinimumVersion() {
    return supportedMajorVersions.stream().min(Integer::compareTo).orElse(-1);
  }

  /**
   * Gets the maximum version that supports this feature
   */
  public int getMaximumVersion() {
    return supportedMajorVersions.stream().max(Integer::compareTo).orElse(-1);
  }

  /**
   * Checks if this feature is deprecated (not supported in latest versions)
   */
  public boolean isDeprecated() {
    return !supportedMajorVersions.contains(8); // Assuming 8 is current major
  }

  /**
   * Gets all features supported in a specific version
   */
  public static Set<VersionCapabilities> getSupportedFeatures(String version) {
    Set<VersionCapabilities> supported = new HashSet<>();
    for (VersionCapabilities capability : values()) {
      if (capability.isSupportedIn(version)) {
        supported.add(capability);
      }
    }
    return supported;
  }

  /**
   * Gets a human-readable description of version support
   */
  public String getSupportDescription() {
    if (supportedMajorVersions.isEmpty()) {
      return "Not supported in any version";
    }

    int min = getMinimumVersion();
    int max = getMaximumVersion();

    if (min == max) {
      return "Only in Lucene " + min;
    } else if (max == 8) {
      return "Lucene " + min + "+";
    } else {
      return "Lucene " + min + " - " + max;
    }
  }
}
