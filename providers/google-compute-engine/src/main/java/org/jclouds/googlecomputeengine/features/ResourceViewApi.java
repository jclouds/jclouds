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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.NDEV_CLOUD_MAN_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.NDEV_CLOUD_MAN_SCOPE;

import java.net.URI;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.ResourceViewEndpoint;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.ResourceView;
import org.jclouds.googlecomputeengine.functions.internal.ParseRegionResourceViewMembers;
import org.jclouds.googlecomputeengine.functions.internal.ParseRegionResourceViews;
import org.jclouds.googlecomputeengine.functions.internal.ParseZoneResourceViewMembers;
import org.jclouds.googlecomputeengine.functions.internal.ParseZoneResourceViews;
import org.jclouds.googlecomputeengine.handlers.PayloadBinder;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.ResourceViewOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to Resource Views via their REST API.
 *
 * @see <a href="https://developers.google.com/compute/docs/resource-views/v1beta1/regionViews"/>
 * @see <a href="https://developers.google.com/compute/docs/resource-views/v1beta1/zoneViews"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
@Endpoint(value = ResourceViewEndpoint.class)
public interface ResourceViewApi {

   /**
    * Returns the specified resource view resource.
    *
    * @param zone                Name of the zone the resource view is in.
    * @param resourceViewName    Name of the resource view resource to return.
    * @return a ResourceView resource.
    */
   @Named("ResourceViews:get")
   @GET
   @Path("/zones/{zone}/resourceViews/{resourceView}")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ResourceView getInZone(@PathParam("zone") String zone,
                          @PathParam("resourceView") String resourceViewName);
   
   /**
    * Returns the specified resource view resource.
    *
    * @param region                Name of the region the resource view is in.
    * @param resourceViewName      Name of the resource view resource to return.
    * @return a ResourceView resource.
    */
   @Named("ResourceViews:get")
   @GET
   @Path("/regions/{region}/resourceViews/{resourceView}")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ResourceView getInRegion(@PathParam("region") String region,
                            @PathParam("resourceView") String resourceViewName);

   /**
    * Creates a zone resource view resource.
    *
    * @param zone       the zone this resource view will live in.
    * @param name       the name of resource view.
    * @return a ResourceView resource.
    */
   @Named("ResourceViews:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/zones/{zone}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   ResourceView createInZone(@PathParam("zone") String zone,
                             @PayloadParam("name") String name);
   
   /**
    * Creates a zone resource view resource with the given options.
    *
    * @param zone       the zone this resource view will live in.
    * @param name       the name of resource view.
    * @param options    the options this resource view will have.
    * @return a ResourceView resource.
    */
   @Named("ResourceViews:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/zones/{zone}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(PayloadBinder.class)
   ResourceView createInZone(@PathParam("zone") String zone,
                             @PayloadParam("name") String name,
                             @PayloadParam("options") ResourceViewOptions options);
   
   /**
    * Creates a region resource view resource.
    *
    * @param region     the region this resource view will live in.
    * @param name       the name of resource view.
    * @return a ResourceView resource.
    */
   @Named("ResourceViews:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   ResourceView createInRegion(@PathParam("region") String region,
                               @PayloadParam("name") String name);
   
   /**
    * Creates a region resource view resource with the given options.
    *
    * @param region     the region this resource view will live in.
    * @param name       the name of resource view.
    * @param options    the options this resource view will have.
    * @return a ResourceView resource.
    */
   @Named("ResourceViews:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(PayloadBinder.class)
   ResourceView createInRegion(@PathParam("region") String region,
                               @PayloadParam("name") String name,
                               @PayloadParam("options") ResourceViewOptions options);
   
   /**
    * Adds the given resources to the resource view resource with the given name.
    *
    * @param zone                the zone this resource view lives in.
    * @param resourceViewName    the name of resource view.
    * @param resources           the resources to add to this resource view.
    */
   @Named("ResourceViews:addResources")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/zones/{zone}/resourceViews/{resourceView}/addResources")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   void addResourcesInZone(@PathParam("zone") String zone,
                           @PathParam("resourceView") String resourceViewName,
                           @PayloadParam("resources") Set<URI> resources);
   
   /**
    * Adds the given resources to the resource view resource with the given name.
    *
    * @param region              the region this resource view lives in.
    * @param resourceViewName    the name of resource view.
    * @param resources           the resources to add to this resource view.
    */
   @Named("ResourceViews:addResources")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/resourceViews/{resourceView}/addResources")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   void addResourcesInRegion(@PathParam("region") String region,
                             @PathParam("resourceView") String resourceViewName,
                             @PayloadParam("resources") Set<URI> resources);
   
