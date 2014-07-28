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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * Cookbook definition as returned by the Chef server >= 0.10.8.
 */
public class CookbookDefinition {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private URI url;
      private ImmutableSet.Builder<Version> versions = ImmutableSet.builder();

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder url(URI url) {
         this.url = checkNotNull(url, "url");
         return this;
      }

      public Builder version(Version version) {
         this.versions.add(checkNotNull(version, "version"));
         return this;
      }

      public Builder versions(Iterable<Version> versions) {
         this.versions.addAll(checkNotNull(versions, "versions"));
         return this;
      }

      public Builder from(CookbookDefinition def) {
         this.url = checkNotNull(def.getUrl(), "url");
         this.versions.addAll(checkNotNull(def.getVersions(), "versions"));
         this.name = def.getName();
         return this;
      }

      public CookbookDefinition build() {
         return new CookbookDefinition(name, url, versions.build());
      }
   }

   private final String name;
   private final URI url;
   private final Set<Version> versions;

   @ConstructorProperties({"name", "url", "versions" })
   protected CookbookDefinition(String name, URI url, @Nullable Set<Version> versions) {
      this.name = name;
      this.url = url;
      this.versions = copyOfOrEmpty(versions);
   }

   public String getName() {
      return name;
   }

   public URI getUrl() {
      return url;
   }

   public Set<Version> getVersions() {
      return versions;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      result = prime * result + ((versions == null) ? 0 : versions.hashCode());
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
      CookbookDefinition other = (CookbookDefinition) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      if (versions == null) {
         if (other.versions != null)
            return false;
      } else if (!versions.equals(other.versions))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "CookbookDefinition [name=" + name + ", url=" + url + ", versions=" + versions + "]";
   }

   public static class Version {
      public static Builder builder() {
         return new Builder();
      }

      public static class Builder {
         private URI url;
         private String version;

         public Builder url(URI url) {
            this.url = checkNotNull(url, "url");
            return this;
         }

         public Builder version(String version) {
            this.version = checkNotNull(version, "version");
            return this;
         }

         public Version build() {
            return new Version(url, version);
         }
      }

      private final URI url;
      private final String version;

      @ConstructorProperties({ "url", "version" })
      protected Version(URI url, String version) {
         this.url = url;
         this.version = version;
      }

      public URI getUrl() {
         return url;
      }

      public String getVersion() {
         return version;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((url == null) ? 0 : url.hashCode());
         result = prime * result + ((version == null) ? 0 : version.hashCode());
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
         Version other = (Version) obj;
         if (url == null) {
            if (other.url != null)
               return false;
         } else if (!url.equals(other.url))
            return false;
         if (version == null) {
            if (other.version != null)
               return false;
         } else if (!version.equals(other.version))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "Version [url=" + url + ", version=" + version + "]";
      }
   }
}
