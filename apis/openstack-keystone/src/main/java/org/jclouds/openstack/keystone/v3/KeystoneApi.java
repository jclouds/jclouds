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
package org.jclouds.openstack.keystone.v3;

import java.io.Closeable;

import org.jclouds.openstack.keystone.v3.features.AuthApi;
import org.jclouds.openstack.keystone.v3.features.CatalogApi;
import org.jclouds.openstack.keystone.v3.features.ProjectApi;
import org.jclouds.openstack.keystone.v3.features.RegionApi;
import org.jclouds.openstack.keystone.v3.features.UserApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides access to the OpenStack Identity (Keystone) REST API.
 */
public interface KeystoneApi extends Closeable {

   /**
    * Provides access to authentication and token management features.
    */
   @Delegate
   AuthApi getAuthApi();

   /**
    * Provides access to service catalog features.
    */
   @Delegate
   CatalogApi getCatalogApi();
   
   /**
    * Provides access to region features.
    */
   @Delegate
   RegionApi getRegionApi();
   
   /**
    * Provides access to project features.
    */
   @Delegate
   ProjectApi getProjectApi();
   
   /**
    * Provides access to user features.
    */
   @Delegate
   UserApi getUserApi();
}
