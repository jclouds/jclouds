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
package org.jclouds.openstack.keystone.v3.domain;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Endpoint {

   @Nullable public abstract String id();
   @Nullable public abstract String region();
   @Nullable public abstract String regionId();
   @Nullable public abstract String serviceId();
   public abstract URI url();
   @Nullable public abstract Boolean enabled();
   public abstract String iface();

   @SerializedNames({ "id", "region", "region_id", "service_id", "url", "enabled", "interface" })
   public static Endpoint create(String id, String region, String regionId, String serviceId, URI url, Boolean enabled,
         String iface) {
      return builder().id(serviceId).region(region).regionId(regionId).serviceId(serviceId).url(url).enabled(enabled)
            .iface(iface).build();
   }

   Endpoint() {
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Endpoint.Builder();
   }
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder region(String region);
      public abstract Builder regionId(String regionId);
      public abstract Builder serviceId(String serviceId);
      public abstract Builder url(URI url);
      public abstract Builder enabled(Boolean enabled);
      public abstract Builder iface(String iface);
      public abstract Endpoint build();
   }
}
