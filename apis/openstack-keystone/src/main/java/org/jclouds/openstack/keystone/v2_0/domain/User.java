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
package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

/**
 * A digital representation of a person, system, or service who uses OpenStack
 * cloud services. Keystone authentication services will validate that incoming
 * request are being made by the user who claims to be making the call. Users
 * have a login and may be assigned tokens to access users. Users may be
 * directly assigned to a particular tenant and behave as if they are contained
 * in that tenant.
 * 
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-identity-service/2.0/content/Identity-User-Concepts-e1362.html"
 *      />
 */
public class User extends ForwardingSet<Role> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUser(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String email;
      protected Boolean enabled;
      protected String tenantId;
      protected ImmutableSet.Builder<Role> roles = ImmutableSet.<Role> builder();

      /**
       * @see User#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see User#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see User#getEmail()
       */
      public T email(String email) {
         this.email = email;
         return self();
      }

      /**
       * @see User#isEnabled()
       */
      public T enabled(Boolean enabled) {
         this.enabled = enabled;
         return self();
      }

      /**
       * @see User#getTenantId
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      /**
       * @see User#delegate()
       */
      public T role(Role role) {
         this.roles.add(role);
         return self();
      }

      /**
       * @see User#delegate()
       */
      public T roles(Iterable<Role> roles) {
         this.roles.addAll(roles);
         return self();
      }

      public User build() {
         return new User(id, name, email, enabled, tenantId, roles.build());
      }

      public T fromUser(User in) {
         return this.id(in.getId()).name(in.getName()).email(in.getEmail()).enabled(in.isEnabled())
               .tenantId(in.getTenantId()).roles(in);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final String email;
   private final Boolean enabled;
   private final String tenantId;
   private final Set<Role> roles;

   @ConstructorProperties({ "id", "name", "email", "enabled", "tenantId", "roles" })
   protected User(String id, String name, @Nullable String email, @Nullable Boolean enabled, @Nullable String tenantId,
         @Nullable Set<Role> roles) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.email = email;
      this.enabled = enabled;
      this.tenantId = tenantId;
      this.roles = roles == null ? ImmutableSet.<Role> of() : ImmutableSet.copyOf(roles);
   }

   /**
    * When providing an ID, it is assumed that the user exists in the current
    * OpenStack deployment
    * 
    * @return the id of the user in the current OpenStack deployment
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of the user
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the e-mail
    */
   public String getEmail() {
      return this.email;
   }

   /**
    * @return if the user is enabled
    */
   public boolean isEnabled() {
      return this.enabled;
   }

   /**
    * @return the user tenant
    */
   public String getTenantId() {
      return this.tenantId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, email, enabled, tenantId, roles);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      User that = User.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.name, that.name)
            && Objects.equal(this.roles, that.roles) && Objects.equal(this.enabled, that.enabled)
            && Objects.equal(this.tenantId, that.tenantId) && Objects.equal(this.email, that.email);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name).add("email", email)
            .add("enabled", enabled).add("roles", roles).add("tenanId", tenantId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   protected Set<Role> delegate() {
      return roles;
   }

}
