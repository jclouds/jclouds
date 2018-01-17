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

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * A Neutron Floating IP
 *
 */
public class FloatingIP {

   private String id;
   @Named("router_id")
   private String routerId;
   @Named("tenant_id")
   private String tenantId;
   // Only mandatory attribute when creating
   @Named("floating_network_id")
   private String floatingNetworkId;
   @Named("fixed_ip_address")
   private String fixedIpAddress;
   @Named("floating_ip_address")
   private String floatingIpAddress;
   @Named("port_id")
   private String portId;

   /**
    * Deserialization constructor
    */
   @ConstructorProperties({"id", "router_id", "tenant_id", "floating_network_id", "fixed_ip_address",
         "floating_ip_address", "port_id"})
   private FloatingIP(String id, String routerId, String tenantId, String floatingNetworkId, String fixedIpAddress,
         String floatingIpAddress, String portId) {
      this.id = id;
      this.routerId = routerId;
      this.tenantId = tenantId;
      this.floatingNetworkId = floatingNetworkId;
      this.fixedIpAddress = fixedIpAddress;
      this.floatingIpAddress = floatingIpAddress;
      this.portId = portId;
   }

   private FloatingIP() {}

   /**
    * @param floatingIP The floating IP to copy from
    */
   private FloatingIP(FloatingIP floatingIP) {
      this(floatingIP.id, floatingIP.routerId, floatingIP.tenantId, floatingIP.floatingNetworkId,
            floatingIP.fixedIpAddress, floatingIP.floatingIpAddress, floatingIP.portId);
   }

   /**
    * @return the id of the floating IP
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the router id of this floating IP
    */
   @Nullable
   public String getRouterId() {
      return routerId;
   }

   /**
    * @return the tenant id of the Floating IP
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the floating network id for this floating IP
    */
   @Nullable
   public String getFloatingNetworkId() {
      return floatingNetworkId;
   }

   /**
    * @return the fixed IP address for this floating IP
    */
   @Nullable
   public String getFixedIpAddress() {
      return fixedIpAddress;
   }

   /**
    * @return the floating IP address for this floating IP
    */
   @Nullable
   public String getFloatingIpAddress() {
      return floatingIpAddress;
   }

   /**
    * @return the port id for this floating IP
    */
   @Nullable
   public String getPortId() {
      return portId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      FloatingIP that = (FloatingIP) o;

      return Objects.equal(this.id, that.id) &&
            Objects.equal(this.routerId, that.routerId) &&
            Objects.equal(this.tenantId, that.tenantId) &&
            Objects.equal(this.floatingNetworkId, that.floatingNetworkId) &&
            Objects.equal(this.fixedIpAddress, that.fixedIpAddress) &&
            Objects.equal(this.floatingIpAddress, that.floatingIpAddress) &&
            Objects.equal(this.portId, that.portId);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, routerId, tenantId, floatingNetworkId, fixedIpAddress, floatingIpAddress,
            portId);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("routerId", routerId)
            .add("tenantId", tenantId)
            .add("floatingNetworkId", floatingNetworkId)
            .add("fixedIpAddress", fixedIpAddress)
            .add("floatingIpAddress", floatingIpAddress)
            .add("portId", portId)
            .toString();
   }

   /**
    * @return the Builder for creating a new floating IP
    */
   public static CreateBuilder createBuilder(String floatingNetworkId) {
      return new CreateBuilder(floatingNetworkId);
   }

   /**
    * @return the Builder for updating a floating IP
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected FloatingIP floatingIP;

      /**
       * No-parameters constructor.
       * */
      private Builder() {
         floatingIP = new FloatingIP();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provide the tenantId for this Floating IP. Admin-only.
       * When keystone is enabled, it is not mandatory to specify tenant_id for resources in create requests, as the
       * tenant identifier will be derived from the Authentication token. Please note that the default authorization
       * settings only allow administrative users to create resources on behalf of a different tenant.
       *
       * @return the Builder.
       * @see FloatingIP#getTenantId()
       */
      public ParameterizedBuilderType tenantId(String tenantId) {
         floatingIP.tenantId = tenantId;
         return self();
      }

      /**
       * Provides the floating network id for this Floating IP.
       * Mandatory when creating a floating IP.
       * Floating IPs can only be created on external networks. If the network specified by floating_network_id is not
       * external (for example, router:external=false), a 400 error is returned.
       *
       * @return the Builder.
       * @see FloatingIP#getFloatingNetworkId() ()
       */
      public ParameterizedBuilderType floatingNetworkId(String floatingNetworkId) {
         floatingIP.floatingNetworkId = floatingNetworkId;
         return self();
      }

      /**
       * Provides the fixed ip address for this Floating IP.
       * As an OpenStack Networking port might be associated with multiple IP addresses, the particular IP address to
       * associate with the floating IP can be specified using the fixed_ip_address request body parameter. The default
       * logic of this operation is to associate the floating IP with a single IP address configured on a port; hence,
       * if a port has multiple IP addresses, it is mandatory to specify the fixed_ip_address attribute. If an invalid
       * IP address is specified in fixed_ip_address a 400 error will be returned.
       *
       * @return the Builder.
       * @see FloatingIP#getFixedIpAddress()
       */
      public ParameterizedBuilderType fixedIpAddress(String fixedIpAddress) {
         floatingIP.fixedIpAddress = fixedIpAddress;
         return self();
      }

      /**
       * Provides the port id for this Floating IP.
       * Users can associate the floating IP with an internal port using the port_id attribute in the request body.
       * If an invalid port identifier is specified, a 404 error will be returned. The internal OpenStack Networking
       * port associated with the Floating IP must have at least an IP address configured, otherwise a 400 error will
       * be returned.
       *
       * @return the Builder.
       * @see FloatingIP#getPortId()
       */
      public ParameterizedBuilderType portId(String portId) {
         floatingIP.portId = portId;
         return self();
      }
   }

   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a Builder
       */
      private CreateBuilder(String floatingNetworkId) {
         floatingIP.floatingNetworkId = floatingNetworkId;
      }

      /**
       * @return a CreateFloatingIP constructed with this Builder.
       */
      public CreateFloatingIP build() {
         return new CreateFloatingIP(floatingIP);
      }

      protected CreateBuilder self() {
         return this;
      }
   }

   public static class UpdateBuilder extends Builder<UpdateBuilder> {
      /**
       * Supply required properties for updating a Builder
       */
      private UpdateBuilder() {
      }

      /**
       * Provide the floating ip address for this Floating IP.
       * An address for the floating ip will be automatically allocated, unless the floating_ip_address attribute is
       * specified in the request body. If the requested floating IP address does not fall in the external network's
       * subnet range, a 400 error will be returned. If the requested floating IP address is already in use, a 409
       * error code will be returned.
       *
       * @return the Builder.
       * @see FloatingIP#getFloatingIpAddress()
       */
      public UpdateBuilder floatingIpAddress(String floatingIpAddress) {
         floatingIP.floatingIpAddress = floatingIpAddress;
         return this;
      }

      /**
       * @return a UpdateFloatingIP constructed with this Builder.
       */
      public UpdateFloatingIP build() {
         return new UpdateFloatingIP(floatingIP);
      }

      protected UpdateBuilder self() {
         return this;
      }
   }

   public static class CreateFloatingIP extends FloatingIP {
      private CreateFloatingIP(FloatingIP floatingIP) {
         super(floatingIP);
      }
   }
   public static class UpdateFloatingIP extends FloatingIP {
      private UpdateFloatingIP(FloatingIP floatingIP) {
         super(floatingIP);
      }
   }
}
