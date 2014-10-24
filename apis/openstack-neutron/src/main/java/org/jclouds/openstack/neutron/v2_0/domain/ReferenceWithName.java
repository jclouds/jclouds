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
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Strings;

import java.beans.ConstructorProperties;

/**
 * Base class for beans in the Neutron v2.0 api
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/Networks.html">api doc</a>
 */
public class ReferenceWithName extends Reference {

   private final String name;

   @ConstructorProperties({
      "id", "tenant_id", "name"
   })
   protected ReferenceWithName(String id, String tenantId, String name) {
      super(id, tenantId);
      this.name = Strings.nullToEmpty(name);
   }

   /**
    * @return the name of the entity
    */
   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ReferenceWithName that = ReferenceWithName.class.cast(obj);
      return super.equals(obj) && Objects.equal(this.name, that.name);
   }

   protected ToStringHelper string() {
      return super.string().add("name", name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromReferenceWithName(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Reference.Builder<T> {
      protected abstract T self();

      protected String name;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      public ReferenceWithName build() {
         return new ReferenceWithName(id, tenantId, name);
      }

      public T fromReferenceWithName(ReferenceWithName in) {
         return super.fromReference(in).name(in.getName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
