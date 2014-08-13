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
package org.jclouds.rackspace.cloudfiles.v1.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * This class ensures that the correct Cloud Files CDN endpoint is retrieved from the endpoint
 * supplier. The CDN API should never be instantiated directly, but rather accessed through the
 * {@link CloudFilesApi#cdnApiInRegion(String)} API.
 * <p/>
 * <h3>NOTE</h3>
 * The Cloud Files Service Type will always default to OpenStack Object Storage ("object-storage").
 * <p/>
 *
 *
 * @see CloudFilesApi#cdnApiInRegion(String)
 * @see CDNApi
 * @see RegionToEndpoint
 * @see org.jclouds.openstack.v2_0.ServiceType#OBJECT_STORE
 * @see org.jclouds.rackspace.cloudidentity.v2_0.ServiceType#OBJECT_CDN
 * @see <a
 *      href="http://docs.rackspace.com/files/api/v1/cf-devguide/content/Service-Access-Endpoints-d1e003.html">
 *      Service Access Endpoints</a>
 */
@Singleton
public class RegionToCDNEndpoint implements Function<Object, URI> {

   private final Supplier<Map<String, Supplier<URI>>> endpointsSupplier;

   @Inject
   public RegionToCDNEndpoint(@ApiVersion final String apiVersion, final RegionIdToURISupplier.Factory factory) {
      this.endpointsSupplier = factory.createForApiTypeAndVersion(ServiceType.OBJECT_CDN, apiVersion);
   }

   public URI apply(@Nullable Object from) {
      checkArgument(from != null && from instanceof String, "you must specify a region, as a String argument");
      Map<String, Supplier<URI>> regionToEndpoint = endpointsSupplier.get();
      checkState(!regionToEndpoint.isEmpty(), "no region name to endpoint mappings configured!");
      checkArgument(regionToEndpoint.containsKey(from),
            "requested location %s, which is not in the configured locations: %s", from, regionToEndpoint);
      return regionToEndpoint.get(from).get();
   }
}
