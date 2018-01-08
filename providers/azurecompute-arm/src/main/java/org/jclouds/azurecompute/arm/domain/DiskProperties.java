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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DiskProperties implements Provisionable {

   @Nullable
   public abstract String provisioningState();
   
   @Nullable
   public abstract String timeCreated();
   
   @Nullable
   public abstract String diskState();

   @Nullable
   public abstract Integer diskSizeGB();
   
   @Nullable
   public abstract Integer lun();

   @Nullable
   public abstract VHD vhd();

   public abstract CreationData creationData();
   
   @SerializedNames({"provisioningState", "timeCreated", "diskState", "diskSizeGB", "lun", "vhd", "creationData"})
   public static DiskProperties create(final String provisioningState, final String timeCreated, final String diskState, final Integer diskSizeGB, final Integer lun, final VHD vhd, final CreationData creationData) {
      return builder()
              .provisioningState(provisioningState)
              .timeCreated(timeCreated)
              .diskState(diskState)
              .diskSizeGB(diskSizeGB)
              .lun(lun)
              .vhd(vhd)
              .creationData(creationData)
              .build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_DiskProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder provisioningState(String provisioningState);
      public abstract Builder timeCreated(String timeCreated);
      public abstract Builder diskState(String diskState);
      public abstract Builder diskSizeGB(Integer diskSizeGB);
      public abstract Builder lun(Integer lun);
      public abstract Builder vhd(VHD vhd);
      public abstract Builder creationData(CreationData creationData);
      public abstract DiskProperties build();

   }
}
