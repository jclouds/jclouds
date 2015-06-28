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
package org.jclouds.digitalocean2.features;

import java.beans.ConstructorProperties;
import java.io.Closeable;
import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.domain.internal.PaginatedCollection;
import org.jclouds.digitalocean2.domain.options.ImageListOptions;
import org.jclouds.digitalocean2.functions.BaseToPagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;

/**
 * Provides access to Images via the REST API.
 *
 * @see <a href="https://developers.digitalocean.com/v2/#images"/>
 * @see ImageApi
 */
@Path("/images")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ImageApi extends Closeable {

   @Named("image:list")
   @GET
   @ResponseParser(ParseImages.class)
   @Transform(ParseImages.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Image> list();
   
   @Named("image:list")
   @GET
   @ResponseParser(ParseImages.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Image> list(ImageListOptions options);
   
   static final class ParseImages extends ParseJson<ParseImages.Images> {
      @Inject ParseImages(Json json) {
         super(json, TypeLiteral.get(Images.class));
      }

      private static class Images extends PaginatedCollection<Image> {
         @ConstructorProperties({ "images", "meta", "links" })
         public Images(List<Image> items, Meta meta, Links links) {
            super(items, meta, links);
         }
      }

      private static class ToPagedIterable extends BaseToPagedIterable<Image, ImageListOptions> {
         @Inject ToPagedIterable(DigitalOcean2Api api, Function<URI, ImageListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<Image> fetchPageUsingOptions(ImageListOptions options, Optional<Object> arg0) {
            return api.imageApi().list(options);
         }
      }
   }

   @Named("image:get")
   @GET
   @SelectJson("image")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Image get(@PathParam("id") int id);

   @Named("image:get")
   @GET
   @SelectJson("image")
   @Path("/{slug}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Image get(@PathParam("slug") String slug);

   @Named("image:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("id") int id);

   //TODO: Add delete and create

}

