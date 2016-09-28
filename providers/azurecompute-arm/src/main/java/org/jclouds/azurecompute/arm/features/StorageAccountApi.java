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
package org.jclouds.azurecompute.arm.features;

import java.net.URI;
import java.util.List;
import java.util.Map;

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

import org.jclouds.Fallbacks;
import org.jclouds.azurecompute.arm.domain.Availability;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.StorageServiceKeys;
import org.jclouds.azurecompute.arm.domain.StorageServiceUpdateParams;
import org.jclouds.azurecompute.arm.filters.ApiVersionFilter;
import org.jclouds.azurecompute.arm.functions.FalseOn204;
import org.jclouds.azurecompute.arm.functions.URIParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * The Azure Resource Management API includes operations for managing the storage accounts in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/mt163683.aspx">docs</a>
 */
@Path("/")
@RequestFilters({ OAuthFilter.class, ApiVersionFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
public interface StorageAccountApi {

   /**
    * The List Storage Accounts operation lists the storage accounts that are available in the specified subscription
    * and resource group.
    * https://msdn.microsoft.com/en-us/library/mt163559.aspx
    */
   @Named("storageaccount:list")
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts")
   @GET
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<StorageService> list();

   /**
    * The Create Storage Account asynchronous operation creates a new storage account in Microsoft Azure.
    * https://msdn.microsoft.com/en-us/library/mt163564.aspx
    * PUT
    */
   @Named("storageaccount:create")
   @Payload("%7B\"location\":\"{location}\",\"tags\":{tags},\"properties\":{properties}%7D")
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   @Produces(MediaType.APPLICATION_JSON)
   @ResponseParser(URIParser.class)
   @MapBinder(BindToJsonPayload.class)
   @PUT
   URI create(@PathParam("storageAccountName") String storageAccountName,
              @PayloadParam("location") String location,
              @Nullable @PayloadParam("tags") Map<String, String> tags,
              @PayloadParam("properties") Map<String, String> properties);

   /**
    * The Check Storage Account Name Availability operation checks to see if the specified storage account name is
    * available, or if it has already been taken. https://msdn.microsoft.com/en-us/library/mt163642.aspx
    * POST
    */
   @Named("CheckStorageAccountNameAvailability")
   @POST
   @Payload("%7B\"name\":\"{name}\",\"type\":\"Microsoft.Storage/storageAccounts\"%7D")
   @Path("/providers/Microsoft.Storage/checkNameAvailability")
   @Produces(MediaType.APPLICATION_JSON)
   Availability isAvailable(@PayloadParam("name") String storageAccountName);

   /**
    * The Get Storage Account Properties operation returns system properties for the specified storage account.
    * https://msdn.microsoft.com/en-us/library/mt163553.aspx
    */
   @Named("storageaccountproperty:get")
   @GET
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   StorageService get(@PathParam("storageAccountName") String storageAccountName);

   /**
    * The Get Storage Keys operation returns the primary and secondary access keys for the specified storage account.
    * https://msdn.microsoft.com/en-us/library/mt163589.aspx
    * POST
    */
   @Named("storageaccountkey:get")
   @POST
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}/listKeys")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   StorageServiceKeys getKeys(@PathParam("storageAccountName") String storageAccountName);

   /**
    * https://msdn.microsoft.com/en-us/library/mt163567.aspx
    * POST
    */
   @Named("RegenerateStorageAccountKeys")
   @POST
   @Payload("%7B\"keyName\":\"{keyName}\"%7D")
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccount}/regenerateKey")
   @Produces(MediaType.APPLICATION_JSON)
   StorageServiceKeys regenerateKeys(@PathParam("storageAccount") String storageAccount,
                                     @PayloadParam("keyName") String keyName);

   /**
    * The Update Storage Account asynchronous operation updates the label, the description, and enables or disables the
    * geo-replication status for the specified storage account. https://msdn.microsoft.com/en-us/library/mt163639.aspx
    * PATCH
    */
   @Named("storageaccount:update")
   @PATCH
   @Payload("%7B\"tags\":{tags},\"properties\":{properties}%7D")
   @MapBinder(BindToJsonPayload.class)
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   @Produces(MediaType.APPLICATION_JSON)
   StorageServiceUpdateParams update(
           @PathParam("storageAccountName") String storageAccountName,
           @Nullable @PayloadParam("tags") Map<String, String> tags,
           @PayloadParam("properties") StorageServiceUpdateParams.StorageServiceUpdateProperties properties);

   /**
    * https://msdn.microsoft.com/en-us/library/mt163652.aspx
    * DELETE
    */
   @Named("storageaccount:delete")
   @DELETE
   @ResponseParser(FalseOn204.class)
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   boolean delete(@PathParam("storageAccountName") String storageAccountName);

}
