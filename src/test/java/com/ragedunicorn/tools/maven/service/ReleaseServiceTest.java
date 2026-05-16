package com.ragedunicorn.tools.maven.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ragedunicorn.tools.maven.CurseForgeClient;
import com.ragedunicorn.tools.maven.model.CurseForgeApiRelease;
import com.ragedunicorn.tools.maven.model.Metadata;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

public class ReleaseServiceTest {
  private static final URI ENDPOINT_URI =
      URI.create("https://wow.curseforge.com/api/projects/abc/upload-file");

  private CurseForgeClient curseForgeClient;
  private CloseableHttpClient httpClient;
  private CloseableHttpResponse httpResponse;
  private StatusLine statusLine;
  private String uploadFile;
  private Metadata metadata;

  @BeforeEach
  public void setUp(@TempDir Path tempDir) throws Exception {
    curseForgeClient = mock(CurseForgeClient.class);
    httpClient = mock(CloseableHttpClient.class);
    httpResponse = mock(CloseableHttpResponse.class);
    statusLine = mock(StatusLine.class);

    when(curseForgeClient.getHttpClient()).thenReturn(httpClient);
    when(curseForgeClient.prepareEndpointUri(any(String.class))).thenReturn(ENDPOINT_URI);
    when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
    when(httpResponse.getStatusLine()).thenReturn(statusLine);

    Path file = tempDir.resolve("upload.zip");
    Files.write(file, "dummy-addon-content".getBytes(StandardCharsets.UTF_8));
    uploadFile = file.toAbsolutePath().toString();

    metadata = new Metadata();
    metadata.setChangelog("# Notes");
    metadata.setChangelogType("markdown");
    metadata.setDisplayName("example-upload");
    metadata.setGameVersions(new int[] {7668, 7350});
    metadata.setReleaseType("release");
  }

  @Test
  public void createReleaseOperation_successfulUpload_returnsReleaseAndPostsMultipart()
      throws Exception {
    when(statusLine.getStatusCode()).thenReturn(200);
    when(httpResponse.getEntity()).thenReturn(entityOf("{\"id\": 123456}"));

    ReleaseService service = new ReleaseService(curseForgeClient);
    CurseForgeApiRelease release = service.createReleaseOperation(metadata, uploadFile);

    assertEquals(123456, release.getId());

    ArgumentCaptor<HttpPost> postCaptor = ArgumentCaptor.forClass(HttpPost.class);
    verify(httpClient).execute(postCaptor.capture());
    verify(httpClient).close();

    HttpPost captured = postCaptor.getValue();
    assertEquals(ENDPOINT_URI, captured.getURI());
    assertTrue(
        captured.getEntity().getContentType().getValue().startsWith("multipart/form-data"),
        "expected multipart entity, got: "
            + captured.getEntity().getContentType().getValue());
  }

  @Test
  public void createReleaseOperation_nonSuccessStatus_throwsMojoExecutionException()
      throws Exception {
    when(statusLine.getStatusCode()).thenReturn(400);
    when(httpResponse.getEntity()).thenReturn(
        entityOf("{\"errorCode\":1018,\"errorMessage\":\"Invalid token\"}"));

    ReleaseService service = new ReleaseService(curseForgeClient);

    MojoExecutionException ex = assertThrows(MojoExecutionException.class,
        () -> service.createReleaseOperation(metadata, uploadFile));
    assertTrue(ex.getMessage().startsWith("Failed to create release"),
        "unexpected message: " + ex.getMessage());
    assertTrue(ex.getMessage().contains("Invalid token"),
        "expected the CurseForge error message to be propagated, got: " + ex.getMessage());
  }

  @Test
  public void createReleaseOperation_ioExceptionOnExecute_throwsMojoExecutionException()
      throws Exception {
    when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException("boom"));

    ReleaseService service = new ReleaseService(curseForgeClient);

    MojoExecutionException ex = assertThrows(MojoExecutionException.class,
        () -> service.createReleaseOperation(metadata, uploadFile));
    assertEquals("Upload to CurseForge failed", ex.getMessage());
  }

  private static HttpEntity entityOf(String body) {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    BasicHttpEntity entity = new BasicHttpEntity();
    entity.setContent(new ByteArrayInputStream(bytes));
    entity.setContentLength(bytes.length);
    return entity;
  }
}
