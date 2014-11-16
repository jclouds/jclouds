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
package org.jclouds.openstack.keystone.v2_0.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURISupplier;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public final class RegionToAdminEndpointURI implements Function<Object, URI> {

   private final RegionIdToAdminURISupplier regionToAdminEndpoints;

   @Inject
   RegionToAdminEndpointURI(RegionIdToAdminURISupplier regionToAdminEndpoints) {
      this.regionToAdminEndpoints = regionToAdminEndpoints;
   }

   @Override
   public URI apply(Object from) {
      Map<String, Supplier<URI>> regionToAdminEndpoint = regionToAdminEndpoints.get();
      checkState(!regionToAdminEndpoint.isEmpty(), "no region name to admin endpoint mappings in keystone!");
      checkArgument(regionToAdminEndpoint.containsKey(from),
            "requested location %s, which is not in the keystone admin endpoints: %s", from, regionToAdminEndpoint);
      return regionToAdminEndpoint.get(from).get();
   }
}
