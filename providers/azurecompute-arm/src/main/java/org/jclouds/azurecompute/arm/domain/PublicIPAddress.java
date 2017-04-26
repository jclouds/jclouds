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

package org.jclouds.azurecompute.arm.domain;

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class PublicIPAddress {

   public abstract String name();
   public abstract String id();
   public abstract String etag();
   public abstract String location();
   @Nullable public abstract Map<String, String> tags();
   public abstract PublicIPAddressProperties properties();

   @SerializedNames({ "name", "id", "etag", "location", "tags", "properties" })
   public static PublicIPAddress create(String name, String id, String etag, String location, Map<String, String> tags,
         PublicIPAddressProperties properties) {
      return builder().name(name).id(id).etag(etag).location(location).tags(tags).properties(properties).build();
   }
   
   PublicIPAddress() {
      
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_PublicIPAddress.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder name(String name);
      public abstract Builder id(String id);
      public abstract Builder etag(String etag);
      public abstract Builder location(String location);
      public abstract Builder tags(Map<String, String> tags);
      public abstract Builder properties(PublicIPAddressProperties properties);
      
      abstract Map<String, String> tags();
      abstract PublicIPAddress autoBuild();

      public PublicIPAddress build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : null);
         return autoBuild();
      }
   }
}
