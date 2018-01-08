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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Map;

/**
 * VirtualMachineScaleSet for subscription
 */
@AutoValue
public abstract class VirtualMachineScaleSet {

   /**
    * The id of the virtual machine scale set
    */
   @Nullable
   public abstract String id();

   /**
    * The name of the virtual machine scale set
    */
   @Nullable
   public abstract String name();

   /**
    * The location of the virtual machine scale set
    */
   @Nullable
   public abstract String location();

   /**
    * Specifies the sku of the virtual machine scale set
    */
   public abstract VirtualMachineScaleSetSKU sku();

   /**
    * Specifies the tags of the virtual machine scale set
    */
   @Nullable
   public abstract Map<String, String> tags();


   /**
    * Specifies the optional plan of the virtual machine scale set (only for market image)
    */
   @Nullable
   public abstract VirtualMachineScaleSetPlan plan();

   /**
    * Specifies the properties of the availability set
    */
   @Nullable
   public abstract VirtualMachineScaleSetProperties properties();

   @SerializedNames({  "id", "name", "location", "sku", "tags", "plan", "properties"})
   public static VirtualMachineScaleSet create(final String id, final String name, final String location,
                                               VirtualMachineScaleSetSKU sku, final Map<String, String> tags,
                                               VirtualMachineScaleSetPlan plan,
                                               VirtualMachineScaleSetProperties properties) {
      return builder().id(id).name(name).location(location).sku(sku).tags(tags)
         .plan(plan).properties(properties)
         .build();
   }

   public abstract Builder toBuilder();

   private static Builder builder() {
      return new AutoValue_VirtualMachineScaleSet.Builder();
   }


   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder name(String name);
      public abstract Builder location(String location);
      public abstract Builder sku(VirtualMachineScaleSetSKU sku);
      public abstract Builder tags(Map<String, String> tags);
      public abstract Builder plan(VirtualMachineScaleSetPlan plan);
      public abstract Builder properties(VirtualMachineScaleSetProperties properties);

      abstract Map<String, String> tags();
      abstract VirtualMachineScaleSet autoBuild();

      public VirtualMachineScaleSet build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : null);
         return autoBuild();
      }
   }
}
