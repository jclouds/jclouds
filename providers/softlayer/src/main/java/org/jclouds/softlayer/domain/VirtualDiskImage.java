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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Class VirtualDiskImage
 *
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Disk_Image"/>
 */
public class VirtualDiskImage {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVirtualDiskImage(this);
   }

   public static class Builder {

      protected int id;
      protected String uuid;
      protected float capacity;
      protected String units;
      protected int typeId;
      protected String description;
      protected String name;
      protected int storageRepositoryId;
      protected ImmutableSet.Builder<VirtualDiskImageSoftware> softwareReferences = ImmutableSet.builder();

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getUuid()
       */
      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getCapacity()
       */
      public Builder capacity(float capacity) {
         this.capacity = capacity;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getUnits()
       */
      public Builder units(String units) {
         this.units = units;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getBuilderypeId()
       */
      public Builder typeId(int typeId) {
         this.typeId = typeId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getStorageRepositoryId()
       */
      public Builder storageRepositoryId(int storageRepositoryId) {
         this.storageRepositoryId = storageRepositoryId;
         return this;
      }

      public Builder softwareReferences(Set<VirtualDiskImageSoftware> softwareReferences) {
         this.softwareReferences.addAll(checkNotNull(softwareReferences, "softwareReferences"));
         return this;
      }

      public Builder softwareReferences(VirtualDiskImageSoftware... in) {
         return softwareReferences(ImmutableSet.copyOf(in));
      }

      public VirtualDiskImage build() {
         return new VirtualDiskImage(id, uuid, capacity, units, typeId, description, name,
                 storageRepositoryId, softwareReferences.build());
      }

      public Builder fromVirtualDiskImage(VirtualDiskImage in) {
         return this
                 .id(in.getId())
                 .uuid(in.getUuid())
                 .capacity(in.getCapacity())
                 .units(in.getUnits())
                 .typeId(in.getBuilderypeId())
                 .description(in.getDescription())
                 .name(in.getName())
                 .storageRepositoryId(in.getStorageRepositoryId())
                 .softwareReferences(in.getSoftwareReferences());
      }
   }

   private final int id;
   private final String uuid;
   private final float capacity;
   private final String units;
   private final int typeId;
   private final String description;
   private final String name;
   private final int storageRepositoryId;
   private final Set<VirtualDiskImageSoftware> softwareReferences;

   @ConstructorProperties({
           "id", "uuid", "capacity", "units", "typeId", "description", "name", "storageRepositoryId", "softwareReferences"
   })
   public VirtualDiskImage(int id, @Nullable String uuid, float capacity, @Nullable String units, int typeId,
                           @Nullable String description, @Nullable String name, int storageRepositoryId,
                           @Nullable Set<VirtualDiskImageSoftware> softwareReferences) {
      this.id = id;
      this.uuid = uuid;
      this.capacity = capacity;
      this.units = units;
      this.typeId = typeId;
      this.description = description;
      this.name = name;
      this.storageRepositoryId = storageRepositoryId;
      this.softwareReferences = softwareReferences == null ? ImmutableSet.<VirtualDiskImageSoftware>of() :
              ImmutableSet.copyOf(softwareReferences);
   }

   public int getId() {
      return id;
   }

   public String getUuid() {
      return uuid;
   }

   public float getCapacity() {
      return capacity;
   }

   public String getUnits() {
      return units;
   }

   public int getBuilderypeId() {
      return typeId;
   }

   public String getDescription() {
      return description;
   }

   public String getName() {
      return name;
   }

   public int getStorageRepositoryId() {
      return storageRepositoryId;
   }

   public Set<VirtualDiskImageSoftware> getSoftwareReferences() {
      return softwareReferences;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualDiskImage that = (VirtualDiskImage) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.uuid, that.uuid) &&
              Objects.equal(this.capacity, that.capacity) &&
              Objects.equal(this.units, that.units) &&
              Objects.equal(this.typeId, that.typeId) &&
              Objects.equal(this.description, that.description) &&
              Objects.equal(this.name, that.name) &&
              Objects.equal(this.storageRepositoryId, that.storageRepositoryId) &&
              Objects.equal(this.softwareReferences, that.softwareReferences);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, uuid, capacity, units, typeId, description,
              name, storageRepositoryId, softwareReferences);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("uuid", uuid)
              .add("capacity", capacity)
              .add("units", units)
              .add("typeId", typeId)
              .add("description", description)
              .add("name", name)
              .add("storageRepositoryId", storageRepositoryId)
              .add("softwareReferences", softwareReferences)
              .toString();
   }
}
