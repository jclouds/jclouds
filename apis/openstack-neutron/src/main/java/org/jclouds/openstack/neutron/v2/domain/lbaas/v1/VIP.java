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
package org.jclouds.openstack.neutron.v2.domain.lbaas.v1;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * A Neutron LBaaS v1 VIP.
 */
public class VIP {

   // Mandatory attributes when creating
   @Named("tenant_id")
   private String tenantId;
   @Named("subnet_id")
   private String subnetId;
   private Protocol protocol;
   @Named("protocol_port")
   private Integer protocolPort;
   // Mandatory attributes that can be updated
   @Named("pool_id")
   private String poolId;
   // Optional attributes when creating
   private String address;
   // Optional attributes that can be updated
   private String name;
   private String description;
   @Named("session_persistence")
   private SessionPersistence sessionPersistence;
   @Named("connection_limit")
   private Integer connectionLimit;
   @Named("admin_state_up")
   private Boolean adminStateUp;
   // Read-only attributes
   private String id;
   @Named("port_id")
   private String portId;
   private LBaaSStatus status;
   @Named("status_description")
   private String statusDescription;

   /**
    * Deserialization constructor.
    */
   @ConstructorProperties({ "id", "tenant_id", "name", "description", "subnet_id", "address", "port_id", "protocol",
         "protocol_port", "pool_id", "session_persistence", "connection_limit", "admin_state_up", "status",
         "status_description" })
   private VIP(String id, String tenantId, String name, String description, String subnetId, String address,
         String portId, Protocol protocol, Integer protocolPort, String poolId, SessionPersistence sessionPersistence,
         Integer connectionLimit, Boolean adminStateUp, LBaaSStatus status, String statusDescription) {
      this.id = id;
      this.tenantId = tenantId;
      this.name = name;
      this.description = description;
      this.subnetId = subnetId;
      this.address = address;
      this.portId = portId;
      this.protocol = protocol;
      this.protocolPort = protocolPort;
      this.poolId = poolId;
      this.sessionPersistence = sessionPersistence;
      this.connectionLimit = connectionLimit;
      this.adminStateUp = adminStateUp;
      this.status = status;
      this.statusDescription = statusDescription;
   }

   /**
    * Default constructor.
    */
   private VIP() {
   }

   /**
    * Copy constructor.
    *
    * @param VIP the VIP to copy from.
    */
   private VIP(VIP vip) {
      this(vip.id, vip.tenantId, vip.name, vip.description, vip.subnetId, vip.address, vip.portId, vip.protocol,
            vip.protocolPort, vip.poolId, vip.sessionPersistence, vip.connectionLimit, vip.adminStateUp, vip.status,
            vip.statusDescription);
   }

   /**
    * @return the id of the VIP.
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the tenant id of the VIP.
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the name of the VIP.
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * @return the description of the VIP.
    */
   @Nullable
   public String getDescription() {
      return description;
   }

   /**
    * @return the subnet id for this VIP.
    */
   @Nullable
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * @return the address for this VIP.
    */
   @Nullable
   public String getAddress() {
      return address;
   }

   /**
    * @return the port id for this VIP.
    */
   @Nullable
   public String getPortId() {
      return portId;
   }

   /**
    * @return the protocol for this VIP.
    */
   @Nullable
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * @return the protocol port for this VIP.
    */
   @Nullable
   public Integer getProtocolPort() {
      return protocolPort;
   }

   /**
    * @return the pool id for this VIP.
    */
   @Nullable
   public String getPoolId() {
      return poolId;
   }

   /**
    * @return the session persistence for this VIP.
    */
   @Nullable
   public SessionPersistence getSessionPersistence() {
      return sessionPersistence;
   }

   /**
    * @return the connection limit for this VIP.
    */
   @Nullable
   public Integer getConnectionLimit() {
      return connectionLimit;
   }

   /**
    * @return the administrative state for this VIP.
    */
   @Nullable
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the status for this VIP.
    */
   @Nullable
   public LBaaSStatus getStatus() {
      return status;
   }

   /**
    * @return the status description for this VIP.
    */
   @Nullable
   public String getStatusDescription() {
      return statusDescription;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      VIP that = (VIP) o;

      return Objects.equal(this.id, that.id) && Objects.equal(this.tenantId, that.tenantId)
            && Objects.equal(this.name, that.name) && Objects.equal(this.description, that.description)
            && Objects.equal(this.subnetId, that.subnetId) && Objects.equal(this.address, that.address)
            && Objects.equal(this.portId, that.portId) && Objects.equal(this.protocol, that.protocol)
            && Objects.equal(this.protocolPort, that.protocolPort) && Objects.equal(this.poolId, that.poolId)
            && Objects.equal(this.sessionPersistence, that.sessionPersistence)
            && Objects.equal(this.connectionLimit, that.connectionLimit)
            && Objects.equal(this.adminStateUp, that.adminStateUp) && Objects.equal(this.status, that.status)
            && Objects.equal(this.statusDescription, that.statusDescription);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId, name, description, subnetId, address, portId, protocol, protocolPort,
            poolId, sessionPersistence, connectionLimit, adminStateUp, status, statusDescription);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("tenantId", tenantId).add("name", name)
            .add("description", description).add("subnetId", subnetId).add("address", address).add("portId", portId)
            .add("protocol", protocol).add("protocolPort", protocolPort).add("poolId", poolId)
            .add("sessionPersistence", sessionPersistence).add("connectionLimit", connectionLimit)
            .add("adminStateUp", adminStateUp).add("status", status).add("statusDescription", statusDescription)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow.
    */

