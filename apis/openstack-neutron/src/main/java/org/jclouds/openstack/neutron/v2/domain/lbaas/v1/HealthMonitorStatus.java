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
package org.jclouds.openstack.neutron.v2.domain.lbaas.v1;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * A Neutron LBaaS v1 HealthMonitorStatus.
 * Contains an id and status describing the health monitor's status.
 *
 * @see Pool#getHealthMonitorsStatus()
 */
public class HealthMonitorStatus {

   // Mandatory attributes
   @Named("monitor_id")
   protected final String id;
   protected final LBaaSStatus status;
   // Optional attributes
   @Named("status_description")
   protected final String statusDescription;

   @ConstructorProperties({ "monitor_id", "status", "status_description" })
   protected HealthMonitorStatus(String id, LBaaSStatus status, String statusDescription) {
      this.id = id;
      this.status = status;
      this.statusDescription = statusDescription;
   }

   /**
    * @return the id of the HealthMonitorStatus.
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the status of the HealthMonitorStatus
    */
   @Nullable
   public LBaaSStatus getStatus() {
      return status;
   }

   /**
    * @return the status description of the HealthMonitorStatus
    */
   @Nullable
   public String getStatusDescription() {
      return statusDescription;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, status, statusDescription);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      HealthMonitorStatus that = HealthMonitorStatus.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.status, that.status)
            && Objects.equal(this.statusDescription, that.statusDescription);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("id", id).add("status", status)
            .add("statusDescription", statusDescription);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
