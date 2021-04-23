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

package com.ragedunicorn.tools.maven.model;

import com.google.gson.annotations.Expose;
import java.util.Arrays;
import java.util.Objects;

public class Metadata {
  @Expose
  private String changelog;

  @Expose
  private String changelogType;

  @Expose
  private String displayName;

  @Expose
  private int[] gameVersions;

  @Expose
  private String releaseType;

  public String getChangelog() {
    return changelog;
  }

  public void setChangelog(String changelog) {
    this.changelog = changelog;
  }

  public String getChangelogType() {
    return changelogType;
  }

  public void setChangelogType(String changelogType) {
    this.changelogType = changelogType;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public int[] getGameVersions() {
    return gameVersions.clone();
  }

  public void setGameVersions(int[] gameVersions) {
    this.gameVersions = gameVersions.clone();
  }

  public String getReleaseType() {
    return releaseType;
  }

  public void setReleaseType(String releaseType) {
    this.releaseType = releaseType;
  }

  @Override
  public String toString() {
    return "Metadata{"
        + "changelog='" + changelog + '\''
        + ", changelogType='" + changelogType + '\''
        + ", displayName='" + displayName + '\''
        + ", gameVersions=" + Arrays.toString(gameVersions)
        + ", releaseType='" + releaseType + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Metadata metadata = (Metadata) o;
    return Objects.equals(changelog, metadata.changelog)
        && Objects.equals(changelogType, metadata.changelogType)
        && Objects.equals(displayName, metadata.displayName)
        && Arrays.equals(gameVersions, metadata.gameVersions)
        && Objects.equals(releaseType, metadata.releaseType);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(changelog, changelogType, displayName, releaseType);
    result = 31 * result + Arrays.hashCode(gameVersions);
    return result;
  }
}
