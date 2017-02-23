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
public abstract class Disk {

   /**
    * The id of the disk
    */
   @Nullable public abstract String id();
   
   /**
    * The name of the disk
    */
   @Nullable public abstract String name();

   /**
    * The location of the disk
    */
   public abstract String location();

   /**
    * The type of the disk
    */
   @Nullable public abstract String type();

   /**
    * The sku of the disk
    */
   @Nullable public abstract SKU sku();

   /**
    * The managed disk parameters.
    */
   public abstract DiskProperties properties();
   
   /**
    * the tags of the disk
    */
   @Nullable public abstract Map<String, String> tags();

   @SerializedNames({"id", "name", "location", "type", "sku", "properties", "tags"})
   public static Disk create(final String id, final String name, final String location,
                             final String type, final SKU sku, 
                             final DiskProperties properties, final Map<String, String> tags) {
      return builder()
              .id(id)
              .name(name)
              .location(location)
              .type(type)
              .sku(sku)
              .properties(properties)
              .tags(tags)
              .build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Disk.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder name(String name);
      public abstract Builder location(String location);
      public abstract Builder type(String type);
      public abstract Builder sku(SKU sku);
      public abstract Builder properties(DiskProperties properties);
      public abstract Builder tags(Map<String, String> tags);

      abstract Map<String, String> tags();
      abstract Disk autoBuild();

      public Disk build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : null);
         return autoBuild();
      }
      
   }
}
