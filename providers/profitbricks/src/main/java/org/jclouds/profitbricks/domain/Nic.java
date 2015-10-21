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
import static org.jclouds.profitbricks.util.Preconditions.checkIps;
import static org.jclouds.profitbricks.util.Preconditions.checkLanId;
import static org.jclouds.profitbricks.util.Preconditions.checkMacAddress;

import java.util.List;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class Nic {

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String name();

   @Nullable
   public abstract String dataCenterId();

   @Nullable
   public abstract Integer lanId();

   @Nullable
   public abstract Boolean internetAccess();

   @Nullable
   public abstract String serverId();

   @Nullable
   public abstract List<String> ips();

   @Nullable
   public abstract String macAddress();

   @Nullable
   public abstract Firewall firewall();

   @Nullable
   public abstract Boolean dhcpActive();

   @Nullable
   public abstract String gatewayIp();

   @Nullable
   public abstract ProvisioningState state();

   public static Builder builder() {
      return new AutoValue_Nic.Builder()
              .ips(ImmutableList.<String>of());
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder dataCenterId(String dataCenterId);

      public abstract Builder lanId(Integer lanId);

      public abstract Builder internetAccess(Boolean internetAccess);

      public abstract Builder serverId(String serverId);

      public abstract Builder ips(List<String> ips);

      public abstract Builder macAddress(String macAddress);

      public abstract Builder firewall(Firewall firewall);

      public abstract Builder dhcpActive(Boolean dhcpActive);

      public abstract Builder gatewayIp(String gatewayIp);

      public abstract Builder state(ProvisioningState state);

      abstract Nic autoBuild();

      public Nic build() {
         Nic nic = autoBuild();
         if (nic.ips() != null)
            checkIps(nic.ips());
         if (nic.gatewayIp() != null)
            checkIp(nic.gatewayIp());
         if (nic.lanId() != null)
            checkLanId(nic.lanId());
         if (nic.macAddress() != null)
            checkMacAddress(nic.macAddress());

         return nic.toBuilder()
                 .ips(ImmutableList.copyOf(nic.ips()))
                 .autoBuild();
      }

   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_Nic_Request_CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_Nic_Request_UpdatePayload.Builder();
      }

      public static SetInternetAccessPayload.Builder setInternetAccessBuilder() {
         return new AutoValue_Nic_Request_SetInternetAccessPayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String serverId();

         public abstract int lanId();

         @Nullable
         public abstract String ip();

         @Nullable
         public abstract String name();

         @Nullable
         public abstract Boolean dhcpActive();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder serverId(String serverId);

            public abstract Builder lanId(int lanId);

            public abstract Builder ip(String ip);

            public abstract Builder name(String name);

            public abstract Builder dhcpActive(Boolean dhcpActive);

            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               CreatePayload payload = autoBuild();
               if (payload.ip() != null)
                  checkIp(payload.ip());
               checkLanId(payload.lanId());

               return payload;
            }
         }
      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();

         @Nullable
         public abstract String ip();

         @Nullable
         public abstract String name();

         @Nullable
         public abstract Boolean dhcpActive();

         @Nullable
         public abstract Integer lanId();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder id(String id);

            public abstract Builder ip(String ip);

            public abstract Builder name(String name);

            public abstract Builder dhcpActive(Boolean dhcpActive);

            public abstract Builder lanId(Integer lanId);

            abstract UpdatePayload autoBuild();

            public UpdatePayload build() {
               UpdatePayload payload = autoBuild();
               if (payload.ip() != null)
                  checkIp(payload.ip());
               if (payload.lanId() != null)
                  checkLanId(payload.lanId());

               return payload;
            }
         }
      }

      @AutoValue
      public abstract static class SetInternetAccessPayload {

         public abstract String dataCenterId();

         public abstract int lanId();

         public abstract boolean internetAccess();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder dataCenterId(String dataCenterId);

            public abstract Builder lanId(int lanId);

            public abstract Builder internetAccess(boolean internetAccess);

            public abstract SetInternetAccessPayload build();

         }
      }
   }

}
