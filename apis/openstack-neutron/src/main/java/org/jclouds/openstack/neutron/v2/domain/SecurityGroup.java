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
import com.google.common.collect.ImmutableList;

/**
 * Contains a mapping between a MAC address and an IP address.
 */
public class SecurityGroup {

   private String id;
   @Named("tenant_id")
   private String tenantId;
   private String name;
   private String description;
   @Named("security_group_rules")
   private ImmutableList<Rule> rules;

   @ConstructorProperties({"id", "tenant_id", "name", "description", "security_group_rules"})
   protected SecurityGroup(String id, String tenantId, String name, String description,
         ImmutableList<Rule> rules) {
      this.id = id;
      this.tenantId = tenantId;
      this.name = name;
      this.description = description;
      this.rules = rules;
   }

   private SecurityGroup(SecurityGroup securityGroup) {
      this(securityGroup.id,
            securityGroup.tenantId,
            securityGroup.name,
            securityGroup.description,
            securityGroup.rules
      );
   }

   private SecurityGroup() {}

   /**
    * @return The identifier for this Security Group.
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return The identifier of the tenant for this Security Group.
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return The name of the Security Group.
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * @return The description of the Security Group.
    */
   @Nullable
   public String getDescription() {
      return description;
   }

   /**
    * @return The collection of rules for this Security Group.
    */
   public ImmutableList<Rule> getRules() {
      return rules!=null ? rules : ImmutableList.<Rule>of();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      SecurityGroup that = (SecurityGroup) o;

      return Objects.equal(this.id, that.id) &&
            Objects.equal(this.tenantId, that.tenantId) &&
            Objects.equal(this.name, that.name) &&
            Objects.equal(this.description, that.description) &&
            Objects.equal(this.rules, that.rules);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId, name, description, rules);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("tenantId", tenantId)
            .add("name", name)
            .add("description", description)
            .add("securityGroupRules", rules)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow
    */

   /**
    * @return the Builder for creating a new SecurityGroup
    */
   public static CreateBuilder createOptions() {
      return new CreateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      // Keep track of the builder's state.
      protected SecurityGroup securityGroup;

      private Builder() {
         securityGroup = new SecurityGroup();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * The tenant id for this Security Group. Usually can only be specified by administrators.
       *
       * @return the Builder.
       * @see SecurityGroup#getTenantId()
       */
      public ParameterizedBuilderType tenantId(String tenantId) {
         securityGroup.tenantId = tenantId;
         return self();
      }

      /**
       * The name for this Security Group.
       *
       * @return the Builder.
       * @see SecurityGroup#getName()
       */
      public ParameterizedBuilderType name(String name) {
         securityGroup.name = name;
         return self();
      }

      /**
       * The description for this Security Group.
       *
       * @return the Builder.
       * @see SecurityGroup#getDescription()
       */
      public ParameterizedBuilderType description(String description) {
         securityGroup.description = description;
         return self();
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a Builder
       */
      private CreateBuilder() {
      }

      /**
       * @return a CreateOptions constructed with this Builder.
       */
      public CreateOptions build() {
         return new CreateOptions(securityGroup);
      }

      protected CreateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreateOptions extends SecurityGroup {
      private CreateOptions(SecurityGroup securityGroup) {
         super(securityGroup);
      }
   }
}
