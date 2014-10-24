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

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions.SessionAffinityValue;
import org.jclouds.javax.annotation.Nullable;

/**
 * Represents an TargetPool resource.
 */
@Beta
public final class TargetPool extends Resource {

   private final URI region;
   private final Set<URI> healthChecks;
   private final Set<URI> instances;
   private final Optional<SessionAffinityValue> sessionAffinity;
   private final float failoverRatio;
   private final Optional<URI> backupPool;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "region", "healthChecks", "instances",
           "sessionAffinity", "failoverRatio", "backupPool"
   })
   private TargetPool(String id, Date creationTimestamp, URI selfLink, String name, String description,
                      URI region, Set<URI> healthChecks, Set<URI> instances, @Nullable SessionAffinityValue sessionAffinity,
                      float failoverRatio, @Nullable URI backupPool) {
      super(Kind.TARGET_POOL, id, creationTimestamp, selfLink, name, description);
      this.region = checkNotNull(region, "region of %s", name);
      this.healthChecks = healthChecks == null ? ImmutableSet.<URI>of() : healthChecks;
      this.instances = instances == null ? ImmutableSet.<URI>of() : instances;
      this.sessionAffinity = fromNullable(sessionAffinity);
      this.failoverRatio = failoverRatio;
      this.backupPool = fromNullable(backupPool);
   }

   public static Builder builder() {
      return new Builder();
   }

   /**
    * @return URL of the region where the forwarding pool resides.
    */
   public URI getRegion() {
      return region;
   }

   /**
    * @return The A URL to one HttpHealthCheck resource. A member VM in this pool is considered healthy if and only if
    * the specified health checks pass. An empty list means all member virtual machines will be considered healthy at
    * all times but the health status of this target pool will be marked as unhealthy to indicate that no health checks
    * are being performed.
    */
   public Set<URI> getHealthChecks() {
      return healthChecks;
   }

   /**
    * @return A list of resource URLs to the member VMs serving this pool. They must live in zones contained in the same
    * region as this pool.
    */
   public Set<URI> getInstances() {
      return instances;
   }

   /**
    * @return the session affinity option, determines the hash method that Google Compute Engine uses to
    * distribute traffic.
    */
   public Optional<SessionAffinityValue> getSessionAffinity() {
      return sessionAffinity;
   }

   /**
    * This field is applicable only when the target pool is serving a forwarding rule as the primary pool.
    * The value of the a float between [0, 1]. If set, backupPool must also be set. Together,
    * they define the fallback behavior of the primary target pool. If the ratio of the healthy VMs in the primary
    * pool is at or below this number, traffic arriving at the load-balanced IP will be directed to the backup pool.
    * In case where failoverRatio is not set or all the VMs in the backup pool are unhealthy,
    * the traffic will be  directed back to the primary pool in the force mode, where traffic will be spread to the
    * healthy VMs with the best effort, or to all VMs when no VM is healthy.
    * @return the failover ratio
    */
   public float getFailoverRatio() {
      return failoverRatio;
   }

   /**
    * This field is applicable only when the target pool is serving a forwarding rule as the primary pool.
    * Must be a fully-qualified URL to a target pool that is in the same region as the primary target pool.
    * If set, failoverRatio must also be set. Together, they define the fallback behavior of the primary target pool.
    * If the ratio of the healthy VMs in the primary pool is at or below this number,
    * traffic arriving at the load-balanced IP will be directed to the backup pool. In case where failoverRatio is
    * not set or all the VMs in the backup pool are unhealthy, the traffic will be directed back to the primary pool
    * in the force mode, where traffic will be spread to the healthy VMs with the best effort,
    * or to all VMs when no VM is healthy.
    * @return the backup pool
    */
   public Optional<URI> getBackupPool() {
      return backupPool;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      TargetPool that = TargetPool.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.region, that.region);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("region", region)
              .add("healthChecks", healthChecks)
              .add("instances", instances)
              .add("sessionAffinity", sessionAffinity.orNull())
              .add("failoverRatio", failoverRatio)
              .add("backupPool", backupPool.orNull());
   }

   public Builder toBuilder() {
      return new Builder().fromTargetPool(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {
      private URI region;
      private ImmutableSet.Builder<URI> healthChecks = ImmutableSet.builder();
      private ImmutableSet.Builder<URI> instances = ImmutableSet.builder();
      private SessionAffinityValue sessionAffinity;
      private float failoverRatio;
      private URI backupPool;

      /**
       * @see TargetPool#getRegion()
       */
      public Builder region(URI region) {
         this.region = region;
         return this;
      }

      /**
       * @see TargetPool#getHealthChecks()
       */
      public Builder healthChecks(Set<URI> healthChecks) {
         this.healthChecks.addAll(healthChecks);
         return this;
      }

      /**
       * @see TargetPool#getInstances()
       */
      public Builder instances(Set<URI> instances) {
         this.instances.addAll(instances);
         return this;
      }

      /**
       * @see TargetPool#getSessionAffinity()
       */
      public Builder sessionAffinity(SessionAffinityValue sessionAffinity) {
         this.sessionAffinity = sessionAffinity;
         return this;
      }

      /**
       * @see TargetPool#getFailoverRatio()
       */
      public Builder failoverRatio(float failoverRatio) {
         this.failoverRatio = failoverRatio;
         return this;
      }

      public Builder backupPool(URI backupPool) {
         this.backupPool = backupPool;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public TargetPool build() {
         return new TargetPool(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, region, healthChecks.build(), instances.build(),
                 sessionAffinity, failoverRatio, backupPool);
      }

      public Builder fromTargetPool(TargetPool in) {
         return super.fromResource(in)
                 .region(in.getRegion())
                 .healthChecks(in.getHealthChecks())
                 .instances(in.getInstances())
                 .sessionAffinity(in.getSessionAffinity().orNull())
                 .failoverRatio(in.getFailoverRatio())
                 .backupPool(in.getBackupPool().orNull());
      }
   }

}
