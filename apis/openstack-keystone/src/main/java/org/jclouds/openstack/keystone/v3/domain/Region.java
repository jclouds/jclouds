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
package org.jclouds.openstack.keystone.v3.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Region {

   public abstract String id();
   public abstract String description();
   @Nullable public abstract Link link();
   @Nullable public abstract String parentRegionId();

   @SerializedNames({ "id", "description", "links", "parent_region_id" })
   public static Region create(String id, String description, Link link, String parentRegionId) {
      return builder().id(id).description(description).link(link).parentRegionId(parentRegionId).build();
   }

   Region() {
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Region.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder description(String name);
      public abstract Builder link(Link link);
      public abstract Builder parentRegionId(String parentRegionId);
      public abstract Region build();
   }
}
