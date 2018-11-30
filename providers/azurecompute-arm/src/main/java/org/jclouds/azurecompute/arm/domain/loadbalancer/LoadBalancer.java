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
package org.jclouds.azurecompute.arm.domain.loadbalancer;

import java.util.Map;

import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class LoadBalancer {
   
   @Nullable public abstract String id();
   @Nullable public abstract String name();
   @Nullable public abstract String location();
   @Nullable public abstract String etag();
   @Nullable public abstract Map<String, String> tags();
   @Nullable public abstract LoadBalancerProperties properties();

   @Nullable
   public abstract SKU sku();

   @SerializedNames({ "id", "name", "location", "etag", "tags", "sku", "properties"})
   public static LoadBalancer create(String id, final String name, final String location, final String etag,
         final Map<String, String> tags, final SKU sku, final LoadBalancerProperties properties) {
      return builder().id(id).name(name).location(location).etag(etag).tags(tags).sku(sku).properties(properties).build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_LoadBalancer.Builder();
   }
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder name(String name);
      public abstract Builder location(String location);
      public abstract Builder etag(String etag);
      public abstract Builder tags(Map<String, String> tags);
      public abstract Builder properties(LoadBalancerProperties properties);

      public abstract Builder sku(SKU sku);
      
      abstract Map<String, String> tags();

      abstract LoadBalancer autoBuild();
      
      public LoadBalancer build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : null);
         return autoBuild();
      }
   }

   @AutoValue
   public abstract static class SKU {

      public enum SKUName {
         Basic, Standard, Unrecognized;

         public static SKUName fromValue(final String text) {
            return (SKUName) GetEnumValue.fromValueOrDefault(text, SKUName.Unrecognized);
         }
      }

      public abstract SKUName name();

      @SerializedNames({ "name" })
      public static SKU create(final SKUName name) {

         return new AutoValue_LoadBalancer_SKU(name);
      }
   }

}
