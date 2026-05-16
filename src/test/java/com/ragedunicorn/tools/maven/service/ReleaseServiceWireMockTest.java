package com.ragedunicorn.tools.maven.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.ragedunicorn.tools.maven.CurseForgeClient;
import com.ragedunicorn.tools.maven.model.CurseForgeApiRelease;
import com.ragedunicorn.tools.maven.model.Metadata;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

public class ReleaseServiceWireMockTest {
  private static final String PROJECT_ID = "355893";
  private static final String UPLOAD_PATH = "/api/projects/" + PROJECT_ID + "/upload-file";

  @RegisterExtension
  static WireMockExtension wm = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicPort())
      .build();

  private CurseForgeClient client() {
    CurseForgeClient client = new CurseForgeClient();
    client.setToken("test-token");
    client.setProjectId(PROJECT_ID);
    client.setGame("wow");
    // point the client at the WireMock server instead of curseforge.com
    client.setBaseUri(wm.getRuntimeInfo().getHttpBaseUrl());
    return client;
  }

  private Metadata metadata() {
    Metadata metadata = new Metadata();
    metadata.setChangelog("test changelog");
    metadata.setChangelogType("markdown");
    metadata.setDisplayName("example-upload");
    metadata.setGameVersions(new int[] {7668, 7350});
    metadata.setReleaseType("release");
    return metadata;
  }

  private String uploadFile(Path tempDir) throws Exception {
    Path file = tempDir.resolve("addon.zip");
    Files.write(file, "dummy-addon-content".getBytes(StandardCharsets.UTF_8));
    return file.toAbsolutePath().toString();
  }

  @Test
  public void testCreateReleaseSuccess(@TempDir Path tempDir) throws Exception {
    wm.stubFor(post(urlPathEqualTo(UPLOAD_PATH))
        .willReturn(okJson("{\"id\": 123456}")));

    ReleaseService service = new ReleaseService(client());
    CurseForgeApiRelease release = service.createReleaseOperation(metadata(), uploadFile(tempDir));

    assertEquals(123456, release.getId());
    wm.verify(postRequestedFor(urlPathEqualTo(UPLOAD_PATH))
        .withHeader("X-Api-Token", equalTo("test-token"))
        .withHeader("User-Agent", equalTo("curseforge-release-plugin")));
  }

  @Test
  public void testCreateReleaseApiErrorIsWrapped(@TempDir Path tempDir) throws Exception {
    wm.stubFor(post(urlPathEqualTo(UPLOAD_PATH))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"errorCode\":1018,\"errorMessage\":\"Invalid token\"}")));

    ReleaseService service = new ReleaseService(client());
    String file = uploadFile(tempDir);
    Metadata metadata = metadata();

    MojoExecutionException ex = assertThrows(MojoExecutionException.class,
        () -> service.createReleaseOperation(metadata, file));
    assertTrue(ex.getMessage().contains("Invalid token"),
        "expected the CurseForge error message to be propagated, got: " + ex.getMessage());
  }

  @Test
  public void testCreateReleaseServerErrorIsWrapped(@TempDir Path tempDir) throws Exception {
    wm.stubFor(post(urlPathEqualTo(UPLOAD_PATH))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"errorCode\":500,\"errorMessage\":\"Internal Server Error\"}")));

    ReleaseService service = new ReleaseService(client());
    String file = uploadFile(tempDir);
    Metadata metadata = metadata();

    assertThrows(MojoExecutionException.class,
        () -> service.createReleaseOperation(metadata, file));
  }
}
