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
import com.google.gson.annotations.SerializedName;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreateNetworkOptions implements MapBinder {

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

      protected String name;
      protected Boolean adminStateUp;
      protected Boolean external;
      protected NetworkType networkType;
      protected String physicalNetworkName;
      protected Integer segmentationId;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions#getExternal()
       */
      public T external(Boolean external) {
         this.external = external;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions#getNetworkType()
       */
      public T networkType(NetworkType networkType) {
         this.networkType = networkType;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions#getPhysicalNetworkName()
       */
      public T physicalNetworkName(String physicalNetworkName) {
         this.physicalNetworkName = physicalNetworkName;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions#getSegmentationId()
       */
      public T segmentationId(Integer segmentationId) {
         this.segmentationId = segmentationId;
         return self();
      }

      public CreateNetworkOptions build() {
         return new CreateNetworkOptions(name, adminStateUp, external, networkType, physicalNetworkName, segmentationId);
      }

      public T fromCreateNetworkOptions(CreateNetworkOptions in) {
         return this.name(in.getName())
            .adminStateUp(in.getAdminStateUp())
            .external(in.getExternal())
            .networkType(in.getNetworkType())
            .physicalNetworkName(in.getPhysicalNetworkName())
            .segmentationId(in.getSegmentationId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected static class CreateNetworkRequest {
      protected String name;
      protected Boolean admin_state_up;
      @SerializedName("router:external")
      protected Boolean external;
      @SerializedName("provider:network_type")
      protected String networkType;
      @SerializedName("provider:physical_network")
      protected String physicalNetworkName;
      @SerializedName("provider:segmentation_id")
      protected Integer segmentationId;
   }

   private final String name;
   private final Boolean adminStateUp;
   private final Boolean external;
   private final NetworkType networkType;
   private final String physicalNetworkName;
   private final Integer segmentationId;

   protected CreateNetworkOptions() {
      this.name = null;
      this.adminStateUp = null;
      this.external = null;
      this.networkType = null;
      this.physicalNetworkName = null;
      this.segmentationId = null;
   }

   public CreateNetworkOptions(String name, Boolean adminStateUp, Boolean external, NetworkType networkType, String physicalNetworkName, Integer segmentationId) {
      this.name = name;
      this.adminStateUp = adminStateUp;
      this.external = external;
      this.networkType = networkType;
      this.physicalNetworkName = physicalNetworkName;
      this.segmentationId = segmentationId;
   }

   /**
    * @return the name of the network
    */
   public String getName() {
      return name;
   }

   /**
    * @return the administrative state of network. If false, the network does not forward packets.
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return true if network is external, false if not
    */
   public Boolean getExternal() {
      return external;
   }

   /**
    * @return the type of the network
    */
   public NetworkType getNetworkType() {
      return networkType;
   }

   /**
    * @return the physical network name
    */
   public String getPhysicalNetworkName() {
      return physicalNetworkName;
   }

   /**
    * @return the segmentation id of the network
    */
   public Integer getSegmentationId() {
      return segmentationId;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      if (this.networkType != null) {
         //Validations for each NetworkType
         if (this.networkType == NetworkType.FLAT) {
            checkNotNull(this.physicalNetworkName, "physicalNetworkName must be present when networkType=FLAT");
         } else if (this.networkType == NetworkType.VLAN) {
            checkNotNull(this.physicalNetworkName, "physicalNetworkName must be present when networkType=VLAN");
            checkNotNull(this.segmentationId, "segmentationId must be present when networkType=VLAN");
         } else if (this.networkType == NetworkType.GRE) {
            checkNotNull(this.segmentationId, "segmentationId must be present when NetworkType=GRE");
         }
      }

      CreateNetworkRequest createNetworkRequest = new CreateNetworkRequest();
      if (this.name != null)
         createNetworkRequest.name = this.name;
      if (this.adminStateUp != null)
         createNetworkRequest.admin_state_up = this.adminStateUp;
      if (this.external != null)
         createNetworkRequest.external = this.external;
      if (this.networkType != null)
         createNetworkRequest.networkType = this.networkType.getValue();
      if (this.physicalNetworkName != null && (networkType == NetworkType.FLAT || networkType == NetworkType.VLAN))
         createNetworkRequest.physicalNetworkName = this.physicalNetworkName;
      if (this.segmentationId != null && (networkType == NetworkType.VLAN || networkType == NetworkType.GRE))
         createNetworkRequest.segmentationId = this.segmentationId;

      return bindToRequest(request, ImmutableMap.of("network", createNetworkRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
