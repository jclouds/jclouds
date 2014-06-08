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

public class CreateRouterOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCreateRouterOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected Boolean adminStateUp;
      protected ExternalGatewayInfo externalGatewayInfo;

      /**
       * @see CreateRouterOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see CreateRouterOptions#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see CreateRouterOptions#getExternalGatewayInfo()
       */
      public T externalGatewayInfo(ExternalGatewayInfo externalGatewayInfo) {
         this.externalGatewayInfo = externalGatewayInfo;
         return self();
      }

      public CreateRouterOptions build() {
         return new CreateRouterOptions(name, adminStateUp, externalGatewayInfo);
      }

      public T fromCreateRouterOptions(CreateRouterOptions options) {
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

   protected static class CreateRouterRequest {
      protected String name;
      protected Boolean admin_state_up;
      protected ExternalGatewayInfo external_gateway_info;

      protected CreateRouterRequest() {
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

   protected CreateRouterOptions() {
      this.name = null;
      this.adminStateUp = null;
      this.externalGatewayInfo = null;
   }

   public CreateRouterOptions(String name, Boolean adminStateUp, ExternalGatewayInfo externalGatewayInfo) {
      this.name = name;
      this.adminStateUp = adminStateUp;
      this.externalGatewayInfo = externalGatewayInfo;
   }

   /**
    * @return the name for the router
    */
   public String getName() {
      return name;
   }

   /**
    * @return the administrative state of the router
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the external gateway info for the router
    */
   public ExternalGatewayInfo getExternalGatewayInfo() {
      return externalGatewayInfo;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      CreateRouterRequest createRouterRequest = new CreateRouterRequest();

      if (this.name != null)
         createRouterRequest.name = this.name;
      if (this.adminStateUp != null)
         createRouterRequest.admin_state_up = this.adminStateUp;
      if (this.externalGatewayInfo != null)
         createRouterRequest.external_gateway_info = new CreateRouterRequest.ExternalGatewayInfo(this.externalGatewayInfo.getNetworkId());

      return bindToRequest(request, ImmutableMap.of("router", createRouterRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
