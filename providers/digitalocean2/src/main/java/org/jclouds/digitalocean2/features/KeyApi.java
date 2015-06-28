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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.digitalocean2.domain.internal.PaginatedCollection;
import org.jclouds.digitalocean2.domain.options.ListOptions;
import org.jclouds.digitalocean2.functions.BaseToPagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;

/**
 * Provides access to Keys via the REST API.
 *
 * @see <a href="https://developers.digitalocean.com/v2/#keys"/>
 * @see KeyApi
 */
@Path("/account/keys")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface KeyApi extends Closeable {

   @Named("key:list")
   @GET
   @ResponseParser(ParseKeys.class)
   @Transform(ParseKeys.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Key> list();

   @Named("key:list")
   @GET
   @ResponseParser(ParseKeys.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Key> list(ListOptions options);
   
   static final class ParseKeys extends ParseJson<ParseKeys.Keys> {
      @Inject ParseKeys(Json json) {
         super(json, TypeLiteral.get(Keys.class));
      }

      private static class Keys extends PaginatedCollection<Key> {
         @ConstructorProperties({ "ssh_keys", "meta", "links" })
         public Keys(List<Key> items, Meta meta, Links links) {
            super(items, meta, links);
         }
      }

      private static class ToPagedIterable extends BaseToPagedIterable<Key, ListOptions> {
         @Inject ToPagedIterable(DigitalOcean2Api api, Function<URI, ListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<Key> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
            return api.keyApi().list(options);
         }
      }
   }

   @Named("key:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("ssh_key")
   @MapBinder(BindToJsonPayload.class)
   Key create(@PayloadParam("name") String name, @PayloadParam("public_key") String key);

   @Named("key:get")
   @GET
   @SelectJson("ssh_key")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Key get(@PathParam("id") int id);

   @Named("key:get")
   @GET
   @SelectJson("ssh_key")
   @Path("/{fingerprint}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Key get(@PathParam("fingerprint") String fingerprint);

   @Named("key:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("ssh_key")
   @Path("/{id}")
   @MapBinder(BindToJsonPayload.class)
   Key update(@PathParam("id") int id, @PayloadParam("name") String name);

   @Named("key:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("ssh_key")
   @Path("/{fingerprint}")
   @MapBinder(BindToJsonPayload.class)
   Key update(@PathParam("fingerprint") String fingerprint, @PayloadParam("name") String name);

   @Named("key:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("id") int id);

   @Named("key:delete")
   @DELETE
   @Path("/{fingerprint}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("fingerprint") String fingerprint);

}

