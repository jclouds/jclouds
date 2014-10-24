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

public class UpdateUserOptions implements MapBinder{
   @Inject
   private BindToJsonPayload jsonBinder;

   private String name;
   private String email;
   private String password;
   private boolean enabled;

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof UpdateUserOptions) {
         final UpdateUserOptions other = UpdateUserOptions.class.cast(object);
         return equal(name, other.name) && equal(email, other.email)
               && equal(enabled, other.enabled) && equal(password, other.password);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, email, enabled, password);
   }

   protected ToStringHelper string() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      toString.add("name", name);
      toString.add("email", email);
      toString.add("password", password);
      toString.add("enabled", Boolean.valueOf(enabled));
      return toString;
   }

   @Override
   public String toString() {
      return string().toString();
   }

   static class ServerRequest {
      
      String name;
      String email;
      String password;
      boolean enabled;

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ServerRequest user = new ServerRequest();
      if (email != null)
         user.email = email;
      if (name != null)
         user.name = name;
      if (password != null)
         user.password = password;
      user.enabled = enabled;

      return bindToRequest(request, ImmutableMap.of("user", user));
   }

   /**
    * Gets the default user name
    */
   public String getName() {
      return this.name;
   }

   /**
    * A name can be defined when updating an user.
    */
   public UpdateUserOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * Gets the default password
    */
   public String getPassword() {
      return password;
   }

   public UpdateUserOptions password(String password) {
      this.password = password;
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
   public UpdateUserOptions email(String email) {
      this.email = email;
      return this;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public UpdateUserOptions enabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateUserOptions#name(String)
       */
      public static UpdateUserOptions name(String name) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.name(name);
      }

      /**
       * @see UpdateUserOptions#email(String)
       */
      public static UpdateUserOptions email(String email) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.email(email);
      }

      /**
       * @see UpdateUserOptions#enabled(boolean)
       */
      public static UpdateUserOptions enabled(boolean enabled) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.enabled(enabled);
      }
      
      /**
       * @see UpdateUserOptions#password(String)
       */
      public static UpdateUserOptions password(String password) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.password(password);
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
