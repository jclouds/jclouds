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
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.http.filters.ProfitBricksSoapMessageEnvelope;
import org.jclouds.profitbricks.http.parser.image.ImageInfoResponseHandler;
import org.jclouds.profitbricks.http.parser.image.ImageListResponseHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;

@RequestFilters({BasicAuthentication.class, ProfitBricksSoapMessageEnvelope.class})
@Consumes(MediaType.TEXT_XML)
@Produces(MediaType.TEXT_XML)
public interface ImageApi {

   /**
    * @return Outputs a list of all HDD and/or CD-ROM/DVD images existing on or uploaded to the ProfitBricks FTP server.
    */
   @POST
   @Named("image:getall")
   @Payload("<ws:getAllImages/>")
   @XMLResponseParser(ImageListResponseHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Image> getAllImages();

   /**
    *
    * @param identifier Image Id
    * @return Returns information about a HDD or CD-ROM/DVD (ISO) image.
    */
   @POST
   @Named("image:get")
   @Payload("<ws:getImage><imageId>{id}</imageId></ws:getImage>")
   @XMLResponseParser(ImageInfoResponseHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Image getImage(@PayloadParam("id") String identifier);
}
