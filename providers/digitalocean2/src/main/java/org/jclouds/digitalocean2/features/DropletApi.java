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
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Backup;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Kernel;
import org.jclouds.digitalocean2.domain.Snapshot;
import org.jclouds.digitalocean2.domain.internal.PaginatedCollection;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
import org.jclouds.digitalocean2.domain.options.ListOptions;
import org.jclouds.digitalocean2.functions.BaseToPagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
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
 * Provides access to Droplets via their REST API.
 *
 * @see <a href="https://developers.digitalocean.com/v2/#droplets"/>
 * @see DropletApi
 */
@Path("/droplets")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface DropletApi extends Closeable {

   @Named("droplet:list")
   @GET
   @ResponseParser(ParseDroplets.class)
   @Transform(ParseDroplets.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Droplet> list();

   @Named("droplet:list")
   @GET
   @ResponseParser(ParseDroplets.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Droplet> list(ListOptions options);

   static final class ParseDroplets extends ParseJson<ParseDroplets.Droplets> {
      @Inject ParseDroplets(Json json) {
         super(json, TypeLiteral.get(Droplets.class));
      }

      private static class Droplets extends PaginatedCollection<Droplet> {
         @ConstructorProperties({ "droplets", "meta", "links" })
         public Droplets(List<Droplet> items, Meta meta, Links links) {
            super(items, meta, links);
         }
      }

      private static class ToPagedIterable extends BaseToPagedIterable<Droplet, ListOptions> {
         @Inject ToPagedIterable(DigitalOcean2Api api, Function<URI, ListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<Droplet> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
            return api.dropletApi().list(options);
         }
      }
   }

   @Named("droplet:listkernels")
   @GET
   @Path("/{id}/kernels")
   @ResponseParser(ParseKernels.class)
   @Transform(ParseKernels.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Kernel> listKernels(@PathParam("id") int id);

   @Named("droplet:listkernels")
   @GET
   @Path("/{id}/kernels")
   @ResponseParser(ParseKernels.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Kernel> listKernels(@PathParam("id") int id, ListOptions options);

   static final class ParseKernels extends ParseJson<ParseKernels.Kernels> {
      @Inject ParseKernels(Json json) {
         super(json, TypeLiteral.get(Kernels.class));
      }

      private static class Kernels extends PaginatedCollection<Kernel> {
         @ConstructorProperties({ "kernels", "meta", "links" })
         public Kernels(List<Kernel> items, Meta meta, Links links) {
            super(items, meta, links);
         }
      }

      private static class ToPagedIterable extends BaseToPagedIterable<Kernel, ListOptions> {
         @Inject ToPagedIterable(DigitalOcean2Api api, Function<URI, ListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<Kernel> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
            return api.dropletApi().listKernels((Integer) arg0.get(), options);
         }
      }
   }

   @Named("droplet:listsnapshots")
   @GET
   @Path("/{id}/snapshots")
   @ResponseParser(ParseSnapshots.class)
   @Transform(ParseSnapshots.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Snapshot> listSnapshots(@PathParam("id") int id);

   @Named("droplet:listsnapshots")
   @GET
   @Path("/{id}/snapshots")
   @ResponseParser(ParseSnapshots.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Snapshot> listSnapshots(@PathParam("id") int id, ListOptions options);

   static final class ParseSnapshots extends ParseJson<ParseSnapshots.Snapshots> {
      @Inject ParseSnapshots(Json json) {
         super(json, TypeLiteral.get(Snapshots.class));
      }

      private static class Snapshots extends PaginatedCollection<Snapshot> {
         @ConstructorProperties({ "snapshots", "meta", "links" })
         public Snapshots(List<Snapshot> items, Meta meta, Links links) {
            super(items, meta, links);
         }
      }

      private static class ToPagedIterable extends BaseToPagedIterable<Snapshot, ListOptions> {
         @Inject ToPagedIterable(DigitalOcean2Api api, Function<URI, ListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<Snapshot> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
            return api.dropletApi().listSnapshots((Integer) arg0.get(), options);
         }
      }
   }

   @Named("droplet:listbackups")
   @GET
   @Path("/{id}/backups")
   @ResponseParser(ParseBackups.class)
   @Transform(ParseBackups.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Backup> listBackups(@PathParam("id") int id);

   @Named("droplet:listbackups")
   @GET
   @Path("/{id}/backups")
   @ResponseParser(ParseBackups.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Backup> listBackups(@PathParam("id") int id, ListOptions options);

   static final class ParseBackups extends ParseJson<ParseBackups.Backups> {
      @Inject ParseBackups(Json json) {
         super(json, TypeLiteral.get(Backups.class));
      }

      private static class Backups extends PaginatedCollection<Backup> {
         @ConstructorProperties({ "backups", "meta", "links" })
         public Backups(List<Backup> items, Meta meta, Links links) {
            super(items, meta, links);
         }
      }

      private static class ToPagedIterable extends BaseToPagedIterable<Backup, ListOptions> {
         @Inject ToPagedIterable(DigitalOcean2Api api, Function<URI, ListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<Backup> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
            return api.dropletApi().listBackups((Integer) arg0.get(), options);
         }
      }
   }

   @Named("droplet:actions")
   @GET
   @Path("/{id}/actions")
   @ResponseParser(ParseDropletActions.class)
   @Transform(ParseDropletActions.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Action> listActions(@PathParam("id") int id);

   @Named("droplet:actions")
   @GET
   @Path("/{id}/actions")
   @ResponseParser(ParseDropletActions.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Action> listActions(@PathParam("id") int id, ListOptions options);

   static final class ParseDropletActions extends ParseJson<ParseDropletActions.DropletActions> {
      @Inject ParseDropletActions(Json json) {
         super(json, TypeLiteral.get(DropletActions.class));
      }

      private static class DropletActions extends PaginatedCollection<Action> {
         @ConstructorProperties({ "actions", "meta", "links" })
         public DropletActions(List<Action> items, Meta meta, Links links) {
            super(items, meta, links);
         }
      }

      private static class ToPagedIterable extends BaseToPagedIterable<Action, ListOptions> {
         @Inject ToPagedIterable(DigitalOcean2Api api, Function<URI, ListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<Action> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
            return api.dropletApi().listActions((Integer) arg0.get(), options);
         }
      }
   }

   @Named("droplet:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   DropletCreate create(@PayloadParam("name") String name, @PayloadParam("region") String region,
         @PayloadParam("size") String size, @PayloadParam("image") String image);

   @Named("droplet:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CreateDropletOptions.class)
   DropletCreate create(@PayloadParam("name") String name, @PayloadParam("region") String region,
         @PayloadParam("size") String size, @PayloadParam("image") String image, CreateDropletOptions options);

   @Named("droplet:get")
   @GET
   @SelectJson("droplet")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Droplet get(@PathParam("id") int id);

   @Named("droplet:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("id") int id);

   @Named("droplet:reboot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\"type\":\"reboot\"}")
   Action reboot(@PathParam("id") int id);

   @Named("droplet:powercycle")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\"type\":\"power_cycle\"}")
   Action powerCycle(@PathParam("id") int id);

   @Named("droplet:shutdown")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\"type\":\"shutdown\"}")
   Action shutdown(@PathParam("id") int id);

   @Named("droplet:poweroff")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\"type\":\"power_off\"}")
   Action powerOff(@PathParam("id") int id);

   @Named("droplet:poweron")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\"type\":\"power_on\"}")
   Action powerOn(@PathParam("id") int id);

   @Named("droplet:snapshot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("%7B\"type\":\"snapshot\",\"name\":\"{name}\"%7D")
   Action snapshot(@PathParam("id") int id, @PayloadParam("name") String name);

}
