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

import static org.jclouds.profitbricks.util.Preconditions.checkInvalidChars;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class DataCenter {

   public abstract String id();

   @Nullable
   public abstract String name();

   public abstract int version();

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   public abstract Location location();

   @Nullable
   public abstract List<Server> servers();

   @Nullable
   public abstract List<Storage> storages();

   public static Builder builder() {
      return new AutoValue_DataCenter.Builder()
              .servers(ImmutableList.<Server>of())
              .storages(ImmutableList.<Storage>of());
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder version(int version);

      public abstract Builder state(ProvisioningState state);

      public abstract Builder location(Location location);

      public abstract Builder servers(List<Server> servers);

      public abstract Builder storages(List<Storage> storages);

      abstract DataCenter autoBuild();
      
      public DataCenter build(){
         DataCenter built = autoBuild();
         return built.toBuilder()
                 .servers(ImmutableList.copyOf(built.servers()))
                 .storages(ImmutableList.copyOf(built.storages()))
                 .autoBuild();
      }

   }

   public static final class Request {

      public static CreatePayload creatingPayload(String name, Location location) {
         CreatePayload payload = new AutoValue_DataCenter_Request_CreatePayload(name, location);
         checkInvalidChars(payload.name());

         return payload;
      }

      public static UpdatePayload updatingPayload(String id, String name) {
         UpdatePayload payload = new AutoValue_DataCenter_Request_UpdatePayload(id, name);
         checkInvalidChars(payload.name());

         return payload;
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String name();

         public abstract Location location();

      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();

         public abstract String name();

      }
   }
}
