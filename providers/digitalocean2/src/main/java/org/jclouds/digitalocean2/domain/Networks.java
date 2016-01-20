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
package org.jclouds.digitalocean2.domain;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Predicate;

@AutoValue
public abstract class Networks {
   
   @AutoValue
   public abstract static class Address {
      public abstract String ip();
      public abstract String netmask();
      public abstract String gateway();
      public abstract String type();

      @SerializedNames({ "ip_address", "netmask", "gateway", "type"})
      public static Address create(String ip, String netmask, String gateway, String type) {
         return new AutoValue_Networks_Address(ip, netmask, gateway, type);
      }
      
      Address() {}
   }
   
   public abstract List<Address> ipv4();
   public abstract List<Address> ipv6();
   
   @SerializedNames({ "v4", "v6" })
   public static Networks create(List<Address> ipv4, List<Address> ipv6) {
      return new AutoValue_Networks(copyOf(ipv4), copyOf(ipv6));
   }
   
   Networks() {}

   public static class Predicates {
      
      public static Predicate<Address> publicNetworks() {
         return new Predicate<Address>() {
            @Override
            public boolean apply(Address network) {
               return network.type().equals("public");
            }
         };
      }
      
      public static Predicate<Address> privateNetworks() {
         return new Predicate<Address>() {
            @Override
            public boolean apply(Address network) {
               return network.type().equals("private");
            }
         };
      }
   }

}
