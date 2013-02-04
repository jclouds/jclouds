/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.domain;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import org.jclouds.domain.JsonBall;

import java.util.Map;

public class Environment {

   private String name;
    @SerializedName("default_attributes")
   private Map<String, JsonBall> attributes = Maps.newLinkedHashMap();
   @SerializedName("override_attributes")
   private Map<String, JsonBall> overrideAttributes = Maps.newLinkedHashMap();
   private String description = "";
   @SerializedName("cookbook_versions")
   private Map<String, String> cookbookVersions = Maps.newLinkedHashMap();
   // internal
   @SerializedName("json_class")
   private String _jsonClass = "Chef::Environment";
   @SerializedName("chef_type")
   private String _chefType = "environment";

   public Environment(String name, Map<String, JsonBall> attributes, Map<String, JsonBall> overrideAttributes,
                      String description, Map<String, String> cookbookVersions) {
      this.name = name;
      this.attributes.putAll(attributes);
      this.overrideAttributes.putAll(overrideAttributes);
      this.description = description;
      this.cookbookVersions.putAll(cookbookVersions);
   }

   public Environment(String name, String description) {
      this.name = name;
      this.description = description;
   }

   public Environment(String name) {
      this(name, null);
   }

   // hidden but needs to be here for json deserialization to work
   Environment() {
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
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Environment that = (Environment) o;

      if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
      if (cookbookVersions != null ? !cookbookVersions.equals(that.cookbookVersions) : that.cookbookVersions != null)
         return false;
      if (description != null ? !description.equals(that.description) : that.description != null) return false;
      if (!name.equals(that.name)) return false;
      if (overrideAttributes != null ? !overrideAttributes.equals(that.overrideAttributes) : that.overrideAttributes != null)
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
      return "[" +
            "name='" + name + '\'' +
            ", attributes=" + attributes +
            ", overrideAttributes=" + overrideAttributes +
            ", description='" + description + '\'' +
            ", cookbookVersions=" + cookbookVersions +
            ']';
   }
}
