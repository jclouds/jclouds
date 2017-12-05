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
package org.jclouds.openstack.keystone.catalog;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;

/**
 * Common properties for OpenStack service endpoints.
 * <p>
 * This class provides a common view on the service catalog endpoints so it can
 * be parsed in a generic way for Keystone v2 and v3.
 */
@AutoValue
public abstract class ServiceEndpoint {

   public enum Interface {
      PUBLIC, ADMIN, INTERNAL, UNRECOGNIZED;

      public static Interface fromValue(String iface) {
         return Enums.getIfPresent(Interface.class, iface.toUpperCase()).or(UNRECOGNIZED);
      }
   }

   @Nullable public abstract String id();
   @Nullable public abstract String regionId();
   public abstract URI url();
   public abstract Interface iface();
   public abstract String type();
   @Nullable public abstract String version();
   
   ServiceEndpoint() {
      
   }
   
   public static Builder builder() {
      return new AutoValue_ServiceEndpoint.Builder();
   }
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder regionId(String regionId);
      public abstract Builder url(URI url);
      public abstract Builder iface(Interface iface);
      public abstract Builder type(String type);
      public abstract Builder version(String version);
      
      public Builder iface(String iface) {
         return iface(Interface.fromValue(iface));
      }
      
      public abstract ServiceEndpoint build();
   }

}
