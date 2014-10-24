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
 * A Neutron LBaaS v1 SessionPersistence.
 * Contains a type and cookie name describing the session persistence.
 */
public class SessionPersistence {

   // Mandatory attributes
   protected final Type type;
   // Optional attributes
   @Named("cookie_name")
   protected final String cookieName;

   @ConstructorProperties({ "type", "cookie_name" })
   protected SessionPersistence(Type type, String cookieName) {
      this.type = type;
      this.cookieName = cookieName;
   }

   /**
    * @return the type of the SessionPersistence.
    */
   @Nullable
   public Type getType() {
      return type;
   }

   /**
    * @return the cookie name of the SessionPersistence.
    */
   @Nullable
   public String getCookieName() {
      return cookieName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type, cookieName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      SessionPersistence that = SessionPersistence.class.cast(obj);
      return Objects.equal(this.type, that.type) && Objects.equal(this.cookieName, that.cookieName);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("type", type).add("cookieName", cookieName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /*
    * Methods to get the builder follow.
    */

   /**
    * @return the Builder for SessionPersistence.
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * Builder.
    */
   public static class Builder {
      protected Type type;
      protected String cookieName;

      /**
       * Provides the type to the SessionPersistence's Builder.
       *
       * @return the Builder.
       * @see SessionPersistence#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * Provides the cookie name to the SessionPersistence's Builder.
       *
       * @return the Builder.
       * @see SessionPersistence#getCookieName()
       */
      public Builder cookieName(String cookieName) {
         this.cookieName = cookieName;
         return this;
      }

      /**
       * @return a SessionPersistence constructed with this Builder.
       */
      public SessionPersistence build() {
         return new SessionPersistence(type, cookieName);
      }
   }

   /**
    * Enumerates supported SessionPersistence types.
    */
   public static enum Type {
      /**
       * All connections that originate from the same source IP address are handled by the same member of the pool.
       */
      SOURCE_IP("SOURCE_IP"),
      /**
       * The load balancing function creates a cookie on the first request from a client. Subsequent requests that
       * contain the same cookie value are handled by the same member of the pool.
       */
      HTTP_COOKIE("HTTP_COOKIE"),
      /**
       * The load balancing function relies on a cookie established by the back-end application. All requests with the
       * same cookie value are handled by the same member of the pool.
       */
      APP_COOKIE("APP_COOKIE"),
      /**
       * Used by jclouds when the service returns an unknown value other than null.
       */
      UNRECOGNIZED("unrecognized");

      private String name;

      private Type(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return name;
      }

      /*
       * This provides GSON enum support in jclouds.
       * */
      public static Type fromValue(String name){
         if (name != null) {
            for (Type value : Type.values()) {
               if (name.equalsIgnoreCase(value.name)) {
                  return value;
               }
            }
            return UNRECOGNIZED;
         }
         return null;
      }
   }
}
