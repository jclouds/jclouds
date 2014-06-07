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

import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.options.CreateUserOptions;
import org.jclouds.openstack.keystone.v2_0.options.UpdateUserOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;

/**
 * Provides synchronous access to User Administration actions.
 * <p/>
 * 
 * @see org.jclouds.openstack.keystone.v2_0.extensions.UserAdminAsyncApi
 */
@Beta
@Extension(of = ServiceType.IDENTITY, namespace = ExtensionNamespaces.OS_KSADM)
public interface UserAdminApi {
   
   /**
    * Creates a new user
    * 
    * @return the new user
    */
   User create(String name, String password);

   /**
    * Creates a new user
    * 
    * @return the new user
    */
   User create(String name, String password, CreateUserOptions options);

   /**
    * Deletes an user
    * 
    * @return true if successful
    */
   boolean delete(String userId);

   /**
    * Updates an user
    * 
    * @return the updated user
    */
   User update(String id, UpdateUserOptions options);

}
