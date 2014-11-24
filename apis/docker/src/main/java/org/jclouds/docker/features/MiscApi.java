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

import java.io.InputStream;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.docker.domain.Info;
import org.jclouds.docker.domain.Version;
import org.jclouds.docker.options.BuildOptions;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.Headers;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/v{jclouds.api-version}")
public interface MiscApi {

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
    * Get the information of the current docker version.
    *
    * @return The information of the current docker version.
    */
   @Named("info")
   @GET
   @Path("/info")
   Info getInfo();

   /**
    * Build an image from Dockerfile via stdin
    *
    * @param inputStream The stream must be a tar archive compressed with one of the following algorithms: identity
    *                    (no compression), gzip, bzip2, xz.
    * @return a stream of the build execution
    */
   @Named("image:build")
   @POST
   @Path("/build")
   @Headers(keys = "Content-Type", values = "application/tar")
   InputStream build(Payload inputStream);

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

}
