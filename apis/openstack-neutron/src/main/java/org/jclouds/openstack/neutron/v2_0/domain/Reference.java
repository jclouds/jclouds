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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for beans in the Neutron v2.0 api
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/Networks.html">api doc</a>
 */
public class Reference {

   private final String id;
   private final String tenantId;

   @ConstructorProperties({
      "id", "tenant_id"
   })
   protected Reference(String id, String tenantId) {
      this.id = checkNotNull(id, "id");
      this.tenantId = checkNotNull(tenantId, "tenantId");
   }

   /**
    * @return the id of the entity
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the id of the tenant where this entity is associated with
    */
   public String getTenantId() {
      return tenantId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Reference that = Reference.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.tenantId, that.tenantId);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("id", id).add("tenantId", tenantId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromReference(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String tenantId;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Reference#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Reference#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      public Reference build() {
         return new Reference(id, tenantId);
      }

      public T fromReference(Reference in) {
         return this.id(in.getId()).tenantId(in.getTenantId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
