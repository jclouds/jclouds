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
 * An OpenStack service, such as Compute (Nova), Object Storage (Swift), or Image Service (Glance).
 * A service provides one or more endpoints through which users can access resources and perform
 * (presumably useful) operations.
 *
 * @see <a href="http://docs.openstack.org/api/openstack-typeentity-service/2.0/content/Identity-Service-Concepts-e1362.html"
/>
 */
public class Service extends ForwardingSet<Endpoint> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromService(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected String type;
      protected String name;
      protected String description;
      protected ImmutableSet.Builder<Endpoint> endpoints = ImmutableSet.<Endpoint>builder();
      
      /**
       * @see Service#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Service#getType()
       */
      public T type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see Service#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }
      
      /**
       * @see Service#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see Service#delegate()
       */
      public T endpoint(Endpoint endpoint) {
         this.endpoints.add(endpoint);
         return self();
      }

      /**
       * @see Service#delegate()
       */
      public T endpoints(Iterable<Endpoint> endpoints) {
         this.endpoints.addAll(endpoints);
         return self();
      }

      public Service build() {
         return new Service(id, type, name, description, endpoints.build());
      }

      public T fromService(Service in) {
         return this
               .id(in.getId())
               .type(in.getType())
               .name(in.getName())
               .description(in.getDescription())
               .endpoints(in);
      }
   }
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String type;
   private final String name;
   private final String description;
   private final Set<Endpoint> endpoints;

   @ConstructorProperties({
         "id", "type", "name", "description", "endpoints"
   })
   protected Service(@Nullable String id, String type, String name, @Nullable String description, @Nullable Set<Endpoint> endpoints) {
      this.id = id;
      this.type = checkNotNull(type, "type");
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.endpoints = endpoints == null ? ImmutableSet.<Endpoint>of() : ImmutableSet.copyOf(endpoints);
   }
   
   /**
    * When providing an ID, it is assumed that the service exists in the current OpenStack deployment
    *
    * @return the id of the service in the current OpenStack deployment
    */
   @Nullable
   public String getId() {
      return this.id;
   }

   /**
    * such as {@code compute} (Nova), {@code object-store} (Swift), or {@code image} (Glance)
    *
    * @return the type of the service in the current OpenStack deployment
    */
   public String getType() {
      return this.type;
   }

   /**
    * @return the name of the service
    */
   public String getName() {
      return this.name;
   }
   
   /**
    * @return the description of the service
    */
   public String getDescription() {
      return this.description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, type, name, description, endpoints);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Service that = Service.class.cast(obj);
      return Objects.equal(this.id, that.id) 
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.endpoints, that.endpoints);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("type", type).add("name", name)
            .add("description", description).add("endpoints", endpoints);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   protected Set<Endpoint> delegate() {
      return endpoints;
   }
}
