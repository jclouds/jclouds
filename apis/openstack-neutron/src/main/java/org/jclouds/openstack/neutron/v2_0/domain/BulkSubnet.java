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

package org.jclouds.openstack.neutron.v2_0.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Set;

/**
 * A Neutron subnet used for creating subnets in bulk
 * The only difference between this and the actual subnet are the missing fields id & tenantId
 */
public class BulkSubnet {

   protected String name;
   protected String networkId;
   protected String gatewayIp;
   protected Integer ipVersion;
   protected String cidr;
   protected Set<AllocationPool> allocationPools;
   protected Boolean enableDhcp;
   protected Set<String> dnsNameServers;
   protected Set<HostRoute> hostRoutes;

   @ConstructorProperties({
      "name", "network_id", "gateway_ip", "ip_version", "cidr", "allocation_pools", "enable_dhcp", "dns_nameservers", "host_routes"
   })
   protected BulkSubnet(String name, String networkId, String gatewayIp, Integer ipVersion, String cidr,
                     Set<AllocationPool> allocationPools, Boolean enableDhcp, Set<String> dnsNameServers, Set<HostRoute> hostRoutes) {
      this.name = name;
      this.networkId = networkId;
      this.gatewayIp = gatewayIp;
      this.ipVersion = ipVersion;
      this.cidr = cidr;
      this.allocationPools = allocationPools != null ? ImmutableSet.copyOf(allocationPools) : ImmutableSet.<AllocationPool>of();
      this.enableDhcp = enableDhcp;
      this.dnsNameServers = dnsNameServers != null ? ImmutableSet.copyOf(dnsNameServers) : ImmutableSet.<String>of();
      this.hostRoutes = hostRoutes != null ? ImmutableSet.copyOf(hostRoutes) : ImmutableSet.<HostRoute>of();
   }

   /**
    * @return the name of the subnet
    */
   public String getName() {
      return name;
   }

   /**
    * @return the id of the network this subnet is associated with
    */
   public String getNetworkId() {
      return networkId;
   }

   /**
    * @return the default gateway used by devices in this subnet
    */
   public String getGatewayIp() {
      return gatewayIp;
   }

   /**
    * @return the ip version used by this subnet
    */
   public Integer getIpVersion() {
      return ipVersion;
   }

   /**
    * @return the cidr representing the IP range for this subnet, based on IP version
    */
   public String getCidr() {
      return cidr;
   }

   /**
    * @return the sub-ranges of cidr available for dynamic allocation to ports
    */
   public Set<AllocationPool> getAllocationPools() {
      return allocationPools;
   }

   /**
    * @return true if DHCP is enabled for this subnet, false if not.
    */
   public Boolean getEnableDhcp() {
      return enableDhcp;
   }

   /**
    * @return the set of DNS name servers used by hosts in this subnet.
    */
   public Set<String> getDnsNameServers() {
      return dnsNameServers;
   }

   /**
    * @return the set of routes that should be used by devices with IPs from this subnet
    */
   public Set<HostRoute> getHostRoutes() {
      return hostRoutes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, networkId, gatewayIp, ipVersion, cidr,
         allocationPools, enableDhcp, dnsNameServers, hostRoutes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BulkSubnet that = BulkSubnet.class.cast(obj);
      return Objects.equal(this.name, that.name)
         && Objects.equal(this.networkId, that.networkId)
         && Objects.equal(this.gatewayIp, that.gatewayIp)
         && Objects.equal(this.ipVersion, that.ipVersion)
         && Objects.equal(this.cidr, that.cidr)
         && Objects.equal(this.allocationPools, that.allocationPools)
         && Objects.equal(this.enableDhcp, that.enableDhcp)
         && Objects.equal(this.dnsNameServers, that.dnsNameServers)
         && Objects.equal(this.hostRoutes, that.hostRoutes);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("name", name).add("networkId", networkId).add("gatewayIp", gatewayIp).add("ipVersion", ipVersion)
         .add("cidr", cidr).add("enableDhcp", enableDhcp).add("allocationPools", allocationPools)
         .add("dnsNameServers", dnsNameServers).add("hostRoutes", hostRoutes);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromBulkSubnet(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String name;
      protected String networkId;
      protected String gatewayIp;
      protected Integer ipVersion;
      protected String cidr;
      protected Set<AllocationPool> allocationPools;
      protected Boolean enableDhcp;
      protected Set<String> dnsNameServers;
      protected Set<HostRoute> hostRoutes;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getNetworkId()
       */
      public Builder networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getGatewayIp()
       */
      public Builder gatewayIp(String gatewayIp) {
         this.gatewayIp = gatewayIp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getIpVersion()
       */
      public Builder ipVersion(Integer ipVersion) {
         this.ipVersion = ipVersion;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getCidr()
       */
      public Builder cidr(String cidr) {
         this.cidr = cidr;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getAllocationPools()
       */
      public Builder allocationPools(Collection<AllocationPool> allocationPools) {
         this.allocationPools = ImmutableSet.copyOf(allocationPools);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getEnableDhcp()
       */
      public Builder enableDhcp(Boolean enableDhcp) {
         this.enableDhcp = enableDhcp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getDnsNameServers()
       */
      public Builder dnsNameServers(Collection<String> dnsNameServers) {
         this.dnsNameServers = ImmutableSet.copyOf(dnsNameServers);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet#getHostRoutes()
       */
      public Builder hostRoutes(Collection<HostRoute> hostRoutes) {
         this.hostRoutes = ImmutableSet.copyOf(hostRoutes);
         return self();
      }

      public BulkSubnet build() {
         return new BulkSubnet(name, networkId, gatewayIp, ipVersion, cidr, allocationPools, enableDhcp, dnsNameServers, hostRoutes);
      }

      public Builder fromBulkSubnet(BulkSubnet in) {
         return this.name(in.getName())
               .networkId(in.getNetworkId())
               .gatewayIp(in.getGatewayIp())
               .ipVersion(in.getIpVersion())
               .cidr(in.getCidr())
               .allocationPools(in.getAllocationPools())
               .enableDhcp(in.getEnableDhcp())
               .dnsNameServers(in.getDnsNameServers())
               .hostRoutes(in.getHostRoutes());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
