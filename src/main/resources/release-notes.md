# curseforge-release-maven-plugin 2.0.2

## Bugfixes

* Fix NoClassDefFoundError at goal execution caused by CurseForgeClient building its default headers via Guava, which is absent from the plugin's runtime ClassRealm

## Improvements

* Add integration test executing the curseforge-release goal in a real plugin ClassRealm
* Add optional baseUri parameter to override the CurseForge API endpoint
* Align Maven core dependencies (maven-core, maven-plugin-api, maven-compat) to provided scope

## Dependencies

* Update Checkstyle to 13.8.0
* Update central-publishing-maven-plugin to 0.11.0
* Update JUnit BOM to 6.1.0
* Update PMD language modules (pmd-velocity, pmd-xml) to 7.25.0
