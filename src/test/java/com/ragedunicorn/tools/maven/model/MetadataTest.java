package com.ragedunicorn.tools.maven.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetadataTest {

  private Gson gson;
  private Metadata metadata;

  @BeforeEach
  public void setUp() {
    gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    metadata = new Metadata();
    metadata.setChangelog("# Notes");
    metadata.setChangelogType("markdown");
    metadata.setDisplayName("example-upload");
    metadata.setGameVersions(new int[] {7668, 7350});
    metadata.setReleaseType("release");
  }

  @Test
  public void toJson_thenFromJson_returnsEqualMetadata() {
    String json = gson.toJson(metadata);
    Metadata roundtripped = gson.fromJson(json, Metadata.class);

    assertEquals(metadata, roundtripped);
  }

  @Test
  public void toJson_exposedFields_useExpectedFieldNames() {
    String json = gson.toJson(metadata);

    assertTrue(json.contains("\"changelog\""), "missing changelog in: " + json);
    assertTrue(json.contains("\"changelogType\""), "missing changelogType in: " + json);
    assertTrue(json.contains("\"displayName\""), "missing displayName in: " + json);
    assertTrue(json.contains("\"gameVersions\""), "missing gameVersions in: " + json);
    assertTrue(json.contains("\"releaseType\""), "missing releaseType in: " + json);

    assertTrue(json.contains("[7668,7350]"),
        "gameVersions should serialize as a JSON array in: " + json);

    assertFalse(json.contains("game_versions"),
        "unexpected snake_case field name leaked into json: " + json);
    assertFalse(json.contains("display_name"),
        "unexpected snake_case field name leaked into json: " + json);
  }
}
