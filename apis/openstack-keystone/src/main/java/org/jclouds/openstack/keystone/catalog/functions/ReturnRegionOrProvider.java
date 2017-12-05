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
package org.jclouds.openstack.keystone.catalog.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Provider;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint;

@Singleton
public class ReturnRegionOrProvider implements ServiceEndpointToRegion {
   private final String provider;

   @Inject
   ReturnRegionOrProvider(@Provider String provider) {
      this.provider = provider;
   }

   @Override
   public String apply(ServiceEndpoint input) {
      return input.regionId() != null ? input.regionId() : provider;
   }
}
