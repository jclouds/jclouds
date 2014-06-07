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

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Arrays;

import org.jclouds.io.payloads.FilePayload;

import com.google.common.primitives.Bytes;

/**
 * Resource object.
 */
public class Resource {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private URI url;
      private byte[] checksum;
      private String path;
      private String specificity = "default";

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder url(URI url) {
         this.url = checkNotNull(url, "url");
         return this;
      }

      public Builder checksum(byte[] checksum) {
         this.checksum = checkNotNull(checksum, "checksum");
         return this;
      }

      public Builder path(String path) {
         this.path = checkNotNull(path, "path");
         return this;
      }

      public Builder specificity(String specificity) {
         this.specificity = checkNotNull(specificity, "specificity");
         return this;
      }

      public Builder fromPayload(FilePayload payload) {
         checkNotNull(payload, "payload");
         this.name(payload.getRawContent().getName());
         this.checksum(payload.getContentMetadata().getContentMD5());
         this.path(payload.getRawContent().getPath());
         return this;
      }

      public Resource build() {
         return new Resource(name, url, checksum, path, specificity);
      }
   }

   private final String name;
   private final URI url;
   private final byte[] checksum;
   private final String path;
   private final String specificity;

   @ConstructorProperties({ "name", "url", "checksum", "path", "specificity" })
   protected Resource(String name, URI url, byte[] checksum, String path, String specificity) {
      this.name = name;
      this.url = url;
      this.checksum = checksum;
      this.path = path;
      this.specificity = specificity;
   }

   public String getName() {
      return name;
   }

   public URI getUrl() {
      return url;
   }

   public byte[] getChecksum() {
      return checksum;
   }

   public String getPath() {
      return path;
   }

   public String getSpecificity() {
      return specificity;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(checksum);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((path == null) ? 0 : path.hashCode());
      result = prime * result + ((specificity == null) ? 0 : specificity.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
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
      Resource other = (Resource) obj;
      if (!Arrays.equals(checksum, other.checksum))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (path == null) {
         if (other.path != null)
            return false;
      } else if (!path.equals(other.path))
         return false;
      if (specificity == null) {
         if (other.specificity != null)
            return false;
      } else if (!specificity.equals(other.specificity))
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Resource [checksum=" + Bytes.asList(checksum) + ", name=" + name + ", path=" + path + ", specificity="
            + specificity + ", url=" + url + "]";
   }

}
