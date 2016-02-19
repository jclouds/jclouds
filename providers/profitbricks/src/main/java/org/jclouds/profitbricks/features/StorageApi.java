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
import org.jclouds.profitbricks.binder.storage.ConnectStorageToServerRequestBinder;
import org.jclouds.profitbricks.binder.storage.CreateStorageRequestBinder;
import org.jclouds.profitbricks.binder.storage.UpdateStorageRequestBinder;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.http.filters.ProfitBricksSoapMessageEnvelope;
import org.jclouds.profitbricks.http.parser.RequestIdOnlyResponseHandler;
import org.jclouds.profitbricks.http.parser.storage.StorageIdOnlyResponseHandler;
import org.jclouds.profitbricks.http.parser.storage.StorageInfoResponseHandler;
import org.jclouds.profitbricks.http.parser.storage.StorageListResponseHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;

@RequestFilters({BasicAuthentication.class, ProfitBricksSoapMessageEnvelope.class})
@Consumes(MediaType.TEXT_XML)
@Produces(MediaType.TEXT_XML)
public interface StorageApi {

   /**
    *
    * @return Returns information about all virtual storage, such as configuration and provisioning state.
    */
   @POST
   @Named("storage:getall")
   @Payload("<ws:getAllStorages/>")
   @XMLResponseParser(StorageListResponseHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Storage> getAllStorages();

   /**
    *
    * @param id Storage identifier
    * @return Returns information about a virtual storage’s configuration and provisioning state.
    */
   @POST
   @Named("storage:get")
   @Payload("<ws:getStorage><storageId>{id}</storageId></ws:getStorage>")
   @XMLResponseParser(StorageInfoResponseHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Storage getStorage(@PayloadParam("id") String id);

   /**
    * Creates a virtual storage within an existing virtual data center. Additional parameters can be specified, e.g. for
    * assigning a HDD image to the storage.
    *
    * @param payload Payload
    * @return storageId of the created storage
    */
   @POST
   @Named("storage:create")
   @MapBinder(CreateStorageRequestBinder.class)
   @XMLResponseParser(StorageIdOnlyResponseHandler.class)
   String createStorage(@PayloadParam("storage") Storage.Request.CreatePayload payload);

   /**
    * Updates parameters of an existing virtual storage device. It is possible to increase the storage size without
    * reboot of an already provisioned storage. The additional capacity is not added to any partition. You have to
    * partition the storage afterwards. Vice versa, it is not possible to decrease the storage size of an already
    * provisioned storage.
    *
    * @param payload Payload
    * @return Identifier of current request
    */
   @POST
   @Named("storage:update")
   @MapBinder(UpdateStorageRequestBinder.class)
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String updateStorage(@PayloadParam("storage") Storage.Request.UpdatePayload payload);

   /**
    * Deletes an existing virtual storage device.
    *
    * @param id Identifier of the target virtual storage
    * @return Identifier of current request
    */
   @POST
   @Named("storage:delete")
   @Payload("<ws:deleteStorage><storageId>{id}</storageId></ws:deleteStorage>")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteStorage(@PayloadParam("id") String id);

   /**
    * Connects a virtual storage device to an existing server.
    *
    * @param payload Payload
    * @return Identifier of current request
    */
   @POST
   @Named("storage:connect")
   @MapBinder(ConnectStorageToServerRequestBinder.class)
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String connectStorageToServer(@PayloadParam("storage") Storage.Request.ConnectPayload payload);

   /**
    * Disconnects a virtual storage device from a connected server.
    *
    * @param storageId Identifier of the connected virtual storage
    * @param serverId Identifier of the connected virtual server
    * @return Identifier of current request
    */
   @POST
   @Named("storage:disconnect")
   @Payload("<ws:disconnectStorageFromServer><storageId>{storageId}</storageId><serverId>{serverId}</serverId></ws:disconnectStorageFromServer>")
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String disconnectStorageFromServer(@PayloadParam("storageId") String storageId, @PayloadParam("serverId") String serverId);
}
