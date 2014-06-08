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

package org.jclouds.openstack.neutron.v2_0.options;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.neutron.v2_0.domain.AllocationPool;
import org.jclouds.openstack.neutron.v2_0.domain.HostRoute;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreateSubnetOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCreateSubnetOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected Set<AllocationPool> allocationPools;
      protected String gatewayIp;
      protected Boolean enableDhcp;
      protected Set<String> dnsNameServers;
      protected Set<HostRoute> hostRoutes;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions#getAllocationPools()
       */
      public T allocationPools(Collection<AllocationPool> allocationPools) {
         this.allocationPools = ImmutableSet.copyOf(allocationPools);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions#getGatewayIp()
       */
      public T gatewayIp(String gatewayIp) {
         this.gatewayIp = gatewayIp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions#getEnableDhcp()
       */
      public T enableDhcp(Boolean enableDhcp) {
         this.enableDhcp = enableDhcp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions#getDnsNameServers()
       */
      public T dnsNameServers(Collection<String> dnsNameServers) {
         this.dnsNameServers = ImmutableSet.copyOf(dnsNameServers);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions#getHostRoutes()
       */
      public T hostRoutes(Collection<HostRoute> hostRoutes) {
         this.hostRoutes = ImmutableSet.copyOf(hostRoutes);
         return self();
      }

      public CreateSubnetOptions build() {
         return new CreateSubnetOptions(name, allocationPools, gatewayIp, enableDhcp, dnsNameServers, hostRoutes);
      }

      public T fromCreateSubnetOptions(CreateSubnetOptions in) {
         return this.name(in.getName())
            .allocationPools(in.getAllocationPools())
            .gatewayIp(in.getGatewayIp())
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

   protected static class CreateSubnetRequest {
      protected String network_id;
      protected Integer ip_version;
      protected String cidr;
      protected String name;
      protected Set<AllocationPool> allocation_pools;
      protected String gateway_ip;
      protected Boolean enable_dhcp;
      protected Set<String> dns_nameservers;
      protected Set<HostRoute> host_routes;

      protected CreateSubnetRequest(String networkId, Integer ipVersion, String cidr) {
         this.network_id = networkId;
         this.ip_version = ipVersion;
         this.cidr = cidr;
      }

      protected static class HostRoute {
         protected String destination;
         protected String nexthop;
      }
   }

   private final String name;
   private final Set<AllocationPool> allocationPools;
   private final String gatewayIp;
   private final Boolean enableDhcp;
   private final Set<String> dnsNameServers;
   private final Set<HostRoute> hostRoutes;

   protected CreateSubnetOptions() {
      this.name = null;
      this.allocationPools = Sets.newHashSet();
      this.gatewayIp = null;
      this.enableDhcp = null;
      this.dnsNameServers = Sets.newHashSet();
      this.hostRoutes = Sets.newHashSet();
   }

   public CreateSubnetOptions(String name, Set<AllocationPool> allocationPools, String gatewayIp,
                              Boolean enableDhcp, Set<String> dnsNameServers, Set<HostRoute> hostRoutes) {
      this.name = name;
      this.allocationPools = allocationPools != null ? ImmutableSet.copyOf(allocationPools) : Sets.<AllocationPool>newHashSet();
      this.gatewayIp = gatewayIp;
      this.enableDhcp = enableDhcp;
      this.dnsNameServers = dnsNameServers != null ? ImmutableSet.copyOf(dnsNameServers) : Sets.<String>newHashSet();
      this.hostRoutes = hostRoutes != null ? ImmutableSet.copyOf(hostRoutes) : Sets.<HostRoute>newHashSet();
   }

   /**
    * @return the name for the subnet
    */
   public String getName() {
      return name;
   }

   /**
    * @return the sub-ranges of cidr which will be available for dynamic allocation to ports
    */
   public Set<AllocationPool> getAllocationPools() {
      return allocationPools;
   }

   /**
    * @return the default gateway which will be used by devices in this subnet
    */
   public String getGatewayIp() {
      return gatewayIp;
   }

   /**
    * @return true to enable DHCP, false to disable
    */
   public Boolean getEnableDhcp() {
      return enableDhcp;
   }

   /**
    * @return the set of DNS name servers to be used by hosts in this subnet.
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
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      CreateSubnetRequest createSubnetRequest =
         new CreateSubnetRequest(
            checkNotNull(postParams.get("network_id"), "networkId not present").toString(),
            Integer.parseInt(checkNotNull(postParams.get("ip_version"), "ipVersion not present").toString()),
            checkNotNull(postParams.get("cidr"), "cidr not present").toString()
         );

      if (this.name != null)
         createSubnetRequest.name = this.name;
      if (!this.allocationPools.isEmpty())
         createSubnetRequest.allocation_pools = this.allocationPools;
      if (this.gatewayIp != null)
         createSubnetRequest.gateway_ip = this.gatewayIp;
      if (this.enableDhcp != null)
         createSubnetRequest.enable_dhcp = this.enableDhcp;
      if (!this.dnsNameServers.isEmpty())
         createSubnetRequest.dns_nameservers = this.dnsNameServers;
      if (!this.hostRoutes.isEmpty()) {
         createSubnetRequest.host_routes = Sets.newHashSet();
         for (HostRoute hostRoute : this.hostRoutes) {
            CreateSubnetRequest.HostRoute requestHostRoute = new CreateSubnetRequest.HostRoute();
            requestHostRoute.destination = hostRoute.getDestinationCidr();
            requestHostRoute.nexthop = hostRoute.getNextHop();
            createSubnetRequest.host_routes.add(requestHostRoute);
         }
      }

      return bindToRequest(request, ImmutableMap.of("subnet", createSubnetRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
