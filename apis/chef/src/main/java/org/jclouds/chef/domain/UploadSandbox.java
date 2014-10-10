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
package org.jclouds.chef.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.chef.util.CollectionUtils.copyOfOrEmpty;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

/**
 * An upload sandbox.
 */
public class UploadSandbox {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private URI uri;
      private ImmutableMap.Builder<List<Byte>, ChecksumStatus> checksums = ImmutableMap.builder();
      private String sandboxId;

      public Builder uri(URI uri) {
         this.uri = checkNotNull(uri, "uri");
         return this;
      }

      public Builder checksum(List<Byte> key, ChecksumStatus value) {
         this.checksums.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder checksums(Map<List<Byte>, ChecksumStatus> checksums) {
         this.checksums.putAll(checkNotNull(checksums, "checksums"));
         return this;
      }

      public Builder sandboxId(String sandboxId) {
         this.sandboxId = checkNotNull(sandboxId, "sandboxId");
         return this;
      }

      public UploadSandbox build() {
         return new UploadSandbox(uri, checksums.build(), sandboxId);
      }
   }

   private final URI uri;
   private final Map<List<Byte>, ChecksumStatus> checksums;
   @SerializedName("sandbox_id")
   private final String sandboxId;

   @ConstructorProperties({ "uri", "checksums", "sandbox_id" })
   protected UploadSandbox(URI uri, @Nullable Map<List<Byte>, ChecksumStatus> checksums, String sandboxId) {
      this.uri = uri;
      this.checksums = copyOfOrEmpty(checksums);
      this.sandboxId = sandboxId;
   }

   public URI getUri() {
      return uri;
   }

   public Map<List<Byte>, ChecksumStatus> getChecksums() {
      return checksums;
   }

   public String getSandboxId() {
      return sandboxId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((checksums == null) ? 0 : checksums.hashCode());
      result = prime * result + ((sandboxId == null) ? 0 : sandboxId.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      UploadSandbox other = (UploadSandbox) obj;
      if (checksums == null) {
         if (other.checksums != null)
            return false;
      } else if (!checksums.equals(other.checksums))
         return false;
      if (sandboxId == null) {
         if (other.sandboxId != null)
            return false;
      } else if (!sandboxId.equals(other.sandboxId))
         return false;
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "UploadSandbox [checksums=" + checksums + ", id=" + sandboxId + ", uri=" + uri + "]";
   }

}
