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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import org.jclouds.javax.annotation.Nullable;

import javax.inject.Named;

/**
 * IP address
 * 
*/
public class Address {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromAddress(this);
   }

   public static Address createV4(String addr) {
      return builder().version(4).addr(addr).build();
   }

   public static Address createV6(String addr) {
      return builder().version(6).addr(addr).build();
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String addr;
      protected int version;
      protected String macAddr;
      protected String type;


      /** 
       * @see Address#getAddr()
       */
      public T addr(String addr) {
         this.addr = addr;
         return self();
      }

      /** 
       * @see Address#getVersion()
       */
      public T version(int version) {
         this.version = version;
         return self();
      }

      public T macAddr(String macAddr) {
         this.macAddr = macAddr;
         return self();
      }

      public T type(String type) {
         this.type = type;
         return self();
      }

      public Address build() {
         return new Address(addr, version, macAddr, type);
      }
      
      public T fromAddress(Address in) {
         return this
                  .addr(in.getAddr())
                  .version(in.getVersion())
                  .macAddr(in.getMacAddr().orNull())
                  .type(in.getType().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String addr;
   private final int version;
   @Named("OS-EXT-IPS-MAC:mac_addr")
   private final Optional<String> macAddr;
   @Named("OS-EXT-IPS:type")
   private final Optional<String> type;

   @ConstructorProperties({
      "addr", "version", "OS-EXT-IPS-MAC:mac_addr", "OS-EXT-IPS:type"
   })
   protected Address(String addr, int version, @Nullable String macAddr, @Nullable String type) {
      this.addr = checkNotNull(addr, "addr");
      this.version = version;
      this.macAddr = Optional.fromNullable(macAddr);
      this.type = Optional.fromNullable(type);
   }

   /**
    * @return the ip address
    */
   public String getAddr() {
      return this.addr;
   }

   /**
    * @return the IP version, ex. 4
    */
   public int getVersion() {
      return this.version;
   }

   public Optional<String> getMacAddr() {
      return this.macAddr;
   }

   public Optional<String> getType() {
      return this.type;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(addr, version, macAddr, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Address that = Address.class.cast(obj);
      return Objects.equal(this.addr, that.addr)
               && Objects.equal(this.version, that.version)
               && Objects.equal(this.macAddr, that.macAddr)
               && Objects.equal(this.type, that.type);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("addr", addr).add("version", version).add("macAddr", macAddr.or("")).add("type", type.or(""));
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
