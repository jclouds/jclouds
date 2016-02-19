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
package org.jclouds.profitbricks.domain;

import java.util.Date;

import com.google.auto.value.AutoValue;

import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class Snapshot implements Provisionable {

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String name();

   @Nullable
   public abstract Float size();

   @Nullable
   public abstract Location location();

   @Nullable
   public abstract OsType osType();

   @Nullable
   public abstract Boolean isBootable();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract Date creationTime();

   @Nullable
   public abstract Date lastModificationTime();

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   public abstract Boolean isCpuHotPlug();

   @Nullable
   public abstract Boolean isCpuHotUnPlug();

   @Nullable
   public abstract Boolean isRamHotPlug();

   @Nullable
   public abstract Boolean isRamHotUnPlug();

   @Nullable
   public abstract Boolean isNicHotPlug();

   @Nullable
   public abstract Boolean isNicHotUnPlug();

   @Nullable
   public abstract Boolean isDiscVirtioHotPlug();

   @Nullable
   public abstract Boolean isDiscVirtioHotUnPlug();

   public static Builder builder() {
      return new AutoValue_Snapshot.Builder();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder size(Float size);

      public abstract Builder location(Location location);

      public abstract Builder osType(OsType osType);

      public abstract Builder isBootable(Boolean bootable);

      public abstract Builder description(String description);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder lastModificationTime(Date lastModificationTime);

      public abstract Builder state(ProvisioningState state);

      public abstract Builder isCpuHotPlug(Boolean isCpuHotPlug);

      public abstract Builder isCpuHotUnPlug(Boolean isCpuHotUnPlug);

      public abstract Builder isRamHotPlug(Boolean isRamHotPlug);

      public abstract Builder isRamHotUnPlug(Boolean isRamHotUnPlug);

      public abstract Builder isNicHotPlug(Boolean isNicHotPlug);

      public abstract Builder isNicHotUnPlug(Boolean isNicHotUnPlug);

      public abstract Builder isDiscVirtioHotPlug(Boolean isDiscVirtioHotPlug);

      public abstract Builder isDiscVirtioHotUnPlug(Boolean isDiscVirtioHotUnPlug);

      public abstract Snapshot build();
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_Snapshot_Request_CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_Snapshot_Request_UpdatePayload.Builder();
      }

      public static RollbackPayload createRollbackPayload(String snapshotId, String storageId) {
         return new AutoValue_Snapshot_Request_RollbackPayload(snapshotId, storageId);
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String storageId();

         public abstract String name();

         @Nullable
         public abstract String description();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder storageId(String storageId);

            public abstract Builder name(String name);

            public abstract Builder description(String description);

            public abstract CreatePayload build();
         }
      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();

         @Nullable
         public abstract String description();

         @Nullable
         public abstract String name();

         @Nullable
         public abstract Boolean bootable();

         @Nullable
         public abstract OsType osType();

         @Nullable
         public abstract Boolean isCpuHotPlug();

         @Nullable
         public abstract Boolean isCpuHotUnPlug();

         @Nullable
         public abstract Boolean isRamHotPlug();

         @Nullable
         public abstract Boolean isRamHotUnPlug();

         @Nullable
         public abstract Boolean isNicHotPlug();

         @Nullable
         public abstract Boolean isNicHotUnPlug();

         @Nullable
         public abstract Boolean isDiscVirtioHotPlug();

         @Nullable
         public abstract Boolean isDiscVirtioHotUnPlug();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder id(String snapshotId);

            public abstract Builder description(String description);

            public abstract Builder name(String name);

            public abstract Builder bootable(Boolean bootable);

            public abstract Builder osType(OsType osType);

            public abstract Builder isCpuHotPlug(Boolean isCpuHotPlug);

            public abstract Builder isCpuHotUnPlug(Boolean isCpuHotUnPlug);

            public abstract Builder isRamHotPlug(Boolean isRamHotPlug);

            public abstract Builder isRamHotUnPlug(Boolean isRamHotUnPlug);

            public abstract Builder isNicHotPlug(Boolean isNicHotPlug);

            public abstract Builder isNicHotUnPlug(Boolean isNicHotUnPlug);

            public abstract Builder isDiscVirtioHotPlug(Boolean isDiscVirtioHotPlug);

            public abstract Builder isDiscVirtioHotUnPlug(Boolean isDiscVirtioHotUnPlug);

            public abstract UpdatePayload build();
         }

      }

      @AutoValue
      public abstract static class RollbackPayload {

         public abstract String snapshotId();

         public abstract String storageId();

      }
   }
}
