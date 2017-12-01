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

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class ServicePrincipal {
   
   @Nullable public abstract String appId();
   @Nullable public abstract Date deletionTimestamp();
   @Nullable public abstract String displayName();
   public abstract String objectId();
   public abstract String objectType();
   public abstract List<String> servicePrincipalNames();

   @SerializedNames({ "appId", "deletionTimestamp", "displayName", "objectId", "objectType", "servicePrincipalNames" })
   public static ServicePrincipal create(String appId, Date deletionTimestamp, String displayName, String objectId,
         String objectType, List<String> servicePrincipalNames) {
      List<String> servicePrincipals = servicePrincipalNames != null ? ImmutableList.copyOf(servicePrincipalNames)
            : ImmutableList.<String> of();
      return builder().appId(appId).deletionTimestamp(deletionTimestamp).displayName(displayName).objectId(objectId)
            .objectType(objectType).servicePrincipalNames(servicePrincipals).build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_ServicePrincipal.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      
      public abstract Builder appId(String appId);
      public abstract Builder deletionTimestamp(Date deletionTimestamp);
      public abstract Builder displayName(String displayName);
      public abstract Builder objectId(String objectId);
      public abstract Builder objectType(String objectType);
      public abstract Builder servicePrincipalNames(List<String> servicePrincipalNames);

      public abstract ServicePrincipal build();
   }
}
