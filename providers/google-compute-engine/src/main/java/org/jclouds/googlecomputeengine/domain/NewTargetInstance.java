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
package org.jclouds.googlecomputeengine.domain;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NewTargetInstance {

   public abstract String name();
   @Nullable public abstract String description();
   @Nullable public abstract String natPolicy();
   @Nullable public abstract URI instance();

   @SerializedNames({"name", "description", "natPolicy", "instance"})
   static NewTargetInstance create(String name, String description, String natPolicy, URI instance){
      return new AutoValue_NewTargetInstance(name, description, natPolicy, instance);
   }

   NewTargetInstance() {
   }

   public static class Builder {
      private String name;
      private String description;
      private String natPolicy;
      private URI instance;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder natPolicy(String natPolicy) {
         this.natPolicy = natPolicy;
         return this;
      }

      public Builder instance(URI instance) {
         this.instance = instance;
         return this;
      }

      public NewTargetInstance build() {
         return NewTargetInstance.create(name, description, natPolicy, instance);
      }
   }
}
