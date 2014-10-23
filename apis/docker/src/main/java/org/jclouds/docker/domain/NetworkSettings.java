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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkSettings {

   @SerializedName("IPAddress")
   private final String ipAddress;
   @SerializedName("IPPrefixLen")
   private final int ipPrefixLen;
   @SerializedName("Gateway")
   private final String gateway;
   @SerializedName("Bridge")
   private final String bridge;
   @SerializedName("PortMapping")
   private final String portMapping;
   @SerializedName("Ports")
   private final Map<String, List<Map<String, String>>> ports;

   @ConstructorProperties({ "IPAddress", "IPPrefixLen", "Gateway", "Bridge", "PortMapping", "Ports" })
   protected NetworkSettings(String ipAddress, int ipPrefixLen, String gateway, String bridge,
                          @Nullable String portMapping, @Nullable Map<String, List<Map<String, String>>> ports) {
      this.ipAddress = checkNotNull(ipAddress, "ipAddress");
      this.ipPrefixLen = checkNotNull(ipPrefixLen, "ipPrefixLen");
      this.gateway = checkNotNull(gateway, "gateway");
      this.bridge = checkNotNull(bridge, "bridge");
      this.portMapping = portMapping;
      this.ports = ports != null ? ImmutableMap.copyOf(ports) : ImmutableMap.<String, List<Map<String, String>>> of();
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public int getIpPrefixLen() {
      return ipPrefixLen;
   }

   public String getGateway() {
      return gateway;
   }

   public String getBridge() {
      return bridge;
   }

   public String getPortMapping() {
      return portMapping;
   }

   public Map<String, List<Map<String, String>>> getPorts() {
      return ports;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NetworkSettings that = (NetworkSettings) o;

      return Objects.equal(this.ipAddress, that.ipAddress) &&
              Objects.equal(this.ipPrefixLen, that.ipPrefixLen) &&
              Objects.equal(this.gateway, that.gateway) &&
              Objects.equal(this.bridge, that.bridge) &&
              Objects.equal(this.portMapping, that.portMapping) &&
              Objects.equal(this.ports, that.ports);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipAddress, ipPrefixLen, gateway, bridge, portMapping, ports);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("ipAddress", ipAddress)
              .add("ipPrefixLen", ipPrefixLen)
              .add("gateway", gateway)
              .add("bridge", bridge)
              .add("portMapping", portMapping)
              .add("ports", ports)
              .toString();
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
         return new NetworkSettings(ipAddress, ipPrefixLen, gateway, bridge, portMapping, ports);
      }

      public Builder fromNetworkSettings(NetworkSettings in) {
         return this
                 .ipAddress(in.getIpAddress())
                 .ipPrefixLen(in.getIpPrefixLen())
                 .gateway(in.getGateway())
                 .bridge(in.getBridge())
                 .portMapping(in.getPortMapping())
                 .ports(in.getPorts());
      }

   }

}
