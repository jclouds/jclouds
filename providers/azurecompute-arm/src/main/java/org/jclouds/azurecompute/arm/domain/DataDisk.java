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

import com.google.auto.value.AutoValue;

import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class DataDisk {

   /**
    * The name of the data disk
    */
   public abstract String name();

   /**
    * The size of the data disk
    */
   public abstract String diskSizeGB();

   /**
    * The lun value of the data disk
    */
   public abstract int lun();

   /**
    * The vhd of the data disk
    */
   public abstract VHD vhd();

   /**
    * The create option of the data disk
    */
   public abstract String createOption();

   @SerializedNames({"name", "diskSizeGB", "lun", "vhd", "createOption"})
   public static DataDisk create(final String name, final String diskSizeGB, final int lun,
                                 final VHD vhd, final String createOption) {
      return builder()
              .name(name)
              .diskSizeGB(diskSizeGB)
              .lun(lun)
              .createOption(createOption)
              .vhd(vhd)
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

      public abstract Builder createOption(String createOption);

      public abstract Builder lun(int lun);

      public abstract Builder vhd(VHD vhd);

      public abstract DataDisk build();
   }
}
