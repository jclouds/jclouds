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
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.auth.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v3.domain.Project;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to the Keystone Projects API.
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Identity.class)
@Path("/projects")
public interface ProjectApi {

   @Named("projects:list")
   @GET
   @SelectJson("projects")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Project> list();
   
   @Named("projects:get")
   @GET
   @Path("/{id}")
   @SelectJson("project")
   @Fallback(NullOnNotFoundOr404.class)
   Project get(@PathParam("id") String id);
   
   @Named("projects:create")
   @POST
   @SelectJson("project")
   @WrapWith("project") 
   Project create(@PayloadParam("name") String name, @Nullable @PayloadParam("description") String description,
         @PayloadParam("enabled") boolean enabled, @PayloadParam("is_domain") boolean isDomain,
         @Nullable @PayloadParam("domain_id") String domainId, @Nullable @PayloadParam("parent_id") String parentId);
   
   @Named("projects:update")
   @PATCH
   @Path("/{id}")
   @SelectJson("project")
   Project update(@PathParam("id") String id, @WrapWith("project") Project project);
   
   @Named("projects:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);
   
   @Named("projects:listTags")
   @GET
   @Path("/{projectId}/tags")
   @SelectJson("tags")
   Set<String> listTags(@PathParam("projectId") String projectId);
   
   @Named("projects:hasTag")
   @HEAD
   @Path("/{projectId}/tags/{tag}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean hasTag(@PathParam("projectId") String projectId, @PathParam("tag") String tag);
   
   @Named("projects:addTag")
   @PUT
   @Path("/{projectId}/tags/{tag}")
   void addTag(@PathParam("projectId") String projectId, @PathParam("tag") String tag);
   
   @Named("projects:removeTag")
   @DELETE
   @Path("/{projectId}/tags/{tag}")
   void removeTag(@PathParam("projectId") String projectId, @PathParam("tag") String tag);
   
   @Named("projects:setTags")
   @PUT
   @Path("/{projectId}/tags")
   @MapBinder(BindToJsonPayload.class)
   void setTags(@PathParam("projectId") String projectId, @PayloadParam("tags") Set<String> tags);
   
   @Named("projects:removeTags")
   @DELETE
   @Path("/{projectId}/tags")
   void removeAllTags(@PathParam("projectId") String projectId);
}
