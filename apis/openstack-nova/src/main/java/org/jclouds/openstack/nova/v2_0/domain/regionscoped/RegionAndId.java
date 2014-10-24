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
package org.jclouds.openstack.nova.v2_0.domain.regionscoped;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class RegionAndId {

   public static RegionAndId fromSlashEncoded(String id) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(id, "id"));
      checkArgument(Iterables.size(parts) == 2, "id must be in format regionId/id");
      return new RegionAndId(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static RegionAndId fromRegionAndId(String regionId, String id) {
      return new RegionAndId(regionId, id);
   }

   private static String slashEncodeRegionAndId(String regionId, String id) {
      return checkNotNull(regionId, "regionId") + "/" + checkNotNull(id, "id");
   }

   public String slashEncode() {
      return slashEncodeRegionAndId(regionId, id);
   }

   protected final String regionId;
   protected final String id;

   protected RegionAndId(String regionId, String id) {
      this.regionId = checkNotNull(regionId, "regionId");
      this.id = checkNotNull(id, "id");
   }

   public String getRegion() {
      return regionId;
   }

   public String getId() {
      return id;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(regionId, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RegionAndId other = (RegionAndId) obj;
      return Objects.equal(regionId, other.regionId) && Objects.equal(id, other.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("regionId", regionId).add("id", id);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
