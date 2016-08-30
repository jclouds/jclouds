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
package org.jclouds.docker.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.docker.domain.Network;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.binders.BindToJsonPayload;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/v{jclouds.api-version}/networks")
public interface NetworkApi {

   /**
    * @return a set of networks
    */
   @Named("networks:list")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Network> listNetworks();

   /**
    * @param network the network’s configuration (@see BindToJsonPayload)
    * @return a new network
    */
   @Named("network:create")
   @POST
   @Path("/create")
   Network createNetwork(@BinderParam(BindToJsonPayload.class) Network network);

   /**
    * Return low-level information on the network id
    * @param networkIdOrName  The id or name of the network to get.
    * @return The details of the network or <code>null</code> if the network with the given id doesn't exist.
    */
   @Named("network:inspect")
   @GET
   @Path("/{idOrName}")
   @Fallback(NullOnNotFoundOr404.class)
   Network inspectNetwork(@PathParam("idOrName") String networkIdOrName);

   /**
    * @param networkIdOrName The id or name of the network to be removed.
    */
   @Named("network:delete")
   @DELETE
   @Path("/{idOrName}")
   void removeNetwork(@PathParam("idOrName") String networkIdOrName);

   /**
    * @param networkIdOrName The id or name of the network where the container will be attached.
    */
   @Named("network:connectContainer")
   @POST
   @Path("/{idOrName}/connect")
   @Payload("%7B\"Container\":\"{containerIdOrName}\"%7D")
   @Headers(keys = "Content-Type", values = "application/json")
   void connectContainerToNetwork(@PathParam("idOrName") String networkIdOrName, @PayloadParam("containerIdOrName") String containerIdOrName);

   /**
    * @param networkIdOrName The id or name of the network where the container was attached.
    */
   @Named("network:disconnectContainer")
   @POST
   @Path("/{idOrName}/disconnect")
   @Payload("%7B\"Container\":\"{containerIdOrName}\"%7D")
   @Headers(keys = "Content-Type", values = "application/json")
   void disconnectContainerFromNetwork(@PathParam("idOrName") String networkIdOrName, @PayloadParam("containerIdOrName") String containerIdOrName);

}
