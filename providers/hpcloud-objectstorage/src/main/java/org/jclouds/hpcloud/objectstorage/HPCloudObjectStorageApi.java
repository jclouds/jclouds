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
package org.jclouds.hpcloud.objectstorage;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.openstack.swift.SwiftFallbacks.TrueOn404FalseOn409;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.hpcloud.objectstorage.extensions.CDNContainerApi;
import org.jclouds.location.Region;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/** Provides synchronous access to HP Cloud Object Storage via the REST API. */
@Deprecated
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Storage.class)
public interface HPCloudObjectStorageApi extends CommonSwiftClient {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   @Override
   @Named("ListContainers")
   @GET
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/") Set<ContainerMetadata> listContainers(ListContainerOptions... options);

   @Override
   @Named("DeleteContainer")
   @DELETE
   @Fallback(TrueOn404FalseOn409.class)
   @Path("/{container}")
   boolean deleteContainerIfEmpty(@PathParam("container") String container);

   /**
    * Provides synchronous access to CDN features.
    */
   @Delegate
   Optional<CDNContainerApi> getCDNExtension();
}
