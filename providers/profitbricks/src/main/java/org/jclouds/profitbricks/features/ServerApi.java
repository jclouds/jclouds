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
package org.jclouds.profitbricks.features;

import java.util.List;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.profitbricks.binder.server.CreateServerRequestBinder;
import org.jclouds.profitbricks.binder.server.UpdateServerRequestBinder;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.http.filters.ProfitBricksSoapMessageEnvelope;
import org.jclouds.profitbricks.http.parser.RequestIdOnlyResponseHandler;
import org.jclouds.profitbricks.http.parser.server.ServerIdOnlyResponseHandler;
import org.jclouds.profitbricks.http.parser.server.ServerInfoResponseHandler;
import org.jclouds.profitbricks.http.parser.server.ServerListResponseHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;

@RequestFilters( { BasicAuthentication.class, ProfitBricksSoapMessageEnvelope.class } )
@Consumes( MediaType.TEXT_XML )
@Produces( MediaType.TEXT_XML )
public interface ServerApi {

   /**
    * @return Returns information about all virtual server, such as configuration, provisioning status, power status, etc.
    */
   @POST
   @Named( "server:getall" )
   @Payload( "<ws:getAllServers/>" )
   @XMLResponseParser( ServerListResponseHandler.class )
   @Fallback( Fallbacks.EmptyListOnNotFoundOr404.class )
   List<Server> getAllServers();

   /**
    * @param identifier Identifier of the virtual server
    * @return Returns information about a virtual server, such as configuration, provisioning status, power status, etc.
    */
   @POST
   @Named( "server:get" )
   @Payload( "<ws:getServer><serverId>{id}</serverId></ws:getServer>" )
   @XMLResponseParser( ServerInfoResponseHandler.class )
   @Fallback( Fallbacks.NullOnNotFoundOr404.class )
   Server getServer( @PayloadParam( "id" ) String identifier );

   /**
    * Starts an existing virtual server
    * <ul>
    * <li>Server may receive new public IP addresses if necessary </li>
    * <li>Billing will continue</li>
    * </ul>
    *
    *
    * @param id Identifier of the target virtual server
    * @return Identifier of current request
    */
   @POST
   @Named( "server:start" )
   @Payload( "<ws:startServer><serverId>{id}</serverId></ws:startServer>" )
   @XMLResponseParser( RequestIdOnlyResponseHandler.class )
   String startServer( @PayloadParam( "id" ) String id );

   /**
    *
    * Stops an existing virtual server forcefully (HARD stop)
    * <ul>
    * <li>Server will be forcefully powered off. Any unsaved data may be lost! </li>
    * <li>Billing for this server will be stopped </li>
    * <li>When restarting the server a new public IP gets assigned, alternatively, you can reserve IP addresses, see reservation of public
    * IP blocks</li>
    * </ul>
    *
    * A graceful stop of a server is not possible through the ProfitBricks API. We recommend to access and execute the command on the
    * virtual server directly. Once the server was shutdown you still can use the "stopServer" method that will stop billing.
    *
    * @param id Identifier of the target virtual server
    * @return Identifier of current request
    */
   @POST
   @Named( "server:stop" )
   @Payload( "<ws:stopServer><serverId>{id}</serverId></ws:stopServer>" )
   @XMLResponseParser( RequestIdOnlyResponseHandler.class )
   String stopServer( @PayloadParam( "id" ) String id );

   /**
    * Resets an existing virtual server (POWER CYCLE).
    * <ul>
    * <li>Server will be forcefully powered off and restarted immediately. Any unsaved data may be lost!</li>
    * <li> Billing will continue</li>
    * </ul>
    * <b>Graceful REBOOT</b>
    *
    * A graceful reboot of a server is not possible through the ProfitBricks API. We recommend to access and execute the command on the
    * virtual server directly.
    *
    * @param id Identifier of the target virtual server
    * @return Identifier of current request
    */
   @POST
   @Named( "server:reset" )
   @Payload( "<ws:resetServer><serverId>{id}</serverId></ws:resetServer>" )
   @XMLResponseParser( RequestIdOnlyResponseHandler.class )
   String resetServer( @PayloadParam( "id" ) String id );

   /**
    * Creates a Virtual Server within an existing data center. Parameters can be specified to set up a boot device and connect the server to
    * an existing LAN or the Internet.
    *
    * @param payload Payload
    * @return serverId of the created server
    */
   @POST
   @Named( "server:create" )
   @MapBinder( CreateServerRequestBinder.class )
   @XMLResponseParser( ServerIdOnlyResponseHandler.class )
   String createServer( @PayloadParam( "server" ) Server.Request.CreatePayload payload );

   /**
    * Updates parameters of an existing virtual server device.
    *
    * @param payload Paylaod
    * @return Identifier of current request
    */
   @POST
   @Named( "server:update" )
   @MapBinder( UpdateServerRequestBinder.class )
   @XMLResponseParser( RequestIdOnlyResponseHandler.class )
   String updateServer( @PayloadParam( "server" ) Server.Request.UpdatePayload payload );

   /**
    * Deletes an existing Virtual Server.
    *
    * @param id Identifier of the target virtual server
    * @return Identifier of current request
    */
   @POST
   @Named( "server:delete" )
   @Payload( "<ws:deleteServer><serverId>{id}</serverId></ws:deleteServer>" )
   @Fallback( Fallbacks.FalseOnNotFoundOr404.class )
   boolean deleteServer( @PayloadParam( "id" ) String id );

}
