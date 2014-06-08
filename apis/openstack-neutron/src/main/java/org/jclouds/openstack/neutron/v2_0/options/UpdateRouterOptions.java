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
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.neutron.v2_0.domain.ExternalGatewayInfo;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Map;

public class UpdateRouterOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUpdateRouterOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected Boolean adminStateUp;
      protected ExternalGatewayInfo externalGatewayInfo;

      /**
       * @see UpdateRouterOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see UpdateRouterOptions#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see UpdateRouterOptions#getExternalGatewayInfo()
       */
      public T externalGatewayInfo(ExternalGatewayInfo externalGatewayInfo) {
         this.externalGatewayInfo = externalGatewayInfo;
         return self();
      }

      public UpdateRouterOptions build() {
         return new UpdateRouterOptions(name, adminStateUp, externalGatewayInfo);
      }

      public T fromUpdateRouterOptions(UpdateRouterOptions options) {
         return this.name(options.getName())
            .adminStateUp(options.getAdminStateUp())
            .externalGatewayInfo(options.getExternalGatewayInfo());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected static class UpdateRouterRequest {
      protected String name;
      protected Boolean admin_state_up;
      protected ExternalGatewayInfo external_gateway_info;

      protected UpdateRouterRequest() {
      }

      protected static final class ExternalGatewayInfo {
         protected String network_id;

         protected ExternalGatewayInfo(String network_id) {
            this.network_id = network_id;
         }
      }
   }

   protected String name;
   protected Boolean adminStateUp;
   protected ExternalGatewayInfo externalGatewayInfo;

   protected UpdateRouterOptions() {
      this.name = null;
      this.adminStateUp = null;
      this.externalGatewayInfo = null;
   }

   public UpdateRouterOptions(String name, Boolean adminStateUp, ExternalGatewayInfo externalGatewayInfo) {
      this.name = name;
      this.adminStateUp = adminStateUp;
      this.externalGatewayInfo = externalGatewayInfo;
   }

   /**
    * @return the new name for the router
    */
   public String getName() {
      return name;
   }

   /**
    * @return the new administrative state for the router
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the new information on external gateway for the router
    */
   public ExternalGatewayInfo getExternalGatewayInfo() {
      return externalGatewayInfo;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      UpdateRouterRequest updateRouterRequest = new UpdateRouterRequest();

      if (this.name != null)
         updateRouterRequest.name = this.name;
      if (this.adminStateUp != null)
         updateRouterRequest.admin_state_up = this.adminStateUp;
      if (this.externalGatewayInfo != null)
         updateRouterRequest.external_gateway_info = new UpdateRouterRequest.ExternalGatewayInfo(this.externalGatewayInfo.getNetworkId());

      return bindToRequest(request, ImmutableMap.of("router", updateRouterRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
