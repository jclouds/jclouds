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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet;
import org.jclouds.openstack.neutron.v2_0.domain.HostRoute;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreateSubnetBulkOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCreateSubnetBulkOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected List<BulkSubnet> subnets;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateSubnetBulkOptions#getSubnets()
       */
      public T subnets(Collection<BulkSubnet> subnets) {
         this.subnets = ImmutableList.copyOf(subnets);
         return self();
      }

      public CreateSubnetBulkOptions build() {
         return new CreateSubnetBulkOptions(this.subnets);
      }

      public T fromCreateSubnetBulkOptions(CreateSubnetBulkOptions in) {
         return this.subnets(in.getSubnets());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final List<BulkSubnet> subnets;

   protected CreateSubnetBulkOptions() {
      this.subnets = Lists.newArrayList();
   }

   public CreateSubnetBulkOptions(List<BulkSubnet> subnets) {
      this.subnets = subnets != null ? ImmutableList.copyOf(subnets) : Lists.<BulkSubnet>newArrayList();
   }

   /**
    * @return The list of subnets to create
    */
   public List<BulkSubnet> getSubnets() {
      return subnets;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      List<CreateSubnetOptions.CreateSubnetRequest> createSubnetRequests = Lists.newArrayList();

      for (BulkSubnet subnet : subnets) {
         CreateSubnetOptions.CreateSubnetRequest createSubnetRequest =
            new CreateSubnetOptions.CreateSubnetRequest(
               checkNotNull(subnet.getNetworkId(), "network id parameter not present"),
               checkNotNull(subnet.getIpVersion(), "ip version parameter not present"),
               checkNotNull(subnet.getCidr(), "cidr parameter not present")
            );

         if (subnet.getName() != null)
            createSubnetRequest.name = subnet.getName();
         if (!subnet.getAllocationPools().isEmpty())
            createSubnetRequest.allocation_pools = subnet.getAllocationPools();
         if (subnet.getGatewayIp() != null)
            createSubnetRequest.gateway_ip = subnet.getGatewayIp();
         if (subnet.getEnableDhcp() != null)
            createSubnetRequest.enable_dhcp = subnet.getEnableDhcp();
         if (!subnet.getDnsNameServers().isEmpty())
            createSubnetRequest.dns_nameservers = subnet.getDnsNameServers();
         if (!subnet.getHostRoutes().isEmpty()) {
            createSubnetRequest.host_routes = Sets.newHashSet();
            for (HostRoute hostRoute : subnet.getHostRoutes()) {
               CreateSubnetOptions.CreateSubnetRequest.HostRoute requestHostRoute = new CreateSubnetOptions.CreateSubnetRequest.HostRoute();
               requestHostRoute.destination = hostRoute.getDestinationCidr();
               requestHostRoute.nexthop = hostRoute.getNextHop();
               createSubnetRequest.host_routes.add(requestHostRoute);
            }
         }

         createSubnetRequests.add(createSubnetRequest);
      }

      return bindToRequest(request, ImmutableMap.of("subnets", createSubnetRequests));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
