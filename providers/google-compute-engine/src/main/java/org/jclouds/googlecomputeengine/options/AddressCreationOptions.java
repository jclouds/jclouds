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
package org.jclouds.googlecomputeengine.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AddressCreationOptions {

   public abstract String name();
   @Nullable public abstract String address();
   @Nullable public abstract String description();

   @SerializedNames({"name", "address", "description"})
   static AddressCreationOptions create(String name, String address, String description){
      return new AutoValue_AddressCreationOptions(name, address, description);
   }

   public static class Builder {
      private String name;
      private String address;
      private String description;

      public Builder(String name) {
         this.name = name;
      }

      public Builder address(String address) {
         this.address = address;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public AddressCreationOptions build() {
         checkNotNull(name, "AddressCreationOptions name cannot be null");
         return create(name, address, description);
      }
   }
}
