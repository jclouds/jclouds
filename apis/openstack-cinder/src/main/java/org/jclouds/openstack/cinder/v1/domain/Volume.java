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
package org.jclouds.openstack.cinder.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * An Openstack Cinder Volume.
 */
public class Volume {

   public static enum Status {
      CREATING, AVAILABLE, ATTACHING, IN_USE, DELETING, ERROR, ERROR_DELETING, UNRECOGNIZED;
      
      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
      }
      
      @Override
      public String toString() {
         return value();
      }
      
      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } 
         catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }
   
   public Builder toBuilder() {
      return new Builder().fromVolume(this);
   }

   public static class Builder {

      protected String id;
      protected Volume.Status status;
      protected int size;
      protected String zone;
      protected Date created;
      protected Set<VolumeAttachment> attachments = ImmutableSet.of();
      protected String volumeType;
      protected String snapshotId;
      protected String name;
      protected String description;
      protected Map<String, String> metadata = ImmutableMap.of();
      protected String tenantId;
   
      /**
       * @see Volume#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Volume#getStatus()
       */
      public Builder status(Volume.Status status) {
         this.status = status;
         return self();
      }

      /** 
       * @see Volume#getSize()
       */
      public Builder size(int size) {
         this.size = size;
         return self();
      }

      /** 
       * @see Volume#getZone()
       */
      public Builder zone(String zone) {
         this.zone = zone;
         return self();
      }

      /** 
       * @see Volume#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return self();
      }

      /** 
       * @see Volume#getAttachments()
       */
      public Builder attachments(Set<VolumeAttachment> attachments) {
         this.attachments = ImmutableSet.copyOf(checkNotNull(attachments, "attachments"));      
         return self();
      }

      public Builder attachments(VolumeAttachment... in) {
         return attachments(ImmutableSet.copyOf(in));
      }

      /** 
       * @see Volume#getVolumeType()
       */
      public Builder volumeType(String volumeType) {
         this.volumeType = volumeType;
         return self();
      }

      /** 
       * @see Volume#getSnapshotId()
       */
      public Builder snapshotId(String snapshotId) {
         this.snapshotId = snapshotId;
         return self();
      }

      /** 
       * @see Volume#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see Volume#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return self();
      }

      /** 
       * @see Volume#getMetadata()
       */
      public Builder metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));     
         return self();
      }

      /**
       * @see Volume#getTenantId()
       */
      public Builder tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      public Volume build() {
         return new Volume(id, status, size, zone, created, attachments, volumeType, snapshotId, name, description, metadata, tenantId);
      }
      
      public Builder fromVolume(Volume in) {
         return this
                  .id(in.getId())
                  .status(in.getStatus())
                  .size(in.getSize())
                  .zone(in.getZone())
                  .created(in.getCreated())
                  .attachments(in.getAttachments())
                  .volumeType(in.getVolumeType())
                  .snapshotId(in.getSnapshotId())
                  .name(in.getName())
                  .description(in.getDescription())
                  .metadata(in.getMetadata())
                  .tenantId(in.getTenantId());
      }

      protected Builder self() {
         return this;
      }
   }

   /**
    * Creates a dummy Volume when you need a Volume with just the volumeId. 
    * Several fields must be set in the returned Volume:
    * 
    * 1. status=Status.UNRECOGNIZED
    * 2. zone="nova"
    * 3. created=[The Date the method was called]
    */
   public static Volume forId(String volumeId) {
      return builder().id(volumeId).status(Status.UNRECOGNIZED).zone("nova").created(new Date()).build();
   }

   private final String id;
   private final Volume.Status status;
   private final int size;
   @Named("availability_zone")
   private final String zone;
   @Named("created_at")
   private final Date created;
   private final Set<VolumeAttachment> attachments;
   @Named("volume_type")
   private final String volumeType;
   @Named("snapshot_id")
   private final String snapshotId;
   @Named("display_name")
   private final String name;
   @Named("display_description")
   private final String description;
   private final Map<String, String> metadata;
   @Named("os-vol-tenant-attr:tenant_id")
   private final String tenantId;

   @ConstructorProperties({"id", "status", "size", "availability_zone", "created_at", "attachments", "volume_type", "snapshot_id", "display_name", "display_description", "metadata", "os-vol-tenant-attr:tenant_id"})
   protected Volume(String id, Volume.Status status, int size, String zone, Date created, @Nullable Set<VolumeAttachment> attachments, @Nullable String volumeType, @Nullable String snapshotId, @Nullable String name, @Nullable String description, @Nullable Map<String, String> metadata, @Nullable String tenantId) {
      this.id = checkNotNull(id, "id");
      this.status = checkNotNull(status, "status");
      this.size = size;
      this.zone = zone;
      this.created = checkNotNull(created, "created");
      this.attachments = attachments == null ? ImmutableSet.<VolumeAttachment>of() : ImmutableSet.copyOf(attachments);      
      this.volumeType = volumeType;
      this.snapshotId = snapshotId;
      this.name = name;
      this.description = description;
      this.metadata = metadata == null ? ImmutableMap.<String, String>of() : ImmutableMap.copyOf(metadata);
      this.tenantId = tenantId;
   }

   /**
    * @return the id of this volume
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the status of this volume
    */
   public Volume.Status getStatus() {
      return this.status;
   }

   /**
    * @return the size in GB of this volume
    */
   public int getSize() {
      return this.size;
   }

   /**
    * @return the availabilityZone containing this volume
    */
   @Nullable
   public String getZone() {
      return this.zone;
   }

   /**
    * @return the time this volume was created
    */
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the set of attachments (to Servers)
    */
   public Set<VolumeAttachment> getAttachments() {
      return this.attachments;
   }

   /**
    * @return the type of this volume
    */
   @Nullable
   public String getVolumeType() {
      return this.volumeType;
   }

   @Nullable
   public String getSnapshotId() {
      return this.snapshotId;
   }

   /**
    * @return the name of this volume - as displayed in the openstack console
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the description of this volume - as displayed in the openstack console
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   public Map<String, String> getMetadata() {
      return this.metadata;
   }

   /**
    * @return the tenant id of this volume
    */
   @Nullable
   public String getTenantId() {
      return this.tenantId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, status, size, zone, created, attachments, volumeType, snapshotId, name, description, metadata, tenantId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Volume that = Volume.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.status, that.status)
               && Objects.equal(this.size, that.size)
               && Objects.equal(this.zone, that.zone)
               && Objects.equal(this.created, that.created)
               && Objects.equal(this.attachments, that.attachments)
               && Objects.equal(this.volumeType, that.volumeType)
               && Objects.equal(this.snapshotId, that.snapshotId)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.description, that.description)
               && Objects.equal(this.metadata, that.metadata)
               && Objects.equal(this.tenantId, that.tenantId);

   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("status", status).add("size", size).add("zone", zone).add("created", created).add("attachments", attachments).add("volumeType", volumeType).add("snapshotId", snapshotId).add("name", name).add("description", description).add("metadata", metadata).add("extendedAttributes", tenantId);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
