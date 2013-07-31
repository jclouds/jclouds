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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a region resource.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta15/regions"/>
 */
@Beta
public final class Region extends Resource {

   public enum Status {
      UP,
      DOWN
   }

   private final Status status;
   private final Set<URI> zones;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "status",
           "zones"
   })
   private Region(String id, Date creationTimestamp, URI selfLink, String name, String description,
                  Status status, Set<URI> zones) {
      super(Kind.REGION, id, creationTimestamp, selfLink, name, description);
      this.status = checkNotNull(status, "status of %name", name);
      this.zones = zones == null ? ImmutableSet.<URI>of() : ImmutableSet
              .copyOf(zones);
   }

   /**
    * @return Status of the region. "UP" or "DOWN".
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @return the zones that can be used in this region.
    */
   @Nullable
   public Set<URI> getZones() {
      return zones;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("status", status)
              .add("zones", zones);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromRegion(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Status status;
      private ImmutableSet.Builder<URI> zones = ImmutableSet.builder();

      /**
       * @see org.jclouds.googlecomputeengine.domain.Region#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see Region#getZones()
       */
      public Builder zone(URI zone) {
         this.zones.add(checkNotNull(zone, "zone"));
         return this;
      }

      /**
       * @see Region#getZones()
       */
      public Builder zones(Set<URI> zones) {
         this.zones.addAll(checkNotNull(zones, "zones"));
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Region build() {
         return new Region(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, status, zones.build());
      }

      public Builder fromRegion(Region in) {
         return super.fromResource(in)
                 .status(in.getStatus())
                 .zones(in.getZones());
      }
   }

}
