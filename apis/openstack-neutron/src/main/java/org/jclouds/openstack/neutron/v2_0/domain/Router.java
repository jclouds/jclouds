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

package org.jclouds.openstack.neutron.v2_0.domain;

import com.google.common.base.Objects;

import java.beans.ConstructorProperties;

/**
 * A Neutron Router
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/router_ext_concepts.html">api doc</a>
 */
public class Router extends ReferenceWithName {

   private final Boolean adminStateUp;
   private final State state;
   private final ExternalGatewayInfo externalGatewayInfo;

   @ConstructorProperties({
      "id", "tenant_id", "name", "admin_state_up", "status", "external_gateway_info"
   })
   protected Router(String id, String tenantId, String name, Boolean adminStateUp, State state, ExternalGatewayInfo externalGatewayInfo) {
      super(id, tenantId, name);
      this.adminStateUp = adminStateUp;
      this.state = state;
      this.externalGatewayInfo = externalGatewayInfo;
   }

   /**
    * @return the administrative state of the router
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the current state of the router
    */
   public State getState() {
      return state;
   }

   /**
    * @return the information on external gateway for the router
    */
   public ExternalGatewayInfo getExternalGatewayInfo() {
      return externalGatewayInfo;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), adminStateUp, state, externalGatewayInfo);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Router that = Router.class.cast(obj);
      return super.equals(obj)
         && Objects.equal(this.adminStateUp, that.adminStateUp)
         && Objects.equal(this.state, that.state)
         && Objects.equal(this.externalGatewayInfo, that.externalGatewayInfo);
   }

   protected Objects.ToStringHelper string() {
      return super.string()
         .add("adminStateUp", adminStateUp).add("state", state).add("externalGatewayInfo", externalGatewayInfo != null ? externalGatewayInfo.toString() : "");
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromRouter(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends ReferenceWithName.Builder<T> {

      protected Boolean adminStateUp;
      protected State state;
      protected ExternalGatewayInfo externalGatewayInfo;

      /**
       * @see Router#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see Router#getState()
       */
      public T state(State state) {
         this.state = state;
         return self();
      }

      /**
       * @see Router#getExternalGatewayInfo()
       */
      public T externalGatewayInfo(ExternalGatewayInfo externalGatewayInfo) {
         this.externalGatewayInfo = externalGatewayInfo;
         return self();
      }

      public Router build() {
         return new Router(id, tenantId, name, adminStateUp, state, externalGatewayInfo);
      }

      public T fromRouter(Router in) {
         return super.fromReference(in)
            .adminStateUp(in.getAdminStateUp())
            .state(in.getState())
            .externalGatewayInfo(in.getExternalGatewayInfo());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
