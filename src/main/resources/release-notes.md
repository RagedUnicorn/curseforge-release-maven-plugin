# curseforge-release-maven-plugin 2.0.0

# breaking changes

* **Requires JDK 21+**: the plugin is now compiled with `--release 21`, so the consuming Maven build must run on Java 21 or newer (previously Java 8)

# release changes

* Migrated the test suite to JUnit 5; added Mockito-based ReleaseService tests and a Metadata serialization test
* Raised the compiler release target to Java 21 (CI and all release workflows now run on Temurin 21)
* Replaced the custom DefaultLog logger with SLF4J (slf4j-api)
* Simplified ReleaseService HTTP handling with try-with-resources
* Pinned PMD language modules (pmd-velocity, pmd-xml) to 7.17.0 and resolved Checkstyle findings
* Added a CI workflow (build, test, checkstyle, pmd)
* Dependency and Maven plugin updates; builds on the temurin JDK distribution
* Documentation fixes (CurseForge capitalization, 2026 copyright year)
