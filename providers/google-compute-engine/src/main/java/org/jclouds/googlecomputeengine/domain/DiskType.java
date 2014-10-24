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

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Represents a DiskType resource.
 *
 * @see <a href="https://cloud.google.com/compute/docs/reference/latest/diskTypes"/>
 */
public final class DiskType extends Resource {

   private final String zone;
   private final Long defaultDiskSizeGb;
   private final Optional<String> validDiskSize;
   private final Optional<Deprecated> deprecated;

   @ConstructorProperties({
      "id", "creationTimestamp", "selfLink", "name", "description", "validDiskSize",
      "deprecated", "zone", "defaultDiskSizeGb"
   })
   private DiskType(String id, Date creationTimestamp, URI selfLink, String name, String description,
                  String validDiskSize, Deprecated deprecated, String zone,  long defaultDiskSizeGb){
      super(Kind.DISK_TYPE, id == null ? "" : id, creationTimestamp, selfLink, name, description);
      this.validDiskSize = fromNullable(validDiskSize);
      this.deprecated = fromNullable(deprecated);
      this.zone = checkNotNull(zone, "zone of %s", name);
      this.defaultDiskSizeGb = defaultDiskSizeGb;
   }

   /**
    * @return An optional textual description of the valid disk size. For example, "10GB-10TB."
    */
   public Optional<String> getValidDiskSize(){
      return validDiskSize;
   }

   /**
    * @return If applicable, the deprecation status associated with this disk type.
    */
   public Optional<Deprecated> getDeprecated(){
      return deprecated;
   }

   /**
    * @return The fully-qualified URL for the zone where the disk type resource resides.
    */
   public String getZone(){
      return zone;
   }

   /**
    * @return Server defined default disk size in GB.
    */
   public long getDefaultDiskSizeGb(){
      return defaultDiskSizeGb;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("validDiskSize", validDiskSize.orNull())
              .add("defaultDiskSizeGb", defaultDiskSizeGb)
              .add("zone", zone)
              .add("deprecated", deprecated.orNull());
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

   public Builder toBuilder(){
      return new Builder().fromDiskType(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private String zone;
      private Long defaultDiskSizeGb;
      private String validDiskSize;
      private Deprecated deprecated;

      /**
       * @see DiskType#getZone()
       */
      public Builder zone(String zone){
         this.zone = zone;
         return this;
      }

      /**
       * @see DiskType#getDefaultDiskSizeGb()
       */
      public Builder defaultDiskSizeGb(long defaultDiskSizeGb){
         this.defaultDiskSizeGb = defaultDiskSizeGb;
         return this;
      }

      /**
       * @see DiskType#getValidDiskSize()
       */
      public Builder validDiskSize(String validDiskSize){
         this.validDiskSize = validDiskSize;
         return this;
      }

      /**
       * @see DiskType#getDeprecated()
       */
      public Builder deprecated(Deprecated deprecated){
         this.deprecated = deprecated;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public DiskType build() {
         return new DiskType(id, creationTimestamp, selfLink, name, description,
               validDiskSize, deprecated, zone, defaultDiskSizeGb);
      }

      public Builder fromDiskType(DiskType in) {
         return super.fromResource(in).zone(in.getZone()).defaultDiskSizeGb(in
                 .getDefaultDiskSizeGb()).validDiskSize(in.getValidDiskSize().orNull())
                 .deprecated(in.getDeprecated().orNull());
      }
   }
}
