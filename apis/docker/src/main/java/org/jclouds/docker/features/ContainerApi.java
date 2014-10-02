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
package org.jclouds.docker.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.ContainerSummary;
import org.jclouds.docker.domain.HostConfig;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.options.CommitOptions;
import org.jclouds.docker.options.ListContainerOptions;
import org.jclouds.docker.options.RemoveContainerOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.binders.BindToJsonPayload;

@Consumes(MediaType.APPLICATION_JSON)
public interface ContainerApi {

   /**
    * List all running containers
    *
    * @return a set of containers
    */
   @Named("containers:list")
   @GET
   @Path("/containers/json")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ContainerSummary> listContainers();

   /**
    * List all running containers
    *
    * @param options the options to list the containers (@see ListContainerOptions)
    * @return a set of containers
    */
   @Named("containers:list")
   @GET
   @Path("/containers/json")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ContainerSummary> listContainers(ListContainerOptions options);

   /**
    * Create a container
    *
    * @param name the name for the new container. Must match /?[a-zA-Z0-9_-]+.
    * @param config the container’s configuration (@see BindToJsonPayload)
    * @return a new container
    */
   @Named("container:create")
   @POST
   @Path("/containers/create")
   Container createContainer(@QueryParam("name") String name, @BinderParam(BindToJsonPayload.class) Config config);

   /**
    * Return low-level information on the container id
    * @param containerId  The id of the container to get.
    * @return The details of the container or <code>null</code> if the container with the given id doesn't exist.
    */
   @Named("container:inspect")
   @GET
   @Path("/containers/{id}/json")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Container inspectContainer(@PathParam("id") String containerId);

   /**
    * Remove the container by id from the filesystem
    *
    * @param containerId The id of the container to be removed.
    */
   @Named("container:delete")
   @DELETE
   @Path("/containers/{id}")
   void removeContainer(@PathParam("id") String containerId);

   /**
    * Remove the container by id from the filesystem
    *
    * @param containerId The id of the container to be removed.
    * @param options the operation’s configuration (@see RemoveContainerOptions)
    */
   @Named("container:delete")
   @DELETE
   @Path("/containers/{id}")
   void removeContainer(@PathParam("id") String containerId, RemoveContainerOptions options);

   /**
    * Start a container by id.
    *
    * @param containerId The id of the container to be started.
    */
   @Named("container:start")
   @POST
   @Path("/containers/{id}/start")
   void startContainer(@PathParam("id") String containerId);

   /**
    * Start a container.
    *
    * @param containerId The id of the container to be started.
    * @param hostConfig the container’s host configuration
    */
   @Named("container:start")
   @POST
   @Path("/containers/{id}/start")
   void startContainer(@PathParam("id") String containerId, @BinderParam(BindToJsonPayload.class) HostConfig hostConfig);

   /**
    * Stop a container by id.
    *
    * @param containerId The id of the container to be stopped.
    * @return the stream of the stop execution.
    */
   @Named("container:stop")
   @POST
   @Path("/containers/{id}/stop")
   void stopContainer(@PathParam("id") String containerId);

   /**
    * Create a new image from a container’s changes
    *
    * @param options the commit’s configuration (@see CommitOptions)
    * @return a new image created from the current container's status.
    */
   @Named("container:commit")
   @POST
   @Path("/commit")
   Image commit(CommitOptions options);

}
