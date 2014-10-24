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
package org.jclouds.openstack.neutron.v2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Collection;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * A Neutron subnet
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api doc</a>
 */
public class Subnet {

   private String id;

   private String name;
   @Named("network_id")
   private String networkId;
   @Named("tenant_id")
   private String tenantId;
   // Cannot be used for updates.
   @Named("allocation_pools")
   private ImmutableSet<AllocationPool> allocationPools;
   @Named("gateway_ip")
   private String gatewayIp;
   @Named("ip_version")
   private Integer ipVersion;
   private String cidr;
   @Named("enable_dhcp")
   private Boolean enableDhcp;
   @Named("dns_nameservers")
   private ImmutableSet<String> dnsNameServers;
   @Named("host_routes")
   private ImmutableSet<HostRoute> hostRoutes;
   @Named("ipv6_address_mode")
   private IPv6DHCPMode ipv6AddressMode;
   @Named("ipv6_ra_mode")
   private IPv6DHCPMode ipv6RaMode;

   @ConstructorProperties({"id", "name", "network_id", "tenant_id", "allocation_pools", "gateway_ip", "ip_version",
         "cidr", "enable_dhcp", "dns_nameservers", "host_routes", "ipv6_address_mode", "ipv6_ra_mode"})
   private Subnet(String id, String name, String networkId, String tenantId, ImmutableSet<AllocationPool> allocationPools,
         String gatewayIp, Integer ipVersion, String cidr, Boolean enableDhcp, ImmutableSet<String> dnsNameServers, ImmutableSet<HostRoute> hostRoutes,
         IPv6DHCPMode ipv6AddressMode, IPv6DHCPMode ipv6RaMode) {
      this.id = id;
      this.name = name;
      this.networkId = networkId;
      this.tenantId = tenantId;
      this.allocationPools = allocationPools;
      this.gatewayIp = gatewayIp;
      this.ipVersion = ipVersion;
      this.cidr = cidr;
      this.enableDhcp = enableDhcp;
      this.dnsNameServers = dnsNameServers;
      this.hostRoutes = hostRoutes;
      this.ipv6AddressMode = ipv6AddressMode;
      this.ipv6RaMode = ipv6RaMode;
   }

   /**
    * Default constructor.
    */
   private Subnet() {}

   /**
    * Copy constructor
    * @param subnet
    */
   private Subnet(Subnet subnet) {
      this(subnet.id,
            subnet.name,
            subnet.networkId,
            subnet.tenantId,
            subnet.allocationPools,
            subnet.gatewayIp,
            subnet.ipVersion,
            subnet.cidr,
            subnet.enableDhcp,
            subnet.dnsNameServers,
            subnet.hostRoutes,
            subnet.ipv6AddressMode,
            subnet.ipv6RaMode);
   }

   /**
    * @return the id of the subnet
    */
   @Nullable
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of the subnet
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the id of the network this subnet is associated with.
    */
   @Nullable
   public String getNetworkId() {
      return networkId;
   }

   /**
    * @return the id of the tenant where this entity is associated with.
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the sub-ranges of CIDR available for dynamic allocation to ports.
    */
   @Nullable
   public ImmutableSet<AllocationPool> getAllocationPools() {
      return allocationPools;
   }

   /**
    * @return the default gateway used by devices in this subnet.
    */
   @Nullable
   public String getGatewayIp() {
      return gatewayIp;
   }

   /**
    * @return the IP version used by this subnet.
    */
   @Nullable
   public Integer getIpVersion() {
      return ipVersion;
   }

   /**
    * @return the CIDR representing the IP range for this subnet, based on IP version.
    */
   @Nullable
   public String getCidr() {
      return cidr;
   }

   /**
    * @return true if DHCP is enabled for this subnet, false if not.
    */
   @Nullable
   public Boolean getEnableDhcp() {
      return enableDhcp;
   }

   /**
    * @return Configurable maximum amount of name servers per subnet. The default is 5.
    */
   @Nullable
   public ImmutableSet<String> getDnsNameservers() {
      return dnsNameServers;
   }

   /**
    * @return Configurable maximum amount of routes per subnet. The default is 20.
    */
   @Nullable
   public ImmutableSet<HostRoute> getHostRoutes() {
      return hostRoutes;
   }

   /**
    * @return The IP v6 Address Mode.
    */
   @Nullable
   public IPv6DHCPMode getIPv6AddressMode() {
      return ipv6AddressMode;
   }

   /**
    * @return The IP v6 Router Advertisement mode.
    */
   @Nullable
   public IPv6DHCPMode getIPv6RAMode() {
      return ipv6RaMode;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, networkId, tenantId, allocationPools, gatewayIp,
            ipVersion, cidr, enableDhcp, dnsNameServers, hostRoutes,
            ipv6AddressMode, ipv6RaMode);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      Subnet that = (Subnet) o;

