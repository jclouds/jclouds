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
package org.jclouds.openstack.keystone.v2_0;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.extensions.RoleAdminApi;
import org.jclouds.openstack.keystone.v2_0.extensions.ServiceAdminApi;
import org.jclouds.openstack.keystone.v2_0.extensions.TenantAdminApi;
import org.jclouds.openstack.keystone.v2_0.extensions.UserAdminApi;
import org.jclouds.openstack.keystone.v2_0.features.ServiceApi;
import org.jclouds.openstack.keystone.v2_0.features.TenantApi;
import org.jclouds.openstack.keystone.v2_0.features.TokenApi;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.base.Optional;

/**
 * Provides access to the OpenStack Identity (Keystone) REST API.
 */
public interface KeystoneApi extends Closeable {

   /**
    * Discover API version information, links to documentation (PDF, HTML, WADL), and supported media types
    *
    * @return the {@link ApiMetadata}
    */
   @Named("keystone:getApiMetadata")
   @GET
   @SelectJson("version")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ApiMetadata getApiMetadata();

   /**
    * Provides access to Token features
    */
   @Delegate
   ServiceApi getServiceApi();

   /**
    * Provides access to Extension features.
    */
   @Delegate
   ExtensionApi getExtensionApi();

   /**
    * Provides access to Token features
    */
   @Delegate
   Optional<? extends TokenApi> getTokenApi();

   /**
    * Provides access to User features
    */
   @Delegate
   Optional<? extends UserApi> getUserApi();

   /**
    * Provides access to Tenant features
    */
   @Delegate
   Optional<? extends TenantApi> getTenantApi();

   /**
    * Provides access to Admin user features
    */
   @Delegate
   Optional<? extends UserAdminApi> getUserAdminApi();

   /**
    * Provides access to Admin tenant features
    */
   @Delegate
   Optional<? extends TenantAdminApi> getTenantAdminApi();

   /**
    * Provides access to Admin role features
    */
   @Delegate
   Optional<? extends RoleAdminApi> getRoleAdminApi();

   /**
    * Provides access to Admin service features
    */
   @Delegate
   Optional<? extends ServiceAdminApi> getServiceAdminApi();
}
