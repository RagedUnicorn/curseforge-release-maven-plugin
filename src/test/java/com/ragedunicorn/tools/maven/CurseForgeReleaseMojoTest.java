package com.ragedunicorn.tools.maven;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class CurseForgeReleaseMojoTest {

  @Rule
  public MojoRule rule = new MojoRule();

  @Test
  public void configureMojo_validPluginConfig_wiresAllFields() throws Exception {
    File testPom = new File(AbstractMojoTestCase.getBasedir(), "src/test/resources/plugin-config.xml");
    assertTrue(testPom.exists());

    CurseForgeReleaseMojo mojo = new CurseForgeReleaseMojo();
    mojo = (CurseForgeReleaseMojo) rule.configureMojo(
        mojo, rule.extractPluginConfiguration("curseforge-release-maven-plugin", testPom)
    );

    assertNotNull(mojo);

    assertEquals("wow", rule.getVariableValueFromObject(mojo, "game"));
    assertEquals("355893", rule.getVariableValueFromObject(mojo, "projectId"));
    assertEquals("example-upload", rule.getVariableValueFromObject(mojo, "displayName"));
    assertEquals("release description overwritten by release notes",
        rule.getVariableValueFromObject(mojo, "changelog"));
    assertEquals("src/main/resources/release-notes-example.md",
        rule.getVariableValueFromObject(mojo, "changelogFile"));
    assertEquals("markdown", rule.getVariableValueFromObject(mojo, "changelogType"));
    assertArrayEquals(new String[] {"7668", "7350"},
        (String[]) rule.getVariableValueFromObject(mojo, "gameVersions"));
    assertEquals("release", rule.getVariableValueFromObject(mojo, "releaseType"));
    assertEquals("src/main/resources/Example.zip",
        rule.getVariableValueFromObject(mojo, "file"));
    assertEquals("curseforge-token", rule.getVariableValueFromObject(mojo, "server"));
  }
}