      return Objects.equal(this.id, that.id) &&
            Objects.equal(this.name, that.name) &&
            Objects.equal(this.networkId, that.networkId) &&
            Objects.equal(this.tenantId, that.tenantId) &&
            Objects.equal(this.allocationPools, that.allocationPools) &&
            Objects.equal(this.gatewayIp, that.gatewayIp) &&
            Objects.equal(this.ipVersion, that.ipVersion) &&
            Objects.equal(this.cidr, that.cidr) &&
            Objects.equal(this.enableDhcp, that.enableDhcp) &&
            Objects.equal(this.dnsNameServers, that.dnsNameServers) &&
            Objects.equal(this.hostRoutes, that.hostRoutes) &&
            Objects.equal(this.ipv6AddressMode, that.ipv6AddressMode) &&
            Objects.equal(this.ipv6RaMode, that.ipv6RaMode);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
            .add("id", id)
            .add("name", name)
            .add("networkId", networkId)
            .add("tenantId", tenantId)
            .add("allocationPools", allocationPools)
            .add("gatewayIp", gatewayIp)
            .add("ipVersion", ipVersion)
            .add("cidr", cidr)
            .add("enableDhcp", enableDhcp)
            .add("dnsNameServers", dnsNameServers)
            .add("hostRoutes", hostRoutes)
            .add("ipv6AddressMode", ipv6AddressMode)
            .add("ipv6RaMode", ipv6RaMode)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow
    */

   /**
    * @return the Builder for creating a new Router
    */
   public static CreateBuilder createBuilder(String networkId, String cidr) {
      return new CreateBuilder(networkId, cidr);
   }

   /**
    * @return the Builder for updating a Router
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected Subnet subnet;

      /**
       * No-parameters constructor used when updating.
       */
      private Builder() {
         subnet = new Subnet();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * @see Subnet#getName()
       */
      public ParameterizedBuilderType name(String name) {
         subnet.name = name;
         return self();
      }

      /**
       * @see Subnet#getNetworkId()
       */
      public ParameterizedBuilderType networkId(String networkId) {
         subnet.networkId = networkId;
         return self();
      }

      /**
       * Only administrators can specify a tenant ID that is not their own.
       * As it is optional, this is usually omitted in requests.
       * @see Subnet#getTenantId()
       */
      public ParameterizedBuilderType tenantId(String tenantId) {
         subnet.tenantId = tenantId;
         return self();
      }

      /**
       * @see Subnet#getAllocationPools()
       */
      public ParameterizedBuilderType allocationPools(Collection<AllocationPool> allocationPools) {
         subnet.allocationPools = ImmutableSet.copyOf(allocationPools);
         return self();
      }

      /**
       * @see Subnet#getGatewayIp()
       */
      public ParameterizedBuilderType gatewayIp(String gatewayIp) {
         subnet.gatewayIp = gatewayIp;
         return self();
      }

      /**
       * @see Subnet#getIpVersion()
       */
      public ParameterizedBuilderType ipVersion(int ipVersion) {
         subnet.ipVersion = ipVersion;
         return self();
      }

      /**
       * @see Subnet#getCidr()
       */
      public ParameterizedBuilderType cidr(String cidr) {
         subnet.cidr = cidr;
         return self();
      }

      /**
       * @see Subnet#getEnableDhcp()
       */
      public ParameterizedBuilderType enableDhcp(Boolean enableDhcp) {
         subnet.enableDhcp = enableDhcp;
         return self();
      }

      /**
       * @see Subnet#getDnsNameservers()
       */
      public ParameterizedBuilderType dnsNameServers(ImmutableSet<String> dnsNameServers) {
         subnet.dnsNameServers = dnsNameServers;
         return self();
      }

      /**
       * @see Subnet#getHostRoutes()
       */
      public ParameterizedBuilderType hostRoutes(ImmutableSet<HostRoute> hostRoutes) {
         subnet.hostRoutes = hostRoutes;
         return self();
      }

      /**
       * @see Subnet#getIPv6RAMode()
       */
      public ParameterizedBuilderType ipv6RaMode(IPv6DHCPMode ipv6RaMode) {
         subnet.ipv6RaMode = ipv6RaMode;
         return self();
      }

      /**
       * @see Subnet#getIPv6AddressMode()
       */
      public ParameterizedBuilderType ipv6AddressMode(IPv6DHCPMode ipv6AddressMode) {
         subnet.ipv6AddressMode = ipv6AddressMode;
         return self();
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       *
       * Supply required properties for creating a Builder
       */
      private CreateBuilder(String networkId, String cidr) {
         subnet.networkId = networkId;
         subnet.cidr = cidr;
      }

      /**
       * @return a CreateSubnet constructed with this Builder.
       */
      public CreateSubnet build() {
         return new CreateSubnet(subnet);
      }

      protected CreateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class UpdateBuilder extends Builder<UpdateBuilder> {
      /**
       * Supply required properties for updating a Builder
       */
      private UpdateBuilder() {
      }

      /**
       * @return a UpdateSubnet constructed with this Builder.
       */
      public UpdateSubnet build() {
         return new UpdateSubnet(subnet);
      }

      protected UpdateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreateSubnet extends Subnet {
      /**
       * Copy constructor
       */
      private CreateSubnet(Subnet subnet) {
         super(subnet);
         checkNotNull(subnet.networkId, "networkId should not be null");
         checkNotNull(subnet.cidr, "cidr should not be null");
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdateSubnet extends Subnet {
      /**
       * Copy constructor
       */
      private UpdateSubnet(Subnet subnet) {
         super(subnet);
      }
   }
}
