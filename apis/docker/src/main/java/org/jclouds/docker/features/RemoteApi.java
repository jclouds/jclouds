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

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.Set;

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
import org.jclouds.docker.binders.BindInputStreamToRequest;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.HostConfig;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.domain.Version;
import org.jclouds.docker.options.BuildOptions;
import org.jclouds.docker.options.CommitOptions;
import org.jclouds.docker.options.CreateImageOptions;
import org.jclouds.docker.options.DeleteImageOptions;
import org.jclouds.docker.options.ListContainerOptions;
import org.jclouds.docker.options.ListImageOptions;
import org.jclouds.docker.options.RemoveContainerOptions;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.binders.BindToJsonPayload;

@Consumes(MediaType.APPLICATION_JSON)
public interface RemoteApi extends Closeable {

   /**
    * Get the information of the current docker version.
    *
    * @return The information of the current docker version.
    */
   @Named("version")
   @GET
   @Path("/version")
   Version getVersion();

   /**
    * List all running containers
    *
    * @return a set of containers
    */
   @Named("containers:list")
   @GET
   @Path("/containers/json")
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<Container> listContainers();

   /**
    * List all running containers
    *
    * @param options the options to list the containers (@see ListContainerOptions)
    * @return a set of containers
    */
   @Named("containers:list")
   @GET
   @Path("/containers/json")
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<Container> listContainers(ListContainerOptions options);

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

   /**
    * List images
    *
    * @return the images available.
    */
   @Named("images:list")
   @GET
   @Path("/images/json")
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<Image> listImages();

   /**
    * List images
    *
    * @param options the configuration to list images (@see ListImageOptions)
    * @return the images available.
    */
   @Named("images:list")
   @GET
   @Path("/images/json")
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<Image> listImages(ListImageOptions options);

   /**
    * Inspect an image
    *
    * @param imageName The id of the image to inspect.
    * @return low-level information on the image name
    */
   @Named("image:inspect")
   @GET
   @Path("/images/{name}/json")
   Image inspectImage(@PathParam("name") String imageName);

   /**
    * Create an image, either by pull it from the registry or by importing it
    *
    * @param options the configuration to create an image (@see CreateImageOptions)
    * @return a stream of the image creation.
    */
   @Named("image:create")
   @POST
   @Path("/images/create")
   InputStream createImage(CreateImageOptions options);

   /**
    * Delete an image.
    *
    * @param name the image name to be deleted
    * @return the stream of the deletion execution.
    */
   @Named("image:delete")
   @DELETE
   @Path("/images/{name}")
   InputStream deleteImage(@PathParam("name") String name);

   /**
    * Remove the image from the filesystem by name
    *
    * @param name the name of the image to be removed
    * @param options the image deletion's options (@see DeleteImageOptions)
    * @return the stream of the deletion execution.
    */
   @Named("image:delete")
   @DELETE
   @Path("/images/{name}")
   InputStream deleteImage(@PathParam("name") String name, DeleteImageOptions options);

   /**
    * Build an image from Dockerfile via stdin
    *
    * @param inputStream The stream must be a tar archive compressed with one of the following algorithms: identity
    *                    (no compression), gzip, bzip2, xz.
    * @param options the image build's options (@see BuildOptions)
    * @return a stream of the build execution
    */
   @Named("image:build")
   @POST
   @Path("/build")
   @Headers(keys = "Content-Type", values = "application/tar")
   InputStream build(Payload inputStream, BuildOptions options);

   /**
    * Build an image from Dockerfile via stdin
    *
    * @param dockerFile The file to be compressed with one of the following algorithms: identity, gzip, bzip2, xz.*
    * @param options the image build's options (@see BuildOptions)
    * @return a stream of the build execution
    */
   @Named("image:build")
   @POST
   @Path("/build")
   @Headers(keys = "Content-Type", values = "application/tar")
   InputStream build(@BinderParam(BindInputStreamToRequest.class) File dockerFile, BuildOptions options);

}
