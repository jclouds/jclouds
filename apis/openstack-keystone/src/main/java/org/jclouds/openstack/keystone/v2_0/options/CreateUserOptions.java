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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

public class CreateUserOptions implements MapBinder{
   @Inject
   private BindToJsonPayload jsonBinder;
   
   private String tenant;
   private String password;
   private String email;
   private boolean enabled;

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof CreateUserOptions) {
         final CreateUserOptions other = CreateUserOptions.class.cast(object);
         return equal(tenant, other.tenant) && equal(password, other.password) && equal(email, other.email)
               && equal(enabled, other.enabled);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tenant, password, email, enabled);
   }

   protected ToStringHelper string() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      toString.add("tenant", tenant);
      toString.add("password", password);
      toString.add("email", email);
      toString.add("enabled", Boolean.valueOf(enabled));
      return toString;
   }

   @Override
   public String toString() {
      return string().toString();
   }

   static class ServerRequest {
      final String name;
      String tenant;
      String password;
      String email;
      boolean enabled;

      private ServerRequest(String name, String password) {
         this.name = name;
         this.password = password;
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ServerRequest user = new ServerRequest(checkNotNull(postParams.get("name"), "name parameter not present")
            .toString(), checkNotNull(postParams.get("password"), "password parameter not present")
            .toString());
      if (email != null)
         user.email = email;
      if (password != null)
         user.password = password;
      if (tenant != null)
         user.tenant = tenant;
      user.enabled = enabled;

      return bindToRequest(request, ImmutableMap.of("user", user));
   }

   /**
    * Gets the default user tenant
    */
   public String getTenant() {
      return this.tenant;
   }

   /**
    * A default tenant can be defined when creating an user.
    */
   public CreateUserOptions tenant(String tenant) {
      this.tenant = tenant;
      return this;
   }

   /**
    * Gets the user e-mail
    */
   public String getEmail() {
      return this.email;
   }

   /**
    * @see #getEmail()
    */
   public CreateUserOptions email(String email) {
      this.email = email;
      return this;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public CreateUserOptions enabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public static class Builder {

      /**
       * @see CreateUserOptions#tenant(String)
       */
      public static CreateUserOptions tenant(String tenant) {
         CreateUserOptions options = new CreateUserOptions();
         return options.tenant(tenant);
      }

      /**
       * @see CreateUserOptions#email(String)
       */
      public static CreateUserOptions email(String email) {
         CreateUserOptions options = new CreateUserOptions();
         return options.email(email);
      }

      /**
       * @see CreateUserOptions#enabled(boolean)
       */
      public static CreateUserOptions enabled(boolean enabled) {
         CreateUserOptions options = new CreateUserOptions();
         return options.enabled(enabled);
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}
