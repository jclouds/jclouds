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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * A Neutron LBaaS v1 Pool.
 */
public class Pool {

   // Load balancing methods that must be supported by all providers.
   // Not an enum type because any provider may support additional balancing methods.
   public static String ROUND_ROBIN = "ROUND_ROBIN";
   public static String LEAST_CONNECTIONS = "LEAST_CONNECTIONS";
   public static String SOURCE_IP = "SOURCE_IP";

   // Mandatory attributes when creating
   @Named("tenant_id")
   private String tenantId;
   @Named("subnet_id")
   private String subnetId;
   private Protocol protocol;
   // Mandatory attributes that can be updated
   @Named("lb_method")
   private String lbMethod;
   // Optional attributes when creating
   private String provider;
   // Optional attributes that can be updated
   private String name;
   private String description;
   @Named("health_monitors")
   private ImmutableSet<String> healthMonitors;
   @Named("admin_state_up")
   private Boolean adminStateUp;
   // Read-only attributes
   private String id;
   @Named("vip_id")
   private String vipId;
   private ImmutableSet<String> members;
   @Named("health_monitors_status")
   private ImmutableList<HealthMonitorStatus> healthMonitorsStatus;
   private LBaaSStatus status;
   @Named("status_description")
   private String statusDescription;

   /**
    * Deserialization constructor.
    */
   @ConstructorProperties({ "id", "tenant_id", "vip_id", "name", "description", "subnet_id", "protocol", "provider",
         "lb_method", "health_monitors", "health_monitors_status", "members", "admin_state_up", "status",
         "status_description" })
   private Pool(String id, String tenantId, String vipId, String name, String description, String subnetId,
         Protocol protocol, String provider, String lbMethod, ImmutableSet<String> healthMonitors,
         ImmutableList<HealthMonitorStatus> healthMonitorsStatus, ImmutableSet<String> members, Boolean adminStateUp,
         LBaaSStatus status, String statusDescription) {
      this.id = id;
      this.tenantId = tenantId;
      this.vipId = vipId;
      this.name = name;
      this.description = description;
      this.subnetId = subnetId;
      this.protocol = protocol;
      this.provider = provider;
      this.lbMethod = lbMethod;
      this.healthMonitors = healthMonitors;
      this.healthMonitorsStatus = healthMonitorsStatus;
      this.members = members;
      this.adminStateUp = adminStateUp;
      this.status = status;
      this.statusDescription = statusDescription;
   }

   /**
    * Default constructor.
    */
   private Pool() {
   }

   /**
    * Copy constructor.
    *
    * @param pool the Pool to copy from.
    */
   private Pool(Pool pool) {
      this(pool.id, pool.tenantId, pool.vipId, pool.name, pool.description, pool.subnetId, pool.protocol,
            pool.provider, pool.lbMethod, pool.healthMonitors, pool.healthMonitorsStatus, pool.members,
            pool.adminStateUp, pool.status, pool.statusDescription);
   }

   /**
    * @return the id of the Pool.
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the tenant id of the Pool.
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the virtual IP id of the Pool.
    */
   @Nullable
   public String getVIPId() {
      return vipId;
   }

   /**
    * @return the name of the Pool.
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * @return the description of the Pool.
    */
   @Nullable
   public String getDescription() {
      return description;
   }

   /**
    * @return the subnet id for this Pool.
    */
   @Nullable
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * @return the protocol for this Pool.
    */
   @Nullable
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * @return the provider for this Pool.
    */
   @Nullable
   public String getProvider() {
      return provider;
   }

   /**
    * @return the load balancing method for this Pool.
    */
   @Nullable
   public String getLBMethod() {
      return lbMethod;
   }

   /**
    * @return the health monitors for this Pool.
    */
   @Nullable
   public ImmutableSet<String> getHealthMonitors() {
      return healthMonitors;
   }

   /**
    * @return the health monitors status for this Pool.
    */
   @Nullable
   public ImmutableList<HealthMonitorStatus> getHealthMonitorsStatus() {
      return healthMonitorsStatus;
   }

   /**
    * @return the members for this Pool.
    */
   @Nullable
   public ImmutableSet<String> getMembers() {
      return members;
   }

   /**
    * @return the administrative state for this Pool.
    */
   @Nullable
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the status for this Pool.
    */
   @Nullable
   public LBaaSStatus getStatus() {
      return status;
   }

