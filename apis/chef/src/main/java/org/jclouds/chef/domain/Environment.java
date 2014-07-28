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
import java.util.Map;

import org.jclouds.domain.JsonBall;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

/**
 * An environment.
 */
public class Environment {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private ImmutableMap.Builder<String, JsonBall> attributes = ImmutableMap.builder();
      private ImmutableMap.Builder<String, JsonBall> overrideAttributes = ImmutableMap.builder();
      private String description = "";
      private ImmutableMap.Builder<String, String> cookbookVersions = ImmutableMap.builder();

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder attribute(String key, JsonBall value) {
         this.attributes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder attributes(Map<String, JsonBall> attributes) {
         this.attributes.putAll(checkNotNull(attributes, "attributes"));
         return this;
      }

      public Builder overrideAttribute(String key, JsonBall value) {
         this.overrideAttributes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder overrideAttributes(Map<String, JsonBall> overrideAttributes) {
         this.overrideAttributes.putAll(checkNotNull(overrideAttributes, "overrideAttributes"));
         return this;
      }

      public Builder cookbookVersion(String key, String version) {
         this.cookbookVersions.put(checkNotNull(key, "key"), checkNotNull(version, "version"));
         return this;
      }

      public Builder cookbookVersions(Map<String, String> cookbookVersions) {
         this.cookbookVersions.putAll(checkNotNull(cookbookVersions, "cookbookVersions"));
         return this;
      }

      public Builder description(String description) {
         this.description = checkNotNull(description, "description");
         return this;
      }

      public Environment build() {
         return new Environment(name, attributes.build(), overrideAttributes.build(), description,
               cookbookVersions.build());
      }
   }

   private final String name;
   @SerializedName("default_attributes")
   private final Map<String, JsonBall> attributes;
   @SerializedName("override_attributes")
   private final Map<String, JsonBall> overrideAttributes;
   private final String description;
   @SerializedName("cookbook_versions")
   private final Map<String, String> cookbookVersions;

   // internal
   @SerializedName("json_class")
   private final String _jsonClass = "Chef::Environment";
   @SerializedName("chef_type")
   private final String _chefType = "environment";

   @ConstructorProperties({ "name", "default_attributes", "override_attributes", "description", "cookbook_versions" })
   protected Environment(String name, @Nullable Map<String, JsonBall> attributes,
         @Nullable Map<String, JsonBall> overrideAttributes, String description,
         @Nullable Map<String, String> cookbookVersions) {
      this.name = name;
      this.attributes = copyOfOrEmpty(attributes);
      this.overrideAttributes = copyOfOrEmpty(overrideAttributes);
      this.description = description;
      this.cookbookVersions = copyOfOrEmpty(cookbookVersions);
   }

   public String getName() {
      return name;
   }

   public Map<String, JsonBall> getAttributes() {
      return attributes;
   }

   public Map<String, JsonBall> getOverrideAttributes() {
      return overrideAttributes;
   }

   public String getDescription() {
      return description;
   }

   public Map<String, String> getCookbookVersions() {
      return cookbookVersions;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      Environment that = (Environment) o;

      if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null)
         return false;
      if (cookbookVersions != null ? !cookbookVersions.equals(that.cookbookVersions) : that.cookbookVersions != null)
         return false;
      if (description != null ? !description.equals(that.description) : that.description != null)
         return false;
      if (!name.equals(that.name))
         return false;
      if (overrideAttributes != null ? !overrideAttributes.equals(that.overrideAttributes)
            : that.overrideAttributes != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = name.hashCode();
      result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
      result = 31 * result + (overrideAttributes != null ? overrideAttributes.hashCode() : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (cookbookVersions != null ? cookbookVersions.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "Environment [" + "name='" + name + '\'' + ", attributes=" + attributes + ", overrideAttributes="
            + overrideAttributes + ", description='" + description + '\'' + ", cookbookVersions=" + cookbookVersions
            + ']';
   }
}
