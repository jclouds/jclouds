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
package org.jclouds.softlayer;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.softlayer.features.AccountApi;
import org.jclouds.softlayer.features.DatacenterApi;
import org.jclouds.softlayer.features.SoftwareDescriptionApi;
import org.jclouds.softlayer.features.VirtualGuestApi;
import org.jclouds.softlayer.features.VirtualGuestBlockDeviceTemplateGroupApi;

import java.io.Closeable;

public interface SoftLayerApi extends Closeable {

   /**
    * Provides access to VirtualGuest features.
    */
   @Delegate
   VirtualGuestApi getVirtualGuestApi();

   /**
    * Provides access to Datacenter features.
    */
   @Delegate
   DatacenterApi getDatacenterApi();

   /**
    * Provides access to SoftwareDescription features.
    */
   @Delegate
   SoftwareDescriptionApi getSoftwareDescriptionApi();

   /**
    * Provides access to VirtualGuestBlockDeviceTemplateGroup features.
    */
   @Delegate
   VirtualGuestBlockDeviceTemplateGroupApi getVirtualGuestBlockDeviceTemplateGroupApi();

   /**
    * Provides access to Account features.
    */
   @Delegate
   AccountApi getAccountApi();
}
