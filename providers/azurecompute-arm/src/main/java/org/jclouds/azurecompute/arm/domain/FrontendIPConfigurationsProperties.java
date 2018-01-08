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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FrontendIPConfigurationsProperties {

   @Nullable
   public abstract IdReference subnet();

   @Nullable
   public abstract String privateIPAddress();

   @Nullable
   public abstract String privateIPAllocationMethod();

   @Nullable
   public abstract IdReference publicIPAddress();


   @SerializedNames({ "subnet", "privateIPAddress", "privateIPAllocationMethod", "publicIPAddress" })
   public static FrontendIPConfigurationsProperties create(final IdReference subnet, final String privateIPAddress,
         final String privateIPAllocationMethod, final IdReference publicIPAddress) {
      return builder().subnet(subnet).publicIPAddress(publicIPAddress).privateIPAddress(privateIPAddress)
            .privateIPAllocationMethod(privateIPAllocationMethod).build();
   }
   
   public abstract Builder toBuilder();
   
   public static Builder builder() {
      return new AutoValue_FrontendIPConfigurationsProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder subnet(IdReference subnet);

      public abstract Builder privateIPAddress(String privateIPAddress);

      public abstract Builder privateIPAllocationMethod(String privateIPAllocationMethod);

      public abstract Builder publicIPAddress(IdReference publicIPAddress);

      public abstract FrontendIPConfigurationsProperties build();
   }
}

