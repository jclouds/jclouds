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
import com.google.common.base.Enums;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import static com.google.common.net.InetAddresses.isInetAddress;
import org.jclouds.javax.annotation.Nullable;

import java.util.Date;
import java.util.List;

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
   public abstract Algorithm loadBalancerAlgorithm();

   @Nullable
   public abstract String dataCenterId();

   @Nullable
   public abstract String dataCenterVersion();

   @Nullable
   public abstract Boolean internetAccess();

   @Nullable
   public abstract String ip();

   @Nullable
   public abstract String lanId();

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

   public static LoadBalancer create(String id, String name, Algorithm loadBalancerAlgorithm,
           String dataCenterId, String dataCenterVersion, boolean internetAccess,
           String ip, String lanId, ProvisioningState state, Date creationTime, Date lastModificationTime, List<Server> balancedServers, List<Firewall> firewalls) {
      return new AutoValue_LoadBalancer(id, name, loadBalancerAlgorithm, dataCenterId, dataCenterVersion, internetAccess, ip, lanId, state, creationTime, lastModificationTime,
              balancedServers != null ? ImmutableList.copyOf(balancedServers) : ImmutableList.<Server>of(),
              firewalls != null ? ImmutableList.copyOf(firewalls) : ImmutableList.<Firewall>of());
   }

   public static Builder builder() {
      return new Builder();
   }

   private static void checkIp(String ip) {
      if (ip != null)
         checkArgument(isInetAddress(ip), "Invalid IP");
   }

   public static class Builder {

      private String id;

      private String name;

      private Algorithm loadBalancerAlgorithm;

      private String dataCenterId;

      private String dataCenterVersion;

      private boolean internetAccess;

      private String ip;

      private String lanId;

      private ProvisioningState state;

      private Date creationTime;

      private Date lastModificationTime;

      public List<Server> balancedServers;

      private List<Firewall> firewalls;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder loadBalancerAlgorithm(Algorithm loadBalancerAlgorithm) {
         this.loadBalancerAlgorithm = loadBalancerAlgorithm;
         return this;
      }

      public Builder dataCenterId(String dataCenterId) {
         this.dataCenterId = dataCenterId;
         return this;
      }

      public Builder dataCenterVersion(String dataCenterVersion) {
         this.dataCenterVersion = dataCenterVersion;
         return this;
      }

      public Builder internetAccess(boolean internetAccess) {
         this.internetAccess = internetAccess;
         return this;
      }

      public Builder ip(String ip) {
         this.ip = ip;
         return this;
      }

      public Builder lanId(String lanId) {
         this.lanId = lanId;
         return this;
      }

      public Builder creationTime(Date creationTime) {
         this.creationTime = creationTime;
         return this;
      }

      public Builder state(ProvisioningState state) {
         this.state = state;
         return this;
      }

      public Builder lastModificationTime(Date lastModificationTime) {
         this.lastModificationTime = lastModificationTime;
         return this;
      }

      public Builder balancedServers(List<Server> balancedServers) {
         this.balancedServers = balancedServers;
         return this;
      }

      public Builder firewalls(List<Firewall> firewalls) {
         this.firewalls = firewalls;
         return this;
      }

      public LoadBalancer build() {
         checkIp(ip);
         return LoadBalancer.create(id, name, loadBalancerAlgorithm, dataCenterId, dataCenterVersion, internetAccess, ip, lanId, state, creationTime, lastModificationTime, balancedServers, firewalls);
      }

      public Builder fromLoadBalancer(LoadBalancer in) {
         return this.id(in.id()).name(in.name()).loadBalancerAlgorithm(in.loadBalancerAlgorithm())
                 .dataCenterId(in.dataCenterId()).dataCenterVersion(in.dataCenterVersion()).internetAccess(in.internetAccess())
                 .ip(in.ip()).lanId(in.lanId()).state(in.state()).creationTime(in.creationTime()).lastModificationTime(in.lastModificationTime()).balancedServers(in.balancedServers()).firewalls(in.firewalls());
      }
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new UpdatePayload.Builder();
      }

      public static RegisterPayload.Builder registerBuilder() {
         return new RegisterPayload.Builder();
      }

      public static DeregisterPayload.Builder deregisterBuilder() {
         return new DeregisterPayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String dataCenterId();

         public abstract String loadBalancerName();

         public abstract Algorithm loadBalancerAlgorithm();

         public abstract String ip();

         public abstract String lanId();

         public abstract List<String> serverIds();

         public static CreatePayload create(String dataCenterId, String loadBalancerName, Algorithm loadBalancerAlgorithm, String ip, String lanId, List<String> serverIds) {
            return new AutoValue_LoadBalancer_Request_CreatePayload(dataCenterId, loadBalancerName, loadBalancerAlgorithm, ip, lanId,
                    serverIds != null ? ImmutableList.copyOf(serverIds) : Lists.<String>newArrayList());
         }

         public static class Builder {

            public String dataCenterId;
            public String loadBalancerName;
            public Algorithm loadBalancerAlgorithm;
            public String ip;
            public String lanId;
            public List<String> serverIds;

            public Builder dataCenterId(String dataCenterId) {
               this.dataCenterId = dataCenterId;
               return this;
            }

            public Builder loadBalancerName(String loadBalancerName) {
               this.loadBalancerName = loadBalancerName;
               return this;
            }

            public Builder loadBalancerAlgorithm(Algorithm loadBalancerAlgorithm) {
               this.loadBalancerAlgorithm = loadBalancerAlgorithm;
               return this;
            }

            public Builder ip(String ip) {
               this.ip = ip;
               return this;
            }

            public Builder lanId(String lanId) {
               this.lanId = lanId;
               return this;
            }

            public Builder serverIds(List<String> serverIds) {
               this.serverIds = serverIds;
               return this;
            }

            public CreatePayload build() {
               checkIp(ip);
               return CreatePayload.create(dataCenterId, loadBalancerName, loadBalancerAlgorithm, ip, lanId, serverIds);
            }
         }
      }

      @AutoValue
      public abstract static class RegisterPayload {

         public abstract List<String> serverIds();

         public abstract String id();

         public static RegisterPayload create(List<String> serverIds, String id) {
            return new AutoValue_LoadBalancer_Request_RegisterPayload(
                    serverIds != null ? ImmutableList.copyOf(serverIds) : Lists.<String>newArrayList(), id);
         }

         public static class Builder {

            public List<String> serverIds;
            public String id;

            public Builder serverIds(List<String> serverIds) {
               this.serverIds = serverIds;
               return this;
            }

            public Builder id(String id) {
               this.id = id;
               return this;
            }

            public RegisterPayload build() {
               return RegisterPayload.create(serverIds, id);
            }
         }
      }

      @AutoValue
      public abstract static class DeregisterPayload {

         public abstract List<String> serverIds();

         public abstract String id();

         public static DeregisterPayload create(List<String> serverIds, String id) {
            return new AutoValue_LoadBalancer_Request_DeregisterPayload(
                    serverIds != null ? ImmutableList.copyOf(serverIds) : Lists.<String>newArrayList(), id);
         }

         public static class Builder {

            public List<String> serverIds;
            public String id;

            public Builder serverIds(List<String> serverIds) {
               this.serverIds = serverIds;
               return this;
            }

            public Builder id(String id) {
               this.id = id;
               return this;
            }

            public DeregisterPayload build() {
               return DeregisterPayload.create(serverIds, id);
            }
         }
      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();

         public abstract String name();

         public abstract Algorithm loadBalancerAlgorithm();

         public abstract String ip();

         public static UpdatePayload create(String id, String name, Algorithm loadBalancerAlgorithm, String ip) {
            return new AutoValue_LoadBalancer_Request_UpdatePayload(id, name, loadBalancerAlgorithm, ip);
         }

         public static class Builder {

            public String id;

            public String name;

            public Algorithm loadBalancerAlgorithm;

            public String ip;

            public Builder id(String id) {
               this.id = id;
               return this;
            }

            public Builder loadBalancerName(String loadBalancerName) {
               this.name = loadBalancerName;
               return this;
            }

            public Builder loadBalancerAlgorithm(Algorithm loadBalancerAlgorithm) {
               this.loadBalancerAlgorithm = loadBalancerAlgorithm;
               return this;
            }

            public Builder ip(String ip) {
               this.ip = ip;
               return this;
            }

            public UpdatePayload build() {
               checkIp(ip);
               return UpdatePayload.create(id, name, loadBalancerAlgorithm, ip);
            }
         }
      }
   }
}
