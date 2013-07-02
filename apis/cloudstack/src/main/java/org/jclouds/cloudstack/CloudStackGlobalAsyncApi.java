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
package org.jclouds.cloudstack;

import org.jclouds.cloudstack.features.GlobalAccountAsyncApi;
import org.jclouds.cloudstack.features.GlobalAlertAsyncApi;
import org.jclouds.cloudstack.features.GlobalCapacityAsyncApi;
import org.jclouds.cloudstack.features.GlobalConfigurationAsyncApi;
import org.jclouds.cloudstack.features.GlobalDomainAsyncApi;
import org.jclouds.cloudstack.features.GlobalHostAsyncApi;
import org.jclouds.cloudstack.features.GlobalOfferingAsyncApi;
import org.jclouds.cloudstack.features.GlobalPodAsyncApi;
import org.jclouds.cloudstack.features.GlobalStoragePoolAsyncApi;
import org.jclouds.cloudstack.features.GlobalUsageAsyncApi;
import org.jclouds.cloudstack.features.GlobalUserAsyncApi;
import org.jclouds.cloudstack.features.GlobalVlanAsyncApi;
import org.jclouds.cloudstack.features.GlobalZoneAsyncApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to CloudStack via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @see CloudStackGlobalApi
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudStackGlobalApi.class)} as
 *             {@link CloudStackGlobalAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CloudStackGlobalAsyncApi extends CloudStackDomainAsyncApi {

   /**
    * Provides asynchronous access to Accounts
    */
   @Delegate
   @Override
   GlobalAccountAsyncApi getAccountApi();

   /**
    * Provides asynchronous access to Users
    */
   @Delegate
   @Override
   GlobalUserAsyncApi getUserApi();

   /**
    * Provides asynchronous access to Alerts
    */
   @Delegate
   GlobalAlertAsyncApi getAlertApi();

   /**
    * Provides asynchronous access to Capacities
    */
   @Delegate
   GlobalCapacityAsyncApi getCapacityApi();

   /**
    * Provides asynchronous access to Offerings
    */
   @Delegate
   @Override
   GlobalOfferingAsyncApi getOfferingApi();

   /**
    * Provides asynchronous access to Hosts
    */
   @Delegate
   GlobalHostAsyncApi getHostApi();

   /**
    * Provides synchronous access to Storage Pools
    */
   @Delegate
   GlobalStoragePoolAsyncApi getStoragePoolApi();

   /**
    * Provides asynchronous access to Usage
    */
   @Delegate
   GlobalUsageAsyncApi getUsageApi();

   /**
    * Provides asynchronous access to Configuration
    */
   @Delegate
   @Override
   GlobalConfigurationAsyncApi getConfigurationApi();

   /**
    * Provides asynchronous access to Domain
    */
   @Delegate
   @Override
   GlobalDomainAsyncApi getDomainApi();

   /**
    * Provides asynchronous access to Zone
    */
   @Delegate
   @Override
   GlobalZoneAsyncApi getZoneApi();

   /**
    * Provides asynchronous access to Pod
    */
   @Delegate
   GlobalPodAsyncApi getPodApi();

   /**
    * Provides asynchronous access to Vlan
    */
   @Delegate
   GlobalVlanAsyncApi getVlanApi();
}
