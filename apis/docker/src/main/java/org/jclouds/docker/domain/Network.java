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
package org.jclouds.docker.domain;

import static org.jclouds.docker.internal.NullSafeCopies.copyOf;
import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Network {

   @AutoValue
   public abstract static class IPAM {

      IPAM() {} // For AutoValue only!

      @Nullable
      public abstract String driver();

      public abstract List<Config> config();

      @SerializedNames({"Driver", "Config"})
      public static IPAM create(String driver, List<Config> config) {
         return new AutoValue_Network_IPAM(driver, copyOf(config));
      }

      @AutoValue
      public abstract static class Config {

         Config() {} // For AutoValue only!

         public abstract String subnet();

         @Nullable
         public abstract String ipRange();

         @Nullable
         public abstract String gateway();

         @SerializedNames({"Subnet", "IPRange", "Gateway"})
         public static Config create(String subnet, String ipRange, String gateway) {
            return new AutoValue_Network_IPAM_Config(subnet, ipRange, gateway);
         }
      }
   }

   @AutoValue
   public abstract static class Details {

      Details() {} // For AutoValue only!

      public abstract String endpoint();

      public abstract String macAddress();

      public abstract String ipv4address();

      public abstract String ipv6address();

      @SerializedNames({ "EndpointID", "MacAddress", "IPv4Address", "IPv6Address" })
      public static Details create(String endpoint, String macAddress, String ipv4address, String ipv6address) {
         return new AutoValue_Network_Details(endpoint, macAddress, ipv4address, ipv6address);
      }
   }

   @Nullable public abstract String name();

   @Nullable public abstract String id();

   @Nullable public abstract String scope();

   @Nullable public abstract String driver();

   @Nullable public abstract IPAM ipam();

   public abstract Map<String, Details> containers();

   public abstract Map<String, String> options();

   Network() {}

   @SerializedNames({ "Name", "Id", "Scope", "Driver", "IPAM", "Containers", "Options" })
   public static Network create(String name, String id, String scope, String driver, IPAM ipam,
                                Map<String, Details> containers, Map<String, String> options) {
      return new AutoValue_Network(name, id, scope, driver, ipam, copyOf(containers), copyOf(options));
   }

}
