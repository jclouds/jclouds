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
import org.jclouds.azurecompute.arm.domain.Plan;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.filters.ApiVersionFilter;
import org.jclouds.azurecompute.arm.functions.URIParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * The Virtual Machine API includes operations for managing the virtual machines in your subscription.
 *
 * @see <a href="https://docs.microsoft.com/en-us/rest/api/compute/virtualmachines">docs</a>
 */
@RequestFilters({ OAuthFilter.class, ApiVersionFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
public interface VirtualMachineApi {

   @Named("GetVirtualMachine")
   @GET
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualMachine get(@PathParam("name") String name);

   /**
    * Get information about the model view and instance view of a virtual machine
    */
   @Named("GetVirtualMachineInstance")
   @GET
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/instanceView")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualMachineInstance getInstanceDetails(@PathParam("name") String name);

   @Named("CreateOrUpdateVirtualMachine")
   @PUT
   @MapBinder(BindToJsonPayload.class)
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{vmname}")
   @QueryParams(keys = "validating", values = "false")
   VirtualMachine createOrUpdate(@PathParam("vmname") String vmname, @PayloadParam("location") String location,
         @PayloadParam("properties") VirtualMachineProperties properties,
         @PayloadParam("tags") Map<String, String> tags, @Nullable @PayloadParam("plan") Plan plan);

   @Named("ListVirtualMachines")
   @GET
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines")
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<VirtualMachine> list();

   @Named("ListVirtualMachinesAll")
   @GET
   @Path("/providers/Microsoft.Compute/virtualMachines")
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<VirtualMachine> listAll();

   @Named("ListVirtualMachinesByLocation")
   @GET
   @Path("/providers/Microsoft.Compute/locations/{location}/virtualMachines")
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<VirtualMachine> listByLocation(@PathParam("location") String location);

   @Named("ListAvailableSizes")
   @GET
   @SelectJson("value")
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/vmSizes")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<VMSize> listAvailableSizes(@PathParam("name") String name);

   @Named("DeleteVirtualMachine")
   @DELETE
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}")
   @ResponseParser(URIParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   URI delete(@PathParam("name") String name);

   @Named("RestartVirtualMachine")
   @POST
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/restart")
   void restart(@PathParam("name") String name);

   @Named("StartVirtualMachine")
   @POST
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/start")
   void start(@PathParam("name") String name);

   @Named("StopVirtualMachine")
   @POST
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/powerOff")
   void stop(@PathParam("name") String name);

   @Named("DeallocateVirtualMachine")
   @POST
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/deallocate")
   void deallocate(@PathParam("name") String name);

   @Named("generalize")
   @POST
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/generalize")
   void generalize(@PathParam("name") String name);

   /**
    * Capture the virtual machine image
    * destinationContainerName: the name of the folder created under the "system" container in the storage account
    * Folder structure: Microsoft.Computer > Images > destinationContainerName
    * Within the folder, there will be 1 page blob for the osDisk vhd and 1 block blob for the vmTemplate json file
    */
   @Named("capture")
   @POST
   @Payload("%7B\"vhdPrefix\":\"{vhdPrefix}\",\"destinationContainerName\":\"{destinationContainerName}\",\"overwriteVhds\":\"true\"%7D")
   @Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/{name}/capture")
   @ResponseParser(URIParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Produces(MediaType.APPLICATION_JSON)
   URI capture(@PathParam("name") String name, @PayloadParam("vhdPrefix") String vhdPrefix,
         @PayloadParam("destinationContainerName") String destinationContainerName);

}

