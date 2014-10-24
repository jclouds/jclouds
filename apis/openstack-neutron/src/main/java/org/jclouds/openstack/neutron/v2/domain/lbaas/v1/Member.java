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
 * A Neutron LBaaS v1 Member.
 */
public class Member {

   // Mandatory attributes when creating
   @Named("tenant_id")
   private String tenantId;
   private String address;
   @Named("protocol_port")
   private Integer protocolPort;
   // Mandatory attributes that can be updated
   @Named("pool_id")
   private String poolId;
   // Optional attributes that can be updated
   private Integer weight;
   @Named("admin_state_up")
   private Boolean adminStateUp;
   // Read-only attributes
   private String id;
   private LBaaSStatus status;
   @Named("status_description")
   private String statusDescription;

   /**
    * Deserialization constructor.
    */
   @ConstructorProperties({ "id", "tenant_id", "pool_id", "address", "protocol_port", "weight", "admin_state_up",
         "status", "status_description" })
   private Member(String id, String tenantId, String poolId, String address, Integer protocolPort, Integer weight,
         Boolean adminStateUp, LBaaSStatus status, String statusDescription) {
      this.id = id;
      this.tenantId = tenantId;
      this.poolId = poolId;
      this.address = address;
      this.protocolPort = protocolPort;
      this.weight = weight;
      this.adminStateUp = adminStateUp;
      this.status = status;
      this.statusDescription = statusDescription;
   }

   /**
    * Default constructor.
    */
   private Member() {
   }

   /**
    * Copy constructor.
    *
    * @param member the Member to copy from.
    */
   private Member(Member member) {
      this(member.id, member.tenantId, member.poolId, member.address, member.protocolPort, member.weight,
            member.adminStateUp, member.status, member.statusDescription);
   }

   /**
    * @return the id of the Member.
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the tenant id of the Member.
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the pool id for this Member.
    */
   @Nullable
   public String getPoolId() {
      return poolId;
   }

   /**
    * @return the address for this Member.
    */
   @Nullable
   public String getAddress() {
      return address;
   }

   /**
    * @return the protocol port for this Member.
    */
   @Nullable
   public Integer getProtocolPort() {
      return protocolPort;
   }

   /**
    * @return the weight for this Member.
    */
   @Nullable
   public Integer getWeight() {
      return weight;
   }

   /**
    * @return the administrative state for this Member.
    */
   @Nullable
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the status for this Member.
    */
   @Nullable
   public LBaaSStatus getStatus() {
      return status;
   }

   /**
    * @return the status description for this Member.
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

      Member that = (Member) o;

      return Objects.equal(this.id, that.id) && Objects.equal(this.tenantId, that.tenantId)
            && Objects.equal(this.poolId, that.poolId) && Objects.equal(this.address, that.address)
            && Objects.equal(this.protocolPort, that.protocolPort) && Objects.equal(this.weight, that.weight)
            && Objects.equal(this.adminStateUp, that.adminStateUp) && Objects.equal(this.status, that.status)
            && Objects.equal(this.statusDescription, that.statusDescription);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId, poolId, address, protocolPort, weight, adminStateUp, status,
            statusDescription);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("tenantId", tenantId).add("poolId", poolId)
            .add("address", address).add("protocolPort", protocolPort).add("weight", weight)
            .add("adminStateUp", adminStateUp).add("status", status).add("statusDescription", statusDescription)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow.
    */

   /**
    * @return the Builder for creating a new Member.
    */
   public static CreateBuilder createBuilder(String poolId, String address, Integer port) {
      return new CreateBuilder(poolId, address, port);
   }

   /**
    * @return the Builder for updating a Member.
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected Member member;

      /**
       * Default constructor.
       */
      private Builder() {
         member = new Member();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provides the pool id for this Member's Builder.
       *
       * @return the Builder.
       * @see Member#getPoolId()
       */
      public ParameterizedBuilderType poolId(String poolId) {
         member.poolId = poolId;
         return self();
      }

      /**
       * Provides the weight for this Member's Builder.
       *
       * @return the Builder.
       * @see Member#getWeight()
       */
      public ParameterizedBuilderType weight(Integer weight) {
         member.weight = weight;
         return self();
      }

      /**
       * Provides the administrative state for this Member's Builder.
       *
       * @return the Builder.
       * @see Member#getAdminStateUp()
       */
      public ParameterizedBuilderType adminStateUp(Boolean adminStateUp) {
         member.adminStateUp = adminStateUp;
         return self();
      }
   }

   /**
    * Create builder (inheriting from Builder).
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a Member's CreateBuilder.
       *
       * @param poolId the pool id.
       * @param address the IP address.
       * @param port the protocol port.
       */
      private CreateBuilder(String poolId, String address, Integer port) {
         poolId(poolId).address(address).protocolPort(port);
      }

      /**
       * Provides the tenantId for this Member's Builder. Admin-only.
       * When keystone is enabled, it is not mandatory to specify tenant_id for resources in create requests, as the
       * tenant identifier will be derived from the Authentication token. Please note that the default authorization
       * settings only allow administrative users to create resources on behalf of a different tenant.
       *
       * @return the Builder.
       * @see Member#getTenantId()
       */
      public CreateBuilder tenantId(String tenantId) {
         member.tenantId = tenantId;
         return self();
      }

      /**
       * Provides the address for this Member's Builder.
       *
       * @return the Builder.
       * @see Member#getAddress()
       */
      public CreateBuilder address(String address) {
         member.address = address;
         return self();
      }

      /**
       * Provides the protocol port for this Member's Builder.
       *
       * @return the Builder.
       * @see Member#getProtocolPort()
       */
      public CreateBuilder protocolPort(Integer protocolPort) {
         member.protocolPort = protocolPort;
         return self();
      }

      /**
       * @return a CreateMember constructed with this Builder.
       */
      public CreateMember build() {
         return new CreateMember(member);
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
       * Supply required properties for creating a Member's UpdateBuilder.
       */
      private UpdateBuilder() {
      }

      /**
       * @return a UpdateMember constructed with this Builder.
       */
      public UpdateMember build() {
         return new UpdateMember(member);
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
   public static class CreateMember extends Member {
      /**
       * Copy constructor.
       *
       * @param member the Member to copy from.
       */
      private CreateMember(Member member) {
         super(member);
      }
   }

   /**
    * Update options - extend the domain class, passed to API update calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdateMember extends Member {
      /**
       * Copy constructor.
       *
       * @param member the Member to copy from.
       */
      private UpdateMember(Member member) {
         super(member);
      }
   }
}