   /**
    * @return the Builder for creating a new VIP.
    */
   public static CreateBuilder createBuilder(String subnetId, Protocol protocol, Integer port, String poolId) {
      return new CreateBuilder(subnetId, protocol, port, poolId);
   }

   /**
    * @return the Builder for updating a VIP.
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected VIP vip;

      /**
       * Default constructor.
       */
      private Builder() {
         vip = new VIP();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provides the name for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getName()
       */
      public ParameterizedBuilderType name(String name) {
         vip.name = name;
         return self();
      }

      /**
       * Provides the description for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getDescription()
       */
      public ParameterizedBuilderType description(String description) {
         vip.description = description;
         return self();
      }

      /**
       * Provides the pool id for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getPoolId()
       */
      public ParameterizedBuilderType poolId(String poolId) {
         vip.poolId = poolId;
         return self();
      }

      /**
       * Provides the session persistence for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getSessionPersistence()
       */
      public ParameterizedBuilderType sessionPersistence(SessionPersistence sessionPersistence) {
         vip.sessionPersistence = sessionPersistence;
         return self();
      }

      /**
       * Provides the connection limit for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getConnectionLimit()
       */
      public ParameterizedBuilderType connectionLimit(Integer connectionLimit) {
         vip.connectionLimit = connectionLimit;
         return self();
      }

      /**
       * Provides the administrative state for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getAdminStateUp()
       */
      public ParameterizedBuilderType adminStateUp(Boolean adminStateUp) {
         vip.adminStateUp = adminStateUp;
         return self();
      }
   }

   /**
    * Create builder (inheriting from Builder).
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a VIP's CreateBuilder
       */
      private CreateBuilder(String subnetId, Protocol protocol, Integer port, String poolId) {
         subnetId(subnetId).protocol(protocol).protocolPort(port).poolId(poolId);
      }

      /**
       * Provides the tenantId for this VIP's Builder. Admin-only.
       * When keystone is enabled, it is not mandatory to specify tenant_id for resources in create requests, as the
       * tenant identifier will be derived from the Authentication token. Please note that the default authorization
       * settings only allow administrative users to create resources on behalf of a different tenant.
       *
       * @return the Builder.
       * @see VIP#getTenantId()
       */
      public CreateBuilder tenantId(String tenantId) {
         vip.tenantId = tenantId;
         return self();
      }

      /**
       * Provides the subnet id for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getSubnetId()
       */
      public CreateBuilder subnetId(String subnetId) {
         vip.subnetId = subnetId;
         return self();
      }

      /**
       * Provides the address for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getAddress()
       */
      public CreateBuilder address(String address) {
         vip.address = address;
         return self();
      }

      /**
       * Provides the protocol for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getProtocol()
       */
      public CreateBuilder protocol(Protocol protocol) {
         vip.protocol = protocol;
         return self();
      }

      /**
       * Provides the protocol port for this VIP's Builder.
       *
       * @return the Builder.
       * @see VIP#getProtocolPort()
       */
      public CreateBuilder protocolPort(Integer protocolPort) {
         vip.protocolPort = protocolPort;
         return self();
      }

      /**
       * @return a CreateVIP constructed with this Builder.
       */
      public CreateVIP build() {
         return new CreateVIP(vip);
      }

      @Override
      protected CreateBuilder self() {
         return this;
      }
   }

   /**
    * Update builder (inheriting from Builder).
    */
   public static class UpdateBuilder extends Builder<UpdateBuilder> {
      /**
       * Supply required properties for creating a VIP's UpdateBuilder.
       */
      private UpdateBuilder() {
      }

      /**
       * @return a UpdateVIP constructed with this Builder.
       */
      public UpdateVIP build() {
         return new UpdateVIP(vip);
      }

      @Override
      protected UpdateBuilder self() {
         return this;
      }
   }

   /**
    * Create options - extend the domain class, passed to API create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreateVIP extends VIP {
      /**
       * Copy constructor.
       *
       * @param vip the VIP to copy from.
       */
      private CreateVIP(VIP vip) {
         super(vip);
      }
   }

   /**
    * Update options - extend the domain class, passed to API update calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdateVIP extends VIP {
      /**
       * Copy constructor.
       *
       * @param vip the VIP to copy from.
       */
      private UpdateVIP(VIP vip) {
         super(vip);
      }
   }
}
