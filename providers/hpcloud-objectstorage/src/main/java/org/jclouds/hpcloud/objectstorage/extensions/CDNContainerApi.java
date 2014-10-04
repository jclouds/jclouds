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
package org.jclouds.hpcloud.objectstorage.extensions;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import static org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import static org.jclouds.hpcloud.objectstorage.reference.HPCloudObjectStorageHeaders.CDN_ENABLED;
import static org.jclouds.hpcloud.objectstorage.reference.HPCloudObjectStorageHeaders.CDN_TTL;

import java.net.URI;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.hpcloud.objectstorage.domain.CDNContainer;
import org.jclouds.hpcloud.objectstorage.functions.ParseCDNContainerFromHeaders;
import org.jclouds.hpcloud.objectstorage.functions.ParseCDNUriFromHeaders;
import org.jclouds.hpcloud.objectstorage.options.ListCDNContainerOptions;
import org.jclouds.hpcloud.services.HPExtensionCDN;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

@Beta
@RequestFilters(AuthenticateRequest.class)
@Endpoint(HPExtensionCDN.class)
public interface CDNContainerApi  {

   @Beta
   @Named("ListCDNEnabledContainers")
   @GET
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<CDNContainer> list();

   @Beta
   @Named("ListCDNEnabledContainers")
   @GET
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<CDNContainer> list(ListCDNContainerOptions options);

   @Beta
   @Named("ListCDNEnabledContainerMetadata")
   @HEAD
   @ResponseParser(ParseCDNContainerFromHeaders.class)
   @Fallback(NullOnContainerNotFound.class)
   @Path("/{container}")
   CDNContainer get(@PathParam("container") String container);

   @Beta
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CDN_ENABLED, values = "True")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   URI enable(@PathParam("container") String container, @HeaderParam(CDN_TTL) long ttl);

   @Beta
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CDN_ENABLED, values = "True")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   URI enable(@PathParam("container") String container);

   @Beta
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   URI update(@PathParam("container") String container, @HeaderParam(CDN_TTL) long ttl);

   @Beta
   @Named("DisableCDNEnabledContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CDN_ENABLED, values = "False")
   boolean disable(@PathParam("container") String container);
}
