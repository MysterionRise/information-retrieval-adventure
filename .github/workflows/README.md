# GitHub Actions Workflows

This directory contains CI/CD workflows for the information-retrieval-adventure project.

## Workflows

### 1. Build and Test (`build.yml`)

**Triggers:** Push to master/main/claude/**, Pull Requests

**Purpose:** Main CI workflow that builds and tests all modules

**Jobs:**
- **build**: Compiles all modules with `mvn clean install`
- **test**: Runs all tests across modules
- **verify-formatting**: Checks code formatting with fmt-maven-plugin
- **dependency-check**: Analyzes dependency tree and checks for conflicts

**Key Features:**
- Maven dependency caching for faster builds
- Build artifact uploads on failure for debugging
- Test result uploads for analysis

### 2. Lucene Version Verification (`version-verification.yml`)

**Triggers:** Push, Pull Requests, Weekly schedule

**Purpose:** Ensures each module loads the correct Lucene version

**Jobs:**
- **verify-versions**: Matrix job testing each module (lucene4-8)
  - Verifies POM declared version matches expected
  - Builds module with test-commons
  - Checks dependency tree for conflicts
  - Fails if multiple Lucene versions detected
- **generate-compatibility-report**: Creates markdown report of versions

**Matrix:**
| Module | Expected Version | Major Version |
|--------|-----------------|---------------|
| lucene4 | 4.10.4 | 4 |
| lucene5 | 5.5.5 | 5 |
| lucene6 | 6.6.6 | 6 |
| lucene7 | 7.7.2 | 7 |
| lucene8 | 8.10.1 | 8 |

**Verification Steps:**
1. ✅ POM version matches expected version
2. ✅ Module builds successfully
3. ✅ Tests pass
4. ✅ No version conflicts in dependency tree
5. ✅ Only single Lucene version present

### 3. Cross-Platform Build (`cross-platform.yml`)

**Triggers:** Push to master/main, Pull Requests

**Purpose:** Verify builds work across operating systems and Java versions

**Matrix:**
- **OS:** Ubuntu, macOS, Windows
- **Java:** 11, 17 (Java 17 only on Ubuntu to reduce matrix size)

**Key Features:**
- Platform-specific build commands (Unix vs Windows)
- Tests compatibility with multiple Java versions
- Ensures cross-platform portability

## Workflow Artifacts

All workflows upload artifacts that can be downloaded from the Actions tab:

- **build-logs**: Build failure logs and error dumps
- **test-results**: Surefire test reports
- **version-test-results-{module}**: Per-module version verification results
- **compatibility-report**: Markdown report of version compatibility

## Status Badges

Add these to your README.md:

```markdown
![Build and Test](https://github.com/MysterionRise/information-retrieval-adventure/workflows/Build%20and%20Test/badge.svg)
![Version Verification](https://github.com/MysterionRise/information-retrieval-adventure/workflows/Lucene%20Version%20Verification/badge.svg)
![Cross-Platform](https://github.com/MysterionRise/information-retrieval-adventure/workflows/Cross-Platform%20Build/badge.svg)
```

## Local Testing

### Validate Workflow Syntax

```bash
# Install act (GitHub Actions local runner)
# https://github.com/nektos/act

# Validate workflow files
act -l

# Run a specific workflow locally (requires Docker)
act -W .github/workflows/build.yml
```

### Manual Version Verification

```bash
# Verify all module versions
for module in lucene4 lucene5 lucene6 lucene7 lucene8; do
  echo "=== $module ==="
  grep '<lucene.version>' $module/pom.xml
done

# Check for dependency conflicts
mvn dependency:tree -pl lucene8 | grep lucene-core
```

## Debugging Failed Workflows

1. **Check the Actions tab** on GitHub
2. **Download artifacts** from failed runs
3. **Review surefire-reports** for test failures
4. **Check dependency tree** for version conflicts
5. **Run locally** with `mvn clean test -pl <module>`

## Maintenance

### Updating Expected Versions

When upgrading Lucene versions, update the matrix in `version-verification.yml`:

```yaml
matrix:
  include:
    - module: lucene8
      expected_version: "8.11.0"  # Update this
      major_version: "8"
```

### Adding New Modules

To add a new module (e.g., lucene9):

1. Add to build jobs in `build.yml` (should auto-detect via Maven)
2. Add matrix entry in `version-verification.yml`:
   ```yaml
   - module: lucene9
     expected_version: "9.x.x"
     major_version: "9"
   ```
3. Update compatibility report generation in `version-verification.yml`

## Performance Optimization

- **Maven caching**: Workflows use `cache: 'maven'` to cache ~/.m2/repository
- **Fail-fast: false**: Allows all matrix jobs to complete for full visibility
- **Selective testing**: Use `-DskipTests` during build phase, separate test phase

## Security

- All workflows use pinned action versions (e.g., `@v3`)
- No secrets required for public repository
- Workflows run in isolated GitHub-hosted runners