   /**
    * Removes the given resources from the resource view resource with the given name.
    *
    * @param zone                the zone this resource view lives in.
    * @param resourceViewName    the name of resource view.
    * @param resources           the resources to remove from this resource view.
    */
   @Named("ResourceViews:removeResources")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/zones/{zone}/resourceViews/{resourceView}/removeResources")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   void removeResourcesInZone(@PathParam("zone") String zone,
                              @PathParam("resourceView") String resourceViewName,
                              @PayloadParam("resources") Set<URI> resources);
   
   /**
    * Removes the given resources from the resource view resource with the given name.
    *
    * @param region              the region this resource view lives in.
    * @param resourceViewName    the name of resource view.
    * @param resources           the resources to remove from this resource view.
    */
   @Named("ResourceViews:removeResources")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/resourceViews/{resourceView}/removeResources")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   void removeResourcesInRegion(@PathParam("region") String region,
                                @PathParam("resourceView") String resourceViewName,
                                @PayloadParam("resources") Set<URI> resources);
   
   /**
    * @see ResourceViewApi#listResourcesAtMarkerInZone(String, String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/zones/{zone}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViewMembers.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<URI> listResourcesFirstPageInZone(@PathParam("zone") String zone,
                                              @PathParam("resourceView") String resourceViewName);

   /**
    * @see ResourceViewApi#listResourcesAtMarkerInZone(String, String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/zones/{zone}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViewMembers.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<URI> listResourcesAtMarkerInZone(@PathParam("zone") String zone,
                                             @PathParam("resourceView") String resourceViewName,
                                             @QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the listPage of resource view resources contained within the specified project and zone.
    * By default the listPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has
    * not been set.
    *
    * @param zone                the zone to search in.
    * @param resourceViewName    the name of the resource view resource to search under.
    * @param marker              marks the beginning of the next list page.
    * @param listOptions         listing options.
    * @return a page of the listPage.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/zones/{zone}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViewMembers.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<URI> listResourcesAtMarkerInZone(@PathParam("zone") String zone,
                                             @PathParam("resourceView") String resourceViewName,
                                             @QueryParam("pageToken") @Nullable String marker, 
                                             ListOptions listOptions);

   /**
    * A paged version of ResourceViewApi#listResourcesAtMarkerInZone(String, String).
    *
    * @param zone                the zone to list in.
    * @param resourceViewName    resource view resources to list in.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listResourcesAtMarkerInZone(String, String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/zones/{zone}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViewMembers.class)
   @Transform(ParseZoneResourceViewMembers.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<URI> listResourcesInZone(@PathParam("zone") String zone,
                                          @PathParam("resourceView") String resourceViewName);

   /**
    * A paged version of ResourceViewApi#listResourcesAtMarkerInZone(String, String).
    *
    * @param zone                the zone to list in.
    * @param resourceViewName    resource view resources to list in.
    * @param listOptions         listing options.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listResourcesAtMarkerInZone(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/zones/{zone}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViewMembers.class)
   @Transform(ParseZoneResourceViewMembers.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<URI> listResourcesInZone(@PathParam("zone") String zone,
                                          @PathParam("resourceView") String resourceViewName,
                                          ListOptions options);
   
   /**
    * @see ResourceViewApi#listResourcesAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/regions/{region}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViewMembers.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<URI> listResourcesFirstPageInRegion(@PathParam("region") String zone,
                                                @PathParam("resourceView") String resourceViewName);

   /**
    * @see ResourceViewApi#listResourcesAtMarkerInRegion(String, String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/regions/{region}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViewMembers.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<URI> listResourcesAtMarkerInRegion(@PathParam("region") String region,
                                               @PathParam("resourceView") String resourceViewName,
                                               @QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the listPage of resource view resources contained within the specified project and zone.
    * By default the listPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has
    * not been set.
    *
    * @param region              the region to search in.
    * @param resourceViewName    the name of the resource view resource to search under.
    * @param marker              marks the beginning of the next list page.
    * @param listOptions         listing options.
    * @return a page of the listPage.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/regions/{region}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViewMembers.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<URI> listResourcesAtMarkerInRegion(@PathParam("region") String region,
                                               @PathParam("resourceView") String resourceViewName,
                                               @QueryParam("pageToken") @Nullable String marker, 
                                               ListOptions listOptions);

   /**
    * A paged version of ResourceViewApi#listResourcesAtMarkerInRegion(String, String).
    *
    * @param region              the region to list in.
    * @param resourceViewName    resource view resources to list in.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listResourcesAtMarkerInZone(String, String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/regions/{region}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViewMembers.class)
   @Transform(ParseRegionResourceViewMembers.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<URI> listResourcesInRegion(@PathParam("region") String region,
                                            @PathParam("resourceView") String resourceViewName);

   /**
    * A paged version of ResourceViewApi#listResourcesAtMarkerInRegion(String, String).
    *
    * @param region              the region to list in.
    * @param resourceViewName    resource view resources to list in.
    * @param listOptions         listing options.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listResourcesAtMarkerInRegion(String, String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:listResources")
   @POST
   @Path("/regions/{region}/resourceViews/{resourceView}/resources")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViewMembers.class)
   @Transform(ParseRegionResourceViewMembers.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<URI> listResourcesInRgion(@PathParam("region") String region,
                                           @PathParam("resourceView") String resourceViewName,
                                           ListOptions options);

   /**
    * Deletes the specified resource view resource.
    *
    * @param zone                the zone the resource view is in.
    * @param resourceViewName    name of the resource view resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ResourceViews:delete")
   @DELETE
   @Path("/zones/{zone}/resourceViews/{resourceView}")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   void deleteInZone(@PathParam("zone") String zone,
                     @PathParam("resourceView") String resourceViewName);
   
   /**
    * Deletes the specified resource view resource.
    *
    * @param region              the region the resource view is in.
    * @param resourceViewName    name of the resource view resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ResourceViews:delete")
   @DELETE
   @Path("/regions/{region}/resourceViews/{resourceView}")
   @OAuthScopes(NDEV_CLOUD_MAN_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   void deleteInRegion(@PathParam("region") String zone,
                       @PathParam("resourceView") String resourceViewName);

   /**
    * @see ResourceViewApi#listAtMarkerInZone(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/zones/{zone}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViews.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ResourceView> listFirstPageInZone(@PathParam("zone") String zone);

   /**
    * @see ResourceViewApi#listAtMarkerInZone(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/zones/{zone}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViews.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ResourceView> listAtMarkerInZone(@PathParam("zone") String zone,
                                             @QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the listPage of resource view resources contained within the specified project and zone.
    * By default the listPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has
    * not been set.
    *
    * @param zone        the zone to search in.
    * @param marker      marks the beginning of the next list page.
    * @param listOptions listing options.
    * @return a page of the listPage.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/zones/{zone}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViews.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ResourceView> listAtMarkerInZone(@PathParam("zone") String zone,
                                             @QueryParam("pageToken") @Nullable String marker,
                                             ListOptions listOptions);

   /**
    * A paged version of ResourceViewApi#listAtMarkerInZone(String).
    *
    * @param zone the zone to list in.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listAtMarkerInZone(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/zones/{zone}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViews.class)
   @Transform(ParseZoneResourceViews.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ResourceView> listInZone(@PathParam("zone") String zone);

   /**
    * A paged version of ResourceViewApi#listMarkerInZone(String).
    *
    * @param zone the zone to list in.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listAtMarkerInZone(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/zones/{zone}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViews.class)
   @Transform(ParseZoneResourceViews.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ResourceView> listInZone(@PathParam("zone") String zone,
                                          ListOptions options);
   
   /**
    * @see ResourceViewApi#listAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/regions/{region}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViews.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ResourceView> listFirstPageInRegion(@PathParam("region") String region);

   /**
    * @see ResourceViewApi#listAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/regions/{region}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViews.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ResourceView> listAtMarkerInRegion(@PathParam("region") String region,
                                               @QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the listPage of resource view resources contained within the specified project and region.
    * By default the listPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has
    * not been set.
    *
    * @param region      the region to search in.
    * @param marker      marks the beginning of the next list page.
    * @param listOptions listing options.
    * @return a page of the listPage.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/regions/{region}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseRegionResourceViews.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ResourceView> listAtMarkerInRegion(@PathParam("region") String region,
                                               @QueryParam("pageToken") @Nullable String marker,
                                               ListOptions listOptions);

   /**
    * A paged version of ResourceViewApi#listAtMarkerInRegion(String).
    *
    * @param region the region to list in.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/regions/{region}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViews.class)
   @Transform(ParseRegionResourceViews.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ResourceView> listInRegion(@PathParam("region") String region);

   /**
    * A paged version of ResourceViewApi#listAtMarkerInRegion(String).
    *
    * @param region the region to list in.
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ResourceViewApi#listAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ResourceViews:list")
   @GET
   @Path("/regions/{region}/resourceViews")
   @OAuthScopes(NDEV_CLOUD_MAN_READONLY_SCOPE)
   @ResponseParser(ParseZoneResourceViews.class)
   @Transform(ParseRegionResourceViews.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ResourceView> listInRegion(@PathParam("region") String region,
                                            ListOptions options);
}
