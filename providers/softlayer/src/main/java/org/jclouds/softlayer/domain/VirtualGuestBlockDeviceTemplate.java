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

import com.google.common.base.Objects;

/**
 * Class VirtualGuestBlockDeviceTemplate
 *
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Block_Device_Template"/>
 */
public class VirtualGuestBlockDeviceTemplate {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVirtualGuestBlockDeviceTemplate(this);
   }

   public static class Builder {

      protected int id;
      protected String device;
      protected int diskImageId;
      protected float diskSpace;
      protected int groupId;
      protected String units;
      protected VirtualDiskImage diskImage;

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplate#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see VirtualGuestBlockDeviceTemplate#getDevice()
       */
      public Builder device(String device) {
         this.device = device;
         return this;
      }

      /**
       * @see VirtualGuestBlockDeviceTemplate#getDiskImage()
       */
      public Builder diskImageId(int diskImageId) {
         this.diskImageId = diskImageId;
         return this;
      }

      /**
       * @see VirtualGuestBlockDeviceTemplate#getDiskSpace()
       */
      public Builder diskSpace(float diskSpace) {
         this.diskSpace = diskSpace;
         return this;
      }

      /**
       * @see VirtualGuestBlockDeviceTemplate#getGroupId()
       */
      public Builder groupId(int groupId) {
         this.groupId = groupId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplate#getUnits()
       */
      public Builder units(String units) {
         this.units = units;
         return this;
      }

      /**
       * @see VirtualGuestBlockDeviceTemplate#getDiskImage()
       */
      public Builder diskImage(VirtualDiskImage diskImage) {
         this.diskImage = diskImage;
         return this;
      }

      public VirtualGuestBlockDeviceTemplate build() {
         return new VirtualGuestBlockDeviceTemplate(id, device, diskImageId, diskSpace, groupId, units, diskImage);
      }

      public Builder fromVirtualGuestBlockDeviceTemplate(VirtualGuestBlockDeviceTemplate in) {
         return this
                 .id(in.getId())
                 .device(in.getDevice())
                 .diskImageId(in.getDiskImageId())
                 .diskSpace(in.getDiskSpace())
                 .groupId(in.getGroupId())
                 .units(in.getUnits())
                 .diskImage(in.getDiskImage());
      }
   }

   private final int id;
   private final String device;
   private final int diskImageId;
   private final float diskSpace;
   private final int groupId;
   private final String units;
   private final VirtualDiskImage diskImage;

   @ConstructorProperties({ "id", "device", "diskImageId", "diskSpace", "groupId", "units", "diskImage" })
   protected VirtualGuestBlockDeviceTemplate(int id, String device, int diskImageId, float diskSpace, int groupId,
                                             String units, VirtualDiskImage diskImage) {
      this.id = id;
      this.device = checkNotNull(device, "device");
      this.diskImageId = diskImageId;
      this.diskSpace = diskSpace;
      this.groupId = groupId;
      this.units = units;
      this.diskImage = checkNotNull(diskImage, "diskImage");
   }

   public int getId() {
      return id;
   }

   public String getDevice() {
      return device;
   }

   public int getDiskImageId() {
      return diskImageId;
   }

   public float getDiskSpace() {
      return diskSpace;
   }

   public int getGroupId() {
      return groupId;
   }

   public String getUnits() {
      return units;
   }

   public VirtualDiskImage getDiskImage() {
      return diskImage;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualGuestBlockDeviceTemplate that = (VirtualGuestBlockDeviceTemplate) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.device, that.device) &&
              Objects.equal(this.diskImageId, that.diskImageId) &&
              Objects.equal(this.diskSpace, that.diskSpace) &&
              Objects.equal(this.groupId, that.groupId) &&
              Objects.equal(this.units, that.units) &&
              Objects.equal(this.diskImage, that.diskImage);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, device, diskImageId, diskSpace, groupId, units, diskImage);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("device", device)
              .add("diskImageId", diskImageId)
              .add("diskSpace", diskSpace)
              .add("groupId", groupId)
              .add("units", units)
              .add("diskImage", diskImage)
              .toString();
   }
}
