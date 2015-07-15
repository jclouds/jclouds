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

import com.google.common.base.Objects;

/**
 * @deprecated This package has been replaced with {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped}.
 *             Please use {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped.ZoneState ZoneState}
 *             instead. To be removed in jclouds 2.0.
 */
@Deprecated
public class ZoneState {

   private final boolean available;

   protected ZoneState(boolean available) {
      this.available = available;
   }

   public boolean available() {
      return this.available;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(available);
   }

   @Override
   public boolean equals(Object obj) {
      if (this != obj) return false;
      if (obj == null || getClass() != obj.getClass()) return false;
      ZoneState that = ZoneState.class.cast(obj);
      return Objects.equal(this.available, that.available);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("available", available);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
