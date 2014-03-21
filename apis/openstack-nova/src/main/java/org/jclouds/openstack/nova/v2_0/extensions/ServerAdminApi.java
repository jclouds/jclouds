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
package org.jclouds.openstack.nova.v2_0.extensions;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.functions.ParseImageIdFromLocationHeader;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;

/**
 * Provide access to the OpenStack Compute (Nova) Admin Server Actions Extension API.
 *
 * Provide additional actions for servers:
 * 'suspend', 'resume', 'migrate', 'lock', 'unlock', 'resetNetwork', 'createBackup', 'pause', 'migrateLive',
 * 'injectNetworkInfo', 'unpause'
 *
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.ADMIN_ACTIONS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/servers/{id}/action")
public interface ServerAdminApi {
   /**
    * Suspend a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:suspend")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"suspend\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean suspend(@PathParam("id") String id);

   /**
    * Resume a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:resume")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"resume\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean resume(@PathParam("id") String id);

   /**
    * Migrate a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:migrate")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"migrate\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean migrate(@PathParam("id") String id);

   /**
    * Lock a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:lock")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"lock\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean lock(@PathParam("id") String id);

   /**
    * Unlock a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:unlock")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"unlock\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean unlock(@PathParam("id") String id);

   /**
    * Reset network of a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:resetNetwork")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"resetNetwork\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean resetNetwork(@PathParam("id") String id);

   /**
    * Create backup of a server.
    *
    * @param id         id of the server
    * @param imageName  the name of the image to create
    * @param backupType the type of backup
    * @param rotation   the number of images to retain (0 to simply overwrite)
    * @param options    optional rotation and/or metadata parameters
    * @return the id of the newly created image
    */
   @Named("serverAdmin:createBackup")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("createBackup")
   @ResponseParser(ParseImageIdFromLocationHeader.class)
   @Fallback(MapHttp4xxCodesToExceptions.class)
   String createBackup(@PathParam("id") String id, @PayloadParam("name") String imageName,
         @PayloadParam("backup_type") BackupType backupType, @PayloadParam("rotation") int rotation,
         CreateBackupOfServerOptions... options);

   /**
    * Pause a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:pause")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"pause\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean pause(@PathParam("id") String id);

   /**
    * Unpause a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:unpause")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"unpause\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean unpause(@PathParam("id") String id);

   /**
    * Live migrate a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:liveMigrate")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("os-migrateLive")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean liveMigrate(@PathParam("id") String id, @PayloadParam("host") String host,
         @PayloadParam("block_migration") boolean blockMigration,
         @PayloadParam("disk_over_commit") boolean diskOverCommit);

   /**
    * Inject network info into a server.
    *
    * @param id id of the server
    */
   @Named("serverAdmin:injectNetwork")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"injectNetworkInfo\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean injectNetworkInfo(@PathParam("id") String id);
}
