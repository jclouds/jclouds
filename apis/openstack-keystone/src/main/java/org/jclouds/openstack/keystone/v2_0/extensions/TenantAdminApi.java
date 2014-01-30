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
package org.jclouds.openstack.keystone.v2_0.extensions;

import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.options.CreateTenantOptions;
import org.jclouds.openstack.keystone.v2_0.options.UpdateTenantOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;

/**
 * Provides synchronous access to Tenant Administration actions.
 * <p/>
 * 
 * @see org.jclouds.openstack.keystone.v2_0.extensions.TenantAdminAsyncApi
 * @author Pedro Navarro
 */
@Beta
@Extension(of = ServiceType.IDENTITY, namespace = ExtensionNamespaces.OS_KSADM)
public interface TenantAdminApi {

   /**
    * Creates a new tenant
    * 
    * @return the new tenant
    */
   Tenant create(String name);
   
   /**
    * Creates a new tenant
    * 
    * @return the new tenant
    */
   Tenant create(String name, CreateTenantOptions options);

   /**
    * Deletes a tenant
    * 
    * @return true if successful
    */
   boolean delete(String userId);

   /**
    * Updates a tenant
    * 
    * @return the updated tenant
    */
   Tenant update(String id, UpdateTenantOptions options);

   /**
    * Adds role to a user on a tenant
    * 
    * @return true if successful
    */
   boolean addRoleOnTenant(String tenantId, String userId, String roleId);

   /**
    * Deletes role to a user on tenant
    * 
    * @return true if successful
    */
   boolean deleteRoleOnTenant(String tenantId, String userId, String roleId);

}
