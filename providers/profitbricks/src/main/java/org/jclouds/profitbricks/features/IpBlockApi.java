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
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.http.filters.ProfitBricksSoapMessageEnvelope;
import org.jclouds.profitbricks.http.parser.RequestIdOnlyResponseHandler;
import org.jclouds.profitbricks.http.parser.ipblock.IpBlockListResponseHandler;
import org.jclouds.profitbricks.http.parser.ipblock.IpBlockResponseHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;

@RequestFilters({BasicAuthentication.class, ProfitBricksSoapMessageEnvelope.class})
@Consumes(MediaType.TEXT_XML)
@Produces(MediaType.TEXT_XML)
public interface IpBlockApi {

   @POST
   @Named("publicipblock:get")
   @Payload("<ws:getPublicIpBlock><blockId>{id}</blockId></ws:getPublicIpBlock>")
   @XMLResponseParser(IpBlockResponseHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   IpBlock getIpBlock(@PayloadParam("id") String identifier);

   @POST
   @Named("publicipblock:getall")
   @Payload("<ws:getAllPublicIpBlocks />")
   @XMLResponseParser(IpBlockListResponseHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<IpBlock> getAllIpBlock();

   @POST
   @Named("publicipblock:reserve")
   @Payload("<ws:reservePublicIpBlock><request><blockSize>{blockSize}</blockSize><location>{location}</location></request></ws:reservePublicIpBlock>")
   @XMLResponseParser(IpBlockResponseHandler.class)
   IpBlock reservePublicIpBlock(@PayloadParam("blockSize") String blockSize, @PayloadParam("location") String location);

   @POST
   @Named("publicipblock:addip")
   @Payload("<ws:addPublicIpToNic><ip>{ip}</ip><nicId>{nicid}</nicId></ws:addPublicIpToNic>")
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String addPublicIpToNic(@PayloadParam("ip") String ip, @PayloadParam("nicid") String nicid);

   @POST
   @Named("publicipblock:removeip")
   @Payload("<ws:removePublicIpFromNic><ip>{ip}</ip><nicId>{nicid}</nicId></ws:removePublicIpFromNic>")
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String removePublicIpFromNic(@PayloadParam("ip") String ip, @PayloadParam("nicid") String nicid);

   @POST
   @Named("publicipblock:releaseblock")
   @Payload("<ws:releasePublicIpBlock><blockId>{blockid}</blockId></ws:releasePublicIpBlock>")
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String releasePublicIpBlock(@PayloadParam("blockid") String blockid);
}
