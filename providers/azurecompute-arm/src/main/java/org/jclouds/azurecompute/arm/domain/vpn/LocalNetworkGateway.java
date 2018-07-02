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
package org.jclouds.azurecompute.arm.domain.vpn;

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class LocalNetworkGateway {

   @Nullable public abstract String id();
   public abstract String name();
   public abstract String location();
   @Nullable public abstract String type();
   @Nullable public abstract Map<String, String> tags();
   @Nullable public abstract String etag();
   public abstract LocalNetworkGatewayProperties properties();

   LocalNetworkGateway() {

   }

   @SerializedNames({ "id", "name", "location", "type", "tags", "etag", "properties" })
   public static LocalNetworkGateway create(String id, String name, String location, String type,
         Map<String, String> tags, String etag, LocalNetworkGatewayProperties properties) {
      return builder(name, location, properties).id(id).type(type).tags(tags).etag(etag).build();
   }

   public abstract Builder toBuilder();

   public static Builder builder(String name, String location, LocalNetworkGatewayProperties properties) {
      return new AutoValue_LocalNetworkGateway.Builder().name(name).location(location).properties(properties);
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder name(String name);
      public abstract Builder location(String location);
      public abstract Builder type(String type);
      public abstract Builder tags(Map<String, String> tags);
      public abstract Builder etag(String etag);
      public abstract Builder properties(LocalNetworkGatewayProperties properties);

      abstract Map<String, String> tags();
      abstract LocalNetworkGateway autoBuild();

      public LocalNetworkGateway build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : null);
         return autoBuild();
      }
   }
}
