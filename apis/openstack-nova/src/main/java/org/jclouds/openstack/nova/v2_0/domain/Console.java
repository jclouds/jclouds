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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents an Openstack Console.
 */
public class Console {
   public enum Type {
      NOVNC("novnc"),
      XVPVNC("xvpvnc"),
      SPICE_HTML5("spice-html5"),
      RDP_HTML5("rdp-html5"),
      UNRECOGNIZED("unrecognized");

      private final String type;

      Type(String type) {
         this.type = type;
      }

      public String type() {
         return type;
      }

      /**
       * Used from jclouds builtin deserializer.
       */
      public static Type fromValue(String type) {
          if (type != null) {
             for (Type value : Type.values()) {
                if (type.equals(value.type)) {
                   return value;
                }
             }
             return UNRECOGNIZED;
          }
          return null;
      }

      @Override
      public String toString() {
          return type();
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromConsole(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected URI url;
      protected Type type;

      /**
       * @see Console#getUrl()
       */
      public T url(URI url) {
         this.url = checkNotNull(url, "url");
         return self();
      }

      /**
       * @see Console#getType()
       */
      public T type(Type type) {
         this.type = type;
         return self();
      }

      public Console build() {
         return new Console(url, type);
      }

      public T fromConsole(Console in) {
         return this.url(in.getUrl()).type(in.getType());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final URI url;
   private final Type type;

   @ConstructorProperties({ "url", "type" })
   protected Console(URI url, Type type) {
      this.url = checkNotNull(url, "url");
      this.type = checkNotNull(type, "type");
   }

   /**
    * @return the url to use to connect to the server.
    */
   public URI getUrl() {
      return this.url;
   }

   /**
    * @return the type of the url
    */
   public Type getType() {
      return this.type;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(url, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Console that = Console.class.cast(obj);
      return Objects.equal(this.url, that.url)
         && Objects.equal(this.type, that.type);
   }

   protected ToStringHelper string() {
      return toStringHelper(this).add("url", url).add("type", type);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
