package org.mystic.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages test data paths and sample data generation.
 * Provides consistent, configurable test data access across all modules.
 *
 * Replaces hardcoded paths like "/home/kperikov/Downloads/..." with
 * configurable, environment-independent paths.
 */
public class TestDataManager {

  private static final String TEST_DATA_DIR_PROPERTY = "test.data.dir";
  private static final String PROJECT_ROOT_PROPERTY = "project.root";

  /**
   * Gets the root directory for test data.
   * Order of precedence:
   * 1. System property: test.data.dir
   * 2. Environment variable: TEST_DATA_DIR
   * 3. Default: project-root/test-data
   */
  public static Path getTestDataRoot() {
    String testDataDir = System.getProperty(TEST_DATA_DIR_PROPERTY);
    if (testDataDir != null) {
      return Paths.get(testDataDir);
    }

    testDataDir = System.getenv("TEST_DATA_DIR");
    if (testDataDir != null) {
      return Paths.get(testDataDir);
    }

    // Default to project root + test-data
    Path projectRoot = getProjectRoot();
    return projectRoot.resolve("test-data");
  }

  /**
   * Gets the project root directory
   */
  public static Path getProjectRoot() {
    String projectRoot = System.getProperty(PROJECT_ROOT_PROPERTY);
    if (projectRoot != null) {
      return Paths.get(projectRoot);
    }

    // Try to detect from user.dir
    Path userDir = Paths.get(System.getProperty("user.dir"));

    // If we're in a module directory, go up one level
    if (userDir.getFileName().toString().startsWith("lucene")
        || userDir.getFileName().toString().equals("elasticsearch7")
        || userDir.getFileName().toString().equals("test-commons")) {
      return userDir.getParent();
    }

    return userDir;
  }

  /**
   * Gets or creates the test data directory
   */
  public static Path ensureTestDataRoot() throws IOException {
    Path testDataRoot = getTestDataRoot();
    if (!Files.exists(testDataRoot)) {
      Files.createDirectories(testDataRoot);
    }
    return testDataRoot;
  }

  /**
   * Gets a temporary directory for test use
   */
  public static Path createTempDirectory(String prefix) throws IOException {
    return Files.createTempDirectory(prefix);
  }

  /**
   * Sample Russian words for phonetic testing (from PhoneticSearchInWords.java)
   */
  public static List<String> getRussianTestWords() {
    return Arrays.asList(
        "сек", "руж", "бан", "лящ", "чив", "ч(щ)ир", "фен", "лём??", "нут", "нож",
        "лачь", "кит", "жых", "пур", "реж", "сом", "чир", "рак", "мяч", "рич",
        "щин", "лёс", "пес", "чиж", "фож", "вожь", "бык", "рачь", "пыр", "бак",
        "кож", "пез", "чим", "сен", "краб", "лон", "мол", "сем", "сум", "фыв(?)",
        "лев", "фур", "пас", "бым", "паж", "сошь", "пурпур", "фор", "луж", "пяж",
        "сош", "пёс", "ряж", "син", "лещ", "ож", "лям", "грач", "люм", "пыж",
        "пож", "чес", "быч", "луш", "теч", "бур", "пав", "гук", "быв", "чев",
        "тяж", "сож", "лячь", "щур", "щер", "суж", "лив", "лом", "люк", "лач",
        "пурр", "ляч", "пуль", "таз", "чер", "меч", "мячь", "пёз", "чиф", "леф",
        "бум", "cом", "риж", "щуп", "рач", "киж", "собака", "фез", "тев", "жук"
    );
  }

  /**
   * Sample English test documents
   */
  public static List<TestDocument> getSampleEnglishDocuments() {
    List<TestDocument> docs = new ArrayList<>();
    docs.add(new TestDocument("doc1", "The quick brown fox jumps over the lazy dog"));
    docs.add(new TestDocument("doc2", "Apache Lucene is a powerful search library"));
    docs.add(new TestDocument("doc3", "Information retrieval is the science of searching"));
    docs.add(new TestDocument("doc4", "BM25 is a ranking function used in search engines"));
    docs.add(new TestDocument("doc5", "Elasticsearch builds on top of Apache Lucene"));
    return docs;
  }

  /**
   * Sample documents with multiple fields
   */
  public static List<TestDocument> getMultiFieldDocuments() {
    List<TestDocument> docs = new ArrayList<>();

    TestDocument doc1 = new TestDocument("1", "Lucene in Action");
    doc1.addField("author", "Erik Hatcher");
    doc1.addField("category", "IT");
    docs.add(doc1);

    TestDocument doc2 = new TestDocument("2", "Introduction to Information Retrieval");
    doc2.addField("author", "Christopher Manning");
    doc2.addField("category", "IT");
    docs.add(doc2);

    TestDocument doc3 = new TestDocument("3", "Search Patterns");
    doc3.addField("author", "Peter Morville");
    doc3.addField("category", "Design");
    docs.add(doc3);

    return docs;
  }

  /**
   * Simple test document class
   */
  public static class TestDocument {
    private final String id;
    private final String content;
    private final java.util.Map<String, String> fields = new java.util.HashMap<>();

    public TestDocument(String id, String content) {
      this.id = id;
      this.content = content;
    }

    public void addField(String name, String value) {
      fields.put(name, value);
    }

    public String getId() {
      return id;
    }

    public String getContent() {
      return content;
    }

    public java.util.Map<String, String> getFields() {
      return fields;
    }

    public String getField(String name) {
      return fields.get(name);
    }
  }
}
