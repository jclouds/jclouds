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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@AutoValue
public abstract class Storage {

   public enum BusType {

      IDE, SCSI, VIRTIO, UNRECOGNIZED;

      public static BusType fromValue(String value) {
         try {
            return valueOf(value);
         } catch (IllegalArgumentException ex) {
            return UNRECOGNIZED;
         }
      }
   }

   public abstract String id();

   @Nullable
   public abstract String name();

   public abstract float size(); // GB

   @Nullable
   public abstract Date creationTime();

   @Nullable
   public abstract Date lastModificationTime();

   public abstract ProvisioningState state();

   @Nullable
   public abstract List<String> serverIds();

   @Nullable
   public abstract Boolean bootDevice();

   @Nullable
   public abstract BusType busType();

   @Nullable
   public abstract Integer deviceNumber();

   public static Storage create(String id, String name, float size, Date creationTime, Date lastModificationTime,
           ProvisioningState state, List<String> serverIds, Boolean bootDevice, BusType busType, Integer deviceNumber) {
      return new AutoValue_Storage(id, name, size, creationTime, lastModificationTime, state,
              serverIds != null ? ImmutableList.copyOf(serverIds) : Lists.<String>newArrayList(),
              bootDevice, busType, deviceNumber);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromStorage(this);
   }

   public static class Builder {

      private String id;
      private String name;
      private float size;
      private Date creationTime;
      private Date lastModificationTime;
      private ProvisioningState state;
      private List<String> serverIds;
      private Boolean bootDevice;
      private BusType busType;
      private Integer deviceNumber;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder size(float size) {
         this.size = size;
         return this;
      }

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

      public Builder serverIds(List<String> serverIds) {
         this.serverIds = serverIds;
         return this;
      }

      public Builder bootDevice(Boolean bootDevice) {
         this.bootDevice = bootDevice;
         return this;
      }

      public Builder busType(BusType busType) {
         this.busType = busType;
         return this;
      }

      public Builder deviceNumber(Integer deviceNumber) {
         this.deviceNumber = deviceNumber;
         return this;
      }

      private Builder fromStorage(Storage in) {
         return this.id(in.id()).name(in.name()).size(in.size()).creationTime(in.creationTime())
                 .lastModificationTime(in.lastModificationTime()).state(in.state()).serverIds(in.serverIds())
                 .bootDevice(in.bootDevice()).busType(in.busType()).deviceNumber(in.deviceNumber());
      }

      public Storage build() {
         return Storage.create(id, name, size, creationTime, lastModificationTime, state, serverIds, bootDevice, busType, deviceNumber);
      }

   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new UpdatePayload.Builder();
      }

      public static ConnectPayload.Builder connectingBuilder() {
         return new ConnectPayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String dataCenterId();

         public abstract float size();

         @Nullable
         public abstract String name();

         @Nullable
         public abstract String mountImageId();

         @Nullable
         public abstract String profitBricksImagePassword();

         public static CreatePayload create(String dataCenterId, float size, String name, String mountImageId, String imagePassword) {
            validateSize(size);
            return new AutoValue_Storage_Request_CreatePayload(dataCenterId, size, name, mountImageId, imagePassword);
         }

         public static class Builder {

            private String dataCenterId;
            private float size;
            private String name;
            private String mountImageId;
            private String profitBricksImagePassword;

            public Builder dataCenterId(String dataCenterId) {
               this.dataCenterId = dataCenterId;
               return this;
            }

            public Builder dataCenterId(DataCenter dataCenter) {
               this.dataCenterId = checkNotNull(dataCenter, "Cannot pass null datacenter").id();
               return this;
            }

            public Builder size(float size) {
               this.size = size;
               return this;
            }

            public Builder mountImageId(String mountImageId) {
               this.mountImageId = mountImageId;
               return this;
            }

            public Builder name(String name) {
               this.name = name;
               return this;
            }

            public Builder imagePassword(String password) {
               this.profitBricksImagePassword = password;
               return this;
            }

            public CreatePayload build() {
               return CreatePayload.create(dataCenterId, size, name, mountImageId, profitBricksImagePassword);
            }
         }

      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();

         @Nullable
         public abstract Float size();

         @Nullable
         public abstract String name();

         @Nullable
         public abstract String mountImageId();

         public static UpdatePayload create(String id, Float size, String name, String mountImageId) {
            validateSize(size);
            return new AutoValue_Storage_Request_UpdatePayload(id, size, name, mountImageId);
         }

         public static class Builder {

            private String id;
            private Float size;
            private String name;
            private String mountImageId;

            public Builder id(String id) {
               this.id = id;
               return this;
            }

            public Builder size(float size) {
               this.size = size;
               return this;
            }

            public Builder name(String name) {
               this.name = name;
               return this;
            }

            public Builder mountImageId(String mountImageId) {
               this.mountImageId = mountImageId;
               return this;
            }

            public UpdatePayload build() {
               return UpdatePayload.create(id, size, name, mountImageId);
            }
         }
      }

      @AutoValue
      public abstract static class ConnectPayload {

         public abstract String storageId();

         public abstract String serverId();

         @Nullable
         public abstract BusType busType();

         @Nullable
         public abstract Integer deviceNumber();

         public static ConnectPayload create(String storageId, String serverId, BusType busType, Integer deviceNumber) {
            return new AutoValue_Storage_Request_ConnectPayload(storageId, serverId, busType, deviceNumber);
         }

         public static class Builder {

            private String storageId;
            private String serverId;
            private BusType busType;
            private Integer deviceNumber;

            public Builder storageId(String storageId) {
               this.storageId = storageId;
               return this;
            }

            public Builder serverId(String serverId) {
               this.serverId = serverId;
               return this;
            }

            public Builder busType(BusType busType) {
               this.busType = busType;
               return this;
            }

            public Builder deviceNumber(Integer deviceNumber) {
               this.deviceNumber = deviceNumber;
               return this;
            }

            public ConnectPayload build() {
               return ConnectPayload.create(storageId, serverId, busType, deviceNumber);
            }

         }
      }

      private static void validateSize(Float size) {
         if (size != null)
            checkArgument(size > 1, "Storage size must be > 1GB");

      }
   }

}
