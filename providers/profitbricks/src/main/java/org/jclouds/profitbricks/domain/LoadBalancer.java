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

import static org.jclouds.profitbricks.util.Preconditions.checkIp;
import static org.jclouds.profitbricks.util.Preconditions.checkLanId;

import java.util.Date;
import java.util.List;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.collect.ImmutableList;

import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class LoadBalancer {

   public enum Algorithm {

      ROUND_ROBIN, UNRECOGNIZED;

      public static Algorithm
              fromValue(String value) {
         return Enums.getIfPresent(Algorithm.class, value).or(UNRECOGNIZED);
      }
   }

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String name();

   @Nullable
   public abstract Algorithm algorithm();

   @Nullable
   public abstract DataCenter dataCenter();

   @Nullable
   public abstract Boolean internetAccess();

   @Nullable
   public abstract String ip();

   @Nullable
   public abstract Integer lanId();

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   public abstract Date creationTime();

   @Nullable
   public abstract Date lastModificationTime();

   @Nullable
   public abstract List<Server> balancedServers();

   @Nullable
   public abstract List<Firewall> firewalls();

   public static Builder builder() {
      return new AutoValue_LoadBalancer.Builder()
              .balancedServers(ImmutableList.<Server>of())
              .firewalls(ImmutableList.<Firewall>of());
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder algorithm(Algorithm algorithm);

      public abstract Builder dataCenter(DataCenter dataCenter);

      public abstract Builder internetAccess(Boolean internetAccess);

      public abstract Builder ip(String ip);

      public abstract Builder lanId(Integer lanId);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder state(ProvisioningState state);

      public abstract Builder lastModificationTime(Date lastModificationTime);

      public abstract Builder balancedServers(List<Server> balancedServers);

      public abstract Builder firewalls(List<Firewall> firewalls);

      abstract LoadBalancer autoBuild();

      public LoadBalancer build() {
         LoadBalancer loadBalancer = autoBuild();
         if (loadBalancer.ip() != null)
            checkIp(loadBalancer.ip());
         if (loadBalancer.lanId() != null)
            checkLanId(loadBalancer.lanId());

         return loadBalancer.toBuilder()
                 .balancedServers(ImmutableList.copyOf(loadBalancer.balancedServers()))
                 .firewalls(ImmutableList.copyOf(loadBalancer.firewalls()))
                 .autoBuild();
      }
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_LoadBalancer_Request_CreatePayload.Builder()
                 .serverIds(ImmutableList.<String>of())
                 .algorithm(Algorithm.ROUND_ROBIN);
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_LoadBalancer_Request_UpdatePayload.Builder()
                 .algorithm(Algorithm.ROUND_ROBIN);
      }

      public static RegisterPayload createRegisteringPaylod(String loadBalancerId, List<String> serverIds) {
         return new AutoValue_LoadBalancer_Request_RegisterPayload(loadBalancerId, ImmutableList.copyOf(serverIds));
      }

      public static DeregisterPayload createDeregisteringPayload(String loadBalancerId, List<String> serverIds) {
         return new AutoValue_LoadBalancer_Request_DeregisterPayload(loadBalancerId, ImmutableList.copyOf(serverIds));
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String dataCenterId();

         @Nullable
         public abstract String name();

         public abstract Algorithm algorithm();

         @Nullable
         public abstract String ip();

         @Nullable
         public abstract Integer lanId();

         public abstract List<String> serverIds();
         
         public abstract Builder toBuilder();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder dataCenterId(String dataCenterId);

            public abstract Builder name(String name);

            public abstract Builder algorithm(Algorithm algorithm);

            public abstract Builder ip(String ip);

            public abstract Builder lanId(Integer lanId);

            public abstract Builder serverIds(List<String> serverIds);

            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               CreatePayload payload = autoBuild();
               if (payload.ip() != null)
                  checkIp(payload.ip());
               if (payload.lanId() != null)
                  checkLanId(payload.lanId());

               return payload.toBuilder()
                       .serverIds(ImmutableList.copyOf(payload.serverIds()))
                       .autoBuild();
            }
         }
      }

      @AutoValue
      public abstract static class RegisterPayload {

         public abstract String id();

         public abstract List<String> serverIds();

      }

      @AutoValue
      public abstract static class DeregisterPayload {

         public abstract String id();

         public abstract List<String> serverIds();

      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();

         @Nullable
         public abstract String name();

         public abstract Algorithm algorithm();

         @Nullable
         public abstract String ip();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder id(String id);

            public abstract Builder name(String name);

            public abstract Builder algorithm(Algorithm algorithm);

            public abstract Builder ip(String ip);

            abstract UpdatePayload autoBuild();

            public UpdatePayload build() {
               UpdatePayload payload = autoBuild();
               if (payload.ip() != null)
                  checkIp(payload.ip());

               return payload;
            }
         }
      }
   }
}
