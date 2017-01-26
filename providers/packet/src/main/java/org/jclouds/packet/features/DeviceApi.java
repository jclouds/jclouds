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
package org.jclouds.packet.features;

import java.beans.ConstructorProperties;
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

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.Href;
import org.jclouds.packet.domain.internal.PaginatedCollection;
import org.jclouds.packet.domain.options.ListOptions;
import org.jclouds.packet.filters.AddApiVersionToRequest;
import org.jclouds.packet.filters.AddXAuthTokenToRequest;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;

@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters({AddXAuthTokenToRequest.class, AddApiVersionToRequest.class})
public interface DeviceApi {

   @Named("device:list")
   @GET
   @Path("/projects/{projectId}/devices")
   @ResponseParser(ParseDevices.class)
   @Transform(ParseDevices.ToPagedIterable.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Device> list();

   @Named("device:list")
   @GET
   @Path("/projects/{projectId}/devices")
   @ResponseParser(ParseDevices.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Device> list(ListOptions options);

   final class ParseDevices extends ParseJson<ParseDevices.Devices> {
      @Inject
      ParseDevices(Json json) {
         super(json, TypeLiteral.get(Devices.class));
      }

       private static class Devices extends PaginatedCollection<Device> {
         @ConstructorProperties({"devices", "meta"})
         public Devices(List<Device> items, Meta meta) {
            super(items, meta);
         }
      }

      public static class ToPagedIterable extends Arg0ToPagedIterable.FromCaller<Device, ToPagedIterable> {

         private final PacketApi api;
         private final Function<Href, ListOptions> hrefToOptions;

         @Inject
         ToPagedIterable(PacketApi api, Function<Href, ListOptions> hrefToOptions) {
            this.api = api;
            this.hrefToOptions = hrefToOptions;
         }

         @Override
         protected Function<Object, IterableWithMarker<Device>> markerToNextForArg0(Optional<Object> arg0) {
            String projectId = arg0.get().toString();
            final DeviceApi deviceApi = api.deviceApi(projectId);
            return new Function<Object, IterableWithMarker<Device>>() {

               @SuppressWarnings("unchecked")
               @Override
               public IterableWithMarker<Device> apply(Object input) {
                  ListOptions listOptions = hrefToOptions.apply(Href.class.cast(input));
                  return IterableWithMarker.class.cast(deviceApi.list(listOptions));
               }

            };
         }
      }
   }

   @Named("device:create")
   @POST
   @Path("/projects/{projectId}/devices")
   @Produces(MediaType.APPLICATION_JSON)
   Device create(@BinderParam(BindToJsonPayload.class) Device.CreateDevice device);


   @Named("device:get")
   @GET
   @Path("/devices/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Device get(@PathParam("id") String id);

   @Named("device:delete")
   @DELETE
   @Path("/devices/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("id") String id);

   @Named("device:powerOff")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/devices/{id}/actions")
   @Payload("{\"type\":\"power_off\"}")
   void powerOff(@PathParam("id") String id);

   @Named("device:powerOn")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/devices/{id}/actions")
   @Payload("{\"type\":\"power_on\"}")
   void powerOn(@PathParam("id") String id);
   
   @Named("device:reboot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/devices/{id}/actions")
   @Payload("{\"type\":\"reboot\"}")
   void reboot(@PathParam("id") String id);

}
