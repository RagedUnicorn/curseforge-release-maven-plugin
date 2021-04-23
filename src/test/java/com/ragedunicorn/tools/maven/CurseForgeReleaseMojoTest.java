package com.ragedunicorn.tools.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import java.io.File;

public class CurseForgeReleaseMojoTest extends AbstractMojoTestCase {

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Tests the proper discovery and configuration of the mojo.
   *
   * @throws Exception If failing to extract plugin configuration
   */
  public void testBasicPluginConfiguration() throws Exception {

    File testPom = new File("src/test/resources/plugin-config.xml" );
    assertNotNull(testPom);
    assertTrue(testPom.exists());

    CurseForgeReleaseMojo mojo = new CurseForgeReleaseMojo();
    mojo = (CurseForgeReleaseMojo) configureMojo(
        mojo, extractPluginConfiguration("curseforge-release-maven-plugin", testPom)
    );

    assertNotNull(mojo);

    // mojo.execute(); execution requires curseforge backend
  }
}
