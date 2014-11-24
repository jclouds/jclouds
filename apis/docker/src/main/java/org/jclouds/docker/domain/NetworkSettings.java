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
package org.jclouds.docker.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.docker.internal.NullSafeCopies.copyOf;

import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class NetworkSettings {
   public abstract String ipAddress();

   public abstract int ipPrefixLen();

   public abstract String gateway();

   public abstract String bridge();

   @Nullable public abstract String portMapping();

   public abstract Map<String, List<Map<String, String>>> ports();

   NetworkSettings() {
   }

   @SerializedNames({ "IPAddress", "IPPrefixLen", "Gateway", "Bridge", "PortMapping", "Ports" })
   public static NetworkSettings create(String ipAddress, int ipPrefixLen, String gateway, String bridge,
         String portMapping, Map<String, List<Map<String, String>>> ports) {
      return new AutoValue_NetworkSettings(ipAddress, ipPrefixLen, gateway, bridge, portMapping, copyOf(ports));
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromNetworkSettings(this);
   }

   public static final class Builder {

      private String ipAddress;
      private int ipPrefixLen;
      private String gateway;
      private String bridge;
      private String portMapping;
      private Map<String, List<Map<String, String>>> ports = ImmutableMap.of();

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder ipPrefixLen(int ipPrefixLen) {
         this.ipPrefixLen = ipPrefixLen;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder bridge(String bridge) {
         this.bridge = bridge;
         return this;
      }

      public Builder portMapping(String portMapping) {
         this.portMapping = portMapping;
         return this;
      }

      public Builder ports(Map<String, List<Map<String, String>>> ports) {
         this.ports = ImmutableMap.copyOf(checkNotNull(ports, "ports"));
         return this;
      }

      public NetworkSettings build() {
         return NetworkSettings.create(ipAddress, ipPrefixLen, gateway, bridge, portMapping, ports);
      }

      public Builder fromNetworkSettings(NetworkSettings in) {
         return this.ipAddress(in.ipAddress()).ipPrefixLen(in.ipPrefixLen()).gateway(in.gateway()).bridge(in.bridge())
               .portMapping(in.portMapping()).ports(in.ports());
      }
   }

}
