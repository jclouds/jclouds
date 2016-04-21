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
package org.jclouds.azurecompute.arm.domain;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Subnet {

   @AutoValue
   public abstract static class IpConfiguration {

      public abstract String id();

      @SerializedNames({"id"})
      public static IpConfiguration create(final String id) {
         return new AutoValue_Subnet_IpConfiguration(id);
      }
   }

   @AutoValue
   public abstract static class SubnetProperties {

      @Nullable
      public abstract String provisioningState();

      @Nullable
      public abstract String addressPrefix();

      @Nullable
      public abstract List<IpConfiguration> ipConfigurations();

      @SerializedNames({"provisioningState", "addressPrefix", "ipConfigurations"})
      public static SubnetProperties create(final String provisioningState, final String addressPrefix, final List<IpConfiguration> ipConfigurations) {
         return builder()
                 .provisioningState(provisioningState)
                 .addressPrefix(addressPrefix)
                 .ipConfigurations(ipConfigurations != null ? copyOf(ipConfigurations) : null)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Subnet_SubnetProperties.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder provisioningState(String provisioningState);

         public abstract Builder addressPrefix(String addressPrefix);

         public abstract Builder ipConfigurations(List<IpConfiguration> ipConfigurations);

         public abstract SubnetProperties build();
      }
   }

   @Nullable
   public abstract String name();

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String etag();

   @Nullable
   public abstract SubnetProperties properties();

   @SerializedNames({"name", "id", "etag", "properties"})
   public static Subnet create(final String name,
                               final String id,
                               final String etag,
                               final SubnetProperties properties) {
      return new AutoValue_Subnet(name, id, etag, properties);
   }
}
