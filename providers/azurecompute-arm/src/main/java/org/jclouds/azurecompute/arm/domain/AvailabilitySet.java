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

import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * AvailabilitySet for subscription
 */
@AutoValue
public abstract class AvailabilitySet {

   @AutoValue
   public abstract static class AvailabilitySetProperties {

      /**
       * A platform Update Domain Count
       */
      public abstract int platformUpdateDomainCount();

      /**
       * A platform Fault Domain Count
       */
      public abstract int platformFaultDomainCount();

      /**
       * A list of virtual machines in the availability set
       */
      @Nullable
      public abstract List<IdReference> virtualMachines();
      
      /**
       * A list of statuses in the availability set
       */
      @Nullable
      public abstract List<Status> statuses();

      @SerializedNames({ "platformUpdateDomainCount", "platformFaultDomainCount", "virtualMachines", "statuses" })
      public static AvailabilitySetProperties create(final int platformUpdateDomainCount,
            final int platformFaultDomainCount, List<IdReference> virtualMachines, List<Status> statuses) {
         return builder().platformUpdateDomainCount(platformUpdateDomainCount)
               .platformFaultDomainCount(platformFaultDomainCount).virtualMachines(virtualMachines).statuses(statuses)
               .build();
      }
      
      public abstract Builder toBuilder();
      
      public static Builder builder() {
         return new AutoValue_AvailabilitySet_AvailabilitySetProperties.Builder();
      }
      
      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder platformUpdateDomainCount(int platformUpdateDomainCount);
         public abstract Builder platformFaultDomainCount(int platformFaultDomainCount);
         public abstract Builder virtualMachines(List<IdReference> virtualMachines);
         public abstract Builder statuses(List<Status> statuses);
         
         abstract List<IdReference> virtualMachines();
         abstract List<Status> statuses();
         abstract AvailabilitySetProperties autoBuild();
         
         public AvailabilitySetProperties build() {
            virtualMachines(virtualMachines() != null ? ImmutableList.copyOf(virtualMachines()) : null);
            statuses(statuses() != null ? ImmutableList.copyOf(statuses()) : null);
            return autoBuild();
         }
      }
   }
   
   public static enum AvailabilitySetType {
      MANAGED("Aligned"),
      CLASSIC("Classic");
      
      private final String value;

      AvailabilitySetType(String value) {
         this.value = value;
      }

      public static AvailabilitySetType fromString(String value) {
         AvailabilitySetType[] items = AvailabilitySetType.values();
         for (AvailabilitySetType item : items) {
            if (item.toString().equalsIgnoreCase(value)) {
               return item;
            }
         }
         throw new IllegalArgumentException("Unexpected type: " + value);
      }

      @Override
      public String toString() {
         return this.value;
      }
   }
   
   @AutoValue
   public abstract static class SKU {
      
      public abstract AvailabilitySetType type();
      
      @SerializedNames({ "name" })
      public static SKU create(final String type) {
         return create(AvailabilitySetType.fromString(type));
      }
      
      public static SKU create(AvailabilitySetType type) {
         return new AutoValue_AvailabilitySet_SKU(type);
      }
   }

   /**
    * The id of the availability set
    */
   @Nullable
   public abstract String id();

   /**
    * The name of the availability set.
    */
   @Nullable
   public abstract String name();

   /**
    * The type of the availability set.
    */
   @Nullable
   public abstract String type();

   /**
    * The location of the availability set
    */
   @Nullable
   public abstract String location();

   /**
    * Specifies the type of the availability set
    */
   @Nullable
   public abstract SKU sku();
   
   /**
    * Specifies the tags of the availability set
    */
   @Nullable
   public abstract Map<String, String> tags();
   
   /**
    * Specifies the properties of the availability set
    */
   @Nullable
   public abstract AvailabilitySetProperties properties();

   @SerializedNames({ "id", "name", "type", "location", "sku", "tags", "properties" })
   public static AvailabilitySet create(final String id, final String name, final String type, final String location,
         SKU sku, final Map<String, String> tags, AvailabilitySetProperties properties) {
      return builder().id(id).name(name).type(type).location(location).sku(sku).tags(tags).properties(properties)
            .build();
   }
   
   public abstract Builder toBuilder();
   
   private static Builder builder() {
      return new AutoValue_AvailabilitySet.Builder();
   }
   
   public static Builder managed() {
      return builder().managed();
   }
   
   public static Builder classic() {
      return builder().classic();
   }
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder name(String name);
      public abstract Builder type(String type);
      public abstract Builder location(String location);
      public abstract Builder tags(Map<String, String> tags);
      public abstract Builder properties(AvailabilitySetProperties properties);
      
      abstract Builder sku(SKU sku);
      public Builder managed() {
         return sku(SKU.create(AvailabilitySetType.MANAGED));
      }
      public Builder classic() {
         return sku(SKU.create(AvailabilitySetType.CLASSIC));
      }
      
      abstract Map<String, String> tags();
      abstract AvailabilitySet autoBuild();
      
      public AvailabilitySet build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : null);
         return autoBuild();
      }
   }
}
