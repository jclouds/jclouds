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
import java.util.List;
import java.util.Map;

import org.jclouds.domain.JsonBall;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

/**
 * Node object.
 */
public class Node {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private ImmutableMap.Builder<String, JsonBall> normalAttributes = ImmutableMap.builder();
      private ImmutableMap.Builder<String, JsonBall> overrideAttributes = ImmutableMap.builder();
      private ImmutableMap.Builder<String, JsonBall> defaultAttributes = ImmutableMap.builder();
      private ImmutableMap.Builder<String, JsonBall> automaticAttributes = ImmutableMap.builder();
      private ImmutableList.Builder<String> runList = ImmutableList.builder();
      private String environment;

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder normalAttribute(String key, JsonBall value) {
         this.normalAttributes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder normalAttributes(Map<String, JsonBall> normalAttributes) {
         this.normalAttributes.putAll(checkNotNull(normalAttributes, "normalAttributes"));
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

      public Builder defaultAttribute(String key, JsonBall value) {
         this.defaultAttributes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder defaultAttributes(Map<String, JsonBall> defaultAttributes) {
         this.defaultAttributes.putAll(checkNotNull(defaultAttributes, "defaultAttributes"));
         return this;
      }

      public Builder automaticAttribute(String key, JsonBall value) {
         this.automaticAttributes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder automaticAttributes(Map<String, JsonBall> automaticAttribute) {
         this.automaticAttributes.putAll(checkNotNull(automaticAttribute, "automaticAttribute"));
         return this;
      }

      public Builder runListElement(String element) {
         this.runList.add(checkNotNull(element, "element"));
         return this;
      }

      public Builder runList(Iterable<String> runList) {
         this.runList.addAll(checkNotNull(runList, "runList"));
         return this;
      }

      /**
       * @since Chef 0.10
       */
      public Builder environment(String environment) {
         this.environment = checkNotNull(environment, "environment");
         return this;
      }

      public Node build() {
         return new Node(name, normalAttributes.build(), overrideAttributes.build(), defaultAttributes.build(),
               automaticAttributes.build(), runList.build(), environment);
      }
   }

   private final String name;
   @SerializedName("normal")
   private final Map<String, JsonBall> normalAttributes;
   @SerializedName("override")
   private final Map<String, JsonBall> overrideAttributes;
   @SerializedName("default")
   private final Map<String, JsonBall> defaultAttributes;
   @SerializedName("automatic")
   private final Map<String, JsonBall> automaticAttributes;
   @SerializedName("run_list")
   private final List<String> runList;
   @SerializedName("chef_environment")
   private final String environment;

   // internal
   @SerializedName("json_class")
   private final String _jsonClass = "Chef::Node";
   @SerializedName("chef_type")
   private final String _chefType = "node";

   @ConstructorProperties({ "name", "normal", "override", "default", "automatic", "run_list", "chef_environment" })
   protected Node(String name, @Nullable Map<String, JsonBall> normalAttributes,
         @Nullable Map<String, JsonBall> overrideAttributes, @Nullable Map<String, JsonBall> defaultAttributes,
         @Nullable Map<String, JsonBall> automaticAttributes, List<String> runList, @Nullable String environment) {
      this.name = name;
      this.environment = environment;
      this.normalAttributes = copyOfOrEmpty(normalAttributes);
      this.overrideAttributes = copyOfOrEmpty(overrideAttributes);
      this.defaultAttributes = copyOfOrEmpty(defaultAttributes);
      this.automaticAttributes = copyOfOrEmpty(automaticAttributes);
      this.runList = copyOfOrEmpty(runList);
   }

   public String getName() {
      return name;
   }

   public Map<String, JsonBall> getNormalAttributes() {
      return normalAttributes;
   }

   public Map<String, JsonBall> getOverrideAttributes() {
      return overrideAttributes;
   }

   public Map<String, JsonBall> getDefaultAttributes() {
      return defaultAttributes;
   }

   public Map<String, JsonBall> getAutomaticAttributes() {
      return automaticAttributes;
   }

   public List<String> getRunList() {
      return runList;
   }

   /**
    * @since Chef 0.10
    */
   public String getEnvironment() {
      return environment;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_chefType == null) ? 0 : _chefType.hashCode());
      result = prime * result + ((_jsonClass == null) ? 0 : _jsonClass.hashCode());
      result = prime * result + ((automaticAttributes == null) ? 0 : automaticAttributes.hashCode());
      result = prime * result + ((defaultAttributes == null) ? 0 : defaultAttributes.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((normalAttributes == null) ? 0 : normalAttributes.hashCode());
      result = prime * result + ((overrideAttributes == null) ? 0 : overrideAttributes.hashCode());
      result = prime * result + ((runList == null) ? 0 : runList.hashCode());
      result = prime * result + ((environment == null) ? 0 : environment.hashCode());
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
      Node other = (Node) obj;
      if (_chefType == null) {
         if (other._chefType != null)
            return false;
      } else if (!_chefType.equals(other._chefType))
         return false;
      if (_jsonClass == null) {
         if (other._jsonClass != null)
            return false;
      } else if (!_jsonClass.equals(other._jsonClass))
         return false;
      if (automaticAttributes == null) {
         if (other.automaticAttributes != null)
            return false;
      } else if (!automaticAttributes.equals(other.automaticAttributes))
         return false;
      if (defaultAttributes == null) {
         if (other.defaultAttributes != null)
            return false;
      } else if (!defaultAttributes.equals(other.defaultAttributes))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (normalAttributes == null) {
         if (other.normalAttributes != null)
            return false;
      } else if (!normalAttributes.equals(other.normalAttributes))
         return false;
      if (overrideAttributes == null) {
         if (other.overrideAttributes != null)
            return false;
      } else if (!overrideAttributes.equals(other.overrideAttributes))
         return false;
      if (runList == null) {
         if (other.runList != null)
            return false;
      } else if (!runList.equals(other.runList))
         return false;
      if (environment == null) {
         if (other.environment != null)
            return false;
      } else if (!environment.equals(other.environment))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Node [name=" + name + ", runList=" + runList + ", normalAttributes=" + normalAttributes
            + ", defaultAttributes=" + defaultAttributes + ", overrideAttributes=" + overrideAttributes
            + ", chefEnvironment=" + environment + ", automaticAttributes=" + automaticAttributes + "]";
   }

}
