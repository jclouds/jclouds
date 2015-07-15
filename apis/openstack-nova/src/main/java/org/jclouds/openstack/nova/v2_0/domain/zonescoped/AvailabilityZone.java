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

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @deprecated This package has been replaced with {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped}.
 *             Please use {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZone AvailabilityZone}
 *             instead. To be removed in jclouds 2.0.
 */
@Deprecated
public class AvailabilityZone {

   @SerializedName("zoneName")
   private final String name;
   private final ZoneState state;

   @ConstructorProperties({"zoneName" , "zoneState"})
   protected AvailabilityZone(String name, ZoneState state) {
      this.name = name;
      this.state = state;
   }

   public String getName() {
      return name;
   }

   public ZoneState getState() {
      return state;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, state);
   }

   @Override
   public boolean equals(Object obj) {
      if (this != obj) return false;
      if (obj == null || getClass() != obj.getClass()) return false;
      AvailabilityZone that = AvailabilityZone.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.state, that.state);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("state", state);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
