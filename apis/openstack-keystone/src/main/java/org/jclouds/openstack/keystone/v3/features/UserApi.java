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
package org.jclouds.openstack.keystone.v3.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.auth.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v3.domain.Group;
import org.jclouds.openstack.keystone.v3.domain.Project;
import org.jclouds.openstack.keystone.v3.domain.User;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

/**
 * Provides access to the Keystone User API.
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Identity.class)
@Path("/users")
public interface UserApi {

   @Named("users:list")
   @GET
   @SelectJson("users")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<User> list();

   @Named("users:get")
   @GET
   @Path("/{id}")
   @SelectJson("user")
   @Fallback(NullOnNotFoundOr404.class)
   User get(@PathParam("id") String id);

   @Named("users:create")
   @POST
   @SelectJson("user")
   @WrapWith("user")
   User create(@PayloadParam("name") String name, @Nullable @PayloadParam("password") String password,
         @Nullable @PayloadParam("enabled") Boolean enabled, @Nullable @PayloadParam("domain_id") String domainId,
         @Nullable @PayloadParam("default_project_id") String defaultProjectId);

   @Named("users:update")
   @PATCH
   @Path("/{id}")
   @SelectJson("user")
   @WrapWith("user")
   User update(@PathParam("id") String id, @PayloadParam("name") String name,
         @Nullable @PayloadParam("password") String password, @Nullable @PayloadParam("enabled") Boolean enabled,
         @Nullable @PayloadParam("domain_id") String domainId,
         @Nullable @PayloadParam("default_project_id") String defaultProjectId);

   @Named("users:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);

   @Named("users:groups")
   @GET
   @Path("/{id}/groups")
   @SelectJson("groups")
   List<Group> listGroups(@PathParam("id") String id);

   @Named("users:projects")
   @GET
   @Path("/{id}/projects")
   @SelectJson("projects")
   List<Project> listProjects(@PathParam("id") String id);

   @Named("users:password")
   @POST
   @Path("/{id}/password")
   @WrapWith("user")
   void changePassword(@PathParam("id") String id, @PayloadParam("original_password") String originalPassword,
         @PayloadParam("password") String newPassword);
}
