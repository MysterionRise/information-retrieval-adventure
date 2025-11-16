# Test-Commons Usage Guide

## What Was Created

The `test-commons` module provides shared testing infrastructure for version verification across all Lucene modules.

### Module Structure

```
test-commons/
├── pom.xml                                    # Module configuration
├── README.md                                  # Detailed documentation
├── USAGE_GUIDE.md                            # This file
└── src/main/java/org/mystic/
    ├── annotations/                           # Test metadata annotations
    │   ├── ExperimentalCode.java             # Mark experimental code
    │   ├── LuceneVersion.java                # Specify version requirements
    │   ├── RelatedStackOverflowQuestion.java # Link to SO questions
    │   └── TestedFeature.java                # Document tested features
    ├── base/                                  # Base test classes
    │   ├── LuceneVersionTestBase.java        # Common test infrastructure
    │   └── VersionVerificationTest.java      # Version verification base
    ├── capabilities/                          # Feature detection
    │   └── VersionCapabilities.java          # Feature/version matrix
    ├── utils/                                 # Test utilities
    │   └── TestDataManager.java              # Test data management
    └── version/                               # Version detection
        └── LuceneVersionDetector.java        # Runtime version detection
```

## Quick Start - Add to Your Module

### Step 1: Add Dependency

Add to `lucene8/pom.xml` (or any module):

```xml
<dependencies>
    <!-- Add BEFORE other dependencies -->
    <dependency>
        <groupId>org.mystic</groupId>
        <artifactId>test-commons</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>

    <!-- Your existing dependencies -->
    <dependency>
        <groupId>org.apache.lucene</groupId>
        <artifactId>lucene-core</artifactId>
        <version>${lucene.version}</version>
    </dependency>
    <!-- ... -->
</dependencies>
```

### Step 2: Create Version Verification Test

Create `lucene8/src/test/java/org/mystic/Lucene8VersionTest.java`:

```java
package org.mystic;

import org.mystic.base.VersionVerificationTest;

/**
 * Verifies that Lucene 8.10.1 is actually loaded at runtime.
 */
public class Lucene8VersionTest extends VersionVerificationTest {

    @Override
    protected String getExpectedVersion() {
        return "8.10.1"; // Must match lucene.version in pom.xml
    }
}
```

### Step 3: Run Verification

```bash
# Test specific module
mvn test -pl lucene8 -Dtest=Lucene8VersionTest

# Test all modules
mvn test -Dtest=*VersionTest

# See detailed version report
mvn test -pl lucene8 -Dtest=Lucene8VersionTest#printVersionReport
```

## Example: Enhanced Test Class

```java
package org.mystic;

import org.junit.Test;
import org.mystic.annotations.TestedFeature;
import org.mystic.annotations.LuceneVersion;
import org.mystic.base.LuceneVersionTestBase;
import org.mystic.capabilities.VersionCapabilities;
import org.mystic.utils.TestDataManager;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static org.junit.Assert.*;

@TestedFeature(
    value = "Standard Analyzer Tokenization",
    category = "Analyzers"
)
@LuceneVersion(min = "4.0")
public class StandardAnalyzerTest extends LuceneVersionTestBase {

    @Override
    protected String getExpectedLuceneVersion() {
        return "8.10.1";
    }

    @Test
    public void testBasicTokenization() throws Exception {
        // Use auto-managed temp directory
        Directory dir = FSDirectory.open(tempDir);

        // Create analyzer (version-aware)
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // Use provided test data
        var docs = TestDataManager.getSampleEnglishDocuments();

        // Version-specific logic if needed
        if (getLuceneMajorVersion() >= 7) {
            // Use modern API
        } else {
            // Use legacy API
        }

        // Your test logic here
        assertNotNull(analyzer);
        assertTrue(docs.size() > 0);
    }

    @Test
    public void testVersionSpecificFeature() {
        // Check if feature is supported
        if (VersionCapabilities.POINT_VALUES.isSupportedInCurrentVersion()) {
            // Use IntPoint, LongPoint, etc.
            System.out.println("Using Point values");
        } else {
            // Use legacy NumericField
            System.out.println("Using legacy numeric fields");
        }
    }
}
```

## Migrating Existing Tests

### Before (Hardcoded):
```java
public class PhoneticSearchInWords {
    public static void main(String[] args) throws IOException {
        Directory dir = new MMapDirectory(
            Files.createTempDirectory(PhoneticSearchInWords.class.getName())
        );

        String wordsString = "'сек', 'руж', 'бан', ..."; // Hardcoded
        // Manual cleanup needed
    }
}
```

### After (Using test-commons):
```java
@TestedFeature("Phonetic Search in Russian")
public class PhoneticSearchTest extends LuceneVersionTestBase {

    @Override
    protected String getExpectedLuceneVersion() {
        return "8.10.1";
    }

    @Test
    public void testRussianPhoneticSearch() throws Exception {
        // Auto-managed temp dir
        Directory dir = FSDirectory.open(tempDir);

        // Reusable test data
        List<String> words = TestDataManager.getRussianTestWords();

        // Test logic
        // ...

        // Auto cleanup in tearDown()
    }
}
```

## Benefits

### 1. Version Safety
```java
// Runtime verification prevents wrong version from sneaking in
@Test
public void testLuceneVersionIsCorrect() {
    // Automatically verifies major version matches
    // Fails fast if wrong Lucene version on classpath
}
```

### 2. Feature Detection
```java
// Programmatically check feature availability
if (VersionCapabilities.SYNONYM_GRAPH_FILTER.isSupportedIn("6.6.6")) {
    // This feature exists in Lucene 6
}
```

### 3. Test Documentation
```java
@TestedFeature("BM25F Scoring")
@LuceneVersion(min = "4.0", max = "8.x")
@RelatedStackOverflowQuestion(
    questionId = "12345678",
    url = "https://stackoverflow.com/q/12345678"
)
// Clear documentation of what test does and why it exists
```

### 4. Consistent Infrastructure
```java
// All tests get:
// - Auto temp directory creation/cleanup
// - Version detection
// - Standardized setup/teardown
// - Test timing
```

## Next Steps

1. **Add to each module** (lucene4, lucene5, etc.):
   - Add test-commons dependency
   - Create module-specific VersionTest
   - Run `mvn test` to verify

2. **Migrate existing experiments**:
   - Convert main() methods to @Test methods
   - Extend LuceneVersionTestBase
   - Use TestDataManager for test data

3. **Document features**:
   - Add @TestedFeature annotations
   - Link to StackOverflow questions
   - Specify version requirements

4. **Set up CI**:
   - Run VersionTest in each module
   - Generate compatibility reports
   - Track version differences

## Troubleshooting

### Build Issues

If you see formatter plugin errors:
```bash
# The test-commons pom.xml already skips the formatter
# But if issues persist, build with:
mvn clean install -pl test-commons -Dmaven.fmt.skip=true
```

### Version Detection Fails

```bash
# Print detailed version info
mvn test -Dtest=VersionTest#printVersionReport

# Check for classpath conflicts
mvn dependency:tree -pl lucene8 | grep lucene-core
```

### Import Errors in IDE

```bash
# Build test-commons first
mvn clean install -pl test-commons

# Then refresh your IDE project
```

## Full Build Command

```bash
# From project root, build test-commons first
mvn clean install -pl test-commons

# Then build and test a specific module
mvn clean test -pl lucene8

# Or build everything
mvn clean test
```

## Questions?

See `README.md` for detailed API documentation and examples.
