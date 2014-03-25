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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;
import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * A representation of a block device that should be attached to the Nova instance to be launched
 *
 */
public class BlockDeviceMapping {

      @Named("delete_on_termination")
      String deleteOnTermination = "0";
      @Named("device_name")
      String deviceName = null;
      @Named("volume_id")
      String volumeId = null;
      @Named("volume_size")
      String volumeSize = "";

   @ConstructorProperties({"volume_id", "volume_size", "device_name", "delete_on_termination"})
   private BlockDeviceMapping(String volumeId, String volumeSize, String deviceName, String deleteOnTermination) {
      checkNotNull(volumeId);
      checkNotNull(deviceName);
      this.volumeId = volumeId;
      this.volumeSize = volumeSize;
      this.deviceName = deviceName;
      if (deleteOnTermination != null) {
         this.deleteOnTermination = deleteOnTermination;
      }
   }

   /**
    * Default constructor.
    */
   private BlockDeviceMapping() {}

   /**
    * Copy constructor
    * @param blockDeviceMapping
    */
   private BlockDeviceMapping(BlockDeviceMapping blockDeviceMapping) {
      this(blockDeviceMapping.volumeId,
           blockDeviceMapping.volumeSize,
           blockDeviceMapping.deviceName,
           blockDeviceMapping.deleteOnTermination);
   }

   /**
    * @return the volume id of the block device
    */
   @Nullable
   public String getVolumeId() {
      return volumeId;
   }

   /**
    * @return the size of the block device
    */
   @Nullable
   public String getVolumeSize() {
      return volumeSize;
   }

   /**
    * @return the device name to which the volume is attached
    */
   @Nullable
   public String getDeviceName() {
      return deviceName;
   }

   /**
    * @return whether the volume should be deleted on terminating the instance
    */
   public String getDeleteOnTermination() {
      return deviceName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(volumeId, volumeSize, deviceName, deleteOnTermination);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      BlockDeviceMapping that = BlockDeviceMapping.class.cast(obj);
      return Objects.equal(this.volumeId, that.volumeId)
            && Objects.equal(this.volumeSize, that.volumeSize)
            && Objects.equal(this.deviceName, that.deviceName)
            && Objects.equal(this.deleteOnTermination, that.deleteOnTermination);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
            .add("volumeId", volumeId)
            .add("volumeSize", volumeSize)
            .add("deviceName", deviceName)
            .add("deleteOnTermination", deleteOnTermination)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow
    */

   /**
    * @return the Builder for creating a new block device mapping
    */
   public static CreateBuilder createOptions(String volumeId, String deviceName) {
      return new CreateBuilder(volumeId, deviceName);
   }

   /**
    * @return the Builder for updating a block device mapping
    */
   public static UpdateBuilder updateOptions() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected BlockDeviceMapping blockDeviceMapping;

      /**
       * No-parameters constructor used when updating.
       * */
      private Builder() {
         blockDeviceMapping = new BlockDeviceMapping();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provide the volume id to the BlockDeviceMapping's Builder.
       *
       * @return the Builder.
       * @see BlockDeviceMapping#getVolumeId()
       */
      public ParameterizedBuilderType volumeId(String volumeId) {
         blockDeviceMapping.volumeId = volumeId;
         return self();
      }

      /**
       * Provide the volume size in GB to the BlockDeviceMapping's Builder.
       *
       * @return the Builder.
       * @see BlockDeviceMapping#getVolumeSize()
       */
      public ParameterizedBuilderType volumeSize(int volumeSize) {
         blockDeviceMapping.volumeSize = Integer.toString(volumeSize);
         return self();
      }

      /**
       * Provide the deviceName to the BlockDeviceMapping's Builder.
       *
       * @return the Builder.
       * @see BlockDeviceMapping#getDeviceName()
       */
      public ParameterizedBuilderType deviceName(String deviceName) {
         blockDeviceMapping.deviceName = deviceName;
         return self();
      }

      /**
       * Provide an option indicated to delete the volume on instance deletion to BlockDeviceMapping's Builder.
       *
       * @return the Builder.
       * @see BlockDeviceMapping#getVolumeSize()
       */
      public ParameterizedBuilderType deleteOnTermination(boolean deleteOnTermination) {
         blockDeviceMapping.deleteOnTermination = deleteOnTermination ? "1" : "0";
         return self();
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a Builder
       */
      private CreateBuilder(String volumeId, String deviceName) {
          blockDeviceMapping.volumeId = volumeId;
          blockDeviceMapping.deviceName = deviceName;
      }

      /**
       * @return a CreateOptions constructed with this Builder.
       */
      public CreateOptions build() {
         return new CreateOptions(blockDeviceMapping);
      }

      protected CreateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class UpdateBuilder extends Builder<UpdateBuilder> {
      /**
       * Supply required properties for updating a Builder
       */
      private UpdateBuilder() {
      }

      /**
       * @return a UpdateOptions constructed with this Builder.
       */
      public UpdateOptions build() {
         return new UpdateOptions(blockDeviceMapping);
      }

      protected UpdateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreateOptions extends BlockDeviceMapping {
      /**
       * Copy constructor
       */
      private CreateOptions(BlockDeviceMapping blockDeviceMapping) {
         super(blockDeviceMapping);
         checkNotNull(blockDeviceMapping.volumeId, "volume id should not be null");
         checkNotNull(blockDeviceMapping.deviceName, "device name should not be null");
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdateOptions extends BlockDeviceMapping {
      /**
       * Copy constructor
       */
      private UpdateOptions(BlockDeviceMapping blockDeviceMapping) {
         super(blockDeviceMapping);
      }
   }
}
