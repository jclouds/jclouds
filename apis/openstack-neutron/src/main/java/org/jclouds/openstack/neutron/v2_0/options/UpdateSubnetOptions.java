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
import org.jclouds.openstack.neutron.v2_0.domain.HostRoute;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UpdateSubnetOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUpdateSubnetOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected String gatewayIp;
      protected Boolean enableDhcp;
      protected Set<String> dnsNameServers;
      protected Set<HostRoute> hostRoutes;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions#getGatewayIp()
       */
      public T gatewayIp(String gatewayIp) {
         this.gatewayIp = gatewayIp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions#getEnableDhcp()
       */
      public T enableDhcp(Boolean enableDhcp) {
         this.enableDhcp = enableDhcp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions#getDnsNameServers()
       */
      public T dnsNameServers(Collection<String> dnsNameServers) {
         this.dnsNameServers = ImmutableSet.copyOf(dnsNameServers);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions#getHostRoutes()
       */
      public T hostRoutes(Collection<HostRoute> hostRoutes) {
         this.hostRoutes = ImmutableSet.copyOf(hostRoutes);
         return self();
      }

      public UpdateSubnetOptions build() {
         return new UpdateSubnetOptions(name, gatewayIp, enableDhcp, dnsNameServers, hostRoutes);
      }

      public T fromUpdateSubnetOptions(UpdateSubnetOptions in) {
         return this.name(in.getName())
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

   private static class UpdateSubnetRequest {
      protected String name;
      protected String gateway_ip;
      protected Boolean enable_dhcp;
      protected Set<String> dns_nameservers;
      protected Set<HostRoute> host_routes;

      private static class HostRoute {
         protected String destination;
         protected String nexthop;
      }
   }

   private final String name;
   private final String gatewayIp;
   private final Boolean enableDhcp;
   private final Set<String> dnsNameServers;
   private final Set<HostRoute> hostRoutes;

   protected UpdateSubnetOptions() {
      this.name = null;
      this.gatewayIp = null;
      this.enableDhcp = null;
      this.dnsNameServers = Sets.newHashSet();
      this.hostRoutes = Sets.newHashSet();
   }

   public UpdateSubnetOptions(String name, String gatewayIp, Boolean enableDhcp, Set<String> dnsNameServers, Set<HostRoute> hostRoutes) {
      this.name = name;
      this.gatewayIp = gatewayIp;
      this.enableDhcp = enableDhcp;
      this.dnsNameServers = dnsNameServers != null ? ImmutableSet.copyOf(dnsNameServers) : Sets.<String>newHashSet();
      this.hostRoutes = hostRoutes != null ? ImmutableSet.copyOf(hostRoutes) : Sets.<HostRoute>newHashSet();
   }

   /**
    * @return the new name for the subnet
    */
   public String getName() {
      return name;
   }

   /**
    * @return the new default gateway used by devices in this subnet
    */
   public String getGatewayIp() {
      return gatewayIp;
   }

   /**
    * @return true if DHCP is enabled for this subnet, false if not
    */
   public Boolean getEnableDhcp() {
      return enableDhcp;
   }

   /**
    * @return the new set of DNS name servers used by hosts in this subnet
    */
   public Set<String> getDnsNameServers() {
      return dnsNameServers;
   }

   /**
    * @return the new set of routes that should be used by devices with IPs from this subnet
    */
   public Set<HostRoute> getHostRoutes() {
      return hostRoutes;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      UpdateSubnetRequest updateSubnetRequest = new UpdateSubnetRequest();

      if (this.name != null)
         updateSubnetRequest.name = this.name;
      if (this.gatewayIp != null)
         updateSubnetRequest.gateway_ip = this.gatewayIp;
      if (this.enableDhcp != null)
         updateSubnetRequest.enable_dhcp = this.enableDhcp;
      if (!this.dnsNameServers.isEmpty())
         updateSubnetRequest.dns_nameservers = this.dnsNameServers;
      if (!this.hostRoutes.isEmpty()) {
         updateSubnetRequest.host_routes = Sets.newHashSet();
         for (HostRoute hostRoute : this.hostRoutes) {
            UpdateSubnetRequest.HostRoute requestHostRoute = new UpdateSubnetRequest.HostRoute();
            requestHostRoute.destination = hostRoute.getDestinationCidr();
            requestHostRoute.nexthop = hostRoute.getNextHop();
            updateSubnetRequest.host_routes.add(requestHostRoute);
         }
      }

      return bindToRequest(request, ImmutableMap.of("subnet", updateSubnetRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
