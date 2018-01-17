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
package org.jclouds.openstack.neutron.v2.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.location.Region;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class VersionAwareRegionToEndpoint implements Function<Object, URI> {

   private final Supplier<Map<String, Supplier<URI>>> regionToEndpointSupplier;

   @Inject
   public VersionAwareRegionToEndpoint(@Region Supplier<Map<String, Supplier<URI>>> regionToEndpointSupplier) {
      this.regionToEndpointSupplier = checkNotNull(regionToEndpointSupplier, "regionToEndpointSupplier");
   }

   /**
    * A quick fix to ensure Neutron works with endpoint definitions that are not version-agnostic.
    * The service-side API will always have a v2.0 in the path.
    * However, the endpoint will sometimes contain a v2.0 and sometimes it will not.
    * The VersionAwareRegionToEndpoint ensures that the endpoint will always look the same
    * before /v2.0 is added to it.
    *
    * Cannot leave labs until fixed:
    * TODO: https://issues.apache.org/jira/browse/JCLOUDS-773
    * This code will be unnecessary once this is supported.
    */
   @Override
   public URI apply(Object from) {
      Map<String, Supplier<URI>> regionToEndpoint = regionToEndpointSupplier.get();
      checkState(!regionToEndpoint.isEmpty(), "no region name to endpoint mappings configured!");
      checkArgument(regionToEndpoint.containsKey(from),
               "requested location %s, which is not in the configured locations: %s", from, regionToEndpoint);
      String uri = regionToEndpoint.get(from).get().toString();

      if (uri.endsWith("/v2.0")) {
         return URI.create(uri.substring(0, uri.length() - 5));
      }

      return regionToEndpoint.get(from).get();
   }
}
