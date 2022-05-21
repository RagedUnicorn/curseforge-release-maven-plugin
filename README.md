# curseforge-release-maven-plugin

> A maven plugin for creating Curseforge mods/addons releases

[![Maven Central](https://img.shields.io/maven-central/v/com.ragedunicorn.tools.maven/curseforge-release-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.ragedunicorn.tools.maven%22%20AND%20a:%22curseforge-release-maven-plugin%22)

## Usage

Setup pom.xml in project

```xml
<project>
  [...]
  <build>
    <plugins>
      <plugin>
        <groupId>com.ragedunicorn.tools.maven</groupId>
        <artifactId>curseforge-release-maven-plugin</artifactId>
        <version>[version]</version>
        <executions>
          <execution>
            <id>default-cli</id>
            <configuration>
              <game>wow</game>
              <projectId>355893</projectId>
              <displayName>example-upload</displayName>
              <changelog>release description overwritten by release notes</changelog>
              <changelogFile>src/main/resources/release-notes-example.md</changelogFile>
              <changelogType>markdown</changelogType>
              <gameVersions>
                <gameVersion>7668</gameVersion>
                <gameVersion>7350</gameVersion>
              </gameVersions>
              <releaseType>release</releaseType>
              <file>src/main/resources/Example.zip</file>
              <server>curseforge-token</server>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
  [...]
</project>
```
| Parameter     | Required | Default Value | Description                                                                                                                    |
|---------------|----------|---------------|--------------------------------------------------------------------------------------------------------------------------------|
| game          | false    | wow           | Type of game (currently only wow is supported)                                                                                 |
| projectId     | true     | <>            | The project id of the curseforge project (can be found on the projects page)                                                   |
| server        | false    | <>            | References a server configuration in your .m2 settings.xml. This is the preferred way for using the generated curseforge token |
| authToken     | false    | <>            | Alternative of using a server configuration. The authToken can directly be placed in the plugin configuration                  |
| displayname   | false    | addon         | An optional displayname for the uploaded file                                                                                  |
| changelog     | false    | <>            | A string containing the changelog                                                                                              |
| changelogFile | false    | <>            | Optional path to a changelog file - will override changelog                                                                    |
| changelogType | false    | text          | Changelog type ["text", "html", "markdown"]                                                                                    |
| gameVersions  | true     | <>            | A list of supported game versions                                                                                              |
| releaseType   | false    | release       | One of "alpha", "beta", "release"                                                                                              |
| file          | true     | <>            | The path to the addon to upload                                                                                                |


### Execute Plugin

```
mvn curseforge-release:curseforge-release
```


## Setup Api Token

Before the plugin can be used an API token has to be generated.

See Curseforge [documentation](https://authors.curseforge.com/account/api-tokens)
   
Once the Api token is generated it can be stored inside the maven `.m2/settings.xml`.
 
 ```xml
<server>
  <id>curseforge-token</id>
  <passphrase>token</passphrase>
</server>
```

Make sure to use `passphrase` instead of `username`and `password` otherwise the plugin will not be able to recognize the token.

It is also possible to set the token with the parameter `authToken` directly inside the plugin configuration. This is however not recommended because those pom files are usually getting commited into source control and potentially leaking the token.
However, using maven commandline this can be useful being able to overwrite this parameter with the `-D` option.

```xml
<configuration>
  ...
  <authToken>${curseforge.auth-token}</authToken>
</configuration>
```

Then invoking via the command line
```
mvn curseforge-release:curseforge-release -D curseforge.auth-token=[token]
```

## Test

Basic tests can be executed with:

```
mvn test
```

Tests are kept basic because for most of the functionality the CurseForge backend is required.

## Development

##### IntelliJ Run Configurations

The project contains IntelliJ run configurations that can be used for most tasks. Create a folder `runConfigurations` inside the `.idea` folder and copy over all run configurations. 


##### Build Project

curseforge-release-maven-plugin

```
clean install
```

#### Create a Release

In maven `settings.xml` configure the ossrh account

```
<server>
  <id>ossrh</id>
  <username></username>
  <password></password>
</server>
```

#### Build and Release 

```
mvn clean deploy -P deploy
```

#### Move Staging to Release

If `autoReleaseAfterClose` is set to false in the `nexus-staging-maven-plugin` plugin an additional step is required to move the deployment from staging to release.

```
mvn nexus-staging:release
```

Or if the deployment didn't workout you can drop the artifact from the staging repository.

```
mvn nexus-staging:drop
```

**Note:** On MacOS the error `gpg: signing failed: Inappropriate ioctl for device` can be solved by setting the tty export variable for gpg.

```
export GPG_TTY=$(tty)
```

If you are using the IntelliJ console this might need to be set directly in that console.

##### Run Example

The example can be used for testing of the plugin during development. It requires some manual setup on GitHub before it can be run.

* Setup curseforge token

curseforge-release-maven-plugin/example

```
clean install
```

Executing the plugin from a different folder won't work without also fixing the path to the release notes and any additional assets configured.

**Note:** The example module is deliberately not included as default module otherwise it would execute each time the project is built.
Instead the module can be considered separate and independent. It is an example of how to use the plugin and it is helpful in testing the plugin during development.


##### Checkstyle

curseforge-release-maven-plugin/plugin

```
mvn checkstyle:checkstyle
```

##### PMD

curseforge-release-maven-plugin/plugin

```
mvn pmd:pmd
```

## License

Copyright (c) 2021 Michael Wiesendanger

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
