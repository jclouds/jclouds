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

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A representation of a block device that can be used to boot a Nova instance.
 */
public class BlockDeviceMapping {

   private String uuid;
   @Named("device_name")
   private String deviceName;
   @Named("device_type")
   private String deviceType;
   @Named("volume_size")
   private Integer volumeSize;
   @Named("source_type")
   private String sourceType;
   @Named("destination_type")
   private String destinationType;
   @Named("disk_bus")
   private String diskBus;
   @Named("no_device")
   private Boolean noDevice;
   @Named("guest_format")
   private String guestFormat;
   @Named("boot_index")
   private Integer bootIndex;
   @Named("delete_on_termination")
   private Boolean deleteOnTermination;

   @ConstructorProperties({"uuid", "device_name", "device_type", "volume_size", "source_type", "destination_type",
      "disk_bus", "no_device", "guest_format", "boot_index", "delete_on_termination"})
   protected BlockDeviceMapping(String uuid, String deviceName, String deviceType, Integer volumeSize,
         String sourceType, String destinationType, String diskBus, Boolean noDevice, String guestFormat,
         Integer bootIndex, Boolean deleteOnTermination) {
      this.uuid = uuid;
      this.deviceName = deviceName;
      this.deviceType = deviceType;
      this.volumeSize = volumeSize;
      this.sourceType = sourceType;
      this.destinationType = destinationType;
      this.diskBus = diskBus;
      this.noDevice = noDevice;
      this.guestFormat = guestFormat;
      this.bootIndex = bootIndex;
      this.deleteOnTermination = deleteOnTermination;
   }

   /**
    * @return the uuid of the volume
    */
   @Nullable
   public String getUuid() {
      return uuid;
   }

   /**
    * @return the device name
    */
   @Nullable
   public String getDeviceName() {
      return deviceName;
   }

   /**
    * @return the device type
    */
   @Nullable
   public String getDeviceType() {
      return deviceType;
   }

   /**
    * @return the size of the volume
    */
   @Nullable
   public Integer getVolumeSize() {
      return volumeSize;
   }

   /**
    * @return the source type of the block device
    */
   @Nullable
   public String getSourceType() {
      return sourceType;
   }

   /**
    * @return the destination type of the block device
    */
   @Nullable
   public String getDestinationType() {
      return destinationType;
   }

   /**
    * @return the disk bus of the block device
    */
   @Nullable
   public String getDiskBus() {
      return diskBus;
   }

   /**
    * @return true if there is no block device
    */
   @Nullable
   public Boolean getNoDevice() {
      return noDevice;
   }

   /**
    * @return the guest format of the block device
    */
   @Nullable
   public String getGuestFormat() {
      return guestFormat;
   }

   /**
    * @return the boot index of the block device
    */
   @Nullable
   public Integer getBootIndex() {
      return bootIndex;
   }

   /**
    * @return true if the block device should terminate on deletion
    */
   @Nullable
   public Boolean getDeleteOnTermination() {
      return deleteOnTermination;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(uuid, deviceName, deviceType, volumeSize, sourceType, destinationType, diskBus,
            noDevice, guestFormat, bootIndex, deleteOnTermination);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      BlockDeviceMapping that = BlockDeviceMapping.class.cast(obj);
      return Objects.equal(this.uuid, that.uuid)
            && Objects.equal(this.deviceName, that.deviceName)
            && Objects.equal(this.deviceType, that.deviceType)
            && Objects.equal(this.volumeSize, that.volumeSize)
            && Objects.equal(this.sourceType, that.sourceType)
            && Objects.equal(this.destinationType, that.destinationType)
            && Objects.equal(this.diskBus, that.diskBus)
            && Objects.equal(this.noDevice, that.noDevice)
            && Objects.equal(this.guestFormat, that.guestFormat)
            && Objects.equal(this.bootIndex, that.bootIndex)
            && Objects.equal(this.deleteOnTermination, that.deleteOnTermination);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("uuid", uuid)
            .add("deviceName", deviceName)
            .add("deviceType", deviceType)
            .add("volumeSize", volumeSize)
            .add("sourceType", sourceType)
            .add("destinationType", destinationType)
            .add("diskBus", diskBus)
            .add("noDevice", noDevice)
            .add("guestFormat", guestFormat)
            .add("bootIndex", bootIndex)
            .add("deleteOnTermination", deleteOnTermination);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromBlockDeviceMapping(this);
   }

   public static class Builder {
      protected String uuid;
      protected String deviceName;
      protected String deviceType;
      protected Integer volumeSize;
      protected String sourceType;
      protected String destinationType;
      protected String diskBus;
      protected Boolean noDevice;
      protected String guestFormat;
      protected Integer bootIndex;
      protected Boolean deleteOnTermination;

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder deviceName(String deviceName) {
         this.deviceName = deviceName;
         return this;
      }

      public Builder deviceType(String deviceType) {
         this.deviceType = deviceType;
         return this;
      }

      public Builder volumeSize(Integer volumeSize) {
         this.volumeSize = volumeSize;
         return this;
      }

      public Builder sourceType(String sourceType) {
         this.sourceType = sourceType;
         return this;
      }

      public Builder destinationType(String destinationType) {
         this.destinationType = destinationType;
         return this;
      }

      public Builder diskBus(String diskBus) {
         this.diskBus = diskBus;
         return this;
      }

      public Builder noDevice(Boolean noDevice) {
         this.noDevice = noDevice;
         return this;
      }

      public Builder guestFormat(String guestFormat) {
         this.guestFormat = guestFormat;
         return this;
      }

      public Builder bootIndex(Integer bootIndex) {
         this.bootIndex = bootIndex;
         return this;
      }

      public Builder deleteOnTermination(Boolean deleteOnTermination) {
         this.deleteOnTermination = deleteOnTermination;
         return this;
      }

      public BlockDeviceMapping build() {
         return new BlockDeviceMapping(uuid, deviceName, deviceType, volumeSize, sourceType, destinationType, diskBus,
               noDevice, guestFormat, bootIndex, deleteOnTermination);
      }

      public Builder fromBlockDeviceMapping(BlockDeviceMapping in) {
         return this
               .uuid(in.getUuid())
               .deviceName(in.getDeviceName())
               .deviceType(in.getDeviceType())
               .volumeSize(in.getVolumeSize())
               .sourceType(in.getSourceType())
               .destinationType(in.getDestinationType())
               .diskBus(in.getDiskBus())
               .noDevice(in.getNoDevice())
               .bootIndex(in.getBootIndex())
               .deleteOnTermination(in.getDeleteOnTermination())
               .guestFormat(in.getGuestFormat());
      }
   }

}
