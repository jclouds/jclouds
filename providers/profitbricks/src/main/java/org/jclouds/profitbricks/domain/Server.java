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

import static org.jclouds.profitbricks.util.Preconditions.checkCores;
import static org.jclouds.profitbricks.util.Preconditions.checkRam;

import java.util.Date;
import java.util.List;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.collect.ImmutableList;

import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class Server {

   public enum Status {

      NOSTATE, RUNNING, BLOCKED, PAUSED, SHUTDOWN, SHUTOFF, CRASHED, UNRECOGNIZED;

      public String value() {
         return name();
      }

      public static Status fromValue(String v) {
         return Enums.getIfPresent(Status.class, v).or(UNRECOGNIZED);
      }
   }

   @Nullable
   public abstract DataCenter dataCenter();

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String name();

   @Nullable
   public abstract Integer cores();

   @Nullable
   public abstract Integer ram();

   @Nullable
   public abstract Boolean hasInternetAccess();

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   public abstract Status status();

   @Nullable
   public abstract OsType osType();

   @Nullable
   public abstract AvailabilityZone availabilityZone();

   @Nullable
   public abstract Date creationTime();

   @Nullable
   public abstract Date lastModificationTime();

   @Nullable
   public abstract List<Storage> storages();

   @Nullable
   public abstract List<Nic> nics();

   @Nullable
   public abstract String balancedNicId();

   @Nullable
   public abstract Boolean loadBalanced();

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

   @Nullable
   public abstract String hostname(); // Non-profitbricks property; Added to hold hostname parsed from image temporarily

   public static Builder builder() {
      return new AutoValue_Server.Builder()
              .storages(ImmutableList.<Storage>of())
              .nics(ImmutableList.<Nic>of());
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder dataCenter(DataCenter dataCenter);

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder cores(Integer cores);

      public abstract Builder ram(Integer ram);

      public abstract Builder hasInternetAccess(Boolean internetAccess);

      public abstract Builder state(ProvisioningState state);

      public abstract Builder status(Status status);

      public abstract Builder osType(OsType osType);

      public abstract Builder availabilityZone(AvailabilityZone availabilityZone);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder lastModificationTime(Date lastModificationTime);

      public abstract Builder storages(List<Storage> storages);

      public abstract Builder nics(List<Nic> nics);

      public abstract Builder balancedNicId(String balancedNicIds);

      public abstract Builder loadBalanced(Boolean isLoadBalanced);

      public abstract Builder isCpuHotPlug(Boolean isCpuHotPlug);

      public abstract Builder isCpuHotUnPlug(Boolean isCpuHotUnPlug);

      public abstract Builder isRamHotPlug(Boolean isRamHotPlug);

      public abstract Builder isRamHotUnPlug(Boolean isRamHotUnPlug);

      public abstract Builder isNicHotPlug(Boolean isNicHotPlug);

      public abstract Builder isNicHotUnPlug(Boolean isNicHotUnPlug);

      public abstract Builder isDiscVirtioHotPlug(Boolean isDiscVirtioHotPlug);

      public abstract Builder isDiscVirtioHotUnPlug(Boolean isDiscVirtioHotUnPlug);

      public abstract Builder hostname(String hostname);

      abstract Server autoBuild();

      public Server build() {
         Server server = autoBuild();
         if (server.cores() != null)
            checkCores(server.cores());
         if (server.ram() != null)
            checkRam(server.ram(), server.isRamHotUnPlug());
         return server.toBuilder()
                 .storages(ImmutableList.copyOf(server.storages()))
                 .nics(ImmutableList.copyOf(server.nics()))
                 .autoBuild();
      }
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_Server_Request_CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_Server_Request_UpdatePayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract int cores();

         public abstract int ram();

         public abstract String dataCenterId();

         public abstract String name();

         @Nullable
         public abstract String bootFromStorageId();

         @Nullable
         public abstract String bootFromImageId();

         @Nullable
         public abstract Integer lanId();

         @Nullable
         public abstract Boolean hasInternetAccess();

         @Nullable
         public abstract AvailabilityZone availabilityZone();

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

            public abstract Builder cores(int cores);

            public abstract Builder ram(int ram);

            public abstract Builder dataCenterId(String dataCenterId);

            public abstract Builder name(String name);

            public abstract Builder bootFromStorageId(String bootFromStorageId);

            public abstract Builder bootFromImageId(String bootFromImageId);

            public abstract Builder lanId(Integer lanId);

            public abstract Builder hasInternetAccess(Boolean hasInternetAccess);

            public abstract Builder availabilityZone(AvailabilityZone availabilityZone);

            public abstract Builder osType(OsType osType);

            public abstract Builder isCpuHotPlug(Boolean isCpuHotPlug);

            public abstract Builder isCpuHotUnPlug(Boolean isCpuHotUnPlug);

            public abstract Builder isRamHotPlug(Boolean isRamHotPlug);

            public abstract Builder isRamHotUnPlug(Boolean isRamHotUnPlug);

            public abstract Builder isNicHotPlug(Boolean isNicHotPlug);

            public abstract Builder isNicHotUnPlug(Boolean isNicHotUnPlug);

            public abstract Builder isDiscVirtioHotPlug(Boolean isDiscVirtioHotPlug);

            public abstract Builder isDiscVirtioHotUnPlug(Boolean isDiscVirtioHotUnPlug);

            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               CreatePayload payload = autoBuild();
               checkCores(payload.cores());
               checkRam(payload.ram(), payload.isRamHotUnPlug());
               return payload;
            }
         }

      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();

         @Nullable
         public abstract Integer cores();

         @Nullable
         public abstract Integer ram();

         @Nullable
         public abstract String name();

         @Nullable
         public abstract String bootFromStorageId();

         @Nullable
         public abstract String bootFromImageId();

         @Nullable
         public abstract AvailabilityZone availabilityZone();

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

            public abstract Builder id(String id);

            public abstract Builder cores(Integer cores);

            public abstract Builder ram(Integer ram);

            public abstract Builder name(String name);

            public abstract Builder bootFromStorageId(String bootFromStorageId);

            public abstract Builder bootFromImageId(String bootFromImageId);

            public abstract Builder availabilityZone(AvailabilityZone availabilityZone);

            public abstract Builder osType(OsType osType);

            public abstract Builder isCpuHotPlug(Boolean isCpuHotPlug);

            public abstract Builder isCpuHotUnPlug(Boolean isCpuHotUnPlug);

            public abstract Builder isRamHotPlug(Boolean isRamHotPlug);

            public abstract Builder isRamHotUnPlug(Boolean isRamHotUnPlug);

            public abstract Builder isNicHotPlug(Boolean isNicHotPlug);

            public abstract Builder isNicHotUnPlug(Boolean isNicHotUnPlug);

            public abstract Builder isDiscVirtioHotPlug(Boolean isDiscVirtioHotPlug);

            public abstract Builder isDiscVirtioHotUnPlug(Boolean isDiscVirtioHotUnPlug);

            abstract UpdatePayload autoBuild();

            public UpdatePayload build() {
               UpdatePayload payload = autoBuild();
               if (payload.cores() != null)
                  checkCores(payload.cores());
               if (payload.ram() != null)
                  checkRam(payload.ram(), payload.isRamHotUnPlug());
               return payload;
            }
         }
      }
   }
}
