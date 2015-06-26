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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMapValidateResult;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Consumes(APPLICATION_JSON)
public interface UrlMapApi {

   /** Returns the specified urlMap resource by name or null if not found. */
   @Named("UrlMaps:get")
   @GET
   @Path("/{urlMap}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   UrlMap get(@PathParam("urlMap") String urlMapName);

   /**
    * Creates a urlMap resource in the specified project using the data included in the request.
    * @param urlMapOptions   the options of the urlMap to add.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:insert")
   @POST
   @Produces(APPLICATION_JSON)
   Operation create(@BinderParam(BindToJsonPayload.class) UrlMapOptions urlMapOptions);

   /**
    * Creates a urlMap resource in the specified project using the data included in the request.
    *
    * @param name            the name of the urlMap to be inserted.
    * @param defaultService  the default backend service of the urlMap to add.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String name,
                    @PayloadParam("defaultService") URI defaultService);

   /**
    * Updates the specified urlMap resource with the data included in the request.
    *
    * @param urlMapName    the name urlMap to be updated.
    * @param urlMapOptions the new urlMap options.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:update")
   @PUT
   @Produces(APPLICATION_JSON)
   @Path("/{urlMap}")
   Operation update(@PathParam("urlMap") String urlMapName,
                    @BinderParam(BindToJsonPayload.class) UrlMapOptions urlMapOptions);

   /**
    * Updates the specified urlMap resource, with patch semantics, with the data included in the request.
    * Note:{@link UrlMapOptions.Builder#buildForPatch()} may be helpful.
    *
    * @param urlMapName    the name urlMap to be updated.
    * @param urlMapOptions the new urlMap options.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:patch")
   @PATCH
   @Produces(APPLICATION_JSON)
   @Path("/{urlMap}")
   Operation patch(@PathParam("urlMap") String urlMapName,
                   @BinderParam(BindToJsonPayload.class) UrlMapOptions urlMapOptions);

   /**
    * Deletes the specified urlMap resource.
    *
    * @param urlMapName name of the urlMap resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   @Named("UrlMaps:delete")
   @DELETE
   @Path("/{urlMap}")
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("urlMap") String urlMapName);

   /**
    * Runs the tests specified for the give urlMap resource.
    *
    * @param urlMapName name of the urlMap to run tests on.
    * @param options    options that represent the url map to be tested.
    * @return the result of the tests for the given urlMap resource.
    */
   @Named("UrlMaps:validate")
   @POST
   @Path("/{urlMap}/validate")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   @MapBinder(BindToJsonPayload.class)
   UrlMapValidateResult validate(@PathParam("urlMap") String urlMapName,
                                 @PayloadParam("resource") UrlMapOptions options);

   /**
    * Runs the tests specified for the give urlMap resource.
    *
    * @param urlMapName name of the urlMap to run tests on.
    * @param urlMap     the url map to be tested.
    * @return the result of the tests for the given urlMap resource.
    */
   @Named("UrlMaps:validate")
   @POST
   @Path("/{urlMap}/validate")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   @MapBinder(BindToJsonPayload.class)
   UrlMapValidateResult validate(@PathParam("urlMap") String urlMapName,
                                 @PayloadParam("resource") UrlMap urlMap);

   /**
    * Retrieves the list of urlMap resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("UrlMaps:list")
   @GET
   ListPage<UrlMap> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, String, ListOptions) */
   @Named("UrlMaps:list")
   @GET
   @Transform(UrlMapPages.class)
   Iterator<ListPage<UrlMap>> list();

   /** @see #listPage(String, String, ListOptions) */
   @Named("UrlMaps:list")
   @GET
   @Transform(UrlMapPages.class)
   Iterator<ListPage<UrlMap>> list(ListOptions options);

   static final class UrlMapPages extends BaseToIteratorOfListPage<UrlMap, UrlMapPages> {

      private final GoogleComputeEngineApi api;

      @Inject UrlMapPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<UrlMap>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<UrlMap>>() {
            @Override public ListPage<UrlMap> apply(String pageToken) {
               return api.urlMaps().listPage(pageToken, options);
            }
         };
      }
   }
}
