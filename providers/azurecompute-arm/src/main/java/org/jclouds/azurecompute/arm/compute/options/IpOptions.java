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
package org.jclouds.azurecompute.arm.compute.options;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;

/**
 * Configures the ip addresses to be configured for the created nodes.
 */
@AutoValue
public abstract class IpOptions {

   /**
    * The subnet where the NIC will be attached.
    */
   public abstract String subnet();

   /**
    * The IP address to be configured, in case of static allocation, or absent
    * for dynamic assignment.
    */
   public abstract Optional<String> address();

   /**
    * Flag to indicate if a public ip address should be allocated and bound to
    * this NIC.
    */
   public abstract boolean allocateNewPublicIp();
   
   /**
    * ID of the public IP to associate with the NIC.
    */
   @Nullable
   public abstract String publicIpId();
   
   IpOptions() {
      
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_IpOptions.Builder().address((String) null).allocateNewPublicIp(false);
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder subnet(String subnet);
      public abstract Builder allocateNewPublicIp(boolean allocatePublicIp);
      public abstract Builder publicIpId(String publicIpId);
      
      abstract Builder address(Optional<String> address);
      public Builder address(String address) {
         return address(Optional.fromNullable(address));
      }
      
      public abstract IpOptions build();
   }
}
