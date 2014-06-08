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
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Map;

public class UpdateNetworkOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUpdateNetworkOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected Boolean adminStateUp;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdateNetworkOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdateNetworkOptions#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      public UpdateNetworkOptions build() {
         return new UpdateNetworkOptions(name, adminStateUp);
      }

      public T fromUpdateNetworkOptions(UpdateNetworkOptions options) {
         return this.name(options.getName()).adminStateUp(options.getAdminStateUp());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private static class UpdateNetworkRequest {
      protected String name;
      protected Boolean admin_state_up;
   }

   private final String name;
   private final Boolean adminStateUp;

   protected UpdateNetworkOptions() {
      this.name = null;
      this.adminStateUp = null;
   }

   public UpdateNetworkOptions(String name, Boolean adminStateUp) {
      this.name = name;
      this.adminStateUp = adminStateUp;
   }

   /**
    * @return the new name for the network
    */
   public String getName() {
      return name;
   }

   /**
    * @return the new administrative state for the network. If false, the network does not forward packets.
    */
   public boolean getAdminStateUp() {
      return adminStateUp;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      UpdateNetworkRequest updateNetworkRequest = new UpdateNetworkRequest();

      if (this.name != null)
         updateNetworkRequest.name = this.name;
      if (this.adminStateUp != null)
         updateNetworkRequest.admin_state_up = this.adminStateUp;

      return bindToRequest(request, ImmutableMap.of("network", updateNetworkRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
