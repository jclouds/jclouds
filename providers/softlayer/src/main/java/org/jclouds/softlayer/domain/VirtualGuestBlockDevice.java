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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * Class VirtualGuestBlockDevice
 *
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Block_Device"/>
 */
public class VirtualGuestBlockDevice {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVirtualGuestBlockDevice(this);
   }

   public static class Builder {

      protected int id;
      protected String uuid;
      protected int statusId;
      protected String mountType;
      protected String mountMode;
      protected int bootableFlag;
      protected String device;
      protected VirtualDiskImage diskImage;
      protected VirtualGuest guest;

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getUuid()
       */
      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getStatusId()
       */
      public Builder statusId(int statusId) {
         this.statusId = statusId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getMountType()
       */
      public Builder mountType(String mountType) {
         this.mountType = mountType;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getMountMode()
       */
      public Builder mountMode(String mountMode) {
         this.mountMode = mountMode;
         return this;
      }

      /**
       * @see VirtualGuestBlockDevice#getBootableFlag()
       */
      public Builder bootableFlag(int bootableFlag) {
         this.bootableFlag = bootableFlag;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getDevice()
       */
      public Builder device(String device) {
         this.device = device;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getVirtualDiskImage()
       */
      public Builder diskImage(VirtualDiskImage diskImage) {
         this.diskImage = diskImage;
         return this;
      }

      public Builder guest(VirtualGuest guest) {
         this.guest = guest;
         return this;
      }

      public VirtualGuestBlockDevice build() {
         return new VirtualGuestBlockDevice(id, uuid, statusId, mountType, mountMode, bootableFlag, device,
                 diskImage, guest);
      }

      public Builder fromVirtualGuestBlockDevice(VirtualGuestBlockDevice in) {
         return this
               .id(in.getId())
               .uuid(in.getUuid())
               .statusId(in.getStatusId())
               .mountMode(in.getMountMode())
               .mountType(in.getMountType())
               .bootableFlag(in.getBootableFlag())
               .device(in.getDevice())
               .diskImage(in.getVirtualDiskImage())
               .guest(in.getVirtualGuest());
      }
   }

   private final int id;
   private final String uuid;
   private final int statusId;
   private final String mountType;
   private final String mountMode;
   private final int bootableFlag;
   private final String device;
   private final VirtualDiskImage diskImage;
   private final VirtualGuest guest;

   @ConstructorProperties({ "id", "uuid", "statusId", "mountType", "mountMode", "bootableFlag", "device",
           "diskImage", "guest" })
   protected VirtualGuestBlockDevice(int id, @Nullable String uuid, int statusId,  @Nullable String mountType,
                                     @Nullable String mountMode, int bootableFlag, String device,
                                     @Nullable VirtualDiskImage diskImage, @Nullable VirtualGuest guest) {
      this.id = id;
      this.uuid = uuid;
      this.statusId = statusId;
      this.mountType = mountType;
      this.mountMode = mountMode;
      this.bootableFlag = bootableFlag;
      this.device = checkNotNull(device, "device");
      this.diskImage = diskImage;
      this.guest = guest;
   }

   public int getId() {
      return id;
   }

   public String getUuid() {
      return uuid;
   }

   public int getStatusId() {
      return statusId;
   }

   public String getMountType() {
      return mountType;
   }

   public String getMountMode() {
      return mountMode;
   }

   public int getBootableFlag() {
      return bootableFlag;
   }

   public String getDevice() {
      return device;
   }

   public VirtualDiskImage getVirtualDiskImage() {
      return diskImage;
   }

   public VirtualGuest getVirtualGuest() {
      return guest;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualGuestBlockDevice that = (VirtualGuestBlockDevice) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.uuid, that.uuid) &&
              Objects.equal(this.statusId, that.statusId) &&
              Objects.equal(this.mountType, that.mountType) &&
              Objects.equal(this.mountMode, that.mountMode) &&
              Objects.equal(this.bootableFlag, that.bootableFlag) &&
              Objects.equal(this.device, that.device) &&
              Objects.equal(this.diskImage, that.diskImage) &&
              Objects.equal(this.guest, that.guest);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, uuid, statusId, mountType, mountMode, bootableFlag,
              device, diskImage, guest);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("uuid", uuid)
              .add("statusId", statusId)
              .add("mountType", mountType)
              .add("mountMode", mountMode)
              .add("bootableFlag", bootableFlag)
              .add("device", device)
              .add("diskImage", diskImage)
              .add("guest", guest)
              .toString();
   }
}
