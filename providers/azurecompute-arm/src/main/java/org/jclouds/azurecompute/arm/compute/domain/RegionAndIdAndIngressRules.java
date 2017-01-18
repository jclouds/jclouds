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
package org.jclouds.azurecompute.arm.compute.domain;

import org.jclouds.azurecompute.arm.domain.RegionAndId;

import com.google.auto.value.AutoValue;
import com.google.common.base.Objects;

@AutoValue
public abstract class RegionAndIdAndIngressRules {

   abstract RegionAndId regionAndId(); // Intentionally hidden
   public abstract int[] inboundPorts();

   RegionAndIdAndIngressRules() {

   }

   public static RegionAndIdAndIngressRules create(String region, String id, int[] inboundPorts) {
      return new AutoValue_RegionAndIdAndIngressRules(RegionAndId.fromRegionAndId(region, id), inboundPorts);
   }

   public String id() {
      return regionAndId().id();
   }

   public String region() {
      return regionAndId().region();
   }

   // Intentionally delegate equals and hashcode to the fields in the parent
   // class so that we can search only by region/id in a map

   @Override
   public int hashCode() {
      return Objects.hashCode(region(), id());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (!(obj instanceof RegionAndId)) {
         return false;
      }
      RegionAndId that = (RegionAndId) obj;
      return Objects.equal(region(), that.region()) && Objects.equal(id(), that.id());
   }

}
