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
package org.jclouds.openstack.keystone.v2_0.options;

import static com.google.common.base.Objects.equal;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

public class UpdateTenantOptions implements MapBinder {
   @Inject
   private BindToJsonPayload jsonBinder;

   private String name;
   private String description;
   private boolean enabled;

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof UpdateTenantOptions) {
         final UpdateTenantOptions other = UpdateTenantOptions.class.cast(object);
         return equal(description, other.description) && equal(enabled, other.enabled) && equal(name, other.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, description, enabled);
   }

   protected ToStringHelper string() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      toString.add("name", name);
      toString.add("description", description);
      toString.add("enabled", Boolean.valueOf(enabled));
      return toString;
   }

   @Override
   public String toString() {
      return string().toString();
   }

   static class ServerRequest {
      String name;
      String description;
      boolean enabled;

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ServerRequest tenant = new ServerRequest();
      if (description != null)
         tenant.description = description;
      if (name != null)
         tenant.name = name;
      tenant.enabled = enabled;

      return bindToRequest(request, ImmutableMap.of("tenant", tenant));
   }

   /**
    * Gets the default tenant description
    */
   public String getDescription() {
      return this.description;
   }

   /**
    * A description can be defined when creating a tenant.
    */
   public UpdateTenantOptions description(String description) {
      this.description = description;
      return this;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public UpdateTenantOptions enabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public UpdateTenantOptions name(String name) {
      this.name = name;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public static class Builder {

      /**
       * @see UpdateTenantOptions#name(String)
       */
      public static UpdateTenantOptions name(String name) {
         UpdateTenantOptions options = new UpdateTenantOptions();
         return options.name(name);
      }

      /**
       * @see UpdateTenantOptions#description(String)
       */
      public static UpdateTenantOptions description(String description) {
         UpdateTenantOptions options = new UpdateTenantOptions();
         return options.description(description);
      }

      /**
       * @see UpdateTenantOptions#enabled(boolean)
       */
      public static UpdateTenantOptions enabled(boolean enabled) {
         UpdateTenantOptions options = new UpdateTenantOptions();
         return options.enabled(enabled);
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}
