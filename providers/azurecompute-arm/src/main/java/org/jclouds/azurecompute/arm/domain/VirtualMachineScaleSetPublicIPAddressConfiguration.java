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
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class VirtualMachineScaleSetPublicIPAddressConfiguration {

   /**
    * The name of the Virtual Machine Scale Set Public IP Address Configuration
    */
   public abstract String name();

   /**
    * The properties of the Virtual Machine Scale Set Public IP Address Configuration
    */
   public abstract VirtualMachineScaleSetPublicIPAddressProperties properties();

   @SerializedNames({ "name",  "properties" })
   public static VirtualMachineScaleSetPublicIPAddressConfiguration create(
      String name,
      VirtualMachineScaleSetPublicIPAddressProperties properties) {
         return builder().name(name).properties(properties).build();
   }

   VirtualMachineScaleSetPublicIPAddressConfiguration() {

   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_VirtualMachineScaleSetPublicIPAddressConfiguration.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder name(String name);
      public abstract Builder properties(VirtualMachineScaleSetPublicIPAddressProperties properties);
      public abstract VirtualMachineScaleSetPublicIPAddressConfiguration build();
   }
}
