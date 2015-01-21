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
package org.jclouds.openstack.nova.v2_0.domain.zonescoped;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Helpful when looking for resources by region and name
 *
 * @deprecated This package has been replaced with {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped}.
 *             Please use {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName RegionAndName}
 *             instead. To be removed in jclouds 2.0.
 */
@Deprecated
public class RegionAndName {

   public static final Function<RegionAndName, String> NAME_FUNCTION = new Function<RegionAndName, String>() {

      @Override
      public String apply(RegionAndName input) {
         return input.getName();
      }

   };

   public static final Function<RegionAndName, String> REGION_FUNCTION = new Function<RegionAndName, String>() {

      @Override
      public String apply(RegionAndName input) {
         return input.getRegion();
      }

   };

   public static RegionAndName fromSlashEncoded(String name) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(name, "name"));
      checkArgument(Iterables.size(parts) == 2, "name must be in format regionId/name");
      return new RegionAndName(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static RegionAndName fromRegionAndName(String regionId, String name) {
      return new RegionAndName(regionId, name);
   }

   private static String slashEncodeRegionAndName(String regionId, String name) {
      return checkNotNull(regionId, "regionId") + "/" + checkNotNull(name, "name");
   }

   public String slashEncode() {
      return slashEncodeRegionAndName(regionId, name);
   }

   protected final String regionId;
   protected final String name;

   protected RegionAndName(String regionId, String name) {
      this.regionId = checkNotNull(regionId, "regionId");
      this.name = checkNotNull(name, "name");
   }

   public String getRegion() {
      return regionId;
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RegionAndName that = RegionAndName.class.cast(o);
      return equal(this.regionId, that.regionId) && equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(regionId, name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("regionId", regionId).add("name", name);
   }
}
