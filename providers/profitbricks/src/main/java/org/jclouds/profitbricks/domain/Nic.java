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

import static com.google.common.base.Preconditions.checkArgument;

import autovalue.shaded.com.google.common.common.collect.Lists;

import com.google.auto.value.AutoValue;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import static com.google.common.net.InetAddresses.isInetAddress;

@AutoValue
public abstract class Nic {

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String name();

   @Nullable
   public abstract String dataCenterId();

   public abstract int lanId();

   public abstract boolean internetAccess();

   @Nullable
   public abstract String serverId();

   @Nullable
   public abstract List<String> ips();

   @Nullable
   public abstract String macAddress();

   @Nullable
   public abstract Firewall firewalls();

   public abstract boolean dhcpActive();

   @Nullable
   public abstract String gatewayIp();

   @Nullable
   public abstract ProvisioningState state();

   public static Nic create(String id, String name, String dataCenterId, int lanId, boolean internetAccess,
           String serverId, List<String> ips, String macAddress, Firewall firewall, boolean dhcpActive,
           String gatewayIp, ProvisioningState state) {
      return new AutoValue_Nic(id, name, dataCenterId, lanId, internetAccess, serverId,
              ips != null ? ImmutableList.copyOf(ips) : ImmutableList.<String>of(), macAddress,
              firewall, dhcpActive, gatewayIp, state);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromNic(this);
   }

   private static void checkIp(String ip) {
      if (ip != null)
         checkArgument(isInetAddress(ip), "Invalid IP");
   }

   private static void checkIps(List<String> ips) {
      for (String ip : ips)
         checkIp(ip);
   }

   public static class Builder {

      public String id;

      public String name;

      public String dataCenterId;

      public int lanId;

      public boolean internetAccess;

      public String serverId;

      @Nullable
      public List<String> ips;

      public String macAddress;

      public Firewall firewall;

      public boolean dhcpActive;

      public String gatewayIp;

      public ProvisioningState state;

      public Builder() {
         this.ips = Lists.newArrayList();
      }

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder dataCenterId(String dataCenterId) {
         this.dataCenterId = dataCenterId;
         return this;
      }

      public Builder lanId(int lanId) {
         this.lanId = lanId;
         return this;
      }

      public Builder internetAccess(boolean internetAccess) {
         this.internetAccess = internetAccess;
         return this;
      }

      public Builder serverId(String serverId) {
         this.serverId = serverId;
         return this;
      }

      public Builder ips(List<String> ips) {
         this.ips = ips;
         return this;
      }

      public Builder ip(String ip) {
         this.ips.add(ip);
         return this;
      }

      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return this;
      }

      public Builder dhcpActive(boolean dhcpActive) {
         this.dhcpActive = dhcpActive;
         return this;
      }

      public Builder gatewayIp(String gatewayIp) {
         this.gatewayIp = gatewayIp;
         return this;
      }

      public Builder state(ProvisioningState state) {
         this.state = state;
         return this;
      }

      public Builder firewall(Firewall firewall) {
         this.firewall = firewall;
         return this;
      }

      public Nic build() {
         checkIps(ips);
         return Nic.create(id, name, dataCenterId, lanId, internetAccess, serverId, ips,
                 macAddress, firewall, dhcpActive, gatewayIp, state);
      }

      private Builder fromNic(Nic in) {
         return this.id(in.id()).name(in.name()).lanId(in.lanId()).internetAccess(in.internetAccess())
                 .serverId(in.serverId()).ips(in.ips()).macAddress(in.macAddress()).dhcpActive(in.dhcpActive())
                 .gatewayIp(in.gatewayIp()).dataCenterId(dataCenterId);
      }
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new UpdatePayload.Builder();
      }

      public static SetInternetAccessPayload.Builder setInternetAccessBuilder() {
         return new SetInternetAccessPayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         @Nullable
         public abstract String ip();

         @Nullable
         public abstract String name();

         @Nullable
         public abstract Boolean dhcpActive();

         public abstract String serverId();

         public abstract int lanId();

         public static CreatePayload create(String ip, String name, Boolean dhcpActive, String serverId, int lanId) {
            return new AutoValue_Nic_Request_CreatePayload(ip, name, dhcpActive, serverId, lanId);
         }

         public static class Builder {

            private String ip;
            private String name;
            private Boolean dhcpActive;
            private String serverId;
            private int lanId;

            public Builder ip(String ip) {
               this.ip = ip;
               return this;
            }

            public Builder name(String name) {
               this.name = name;
               return this;
            }

            public Builder dhcpActive(Boolean dhcpActive) {
               this.dhcpActive = dhcpActive;
               return this;
            }

            public Builder serverId(String serverId) {
               this.serverId = serverId;
               return this;
            }

            public Builder lanId(int lanId) {
               this.lanId = lanId;
               return this;
            }

            public CreatePayload build() {
               checkIp(ip);
               return CreatePayload.create(ip, name, dhcpActive, serverId, lanId);
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

         public abstract int lanId();

         public static UpdatePayload create(String id, String ip, String name, Boolean dhcpActive, int lanId) {
            return new AutoValue_Nic_Request_UpdatePayload(id, ip, name, dhcpActive, lanId);
         }

         public static class Builder {

            private String id;
            private String ip;
            private String name;
            private Boolean dhcpActive;
            private int lanId;

            public Builder ip(String ip) {
               this.ip = ip;
               return this;
            }

            public Builder name(String name) {
               this.name = name;
               return this;
            }

            public Builder dhcpActive(Boolean dhcpActive) {
               this.dhcpActive = dhcpActive;
               return this;
            }

            public Builder lanId(int lanId) {
               this.lanId = lanId;
               return this;
            }

            public Builder id(String id) {
               this.id = id;
               return this;
            }

            public UpdatePayload build() {
               checkIp(ip);
               return UpdatePayload.create(id, ip, name, dhcpActive, lanId);
            }
         }
      }

      @AutoValue
      public abstract static class SetInternetAccessPayload {

         public abstract String dataCenterId();

         public abstract int lanId();

         public abstract boolean internetAccess();

         public static SetInternetAccessPayload create(String dataCenterId, int lanId, boolean internetAccess) {
            return new AutoValue_Nic_Request_SetInternetAccessPayload(dataCenterId, lanId, internetAccess);
         }

         public static class Builder {

            public String dataCenterId;
            public int lanId;
            public boolean internetAccess;

            public Builder dataCenterId(String dataCenterId) {
               this.dataCenterId = dataCenterId;
               return this;
            }

            public Builder lanId(int lanId) {
               this.lanId = lanId;
               return this;
            }

            public Builder internetAccess(boolean internetAccess) {
               this.internetAccess = internetAccess;
               return this;
            }

            public SetInternetAccessPayload build() {
               return SetInternetAccessPayload.create(dataCenterId, lanId, internetAccess);
            }
         }
      }
   }
}