   /**
    * @return the status description for this Pool.
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

      Pool that = (Pool) o;

      return Objects.equal(this.id, that.id) && Objects.equal(this.tenantId, that.tenantId)
            && Objects.equal(this.vipId, that.vipId) && Objects.equal(this.name, that.name)
            && Objects.equal(this.description, that.description) && Objects.equal(this.subnetId, that.subnetId)
            && Objects.equal(this.protocol, that.protocol) && Objects.equal(this.provider, that.provider)
            && Objects.equal(this.lbMethod, that.lbMethod) && Objects.equal(this.healthMonitors, that.healthMonitors)
            && Objects.equal(this.healthMonitorsStatus, that.healthMonitorsStatus)
            && Objects.equal(this.members, that.members) && Objects.equal(this.adminStateUp, that.adminStateUp)
            && Objects.equal(this.status, that.status) && Objects.equal(this.statusDescription, that.statusDescription);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId, vipId, name, description, subnetId, protocol, provider, lbMethod,
            healthMonitors, healthMonitorsStatus, members, adminStateUp, status, statusDescription);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("tenantId", tenantId).add("vipId", vipId)
            .add("name", name).add("description", description).add("subnetId", subnetId).add("protocol", protocol)
            .add("provider", provider).add("lbMethod", lbMethod).add("healthMonitors", healthMonitors)
            .add("healthMonitorsStatus", healthMonitorsStatus).add("members", members)
            .add("adminStateUp", adminStateUp).add("status", status).add("statusDescription", statusDescription)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow.
    */

   /**
    * @return the Builder for creating a new Pool.
    */
   public static CreateBuilder createBuilder(String subnetId, Protocol protocol, String lbMethod) {
      return new CreateBuilder(subnetId, protocol, lbMethod);
   }

   /**
    * @return the Builder for updating a Pool.
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected Pool pool;

      /**
       * Default constructor.
       */
      private Builder() {
         pool = new Pool();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provides the name for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getName()
       */
      public ParameterizedBuilderType name(String name) {
         pool.name = name;
         return self();
      }

      /**
       * Provides the description for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getDescription()
       */
      public ParameterizedBuilderType description(String description) {
         pool.description = description;
         return self();
      }

      /**
       * Provides the load balancing method for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getLBMethod()
       */
      public ParameterizedBuilderType lbMethod(String lbMethod) {
         pool.lbMethod = lbMethod;
         return self();
      }

      /**
       * Provides the health monitors for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getHealthMonitors()
       */
      public ParameterizedBuilderType healthMonitors(ImmutableSet<String> healthMonitors) {
         pool.healthMonitors = healthMonitors;
         return self();
      }

      /**
       * Provides the administrative state for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getAdminStateUp()
       */
      public ParameterizedBuilderType adminStateUp(Boolean adminStateUp) {
         pool.adminStateUp = adminStateUp;
         return self();
      }
   }

   /**
    * Create builder (inheriting from Builder).
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a Pool's CreateBuilder.
       *
       * @param subnetId the subnet id.
       * @param protocol the protocol.
       * @param lbMethod the load balancing method.
       */
      private CreateBuilder(String subnetId, Protocol protocol, String lbMethod) {
         subnetId(subnetId).protocol(protocol).lbMethod(lbMethod);
      }

      /**
       * Provides the tenantId for this Pool's Builder. Admin-only.
       * When keystone is enabled, it is not mandatory to specify tenant_id for resources in create requests, as the
       * tenant identifier will be derived from the Authentication token. Please note that the default authorization
       * settings only allow administrative users to create resources on behalf of a different tenant.
       *
       * @return the Builder.
       * @see Pool#getTenantId()
       */
      public CreateBuilder tenantId(String tenantId) {
         pool.tenantId = tenantId;
         return self();
      }

      /**
       * Provides the subnet id for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getSubnetId()
       */
      public CreateBuilder subnetId(String subnetId) {
         pool.subnetId = subnetId;
         return self();
      }

      /**
       * Provides the protocol for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getProtocol()
       */
      public CreateBuilder protocol(Protocol protocol) {
         pool.protocol = protocol;
         return self();
      }

      /**
       * Provides the provider for this Pool's Builder.
       *
       * @return the Builder.
       * @see Pool#getProvider()
       */
      public CreateBuilder provider(String provider) {
         pool.provider = provider;
         return self();
      }

      /**
       * @return a CreatePool constructed with this Builder.
       */
      public CreatePool build() {
         return new CreatePool(pool);
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
       * Supply required properties for creating a Pool's UpdateBuilder.
       */
      private UpdateBuilder() {
      }

      /**
       * @return a UpdatePool constructed with this Builder.
       */
      public UpdatePool build() {
         return new UpdatePool(pool);
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
   public static class CreatePool extends Pool {
      /**
       * Copy constructor.
       *
       * @param pool the Pool to copy from.
       */
      private CreatePool(Pool pool) {
         super(pool);
      }
   }

   /**
    * Update options - extend the domain class, passed to API update calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdatePool extends Pool {
      /**
       * Copy constructor.
       *
       * @param pool the Pool to copy from.
       */
      private UpdatePool(Pool pool) {
         super(pool);
      }
   }
}
