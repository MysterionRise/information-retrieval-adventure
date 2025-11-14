# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an experimental repository for exploring information retrieval concepts using Apache Lucene, Apache Solr, and Elasticsearch. The project contains implementations of various IR techniques, custom analyzers, scoring functions, and indexing strategies, often inspired by Stack Overflow questions and real-world use cases.

The repository is organized as a multi-module Maven project with modules for different versions of Lucene/Solr (4, 5, 6, 7, 8) and Elasticsearch 7, allowing experimentation across API versions.

## Technology Stack

- **Languages**: Java 11, Scala 2.13.6
- **Build Tool**: Maven
- **IR Frameworks**:
  - Lucene versions: 4.10.4, 5.x, 6.x, 7.x, 8.10.1
  - Elasticsearch version: 7.16.1
  - Solr (bundled with Lucene modules)

## Build and Development Commands

### Building the Project

```bash
# Build all modules
mvn clean install

# Build specific module
mvn clean install -pl lucene8
mvn clean install -pl elasticsearch7

# Run tests
mvn clean test

# Package with shaded JAR (creates runnable JARs for lucene8 and elasticsearch7)
mvn clean package
```

### Code Formatting

The project uses the `fmt-maven-plugin` for code formatting:

```bash
# Format all code
mvn fmt:format

# Check formatting without changes
mvn fmt:check
```

### Running Individual Experiments

Many files in this project are standalone runnable classes with main methods rather than traditional unit tests. To run a specific experiment:

```bash
# For lucene8 module (WikiIndexingTest is the configured main class)
cd lucene8
mvn clean package
java -jar target/lucene8-1.0-SNAPSHOT.jar

# For elasticsearch7 module (SetPipelineIndexRequest is the configured main class)
cd elasticsearch7
mvn clean package
java -jar target/elasticsearch7-1.0-SNAPSHOT.jar

# Run a specific class directly
mvn exec:java -Dexec.mainClass="ClassName" -pl <module>
```

### Testing

```bash
# Run all tests
mvn test

# Run tests in specific module
mvn test -pl lucene8

# Run a specific test class
mvn test -Dtest=BM25FQueryTest -pl lucene4
mvn test -Dtest=TermsScoringTest -pl elasticsearch7
```

## Project Structure

### Module Organization

Each module (`lucene4` through `lucene8`, `elasticsearch7`) follows this structure:

```
<module>/
├── src/
│   ├── main/
│   │   ├── java/          # Java experiments and implementations
│   │   ├── scala/         # Scala experiments (primarily in lucene5, lucene8)
│   │   └── resources/     # Solr configurations (mainly in lucene8)
│   └── test/
│       └── java/          # Actual unit tests (JUnit)
└── pom.xml                # Module-specific dependencies
```

### Key Architectural Patterns

1. **Experiments as Runnable Classes**: Most files in `src/main/` are standalone experiments with `main()` methods, not traditional library code. They demonstrate specific IR techniques or answer StackOverflow questions.

2. **Version Isolation**: Each module is self-contained with its own version of Lucene/Elasticsearch dependencies. This allows testing how different API versions handle the same IR problems.

3. **Mixed Language Codebase**:
   - Java is used for most experiments and custom plugins
   - Scala is used primarily in lucene5 and lucene8 for certain experiments
   - Both languages compile in the correct order via `scala-maven-plugin`

4. **Solr Configurations**: The `lucene8/src/main/resources/` directory contains various Solr configuration examples:
   - `solr-cloud/`: Cloud/SolrCloud configurations
   - `solr-cdcr/`: Cross-Data Center Replication setup
   - `solr/cores/elevator/`: Query elevation/boosting examples
   - `solr-legacy/`: Legacy Solr configurations (master/slave)

## Important Implementation Details

### Custom Components

- **Custom Analyzers**: See `MyAnalyzerPlugin.java` (elasticsearch7), `CustomAnalyzer.java` (lucene5)
- **Custom Token Filters**: `SuffixShingleTokenFilter.java`, `SuffixShingleFactory.java` (lucene5)
- **Custom Scoring**: `BoostPrefixScoringRewrite.java` (lucene5), index-time scoring factors
- **Custom Plugins**: `EnricherPlugin.java` (lucene6) - demonstrates Solr plugin development

### Common IR Concepts Demonstrated

- Document values and ordinals (ord) manipulation
- Managed schema CRUD operations
- Phonetic search implementations
- Query elevation/boosting
- Faceting with date gaps
- Synonym and stopword handling
- Type-ahead/autocomplete
- Custom scoring and ranking functions
- BM25F implementation (lucene4)

### Module-Specific Notes

**lucene8**:
- Most feature-complete module
- Contains Solr configuration examples
- Main class: `WikiIndexingTest`
- Includes Wikipedia indexing examples

**elasticsearch7**:
- Demonstrates Elasticsearch plugin development
- Custom analyzer plugins
- Pipeline processors
- Main class: `SetPipelineIndexRequest`

**lucene4-lucene7**:
- Version-specific API demonstrations
- Useful for understanding API evolution
- Some contain experimental BM25F implementations

## Wikipedia Dataset

Several experiments index Russian Wikipedia pages (titles and URIs). The paths in the code (e.g., `/home/kperikov/Downloads/solr-4.4.0`) are hardcoded from the original development environment and will need adjustment for your environment.

## CI/CD

Travis CI configuration (`.travis.yml`) runs `mvn clean test` on both Linux and macOS with JDK 8.
