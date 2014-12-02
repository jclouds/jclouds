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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseArg0ToIteratorOfListPage;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.DeprecateOptions;
import org.jclouds.googlecomputeengine.options.ImageCreationOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Consumes(APPLICATION_JSON)
public interface ImageApi {

   /** Returns an image by self-link or null if not found. */
   @Named("Images:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Image get(@EndpointParam URI selfLink);

   /** Returns an image by name or null if not found. */
   @Named("Images:get ")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/global/images/{image}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Image get(@PathParam("image") String image);

   /** Deletes an image by name and returns the operation in progress, or null if not found. */
   @Named("Images:delete")
   @DELETE
   @Endpoint(CurrentProject.class)
   @Path("/global/images/{image}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("image") String image);

   /**
    * Creates an image resource in the specified project from the provided persistent disk.
    *
    * @param image  the name of the created image
    * @param sourceDisk fully qualified URL for the persistent disk to create the image from
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Images:insert")
   @POST
   @Endpoint(CurrentProject.class)
   @Path("/global/images")
   @Produces(APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Operation createFromDisk(@PayloadParam("name") String image, @PayloadParam("sourceDisk") String sourceDisk);

   @Named("Images:insert")
   @POST
   @Endpoint(CurrentProject.class)
   @Path("/global/images")
   @Produces(APPLICATION_JSON)
   Operation create(@BinderParam(BindToJsonPayload.class) ImageCreationOptions options);

   /**
    * Sets the deprecation status of an image. If no message body is given, clears the deprecation status instead.
    *
    * @param image  The Image resource to deprecate.
    * @param deprecated the deprecation status to return
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Images:deprecate")
   @POST
   @Endpoint(CurrentProject.class)
   @Path("/global/images/{image}/deprecate")
   Operation deprecate(@PathParam("image") String image, @BinderParam(BindToJsonPayload.class) DeprecateOptions deprecated);

   /**
    * Retrieves the list of image resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Images:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/global/images")
   ListPage<Image> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("Images:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/global/images")
   @Transform(ImagePages.class)
   Iterator<ListPage<Image>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("Images:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/global/images")
   @Transform(ImagePages.class)
   Iterator<ListPage<Image>> list(ListOptions options);

   static final class ImagePages extends BaseToIteratorOfListPage<Image, ImagePages> {

      private final GoogleComputeEngineApi api;

      @Inject ImagePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Image>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Image>>() {
            @Override public ListPage<Image> apply(String pageToken) {
               return api.images().listPage(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of image resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Images:list")
   @GET
   @Path("/projects/{project}/global/images")
   ListPage<Image> listPageInProject(@PathParam("project") String projectName,
         @Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /**
    * @see #listPageInProject(String, String, ListOptions)
    */
   @Named("Images:list")
   @GET
   @Path("/projects/{project}/global/images")
   @Transform(ImagePagesInProject.class)
   Iterator<ListPage<Image>> listInProject(@PathParam("project") String projectName);

   /**
    * @see #listPageInProject(String, String, ListOptions)
    */
   @Named("Images:list")
   @GET
   @Path("/projects/{project}/global/images")
   @Transform(ImagePagesInProject.class)
   Iterator<ListPage<Image>> listInProject(@PathParam("project") String projectName, ListOptions options);

   static final class ImagePagesInProject extends BaseArg0ToIteratorOfListPage<Image, ImagePagesInProject> {

      private final GoogleComputeEngineApi api;

      @Inject ImagePagesInProject(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Image>> fetchNextPage(final String projectName, final ListOptions options) {
         return new Function<String, ListPage<Image>>() {
            @Override public ListPage<Image> apply(String pageToken) {
               return api.images().listPageInProject(projectName, pageToken, options);
            }
         };
      }
   }
}
