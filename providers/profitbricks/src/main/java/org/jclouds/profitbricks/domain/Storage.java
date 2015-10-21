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

import static org.jclouds.profitbricks.util.Preconditions.checkPassword;
import static org.jclouds.profitbricks.util.Preconditions.checkSize;

import java.util.Date;
import java.util.List;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;


@AutoValue
public abstract class Storage {

   public enum BusType {

      IDE, SCSI, VIRTIO, UNRECOGNIZED;

      public static BusType fromValue(String value) {
         return Enums.getIfPresent(BusType.class, value).or(UNRECOGNIZED);
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

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   public abstract List<String> serverIds();

   @Nullable
   public abstract Boolean bootDevice();

   @Nullable
   public abstract BusType busType();

   @Nullable
   public abstract Integer deviceNumber();

   public static Builder builder() {
      return new AutoValue_Storage.Builder()
              .serverIds(ImmutableList.<String>of());
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder size(float size);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder lastModificationTime(Date lastModificationTime);

      public abstract Builder state(ProvisioningState state);

      public abstract Builder serverIds(List<String> serverIds);

      public abstract Builder bootDevice(Boolean bootDevice);

      public abstract Builder busType(BusType busType);

      public abstract Builder deviceNumber(Integer deviceNumber);

      abstract Storage autoBuild();
      
      public Storage build(){
         Storage built = autoBuild();
         return built.toBuilder()
                 .serverIds(ImmutableList.copyOf(built.serverIds()))
                 .autoBuild();
      }

   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_Storage_Request_CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_Storage_Request_UpdatePayload.Builder();
      }

      public static ConnectPayload.Builder connectingBuilder() {
         return new AutoValue_Storage_Request_ConnectPayload.Builder();
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
         public abstract String imagePassword();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder dataCenterId(String dataCenterId);

            public abstract Builder size(float size);

            public abstract Builder name(String name);

            public abstract Builder mountImageId(String mountImageId);

            public abstract Builder imagePassword(String profitBricksImagePassword);

            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               CreatePayload payload = autoBuild();
               if (payload.imagePassword() != null)
                  checkPassword(payload.imagePassword());
               checkSize(payload.size());

               return payload;
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

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder id(String id);

            public abstract Builder size(Float size);

            public abstract Builder name(String name);

            public abstract Builder mountImageId(String mountImageId);

            abstract UpdatePayload autoBuild();

            public UpdatePayload build() {
               UpdatePayload payload = autoBuild();
               if (payload.size() != null)
                  checkSize(payload.size());

               return payload;
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

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder storageId(String storageId);

            public abstract Builder serverId(String serverId);

            public abstract Builder busType(BusType busType);

            public abstract Builder deviceNumber(Integer deviceNumber);

            public abstract ConnectPayload build();
         }
      }

   }
}
