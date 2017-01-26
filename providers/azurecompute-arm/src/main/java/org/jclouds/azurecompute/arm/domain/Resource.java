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

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class Resource {

   @AutoValue
   public abstract static class Identity {

      public abstract String principalId();
      public abstract String tenantId();
      public abstract String type();

      @SerializedNames({"principalId", "tenantId", "type" })
      public static Identity create(String principalId, String tenantId, String type) {
         return new AutoValue_Resource_Identity(principalId, tenantId, type);
      }
   }
   
   @AutoValue
   public abstract static class ResourceProperties{
      @Nullable
      public abstract String provisioningState();

      @SerializedNames({"provisioningState"})
      public static ResourceProperties create(final String provisioningState) {
         return new AutoValue_Resource_ResourceProperties(provisioningState);
      }
   }

   public abstract String id();
   public abstract String name();
   public abstract String type();
   public abstract String location();
   @Nullable public abstract Map<String, String> tags();
   @Nullable public abstract Identity identity();
   @Nullable public abstract SKU sku();
   @Nullable public abstract String managedBy();
   @Nullable public abstract String kind();
   @Nullable public abstract Plan plan();
   @Nullable public abstract ResourceProperties properties();

   @SerializedNames({"id", "name", "type", "location", "tags", "identity", "sku", "managedBy", "kind", "plan", "properties"})
   public static Resource create(String id, String name, String type, String location, Map<String, String> tags, 
                                 Identity identity, SKU sku, String managedBy, String kind, Plan plan, ResourceProperties properties) {
      return new AutoValue_Resource(id, name, type, location, tags == null ? null : ImmutableMap.copyOf(tags), 
              identity, sku, managedBy, kind, plan, properties);
   }
}
