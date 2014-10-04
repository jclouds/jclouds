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
package org.jclouds.cloudservers;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Fallbacks.*;

import java.io.Closeable;
import java.util.Set;

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
import org.jclouds.cloudservers.binders.BindBackupScheduleToJsonPayload;
import org.jclouds.cloudservers.domain.Addresses;
import org.jclouds.cloudservers.domain.BackupSchedule;
import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.cloudservers.domain.Image;
import org.jclouds.cloudservers.domain.Limits;
import org.jclouds.cloudservers.domain.RebootType;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.SharedIpGroup;
import org.jclouds.cloudservers.options.CreateServerOptions;
import org.jclouds.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.cloudservers.options.ListOptions;
import org.jclouds.cloudservers.options.RebuildServerOptions;
import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.services.Compute;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Unwrap;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Cloud Servers via their REST API.
 *
 * @deprecated The Rackspace First-Gen Cloud Servers product has been deprecated. Please refer to the
 *             <a href="http://jclouds.apache.org/guides/rackspace">Rackspace Getting Started Guide</a>
 *             for accessing the Rackspace Cloud. This API will be removed in 2.0.
 */
@Deprecated
@RequestFilters({ AuthenticateRequest.class, AddTimestampQuery.class })
@Endpoint(Compute.class)
public interface CloudServersClient extends Closeable {
   /**
    * All accounts, by default, have a preconfigured set of thresholds (or limits) to manage
    * capacity and prevent abuse of the system. The system recognizes two kinds of limits: rate
    * limits and absolute limits. Rate limits are thresholds that are reset after a certain amount
    * of time passes. Absolute limits are fixed.
    *
    * @return limits on the account
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/limits")
   @Fallback(NullOnNotFoundOr404.class)
   Limits getLimits();

   /**
    *
    * List all servers (IDs and names only)
    *
    * This operation provides a list of servers associated with your identity. Servers that have
    * been deleted are not included in this list.
    * <p/>
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Server> listServers(ListOptions... options);

   /**
    *
    * This operation returns details of the specified server.
    *
    * @return null, if the server is not found
    * @see Server
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/servers/{id}")
   Server getServer(@PathParam("id") int id);

   /**
    *
    * This operation deletes a cloud server instance from the system.
    * <p/>
    * Note: When a server is deleted, all images created from that server are also removed.
    *
    * @return false if the server is not found
    * @see Server
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/servers/{id}")
   boolean deleteServer(@PathParam("id") int id);

   /**
    * The reboot function allows for either a soft or hard reboot of a server.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBOOT - ACTIVE (soft reboot)
    * <p/>
    * ACTIVE - HARD_REBOOT - ACTIVE (hard reboot)
    *
    * @param rebootType
    *           With a soft reboot, the operating system is signaled to restart, which allows for a
    *           graceful shutdown of all processes. A hard reboot is the equivalent of power cycling
    *           the server.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(APPLICATION_JSON)
   @Payload("%7B\"reboot\":%7B\"type\":\"{type}\"%7D%7D")
   void rebootServer(@PathParam("id") int id, @PayloadParam("type") RebootType rebootType);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - QUEUE_RESIZE - PREP_RESIZE - VERIFY_RESIZE
    * <p/>
    * ACTIVE - QUEUE_RESIZE - ACTIVE (on error)
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(APPLICATION_JSON)
   @Payload("%7B\"resize\":%7B\"flavorId\":{flavorId}%7D%7D")
   void resizeServer(@PathParam("id") int id, @PayloadParam("flavorId") int flavorId);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(APPLICATION_JSON)
   @Payload("{\"confirmResize\":null}")
   void confirmResizeServer(@PathParam("id") int id);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly reverted, at which time the
    * original server is removed. All resizes are automatically reverted after 24 hours if they are
    * not reverted or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(APPLICATION_JSON)
   @Payload("{\"revertResize\":null}")
   void revertResizeServer(@PathParam("id") int id);

   /**
    * This operation asynchronously provisions a new server. The progress of this operation depends
    * on several factors including location of the requested image, network i/o, host load, and the
    * selected flavor. The progress of the request can be checked by performing a GET on /server/id,
    * which will return a progress attribute (0-100% completion). A password will be randomly
    * generated for you and returned in the response object. For security reasons, it will not be
    * returned in subsequent GET calls against a given server ID.
    *
    * @param options
    *           - used to specify extra files, metadata, or ip parameters during server creation.
    */
   @POST
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   Server createServer(@PayloadParam("name") String name, @PayloadParam("imageId") int imageId,
         @PayloadParam("flavorId") int flavorId, CreateServerOptions... options);

