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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.handlers.MetadataBinder;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides access to Projects via their REST API.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/projects"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface ProjectApi {

   /**
    * Returns the specified project resource.
    *
    * @param projectName name of the project to return
    * @return if successful, this method returns a Project resource
    */
   @Named("Projects:get")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/projects/{project}")
   Project get(@PathParam("project") String projectName);

   /**
    * Sets metadata common to all instances within the specified project using the data included in the request.
    * <p/>
    * NOTE: This *sets* metadata items on the project (vs *adding* items to metadata),
    * if there are pre-existing metadata items that must be kept these must be fetched first and then re-set on the
    * new Metadata, e.g.
    * <pre><tt>
    *    Metadata.Builder current = projectApi.get("myProject").getCommonInstanceMetadata().toBuilder();
    *    current.addItem("newItem","newItemValue");
    *    projectApi.setCommonInstanceMetadata(current.build());
    * </tt></pre>
    *
    * @param projectName            name of the project to return
    * @param metadata the metadata to set
    * @param fingerprint  The current fingerprint for the metadata
    * @return an Operations resource. To check on the status of an operation, poll the Operations resource returned
    *         to you, and look for the status field.
    */
   @Named("Projects:setCommonInstanceMetadata")
   @POST
   @Path("/projects/{project}/setCommonInstanceMetadata")
   @OAuthScopes(COMPUTE_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(MetadataBinder.class)
   Operation setCommonInstanceMetadata(@PathParam("project") String projectName,
                                       @PayloadParam("items") Map<String, String> metadata,
                                       @PayloadParam("fingerprint") String fingerprint);
}
