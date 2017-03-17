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
public abstract class ImageProperties implements Provisionable {

   @Nullable
   public abstract IdReference sourceVirtualMachine();
   
   @Nullable
   public abstract StorageProfile storageProfile();

   @Nullable
   public abstract String provisioningState();
   
   @SerializedNames({ "sourceVirtualMachine", "storageProfile", "provisioningState"})
   public static ImageProperties create(final IdReference sourceVirtualMachine, 
                                        final StorageProfile storageProfile,
                                        final String provisioningState) {
      return builder()
              .sourceVirtualMachine(sourceVirtualMachine)
              .storageProfile(storageProfile)
              .provisioningState(provisioningState)
              .build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_ImageProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder sourceVirtualMachine(IdReference sourceVirtualMachine);
      public abstract Builder storageProfile(StorageProfile storageProfile);
      public abstract Builder provisioningState(String provisioningState);
      public abstract ImageProperties build();

   }
}
