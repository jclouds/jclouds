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
package org.jclouds.googlecomputeengine.domain;

import static org.jclouds.googlecloud.internal.NullSafeCopies.copyOf;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions.SessionAffinityValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TargetPool {

   public abstract String id();

   public abstract URI selfLink();

   public abstract Date creationTimestamp();

   public abstract String name();

   @Nullable public abstract String description();

   public abstract URI region();

   /**
    * URL to HttpHealthCheck resources. A member VM in this pool is considered healthy if and only if
    * the specified health checks pass. An empty list means all member virtual machines will be considered healthy at
    * all times but the health status of this target pool will be marked as unhealthy to indicate that no health checks
    * are being performed.
    */
   public abstract List<URI> healthChecks();

   /**
    * A list of resource URLs to the member VMs serving this pool. They must live in zones contained in the same
    * region as this pool.
    */
   public abstract List<URI> instances();

   /**
    * The session affinity option, determines the hash method that Google Compute Engine uses to
    * distribute traffic.
    */
   @Nullable public abstract SessionAffinityValue sessionAffinity();

   /**
    * This field is applicable only when the target pool is serving a forwarding rule as the primary pool.
    * The value of the a float between [0, 1]. If set, backupPool must also be set. Together,
    * they define the fallback behavior of the primary target pool. If the ratio of the healthy VMs in the primary
    * pool is at or below this number, traffic arriving at the load-balanced IP will be directed to the backup pool.
    * In case where failoverRatio is not set or all the VMs in the backup pool are unhealthy,
    * the traffic will be  directed back to the primary pool in the force mode, where traffic will be spread to the
    * healthy VMs with the best effort, or to all VMs when no VM is healthy.
    */
   @Nullable public abstract Float failoverRatio();

   /**
    * This field is applicable only when the target pool is serving a forwarding rule as the primary pool.
    * Must be a fully-qualified URL to a target pool that is in the same region as the primary target pool.
    * If set, failoverRatio must also be set. Together, they define the fallback behavior of the primary target pool.
    * If the ratio of the healthy VMs in the primary pool is at or below this number,
    * traffic arriving at the load-balanced IP will be directed to the backup pool. In case where failoverRatio is
    * not set or all the VMs in the backup pool are unhealthy, the traffic will be directed back to the primary pool
    * in the force mode, where traffic will be spread to the healthy VMs with the best effort,
    * or to all VMs when no VM is healthy.
    */
   @Nullable public abstract URI backupPool();

   @SerializedNames({ "id", "selfLink", "creationTimestamp", "name", "description", "region", "healthChecks", "instances", "sessionAffinity",
         "failoverRatio", "backupPool" })
   public static TargetPool create(String id, URI selfLink, Date creationTimestamp, String name, String description, URI region,
         List<URI> healthChecks, List<URI> instances, SessionAffinityValue sessionAffinity, Float failoverRatio,
         URI backupPool) {
      return new AutoValue_TargetPool(id, selfLink, creationTimestamp, name, description, region, copyOf(healthChecks), copyOf(instances),
            sessionAffinity, failoverRatio, backupPool);
   }

   TargetPool() {
   }
}
