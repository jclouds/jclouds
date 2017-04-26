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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class NetworkProfile {

   @AutoValue
   public abstract static class NetworkInterface {
      public abstract String id();
      @Nullable public abstract NetworkInterfaceProperties properties();
      
      @AutoValue
      public abstract static class NetworkInterfaceProperties {
         public abstract boolean primary();
         
         NetworkInterfaceProperties() {
            
         }
         
         @SerializedNames({"primary"})
         public static NetworkInterfaceProperties create(boolean primary) {
            return new AutoValue_NetworkProfile_NetworkInterface_NetworkInterfaceProperties(primary);
         }
      }
      
      NetworkInterface() {
         
      }
      
      @SerializedNames({"id", "properties"})
      public static NetworkInterface create(String id, NetworkInterfaceProperties properties) {
         return new AutoValue_NetworkProfile_NetworkInterface(id, properties);
      }
   }
   
   /**
    * List of network interfaces
    */
   public abstract List<NetworkInterface> networkInterfaces();

   @SerializedNames({"networkInterfaces"})
   public static NetworkProfile create(final List<NetworkInterface> networkInterfaces) {
      return builder().networkInterfaces(networkInterfaces).build();
   }
   
   NetworkProfile() {
      
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_NetworkProfile.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder networkInterfaces(List<NetworkInterface> networkInterfaces);

      abstract List<NetworkInterface> networkInterfaces();

      abstract NetworkProfile autoBuild();

      public NetworkProfile build() {
         networkInterfaces(networkInterfaces() != null ? ImmutableList.copyOf(networkInterfaces()) : ImmutableList.<NetworkInterface>of());
         return autoBuild();
      }
   }
}
