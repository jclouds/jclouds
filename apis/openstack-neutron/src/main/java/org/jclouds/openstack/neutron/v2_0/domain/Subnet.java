/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.neutron.v2_0.domain;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Set;

/**
 * A Neutron network
 *
 * @author Nick Livens
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api doc</a>
 */
public class Subnet extends ReferenceWithName {

   private final String networkId;
   private final String gatewayIp;
   private final Integer ipVersion;
   private final String cidr;
   private final Set<AllocationPool> allocationPools;
   private final Boolean enableDhcp;
   private final Set<String> dnsNameServers;
   private final Set<HostRoute> hostRoutes;

   @ConstructorProperties({
      "id", "tenant_id", "name", "network_id", "gateway_ip", "ip_version", "cidr", "allocation_pools", "enable_dhcp", "dns_nameservers", "host_routes"
   })
   protected Subnet(String id, String tenantId, String name, String networkId,
                    String gatewayIp, Integer ipVersion, String cidr, Set<AllocationPool> allocationPools,
                    Boolean enableDhcp, Set<String> dnsNameServers, Set<HostRoute> hostRoutes) {
      super(id, tenantId, name);
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
      return Objects.hashCode(super.hashCode(), networkId, gatewayIp, ipVersion, cidr,
         allocationPools, enableDhcp, dnsNameServers, hostRoutes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Subnet that = Subnet.class.cast(obj);
      return super.equals(obj)
         && Objects.equal(this.networkId, that.networkId)
         && Objects.equal(this.gatewayIp, that.gatewayIp)
         && Objects.equal(this.ipVersion, that.ipVersion)
         && Objects.equal(this.cidr, that.cidr)
         && Objects.equal(this.allocationPools, that.allocationPools)
         && Objects.equal(this.enableDhcp, that.enableDhcp)
         && Objects.equal(this.dnsNameServers, that.dnsNameServers)
         && Objects.equal(this.hostRoutes, that.hostRoutes);
   }

   protected ToStringHelper string() {
      return super.string()
         .add("networkId", networkId)
         .add("gatewayIp", gatewayIp)
         .add("ipVersion", ipVersion)
         .add("cidr", cidr)
         .add("enableDHCP", enableDhcp)
         .add("allocationPools", allocationPools)
         .add("dnsNameServers", dnsNameServers)
         .add("hostRoutes", hostRoutes);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSubnet(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends ReferenceWithName.Builder<T> {
      protected String networkId;
      protected String gatewayIp;
      protected Integer ipVersion;
      protected String cidr;
      protected Set<AllocationPool> allocationPools;
      protected Boolean enableDhcp;
      protected Set<String> dnsNameServers;
      protected Set<HostRoute> hostRoutes;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getNetworkId()
       */
      public T networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getGatewayIp()
       */
      public T gatewayIp(String gatewayIp) {
         this.gatewayIp = gatewayIp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getIpVersion()
       */
      public T ipVersion(Integer ipVersion) {
         this.ipVersion = ipVersion;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getCidr()
       */
      public T cidr(String cidr) {
         this.cidr = cidr;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getAllocationPools()
       */
      public T allocationPools(Collection<AllocationPool> allocationPools) {
         this.allocationPools = ImmutableSet.copyOf(allocationPools);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getEnableDhcp()
       */
      public T enableDhcp(Boolean enableDhcp) {
         this.enableDhcp = enableDhcp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getDnsNameServers()
       */
      public T dnsNameServers(Collection<String> dnsNameServers) {
         this.dnsNameServers = ImmutableSet.copyOf(dnsNameServers);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Subnet#getHostRoutes()
       */
      public T hostRoutes(Collection<HostRoute> hostRoutes) {
         this.hostRoutes = ImmutableSet.copyOf(hostRoutes);
         return self();
      }

      public Subnet build() {
         return new Subnet(id, tenantId, name, networkId, gatewayIp, ipVersion, cidr, allocationPools, enableDhcp, dnsNameServers, hostRoutes);
      }

      public T fromSubnet(Subnet in) {
         return super.fromReference(in)
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

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
