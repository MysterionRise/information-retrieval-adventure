# Test Commons Module

This module provides shared testing utilities, base classes, and contracts for cross-version Lucene/Solr testing.

## Purpose

The `test-commons` module enables:
- **Version verification**: Ensure tests run against the correct Lucene version
- **Consistent test infrastructure**: Shared base classes for common setup/teardown
- **Feature detection**: Programmatically check which features are available in each version
- **Test documentation**: Annotations to describe what tests demonstrate
- **Reusable test data**: Common test data and utilities

## Components

### 1. Version Detection and Verification

#### `LuceneVersionDetector`
Detects the actual Lucene version at runtime using multiple methods:
```java
String version = LuceneVersionDetector.detectLuceneVersion();
int majorVersion = LuceneVersionDetector.getMajorVersion(version);
LuceneVersionDetector.printAllLucenePackages(); // Debug classpath issues
```

#### `VersionVerificationTest`
Base class for version verification tests. Each module should extend this:

```java
// In lucene8/src/test/java/org/mystic/Lucene8VersionTest.java
public class Lucene8VersionTest extends VersionVerificationTest {
    @Override
    protected String getExpectedVersion() {
        return "8.10.1";
    }
}
```

### 2. Base Test Classes

#### `LuceneVersionTestBase`
Abstract base class with common test infrastructure:
```java
public class MyTest extends LuceneVersionTestBase {
    @Override
    protected String getExpectedLuceneVersion() {
        return "8.10.1";
    }

    @Test
    public void testSomething() {
        // tempDir is automatically created and cleaned up
        Path indexPath = tempDir.resolve("index");

        // Verify version requirements
        requireMinimumVersion(6); // Throws if version < 6

        // Check version
        int majorVersion = getLuceneMajorVersion();
    }
}
```

### 3. Feature Capabilities

#### `VersionCapabilities`
Enum defining which features are available in which versions:

```java
// Check if feature is supported
if (VersionCapabilities.POINT_VALUES.isSupportedIn("8.10.1")) {
    // Use IntPoint, LongPoint, etc.
}

// Or check current runtime version
if (VersionCapabilities.SYNONYM_GRAPH_FILTER.isSupportedInCurrentVersion()) {
    // Use SynonymGraphFilter
}

// Get all supported features
Set<VersionCapabilities> features =
    VersionCapabilities.getSupportedFeatures("6.6.6");
```

### 4. Test Data Management

#### `TestDataManager`
Manages test data paths and provides sample data:

```java
// Get configurable test data root
Path testDataRoot = TestDataManager.getTestDataRoot();

// Create temp directory
Path tempDir = TestDataManager.createTempDirectory("test-");

// Get sample data
List<String> russianWords = TestDataManager.getRussianTestWords();
List<TestDocument> docs = TestDataManager.getSampleEnglishDocuments();
List<TestDocument> multiField = TestDataManager.getMultiFieldDocuments();
```

Configure test data location:
```bash
# Via system property
mvn test -Dtest.data.dir=/path/to/data

# Via environment variable
export TEST_DATA_DIR=/path/to/data
mvn test
```

### 5. Test Annotations

Document your tests with metadata annotations:

#### `@TestedFeature`
```java
@TestedFeature(
    value = "Phonetic Search with ICU",
    category = "Analyzers"
)
public class PhoneticSearchTest { }
```

#### `@LuceneVersion`
```java
@LuceneVersion(min = "6.0", max = "8.x")
public class PointValuesTest { }
```

#### `@RelatedStackOverflowQuestion`
```java
@RelatedStackOverflowQuestion(
    questionId = "12345678",
    url = "https://stackoverflow.com/q/12345678",
    description = "How to implement custom BM25F scoring"
)
public class BM25FTest { }
```

#### `@ExperimentalCode`
```java
@ExperimentalCode(
    purpose = "Testing custom scoring formula",
    status = "Work in Progress"
)
public class CustomScoringExperiment { }
```

## Usage in Module Tests

### Step 1: Add Dependency

Add to your module's `pom.xml`:
```xml
<dependency>
    <groupId>org.mystic</groupId>
    <artifactId>test-commons</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

### Step 2: Create Version Verification Test

Create `src/test/java/org/mystic/VersionTest.java`:
```java
package org.mystic;

import org.mystic.base.VersionVerificationTest;

public class VersionTest extends VersionVerificationTest {
    @Override
    protected String getExpectedVersion() {
        return "8.10.1"; // Your module's version
    }
}
```

### Step 3: Use in Your Tests

```java
import org.mystic.base.LuceneVersionTestBase;
import org.mystic.capabilities.VersionCapabilities;
import org.mystic.utils.TestDataManager;

public class MyLuceneTest extends LuceneVersionTestBase {

    @Override
    protected String getExpectedLuceneVersion() {
        return "8.10.1";
    }

    @Test
    public void testIndexing() throws Exception {
        // Use temp directory (auto-cleanup)
        Directory dir = FSDirectory.open(tempDir);

        // Use test data
        List<TestDocument> docs = TestDataManager.getSampleEnglishDocuments();

        // Feature detection
        if (VersionCapabilities.POINT_VALUES.isSupportedInCurrentVersion()) {
            // Use modern API
        } else {
            // Use legacy API
        }
    }
}
```

## Running Version Verification

```bash
# Verify specific module
mvn test -pl lucene8 -Dtest=VersionTest

# Verify all modules
mvn test -Dtest=VersionTest

# Print version information
mvn test -pl lucene8 -Dtest=VersionTest#printVersionReport
```

## Benefits

1. **Prevents version conflicts**: Detects when wrong Lucene version is on classpath
2. **Documents compatibility**: Clear indication of which features work in which versions
3. **Reduces boilerplate**: Common setup/teardown in base classes
4. **Improves maintainability**: Centralized test utilities
5. **Enables comparison**: Same test can run across versions with version-specific implementations
6. **Better documentation**: Annotations make purpose of tests clear

## Example: Cross-Version Test

```java
// In each module, same test logic, version-specific implementation
@TestedFeature("Basic Tokenization")
@LuceneVersion(min = "4.0")
public class TokenizationTest extends LuceneVersionTestBase {

    @Test
    public void testStandardTokenizer() {
        String text = "The quick brown fox";
        List<String> tokens = tokenize(text);
        assertEquals(Arrays.asList("quick", "brown", "fox"), tokens);
    }

    // Version-specific implementation
    private List<String> tokenize(String text) {
        if (getLuceneMajorVersion() >= 7) {
            // Lucene 7+ API (no Version parameter)
            return tokenizeModern(text);
        } else {
            // Lucene 4-6 API (requires Version)
            return tokenizeLegacy(text);
        }
    }
}
```

## Future Enhancements

- Cross-version result comparison framework
- Automated compatibility matrix generation
- Performance comparison utilities
- Index format migration helpers
