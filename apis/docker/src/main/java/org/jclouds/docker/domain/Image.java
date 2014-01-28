/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.docker.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Image {

   @SerializedName("Id")
   private final String id;
   @SerializedName("Parent")
   private final String parent;
   @SerializedName("Created")
   private final String created;
   @SerializedName("Container")
   private final String container;
   @SerializedName("DockerVersion")
   private final String dockerVersion;
   @SerializedName("Architecture")
   private final String architecture;
   @SerializedName("Os")
   private final String os;
   @SerializedName("Size")
   private final long size;
   @SerializedName("VirtualSize")
   private final long virtualSize;
   @SerializedName("RepoTags")
   private final List<String> repoTags;

   @ConstructorProperties({ "Id", "Parent", "Created", "Container", "DockerVersion", "Architecture", "Os", "Size",
           "VirtualSize", "RepoTags", "Architecture" })
   protected Image(String id, @Nullable String parent, @Nullable String created, @Nullable String container,
                @Nullable String dockerVersion, @Nullable String architecture, @Nullable String os, long size,
                @Nullable long virtualSize, @Nullable List<String> repoTags) {
      this.id = checkNotNull(id, "id");
      this.parent = parent;
      this.created = created;
      this.container = container;
      this.dockerVersion = dockerVersion;
      this.architecture = architecture;
      this.os = os;
      this.size = size;
      this.virtualSize = virtualSize;
      this.repoTags = repoTags != null ? ImmutableList.copyOf(repoTags) : ImmutableList.<String> of();
   }

   public String getId() {
      return id;
   }

   public String getParent() {
      return parent;
   }

   public String getCreated() {
      return created;
   }

   public String getContainer() {
      return container;
   }

   public String getDockerVersion() {
      return dockerVersion;
   }

   public String getArchitecture() {
      return architecture;
   }

   public String getOs() {
      return os;
   }

   public long getSize() {
      return size;
   }

   public long getVirtualSize() {
      return virtualSize;
   }

   public List<String> getRepoTags() {
      return repoTags;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Image that = (Image) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.parent, that.parent) &&
              Objects.equal(this.created, that.created) &&
              Objects.equal(this.container, that.container) &&
              Objects.equal(this.dockerVersion, that.dockerVersion) &&
              Objects.equal(this.architecture, that.architecture) &&
              Objects.equal(this.os, that.os) &&
              Objects.equal(this.size, that.size) &&
              Objects.equal(this.virtualSize, that.virtualSize);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, parent, created, container, dockerVersion, architecture, os, size,
              virtualSize);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("parent", parent)
              .add("created", created)
              .add("container", container)
              .add("dockerVersion", dockerVersion)
              .add("architecture", architecture)
              .add("os", os)
              .add("size", size)
              .add("virtualSize", virtualSize)
              .add("repoTags", repoTags)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromImage(this);
   }

   public static final class Builder {

      private String id;
      private String parent;
      private String created;
      private String container;
      private String dockerVersion;
      private String architecture;
      private String os;
      private long size;
      private long virtualSize;
      private List<String> repoTags = ImmutableList.of();

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder parent(String parent) {
         this.parent = parent;
         return this;
      }

      public Builder created(String created) {
         this.created = created;
         return this;
      }

      public Builder container(String container) {
         this.container = container;
         return this;
      }

      public Builder dockerVersion(String dockerVersion) {
         this.dockerVersion = dockerVersion;
         return this;
      }

      public Builder architecture(String architecture) {
         this.architecture = architecture;
         return this;
      }

      public Builder os(String os) {
         this.os = os;
         return this;
      }

      public Builder size(long size) {
         this.size = size;
         return this;
      }

      public Builder virtualSize(long virtualSize) {
         this.virtualSize = virtualSize;
         return this;
      }

      public Builder repoTags(List<String> repoTags) {
         this.repoTags = ImmutableList.copyOf(checkNotNull(repoTags, "repoTags"));
         return this;
      }

      public Image build() {
         return new Image(id, parent, created, container, dockerVersion, architecture, os, size,
                 virtualSize, repoTags);
      }

      public Builder fromImage(Image in) {
         return this
                 .id(in.getId())
                 .parent(in.getParent())
                 .created(in.getCreated())
                 .container(in.getContainer())
                 .dockerVersion(in.getDockerVersion())
                 .architecture(in.getArchitecture())
                 .os(in.getOs())
                 .size(in.getSize())
                 .virtualSize(in.getVirtualSize());
                 //DO NOT add .repoTags(in.getRepoTags());
      }
   }
}
