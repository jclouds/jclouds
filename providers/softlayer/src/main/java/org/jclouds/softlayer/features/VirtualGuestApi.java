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
package org.jclouds.softlayer.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.softlayer.binders.TagToJson;
import org.jclouds.softlayer.binders.VirtualGuestToJson;
import org.jclouds.softlayer.domain.ContainerVirtualGuestConfiguration;
import org.jclouds.softlayer.domain.VirtualGuest;

/**
 * Provides access to VirtualGuest via their REST API.
 * <p/>
 *
 * @see <a http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest" />
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_JSON)
public interface VirtualGuestApi {

   public static String GUEST_MASK = "id;hostname;domain;fullyQualifiedDomainName;powerState;maxCpu;maxMemory;" +
           "statusId;operatingSystem.passwords;primaryBackendIpAddress;primaryIpAddress;activeTransactionCount;" +
           "blockDevices.diskImage;datacenter;tagReferences";

   /**
    * Enables the creation of computing instances on an account.
    * @param virtualGuest this data type presents the structure in which all virtual guests will be presented.
    * @return the new Virtual Guest
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/createObject" />
    */
   @Named("VirtualGuests:create")
   @POST
   @Path("SoftLayer_Virtual_Guest")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualGuest createVirtualGuest(@BinderParam(VirtualGuestToJson.class) VirtualGuest virtualGuest);

   /**
    * @param id
    *           id of the virtual guest
    * @return virtual guest or null if not found
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/getObject" />
    */
   @Named("VirtualGuests:get")
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/getObject")
   @QueryParams(keys = "objectMask", values = GUEST_MASK)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualGuest getVirtualGuest(@PathParam("id") long id);

   /**
    * Delete a computing instance
    * @param id the id of the virtual guest.
    * @return the result of the deletion
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/deleteObject" />
    */
   @Named("VirtualGuests:delete")
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/deleteObject")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteVirtualGuest(@PathParam("id") long id);

   /**
    * Determine options available when creating a computing instance
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/getCreateObjectOptions" />
    */
   @Named("VirtualGuests:getCreateObjectOptions")
   @GET
   @Path("/SoftLayer_Virtual_Guest/getCreateObjectOptions")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ContainerVirtualGuestConfiguration getCreateObjectOptions();

   /**
    * Hard reboot the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @Named("VirtualGuest:rebootHard")
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/rebootHard.json")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void rebootHardVirtualGuest(@PathParam("id") long id);

   /**
    * Pause the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @Named("VirtualGuest:pause")
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/pause.json")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void pauseVirtualGuest(@PathParam("id") long id);

   /**
    * Resume the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @Named("VirtualGuest:resume")
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/resume.json")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void resumeVirtualGuest(@PathParam("id") long id);

   /**
    * Resume the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @Named("VirtualGuest:setTags")
   @POST
   @Path("/SoftLayer_Virtual_Guest/{id}/setTags")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean setTags(@PathParam("id") long id, @BinderParam(TagToJson.class) Set<String> tags);
}
