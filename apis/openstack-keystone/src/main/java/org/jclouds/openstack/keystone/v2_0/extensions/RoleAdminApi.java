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

import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Role Administration actions.
 * <p/>
 * 
 * @see org.jclouds.openstack.keystone.v2_0.extensions.RoleAdminAsyncApi
 * @author Pedro Navarro
 */
@Beta
@Extension(of = ServiceType.IDENTITY, namespace = ExtensionNamespaces.OS_KSADM)
public interface RoleAdminApi {

   /**
    * Returns a summary list of roles.
    * 
    * @return The list of roles
    */
   FluentIterable<? extends Role> list();

   /**
    * Creates a new Role
    * 
    * @return the new Role
    */
   Role create(String name);

   /**
    * Gets the role
    * 
    * @return the role
    */
   Role get(String roleId);

   /**
    * Deletes a role
    * 
    * @return true if successful
    */
   boolean delete(String roleId);

}
