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
import static org.jclouds.docker.internal.NullSafeCopies.copyWithNullOf;

import java.util.List;
import java.util.Map;

import com.google.auto.value.AutoValue;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Network {

   @AutoValue
   public abstract static class IPAM {

      IPAM() { }

      @Nullable public abstract String driver();

      public abstract List<Config> config();

      @SerializedNames({"Driver", "Config"})
      public static IPAM create(@Nullable String driver, List<Config> config) {
         return builder()
               .driver(driver)
               .config(config)
               .build();
      }

      public static Builder builder() {
         return new AutoValue_Network_IPAM.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder driver(@Nullable String driver);

         public abstract Builder config(List<Config> config);

         abstract List<Config> config();

         abstract IPAM autoBuild();

         public IPAM build() {
            return config(copyOf(config()))
                  .autoBuild();
         }
      }

      @AutoValue
      public abstract static class Config {

         Config() { }

         public abstract String subnet();

         @Nullable public abstract String ipRange();

         @Nullable public abstract String gateway();

         @SerializedNames({"Subnet", "IPRange", "Gateway"})
         public static Config create(String subnet, @Nullable String ipRange, @Nullable String gateway) {
            return builder()
                  .subnet(subnet)
                  .ipRange(ipRange)
                  .gateway(gateway)
                  .build();
         }

         public static Builder builder() {
            return new AutoValue_Network_IPAM_Config.Builder();
         }

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder subnet(String subnet);

            public abstract Builder ipRange(@Nullable String ipRange);

            public abstract Builder gateway(@Nullable String gateway);

            abstract Config build();
         }
      }
   }

   @AutoValue
   public abstract static class Details {

      Details() { }

      public abstract String endpoint();

      public abstract String macAddress();

      public abstract String ipv4address();

      public abstract String ipv6address();

      @SerializedNames({ "EndpointID", "MacAddress", "IPv4Address", "IPv6Address" })
      public static Details create(String endpoint, String macAddress, String ipv4address, String ipv6address) {
         return builder()
               .endpoint(endpoint)
               .macAddress(macAddress)
               .ipv4address(ipv4address)
               .ipv6address(ipv6address)
               .build();
      }

      public static Builder builder() {
         return new AutoValue_Network_Details.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder endpoint(String endpoint);

         public abstract Builder macAddress(String macAddress);

         public abstract Builder ipv4address(String ipv4address);

         public abstract Builder ipv6address(String ipv6address);

         abstract Details build();
      }
   }

   @Nullable public abstract String name();

   @Nullable public abstract String id();

   @Nullable public abstract String scope();

   @Nullable public abstract String driver();

   @Nullable public abstract IPAM ipam();

   @Nullable public abstract Map<String, Details> containers();

   @Nullable public abstract Map<String, String> options();

   Network() { }

   @SerializedNames({ "Name", "Id", "Scope", "Driver", "IPAM", "Containers", "Options" })
   public static Network create(@Nullable String name, @Nullable String id, @Nullable String scope,
         @Nullable String driver, @Nullable IPAM ipam, @Nullable Map<String, Details> containers,
         @Nullable Map<String, String> options) {
      return builder()
            .name(name)
            .id(id)
            .scope(scope)
            .driver(driver)
            .ipam(ipam)
            .containers(containers)
            .options(options)
            .build();
   }

   public static Builder builder() {
      return new AutoValue_Network.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder name(@Nullable String name);

      public abstract Builder id(@Nullable String id);

      public abstract Builder scope(@Nullable String scope);

      public abstract Builder driver(@Nullable String driver);

      public abstract Builder ipam(@Nullable IPAM ipam);

      public abstract Builder containers(@Nullable Map<String, Details> containers);

      public abstract Builder options(@Nullable Map<String, String> options);

      abstract Map<String, Details> containers();

      abstract Map<String, String> options();

      abstract Network autoBuild();

      public Network build() {
         return containers(copyWithNullOf(containers()))
               .options(copyOf(options()))
               .autoBuild();
      }
   }
}
