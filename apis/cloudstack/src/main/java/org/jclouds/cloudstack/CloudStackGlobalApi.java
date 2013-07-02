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

import org.jclouds.cloudstack.features.GlobalAccountApi;
import org.jclouds.cloudstack.features.GlobalAlertApi;
import org.jclouds.cloudstack.features.GlobalCapacityApi;
import org.jclouds.cloudstack.features.GlobalConfigurationApi;
import org.jclouds.cloudstack.features.GlobalDomainApi;
import org.jclouds.cloudstack.features.GlobalHostApi;
import org.jclouds.cloudstack.features.GlobalOfferingApi;
import org.jclouds.cloudstack.features.GlobalPodApi;
import org.jclouds.cloudstack.features.GlobalStoragePoolApi;
import org.jclouds.cloudstack.features.GlobalUsageApi;
import org.jclouds.cloudstack.features.GlobalUserApi;
import org.jclouds.cloudstack.features.GlobalVlanApi;
import org.jclouds.cloudstack.features.GlobalZoneApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to CloudStack.
 * <p/>
 * 
 * @deprecated  The *Api classes will be replaced with the *Api classes in jclouds 1.7.
 * @author Adrian Cole
 * @see CloudStackDomainAsyncApi
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
@Deprecated
public interface CloudStackGlobalApi extends CloudStackDomainApi {

   /**
    * Provides synchronous access to Accounts
    */
   @Delegate
   @Override
   GlobalAccountApi getAccountApi();

   /**
    * Provides synchronous access to Users
    */
   @Delegate
   @Override
   GlobalUserApi getUserApi();

   /**
    * Provides synchronous access to Alerts
    */
   @Delegate
   GlobalAlertApi getAlertApi();

   /**
    * Provides synchronous access to Capacities
    */
   @Delegate
   GlobalCapacityApi getCapacityApi();

   /**
    * Provides synchronous access to Offerings
    */
   @Delegate
   @Override
   GlobalOfferingApi getOfferingApi();

   /**
    * Provides synchronous access to Hosts
    */
   @Delegate
   GlobalHostApi getHostApi();

   /**
    * Provides synchronous access to Storage Pools
    */
   @Delegate
   GlobalStoragePoolApi getStoragePoolApi();

   /**
    * Provides synchronous access to Usage
    */
   @Delegate
   GlobalUsageApi getUsageApi();

   /**
    * Provides synchronous access to Configuration
    */
   @Delegate
   @Override
   GlobalConfigurationApi getConfigurationApi();

   /**
    * Provides synchronous access to Domain
    */
   @Delegate
   @Override
   GlobalDomainApi getDomainApi();

   /**
    * Provides synchronous access to Zone
    */
   @Delegate
   @Override
   GlobalZoneApi getZoneApi();

   /**
    * Provides synchronous access to Pod
    */
   @Delegate
   GlobalPodApi getPodApi();

   /**
    * Provides synchronous access to Vlan
    */
   @Delegate
   GlobalVlanApi getVlanApi();
}