   /**
    * The rebuild function removes all data on the server and replaces it with the specified image.
    * Server ID and IP addresses remain the same.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBUILD - ACTIVE
    * <p/>
    * ACTIVE - REBUILD - ERROR (on error)
    * <p/>
    *
    * @param options
    *           - imageId is an optional argument. If it is not specified, the server is rebuilt
    *           with the original imageId.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @MapBinder(RebuildServerOptions.class)
   void rebuildServer(@PathParam("id") int id, RebuildServerOptions... options);

   /**
    * /** This operation allows you share an IP address to the specified server
    * <p/>
    * This operation shares an IP from an existing server in the specified shared IP group to
    * another specified server in the same group. The operation modifies cloud network restrictions
    * to allow IP traffic for the given IP to/from the server specified.
    *
    * <p/>
    * Status Transition: ACTIVE - SHARE_IP - ACTIVE (if configureServer is true) ACTIVE -
    * SHARE_IP_NO_CONFIG - ACTIVE
    *
    * @param configureServer
    *           <p/>
    *           if set to true, the server is configured with the new address, though the address is
    *           not enabled. Note that configuring the server does require a reboot.
    *           <p/>
    *           If set to false, does not bind the IP to the server itself. A heartbeat facility
    *           (e.g. keepalived) can then be used within the servers to perform health checks and
    *           manage IP failover.
    */
   @PUT
   @Path("/servers/{id}/ips/public/{address}")
   @Produces(APPLICATION_JSON)
   @Payload("%7B\"shareIp\":%7B\"sharedIpGroupId\":{sharedIpGroupId},\"configureServer\":{configureServer}%7D%7D")
   void shareIp(@PathParam("address") String addressToShare, @PathParam("id") int serverToTosignBindressTo,
         @PayloadParam("sharedIpGroupId") int sharedIpGroup, @PayloadParam("configureServer") boolean configureServer);

   /**
    * This operation removes a shared IP address from the specified server.
    * <p/>
    * Status Transition: ACTIVE - DELETE_IP - ACTIVE
    *
    * @param addressToShare
    * @param serverToTosignBindressTo
    * @return
    */
   @DELETE
   @Path("/servers/{id}/ips/public/{address}")
   @Fallback(VoidOnNotFoundOr404.class)
   void unshareIp(@PathParam("address") String addressToShare, @PathParam("id") int serverToTosignBindressTo);

   /**
    * This operation allows you to change the administrative password.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    *
    */
   @PUT
   @Path("/servers/{id}")
   @Produces(APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"adminPass\":\"{adminPass}\"%7D%7D")
   void changeAdminPass(@PathParam("id") int id, @PayloadParam("adminPass") String adminPass);

   /**
    * This operation allows you to update the name of the server. This operation changes the name of
    * the server in the Cloud Servers system and does not change the server host name itself.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    *
    */
   @PUT
   @Path("/servers/{id}")
   @Produces(APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"name\":\"{name}\"%7D%7D")
   void renameServer(@PathParam("id") int id, @PayloadParam("name") String newName);

