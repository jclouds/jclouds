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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import java.util.Map;
import java.util.List;

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
       * A list of virtual machines in availability set
       */
      @Nullable
      public abstract List<AvailabilitySetVirtualMachine> virtualMachines();

      @SerializedNames({"platformUpdateDomainCount", "platformFaultDomainCount", "virtualMachines"})
      public static AvailabilitySetProperties create(final int platformUpdateDomainCount,
                                                     final int platformFaultDomainCount,
                                                     List<AvailabilitySetVirtualMachine> virtualMachines) {
         return new AutoValue_AvailabilitySet_AvailabilitySetProperties(platformUpdateDomainCount,
                 platformFaultDomainCount,
                 virtualMachines == null ? null : ImmutableList.copyOf(virtualMachines));
      }
   }

   /**
    * The id of the availability set
    */
   public abstract String id();

   /**
    * The name of the availability set.
    */
   @Nullable
   public abstract String name();

   /**
    * The name of the availability set.
    */
   @Nullable
   public abstract String type();

   /**
    * The location of the availability set
    */
   @Nullable
   public abstract String location();

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


   @SerializedNames({"id", "name", "type", "location", "tags", "properties"})
   public static AvailabilitySet create(final String id, final String name, final String type, final String location,
                                        final Map<String, String> tags, AvailabilitySetProperties properties) {
      return new AutoValue_AvailabilitySet(id, name, type, location, tags == null ? null : ImmutableMap.copyOf(tags), properties);
   }
}
