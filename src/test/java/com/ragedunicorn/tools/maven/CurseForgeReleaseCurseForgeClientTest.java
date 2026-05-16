package com.ragedunicorn.tools.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

public class CurseForgeReleaseCurseForgeClientTest {
  private static final String ENDPOINT = "/api/projects/:projectId/upload-file";

  private CurseForgeClient fullyConfiguredClient() {
    CurseForgeClient client = new CurseForgeClient();
    client.setProjectId("111111");
    client.setGame("wow");
    client.setToken("test-token");
    return client;
  }

  @Test
  public void testEndpointUriPreparation() throws MojoExecutionException {
    CurseForgeClient client = fullyConfiguredClient();

    URI preparedUri = client.prepareEndpointUri(ENDPOINT);

    assertEquals(
        "https://wow.curseforge.com/api/projects/111111/upload-file",
        preparedUri.toString());
  }

  @Test
  public void testEndpointUriResolvesGamePlaceholderInBaseUri() throws MojoExecutionException {
    CurseForgeClient client = fullyConfiguredClient();
    client.setGame("minecraft");
    client.setBaseUri("https://:game.example.com");

    URI preparedUri = client.prepareEndpointUri(ENDPOINT);

    assertEquals(
        "https://minecraft.example.com/api/projects/111111/upload-file",
        preparedUri.toString());
  }

  @Test
  public void testPrepareEndpointUriOnEmptyClientThrows() {
    CurseForgeClient client = new CurseForgeClient();

    assertThrows(IllegalStateException.class, () -> client.prepareEndpointUri("/some/url"));
  }

  @Test
  public void testGetHttpClientOnEmptyClientThrows() {
    CurseForgeClient client = new CurseForgeClient();

    assertThrows(IllegalStateException.class, client::getHttpClient);
  }

  @Test
  public void testMissingTokenThrows() {
    CurseForgeClient client = fullyConfiguredClient();
    client.setToken(null);

    assertThrows(IllegalStateException.class, client::getHttpClient);
    assertThrows(IllegalStateException.class, () -> client.prepareEndpointUri(ENDPOINT));
  }

  @Test
  public void testMissingProjectIdThrows() {
    CurseForgeClient client = fullyConfiguredClient();
    client.setProjectId(null);

    assertThrows(IllegalStateException.class, client::getHttpClient);
    assertThrows(IllegalStateException.class, () -> client.prepareEndpointUri(ENDPOINT));
  }

  @Test
  public void testMissingGameThrows() {
    CurseForgeClient client = fullyConfiguredClient();
    client.setGame(null);

    assertThrows(IllegalStateException.class, client::getHttpClient);
    assertThrows(IllegalStateException.class, () -> client.prepareEndpointUri(ENDPOINT));
  }

  @Test
  public void testGetHttpClientWithFullyConfiguredClient() {
    CurseForgeClient client = fullyConfiguredClient();

    assertNotNull(client.getHttpClient());
  }
}