   /**
    *
    * List available flavors (IDs and names only)
    *
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Flavor> listFlavors(ListOptions... options);

   /**
    *
    * This operation returns details of the specified flavor.
    *
    * @return null, if the flavor is not found
    * @see Flavor
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Flavor getFlavor(@PathParam("id") int id);

   /**
    *
    * List available images (IDs and names only)
    *
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Image> listImages(ListOptions... options);

   /**
    *
    * This operation returns details of the specified image.
    *
    * @return null, if the image is not found
    *
    * @see Image
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images/{id}")
   Image getImage(@PathParam("id") int id);

   /**
    *
    * This operation deletes an image from the system.
    * <p/>
    * Note: Images are immediately removed. Currently, there are no state transitions to track the
    * delete operation.
    *
    * @return false if the image is not found
    * @see Image
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/images/{id}")
   boolean deleteImage(@PathParam("id") int id);

   /**
    *
    * This operation creates a new image for the given server ID. Once complete, a new image will be
    * available that can be used to rebuild or create servers. Specifying the same image name as an
    * existing custom image replaces the image. The image creation status can be queried by
    * performing a GET on /images/id and examining the status and progress attributes.
    *
    * Status Transition:
    * <p/>
    * QUEUED - PREPARING - SAVING - ACTIVE
    * <p/>
    * QUEUED - PREPARING - SAVING - FAILED (on error)
    * <p/>
    * Note: At present, image creation is an asynchronous operation, so coordinating the creation
    * with data quiescence, etc. is currently not possible.
    *
    * @throws ResourceNotFoundException if the server is not found
    * @see Image
    */
   @POST
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   @Produces(APPLICATION_JSON)
   @Payload("%7B\"image\":%7B\"serverId\":{serverId},\"name\":\"{name}\"%7D%7D")
   Image createImageFromServer(@PayloadParam("name") String imageName, @PayloadParam("serverId") int serverId);

   /**
    *
    * List shared IP groups (IDs and names only)
    *
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<SharedIpGroup> listSharedIpGroups(ListOptions... options);

   /**
    *
    * This operation returns details of the specified shared IP group.
    *
    * @return null, if the shared ip group is not found
    *
    * @see SharedIpGroup
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   SharedIpGroup getSharedIpGroup(@PathParam("id") int id);

   /**
    * This operation creates a new shared IP group. Please note, all responses to requests for
    * shared_ip_groups return an array of servers. However, on a create request, the shared IP group
    * can be created empty or can be initially populated with a single server. Use
    * {@link CreateSharedIpGroupOptions} to specify an server.
    */
   @POST
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   @MapBinder(CreateSharedIpGroupOptions.class)
   SharedIpGroup createSharedIpGroup(@PayloadParam("name") String name,
         CreateSharedIpGroupOptions... options);

   /**
    * This operation deletes the specified shared IP group. This operation will ONLY succeed if 1)
    * there are no active servers in the group (i.e. they have all been terminated) or 2) no servers
    * in the group are actively sharing IPs.
    *
    * @return false if the shared ip group is not found
    * @see SharedIpGroup
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/shared_ip_groups/{id}")
   boolean deleteSharedIpGroup(@PathParam("id") int id);

   /**
    * List the backup schedule for the specified server
    *
    * @throws ResourceNotFoundException, if the server doesn't exist
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/backup_schedule")
   BackupSchedule getBackupSchedule(@PathParam("id") int serverId);

   /**
    * Delete backup schedule for the specified server.
    * <p/>
    * Web Hosting #119571 currently disables the schedule, not deletes it.
    *
    * @return false if the schedule is not found
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/servers/{id}/backup_schedule")
   boolean deleteBackupSchedule(@PathParam("id") int serverId);

   /**
    * Enable/update the backup schedule for the specified server
    *
    */
   @POST
   @Path("/servers/{id}/backup_schedule")
   void replaceBackupSchedule(@PathParam("id") int id,
         @BinderParam(BindBackupScheduleToJsonPayload.class) BackupSchedule backupSchedule);

   /**
    * List all server addresses
    *
    * returns empty set if the server doesn't exist
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips")
   Addresses getAddresses(@PathParam("id") int serverId);

   /**
    * List all public server addresses
    *
    * returns empty set if the server doesn't exist
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/public")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listPublicAddresses(@PathParam("id") int serverId);

   /**
    * List all private server addresses
    *
    * returns empty set if the server doesn't exist
    */
   @GET
   @Unwrap
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/private")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listPrivateAddresses(@PathParam("id") int serverId);
}
