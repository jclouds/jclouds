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
package org.jclouds.rackspace.cloudfiles.v1.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_ENABLED;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_TTL;

import java.io.Closeable;
import java.net.URI;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudfiles.v1.binders.BindCDNPurgeEmailAddressesToHeaders;
import org.jclouds.rackspace.cloudfiles.v1.domain.CDNContainer;
import org.jclouds.rackspace.cloudfiles.v1.functions.ParseCDNContainerFromHeaders;
import org.jclouds.rackspace.cloudfiles.v1.functions.ParseCDNContainerURIFromHeaders;
import org.jclouds.rackspace.cloudfiles.v1.options.ListCDNContainerOptions;
import org.jclouds.rackspace.cloudfiles.v1.options.UpdateCDNContainerOptions;
import org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
/**
 * Provides access to the Rackspace Cloud Files CDN API features.
 *
 * <h3>NOTE</h3>
 * Before a container can be CDN enabled, it must exist in the storage system.
 * To CDN enable the container, perform PUT request against it using the <code>publicURL</code>
 * noted in the service catalog for Cloud Files during Authentication and set the
 * <code>X-CDN-Enabled</code> header to <code>true</code>.
 *
 * @see {@link org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi#getCDNApi(String)}
 */
@Beta
@RequestFilters(AuthenticateRequest.class)
@Consumes(APPLICATION_JSON)
public interface CDNApi extends Closeable {

   /**
    * Lists up to 10,000 CDN containers.
    *
    * @return a list of CDN enabled containers ordered by name.
    */
   @Named("cdn:list")
   @GET
   @QueryParams(keys = {"format", "enabled_only"}, values = {"json", "true"})
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<CDNContainer> list();

   /**
    * Lists CDN containers, with the given options.
    *
    * @param options
    *           the options to control output.
    *
    * @return a list of CDN enabled containers ordered by name.
    */
   @Named("cdn:list")
   @GET
   @QueryParams(keys = {"format", "enabled_only"}, values = {"json", "true"})
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<CDNContainer> list(ListCDNContainerOptions options);

   /**
    * Gets the specified CDN Container.
    *
    * @param containerName
    *           the name of the CDN Container
    *
    * @return the CDNContainer or null, if not found.
    */
   @Named("cdn:get")
   @HEAD
   @ResponseParser(ParseCDNContainerFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{container}")
   @Nullable
   CDNContainer get(@PathParam("container") String containerName);

   /**
    * Enables the {@link CDNContainer}.
    *
    * @param containerName
    *           corresponds to {@link CDNContainer#getName()}.
    *
    * @return the CDN container {@link URI} or {@code null}, if not found.
    */
   @Named("cdn:enable")
   @PUT
   @ResponseParser(ParseCDNContainerURIFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{containerName}")
   @Headers(keys = CDN_ENABLED, values = "true")
   @Nullable
   URI enable(@PathParam("containerName") String containerName);

   /**
    * Enables the {@link CDNContainer} with a TTL.
    *
    * @param containerName
    *           corresponds to {@link CDNContainer#getName()}.
    * @param ttl
    *           the TTL for the CDN Container.
    *
    * @return the CDN container {@link URI} or {@code null}, if not found.
    */
   @Named("cdn:enable")
   @PUT
   @ResponseParser(ParseCDNContainerURIFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{containerName}")
   @Headers(keys = CDN_ENABLED, values = "true")
   @Nullable
   URI enable(@PathParam("containerName") String containerName,
         @HeaderParam(CDN_TTL) int ttl);

   /**
    * Disables the {@link CDNContainer}.
    *
    * @param containerName
    *           corresponds to {@link CDNContainer#getName()}.
    *
    * @return {@code true} if the container was disabled, {@code false} if not.
    */
   @Named("cdn:disable")
   @PUT
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   @Headers(keys = CDN_ENABLED, values = "False")
   boolean disable(@PathParam("containerName") String containerName);

   /**
    * Purges an object from the CDN.
    *
    * @param containerName
    *           corresponds to {@link CDNContainer#getName()}.
    * @param objectName
    *           the object in the {@link CDNContainer} to purge.
    * @param emails
    *           the email addresses to notify after purging.
    *
    * @return {@code true} if the object was successfully purged, {@code false} if not.
    */
   @Named("cdn:purge")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}/{objectName}")
   @Headers(keys = CloudFilesHeaders.CDN_PURGE_OBJECT_EMAIL, values = "{email}")
   boolean purgeObject(@PathParam("containerName") String containerName,
         @PathParam("objectName") String objectName,
         @BinderParam(BindCDNPurgeEmailAddressesToHeaders.class) Iterable<String> emails);

   /**
    * Updates a CDN container with the supplied {@link UpdateCDNContainerOptions} options.
    *
    * @param containerName
    *           corresponds to {@link CDNContainer#getName()}.
    *
    * @param options
    *           the {@link UpdateCDNContainerOptions} options.
    */
   @Named("cdn:update")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   boolean update(@PathParam("containerName") String containerName, UpdateCDNContainerOptions options);
}
