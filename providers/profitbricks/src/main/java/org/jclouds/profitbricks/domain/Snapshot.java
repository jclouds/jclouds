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

import com.google.auto.value.AutoValue;

import org.jclouds.javax.annotation.Nullable;

import java.util.Date;

import org.jclouds.profitbricks.domain.internal.HotPluggable;
import org.jclouds.profitbricks.domain.internal.Provisionable;

@AutoValue
public abstract class Snapshot implements Provisionable {

   @Nullable
   @Override
   public abstract String id();

   @Nullable
   @Override
   public abstract String name();

   public abstract boolean bootable();

   @Nullable
   public abstract String description();

   @Nullable
   @Override
   public abstract OsType osType();

   @Nullable
   public abstract Date creationTime();

   @Nullable
   public abstract Date lastModificationTime();

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   @Override
   public abstract Location location();

   public static Snapshot create(String id, String name, float size, boolean bootable, String description, OsType osType,
           Boolean cpuHotPlug, Boolean cpuHotUnPlug, Boolean discVirtioHotPlug, Boolean discVirtioHotUnPlug,
           Boolean ramHotPlug, Boolean ramHotUnPlug, Boolean nicHotPlug, Boolean nicHotUnPlug, Date creationTime,
           Date lastModificationTime, ProvisioningState state, Location location) {
      return new AutoValue_Snapshot(cpuHotPlug, cpuHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug, ramHotPlug,
              ramHotUnPlug, nicHotPlug, nicHotUnPlug, size, id, name, bootable, description, osType, creationTime,
              lastModificationTime, state, location);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends Provisionable.Builder<Builder, Snapshot> {

      private Date creationTime;
      private Date lastModificationTime;
      private ProvisioningState state;
      private boolean bootable;
      private String description;

      public Builder creationTime(Date creationTime) {
         this.creationTime = creationTime;
         return this;
      }

      public Builder lastModificationTime(Date lastModificationTime) {
         this.lastModificationTime = lastModificationTime;
         return this;
      }

      public Builder state(ProvisioningState state) {
         this.state = state;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder isBootable(boolean bootable) {
         this.bootable = bootable;
         return this;
      }

      private Builder fromSnapshot(Snapshot in) {
         return this.id(in.id()).name(in.name()).size(in.size()).creationTime(in.creationTime())
                 .lastModificationTime(in.lastModificationTime()).state(in.state()).isBootable(in.bootable())
                 .description(in.description()).isCpuHotPlug(in.isCpuHotPlug()).isCpuHotUnPlug(in.isCpuHotUnPlug())
                 .isDiscVirtioHotPlug(in.isDiscVirtioHotPlug()).isDiscVirtioHotUnPlug(in.isDiscVirtioHotUnPlug())
                 .isRamHotPlug(in.isRamHotPlug()).isRamHotUnPlug(in.isRamHotUnPlug())
                 .isNicHotPlug(in.isNicHotPlug()).isNicHotUnPlug(in.isNicHotUnPlug());
      }

      @Override
      public Snapshot build() {
         return Snapshot.create(id, name, size, bootable, description, osType, cpuHotPlug, cpuHotUnPlug,
                 discVirtioHotPlug, discVirtioHotUnPlug, ramHotPlug, ramHotUnPlug, nicHotPlug, nicHotUnPlug,
                 creationTime, lastModificationTime, state, location);
      }

      @Override
      public Builder self() {
         return this;
      }
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new UpdatePayload.Builder();
      }

      public static RollbackPayload.Builder rollbackBuilder() {
         return new RollbackPayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String storageId();

         public abstract String description();

         public abstract String name();

         public static CreatePayload create(String storageId, String description, String name) {
            return new AutoValue_Snapshot_Request_CreatePayload(storageId, description, name);
         }

         public static class Builder {

            private String storageId;
            private String description;
            private String name;

            public Builder storageId(String storageId) {
               this.storageId = storageId;
               return this;
            }

            public Builder description(String description) {
               this.description = description;
               return this;
            }

            public Builder name(String name) {
               this.name = name;
               return this;
            }

            public CreatePayload build() {
               return CreatePayload.create(storageId, description, name);
            }
         }
      }

      @AutoValue
      public abstract static class UpdatePayload implements HotPluggable {

         public abstract String snapshotId();

         public abstract String description();

         public abstract String name();

         public abstract boolean bootable();

         @Nullable
         public abstract OsType osType();

         public static UpdatePayload create(String snapshotId, String description, String name, boolean bootable,
                 OsType osType, Boolean cpuHotplug, Boolean cpuHotunplug, Boolean ramHotplug, Boolean ramHotunplug,
                 Boolean nicHotplug, Boolean nicHotunplug, Boolean discVirtioHotplug, Boolean discVirtioHotunplug) {
            return new AutoValue_Snapshot_Request_UpdatePayload(
                    cpuHotplug, cpuHotunplug, ramHotplug, ramHotunplug, nicHotplug, nicHotunplug, discVirtioHotplug,
                    discVirtioHotunplug, snapshotId, description, name, bootable, osType);
         }

         public static class Builder extends HotPluggable.Builder<Builder, UpdatePayload> {

            private String snapshotId;

            @Nullable
            private String description;

            @Nullable
            private String name;

            private boolean bootable;

            private OsType osType;

            public Builder snapshotId(String snapshotId) {
               this.snapshotId = snapshotId;
               return this;
            }

            public Builder description(String description) {
               this.description = description;
               return this;
            }

            public Builder name(String name) {
               this.name = name;
               return this;
            }

            public Builder bootable(boolean bootable) {
               this.bootable = bootable;
               return this;
            }

            public Builder osType(OsType osType) {
               this.osType = osType;
               return this;
            }

            @Override
            public UpdatePayload build() {
               return UpdatePayload.create(snapshotId, description, name, bootable, osType, cpuHotPlug, cpuHotUnPlug,
                       ramHotPlug, ramHotUnPlug, nicHotUnPlug, nicHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug);
            }

            @Override
            public Builder self() {
               return this;
            }
         }

      }

      @AutoValue
      public abstract static class RollbackPayload {

         public abstract String snapshotId();

         public abstract String storageId();

         public static RollbackPayload create(String snapshotId, String storageId) {
            return new AutoValue_Snapshot_Request_RollbackPayload(snapshotId, storageId);
         }

         public static class Builder {

            private String snapshotId;

            private String storageId;

            public Builder snapshotId(String snapshotId) {
               this.snapshotId = snapshotId;
               return this;
            }

            public Builder storageId(String storageId) {
               this.storageId = storageId;
               return this;
            }

            public RollbackPayload build() {
               return RollbackPayload.create(snapshotId, storageId);
            }
         }
      }
   }
}
