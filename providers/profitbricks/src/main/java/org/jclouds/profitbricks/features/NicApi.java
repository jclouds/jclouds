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

import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.profitbricks.binder.nic.CreateNicRequestBinder;
import org.jclouds.profitbricks.binder.nic.SetInternetAccessBinder;
import org.jclouds.profitbricks.binder.nic.UpdateNicRequestBinder;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.http.filters.ProfitBricksSoapMessageEnvelope;
import org.jclouds.profitbricks.http.parser.nic.NicListResponseHandler;
import org.jclouds.profitbricks.http.parser.nic.NicResponseHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;

import java.util.List;

import org.jclouds.profitbricks.http.parser.RequestIdOnlyResponseHandler;
import org.jclouds.profitbricks.http.parser.nic.NicIdOnlyResponseHandler;

@RequestFilters({BasicAuthentication.class, ProfitBricksSoapMessageEnvelope.class})
@Consumes(MediaType.TEXT_XML)
@Produces(MediaType.TEXT_XML)
public interface NicApi {

   @POST
   @Named("nics:getall")
   @Payload("<ws:getAllNic/>")
   @XMLResponseParser(NicListResponseHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Nic> getAllNics();

   @POST
   @Named("nic:create")
   @MapBinder(CreateNicRequestBinder.class)
   @XMLResponseParser(NicIdOnlyResponseHandler.class)
   String createNic(@PayloadParam("nic") Nic.Request.CreatePayload payload);

   @POST
   @Named("nic:get")
   @Payload("<ws:getNic><nicId>{id}</nicId></ws:getNic>")
   @XMLResponseParser(NicResponseHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Nic getNic(@PayloadParam("id") String identifier);

   @POST
   @Named("nic:update")
   @MapBinder(UpdateNicRequestBinder.class)
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String updateNic(@PayloadParam("nic") Nic.Request.UpdatePayload payload);

   @POST
   @Named("nic:setInternetAccess")
   @MapBinder(SetInternetAccessBinder.class)
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String setInternetAccess(@PayloadParam("nic") Nic.Request.SetInternetAccessPayload payload);

   @POST
   @Named("nic:delete")
   @Payload("<ws:deleteNic><nicId>{id}</nicId></ws:deleteNic>")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteNic(@PayloadParam("id") String id);
}
