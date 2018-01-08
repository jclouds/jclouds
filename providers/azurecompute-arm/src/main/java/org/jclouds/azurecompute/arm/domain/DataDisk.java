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
package org.jclouds.azurecompute.arm.domain;

import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.CaseFormat;

@AutoValue
public abstract class DataDisk implements Provisionable {

   public enum DiskCreateOptionTypes {
      FROM_IMAGE,
      EMPTY,
      ATTACH,
      UNRECOGNIZED;

      public static DiskCreateOptionTypes fromValue(final String text) {
         return (DiskCreateOptionTypes) GetEnumValue.fromValueOrDefault(text, UNRECOGNIZED);
      }

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }
   }
   
   public enum CachingTypes {
      NONE,
      READ_ONLY,
      READ_WRITE,
      UNRECOGNIZED;

      public static CachingTypes fromValue(final String text) {
         for (CachingTypes type : CachingTypes.values()) {
            if (type.toString().equals(text)) {
               return type;
            }
         }
         return UNRECOGNIZED;
      }

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }
   }

   /**
    * The name of the data disk
    */
   @Nullable public abstract String name();

   /**
    * The size of the data disk
    */
   @Nullable public abstract String diskSizeGB();

   /**
    * The lun value of the data disk
    */
   @Nullable public abstract Integer lun();

   /**
    * The vhd of the data disk
    */
   @Nullable public abstract VHD vhd();

   /**
    * The source user image virtual hard disk. This virtual hard disk will be
    * copied before using it to attach to the virtual machine. If SourceImage
    * is provided, the destination virtual hard disk must not exist.
    */
   @Nullable public abstract VHD image();

   /**
    * The create option of the data disk
    */
   public abstract DiskCreateOptionTypes createOption();
   
   /**
    * The caching type. Possible values include: 'None', 'ReadOnly',
    * 'ReadWrite'.
    */
   @Nullable public abstract CachingTypes caching();

   /**
    * The managed disk parameters.
    */
   @Nullable public abstract ManagedDiskParameters managedDiskParameters();

   @Nullable
   public abstract String provisioningState();

   @SerializedNames({"name", "diskSizeGB", "lun", "vhd", "image", "createOption", "caching", "managedDisk", "provisioningState"})
   public static DataDisk create(final String name, final String diskSizeGB, final Integer lun,
                                 final VHD vhd, final VHD image, final String createOption, final String caching, 
                                 final ManagedDiskParameters managedDiskParamenters, final String provisioningState) {
      final Builder builder = builder();
      if (caching != null) {
         builder.caching(CachingTypes.fromValue(caching));
      }
      return builder.name(name)
              .diskSizeGB(diskSizeGB)
              .lun(lun)
              .vhd(vhd)
              .image(image)
              .createOption(DiskCreateOptionTypes.fromValue(createOption))
              .managedDiskParameters(managedDiskParamenters)
              .provisioningState(provisioningState)
              .build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_DataDisk.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      
      public abstract Builder name(String name);

      public abstract Builder diskSizeGB(String diskSizeGB);

      public abstract Builder createOption(DiskCreateOptionTypes createOption);

      public abstract Builder lun(Integer lun);

      public abstract Builder vhd(VHD vhd);

      public abstract Builder image(VHD image);

      public abstract Builder caching(CachingTypes caching);

      public abstract Builder managedDiskParameters(ManagedDiskParameters managedDiskParameters);
      
      public abstract Builder provisioningState(String provisioningState);

      public abstract DataDisk build();
   }
}
