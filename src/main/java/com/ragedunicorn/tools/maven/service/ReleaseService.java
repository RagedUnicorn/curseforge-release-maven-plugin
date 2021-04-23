/*
 * Copyright (c) 2021 Michael Wiesendanger
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:

 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ragedunicorn.tools.maven.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ragedunicorn.tools.maven.CurseForgeClient;
import com.ragedunicorn.tools.maven.log.DefaultLog;
import com.ragedunicorn.tools.maven.model.CurseForgeApiClientError;
import com.ragedunicorn.tools.maven.model.CurseForgeApiRelease;
import com.ragedunicorn.tools.maven.model.Metadata;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoExecutionException;

public class ReleaseService {
  private static final String ENDPOINT = "/api/projects/:projectId/upload-file";

  private final DefaultLog logger = new DefaultLog();

  private final CurseForgeClient curseForgeClient;

  public ReleaseService(CurseForgeClient curseForgeClient) {
    this.curseForgeClient = curseForgeClient;
  }

  /**
   * Create a new release.
   *
   * @param metadata All metadata related to the upload
   * @param file Path to the file to upload
   * @return The received response from CurseForge after uploading the addon
   * @throws MojoExecutionException If the request to the CurseForge Api failed
   */
  public CurseForgeApiRelease createReleaseOperation(Metadata metadata, String file)
      throws MojoExecutionException {
    CurseForgeApiRelease curseForgeApiRelease;
    final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    HttpEntity entity = MultipartEntityBuilder
        .create()
        .addTextBody("metadata", gson.toJson(metadata))
        .addBinaryBody("file", new File(file), ContentType.create("application/octet-stream"),
            metadata.getDisplayName())
        .build();

    CloseableHttpClient httpClient = curseForgeClient.getHttpClient();

    HttpPost httpPost = new HttpPost();
    URI preparedEndpointUrl = curseForgeClient.prepareEndpointUri(ENDPOINT);
    if (logger.isDebugEnabled()) {
      logger.debug("Endpoint Uri: " + preparedEndpointUrl.getPath());
    }
    httpPost.setURI(preparedEndpointUrl);
    httpPost.setEntity(entity);

    try {
      CloseableHttpResponse response = httpClient.execute(httpPost);
      curseForgeApiRelease = responseHandler(response);

      if (logger.isInfoEnabled()) {
        logger.info("Upload successful");
        logger.info("File id: " + curseForgeApiRelease.getId());
      }
    } catch (IOException e) {
      throw new MojoExecutionException("Upload to CurseForge failed", e);
    }

    try {
      httpClient.close();
    } catch (IOException e) {
      throw new MojoExecutionException("Failed to close http client", e);
    }

    return curseForgeApiRelease;
  }

  /**
   * Check if the release request was successful and handle both failure and success.
   *
   * @param response The response from the CurseForge Api
   * @return The response from the CurseForge Api as POJO
   * @throws IOException            If entity cannot be converted to a string
   * @throws MojoExecutionException If creation of release failed
   */
  private CurseForgeApiRelease responseHandler(CloseableHttpResponse response)
      throws IOException, MojoExecutionException {
    final Gson gson = new Gson();
    final HttpEntity entity = response.getEntity();
    final String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
    final int statusCode = response.getStatusLine().getStatusCode();

    if (statusCode / 100 != 2) {
      CurseForgeApiClientError clientError = gson.fromJson(responseString, CurseForgeApiClientError.class);
      logger.error(clientError.toString());

      throw new MojoExecutionException("Failed to create release - reason: "
          + clientError.getErrorMessage());
    } else {
      return gson.fromJson(responseString, CurseForgeApiRelease.class);
    }
  }
}
