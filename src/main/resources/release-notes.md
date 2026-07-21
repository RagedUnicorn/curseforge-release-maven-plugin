# curseforge-release-maven-plugin 2.0.2

## Bugfixes

* Fix NoClassDefFoundError at goal execution caused by CurseForgeClient building its default headers via Guava, which is absent from the plugin's runtime ClassRealm

## Improvements

* Add integration test executing the curseforge-release goal in a real plugin ClassRealm
* Add optional baseUri parameter to override the CurseForge API endpoint
* Align Maven core dependencies (maven-core, maven-plugin-api) to provided scope
* Declare maven-settings and httpcore explicitly instead of relying on transitive resolution
* Remove the unused maven-compat and jsr311-api dependencies
* Fail the build on declared/used dependency mismatches via maven-dependency-plugin analyze-only

## Dependencies

* Update central-publishing-maven-plugin to 0.11.0
* Update Checkstyle to 13.8.0
* Update GitHub Actions
* Update github-release-maven-plugin to 1.0.8
* Update JUnit BOM to 6.1.2
* Update Maven core dependencies (maven-core, maven-plugin-api) to 3.9.16
* Update maven-surefire-plugin to 3.5.6
* Update PMD language modules (pmd-velocity, pmd-xml) to 7.26.0
