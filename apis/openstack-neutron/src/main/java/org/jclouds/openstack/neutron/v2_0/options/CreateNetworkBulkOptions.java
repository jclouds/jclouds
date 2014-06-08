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
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions.CreateNetworkRequest;

public class CreateNetworkBulkOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCreateNetworkOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected List<BulkNetwork> networks;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateNetworkBulkOptions#getNetworks()
       */
      public T networks(Collection<BulkNetwork> networks) {
         this.networks = ImmutableList.copyOf(networks);
         return self();
      }

      public CreateNetworkBulkOptions build() {
         return new CreateNetworkBulkOptions(networks);
      }

      public T fromCreateNetworkOptions(CreateNetworkBulkOptions in) {
         return this.networks(in.getNetworks());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final List<BulkNetwork> networks;

   protected CreateNetworkBulkOptions() {
      this.networks = Lists.newArrayList();
   }

   public CreateNetworkBulkOptions(List<BulkNetwork> networks) {
      this.networks = networks != null ? ImmutableList.copyOf(networks) : Lists.<BulkNetwork>newArrayList();
   }

   /**
    * @return the list of networks to create
    */
   public List<BulkNetwork> getNetworks() {
      return networks;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      List<CreateNetworkRequest> createNetworkRequests = Lists.newArrayList();

      for (BulkNetwork network : this.networks) {
         if (network.getNetworkType() != null) {
            //Validations for each NetworkType
            if (network.getNetworkType() == NetworkType.FLAT) {
               checkNotNull(network.getPhysicalNetworkName(), "physicalNetworkName must be present when networkType=FLAT");
            } else if (network.getNetworkType() == NetworkType.VLAN) {
               checkNotNull(network.getPhysicalNetworkName(), "physicalNetworkName must be present when networkType=VLAN");
               checkNotNull(network.getSegmentationId(), "segmentationId must be present when networkType=VLAN");
            } else if (network.getNetworkType() == NetworkType.GRE) {
               checkNotNull(network.getSegmentationId(), "segmentationId must be present when NetworkType=GRE");
            }
         }

         CreateNetworkRequest createNetworkRequest = new CreateNetworkRequest();
         if (network.getName() != null)
            createNetworkRequest.name = network.getName();
         if (network.getAdminStateUp() != null)
            createNetworkRequest.admin_state_up = network.getAdminStateUp();
         if (network.getExternal() != null)
            createNetworkRequest.external = network.getExternal();
         if (network.getNetworkType() != null)
            createNetworkRequest.networkType = network.getNetworkType().getValue();
         if (network.getPhysicalNetworkName() != null && (network.getNetworkType() == NetworkType.FLAT || network.getNetworkType() == NetworkType.VLAN))
            createNetworkRequest.physicalNetworkName = network.getPhysicalNetworkName();
         if (network.getSegmentationId() != null && (network.getNetworkType() == NetworkType.VLAN || network.getNetworkType() == NetworkType.GRE))
            createNetworkRequest.segmentationId = network.getSegmentationId();

         createNetworkRequests.add(createNetworkRequest);
      }

      return bindToRequest(request, ImmutableMap.of("networks", createNetworkRequests));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
