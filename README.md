# Information Retrieval Adventure

[![Build and Test](https://github.com/MysterionRise/information-retrieval-adventure/workflows/Build%20and%20Test/badge.svg)](https://github.com/MysterionRise/information-retrieval-adventure/actions/workflows/build.yml)
[![Lucene Version Verification](https://github.com/MysterionRise/information-retrieval-adventure/workflows/Lucene%20Version%20Verification/badge.svg)](https://github.com/MysterionRise/information-retrieval-adventure/actions/workflows/version-verification.yml)
[![Build Status](https://travis-ci.org/MysterionRise/information-retrieval-adventure.svg?branch=master)](https://travis-ci.org/MysterionRise/information-retrieval-adventure)

Experimental repository for exploring information retrieval concepts using Apache Lucene (versions 4-8), Apache Solr, and Elasticsearch.

## Features

* **Multi-version testing**: Separate modules for Lucene 4.x, 5.x, 6.x, 7.x, 8.x, and Elasticsearch 7.x
* **Version verification**: Automated testing ensures correct library versions are loaded (via `test-commons` module)
* **Real-world examples**: Code from Stack Overflow questions and production use cases
* **IR Techniques**: Custom analyzers, scoring functions, faceting, phonetic search, and more

## What's Inside

* Indexing Russian Wikipedia pages (titles and URIs)
* Document values and ordinals manipulation
* Managed schema CRUD operations
* Custom analyzers and token filters
* Phonetic search implementations
* Query elevation and boosting
* BM25F scoring implementations
* Solr Cloud and CDCR configurations
* Elasticsearch custom plugins
* Post-filters and custom sorting via ValueSourceParser

## Modules

| Module | Lucene Version | Description |
|--------|---------------|-------------|
| `test-commons` | N/A | Shared testing utilities and version verification |
| `lucene4` | 4.10.4 | Lucene 4.x experiments and BM25F implementation |
| `lucene5` | 5.5.5 | Custom analyzers and token filters |
| `lucene6` | 6.6.6 | Plugin development and custom scoring |
| `lucene7` | 7.7.2 | Modern Lucene API examples |
| `lucene8` | 8.10.1 | Latest features, Solr configurations, Wikipedia indexing |
| `elasticsearch7` | 7.16.1 | Custom analyzers, plugins, and pipeline processors |

## Building

```bash
# Build all modules
mvn clean install

# Build specific module
mvn clean install -pl lucene8

# Run tests
mvn test

# Verify versions are correct
mvn test -Dtest=*VersionTest
```

## Version Verification

This repository includes automated version verification to ensure each module loads the correct Lucene version:

```bash
# Run version verification tests
mvn test -Dtest=*VersionTest

# Generate version compatibility report (via GitHub Actions)
# See .github/workflows/version-verification.yml
```

See [`test-commons/README.md`](test-commons/README.md) for details on the testing infrastructure.

## CI/CD

GitHub Actions workflows verify:
- ✅ All modules build successfully
- ✅ Correct Lucene versions are loaded in each module
- ✅ No dependency conflicts

- ✅ Code formatting compliance

See [`.github/workflows/README.md`](.github/workflows/README.md) for workflow details.
