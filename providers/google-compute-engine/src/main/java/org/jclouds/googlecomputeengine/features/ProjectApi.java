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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.NullOn400or404;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToJsonPayload;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Consumes(APPLICATION_JSON)
public interface ProjectApi {

   /** Get the current project. */
   @Named("Projects:get")
   @GET
   @Fallback(NullOn400or404.class)
   Project get();

   /**
    * Sets metadata common to all instances within the current project using the data included in the request.
    * <p/>
    * NOTE: This *sets* metadata items on the project (vs *adding* items to metadata),
    * if there are existing metadata that must be kept these must be fetched first and then re-sent on update.
    * <pre><tt>
    *    Metadata update = projectApi.get("myProject").getCommonInstanceMetadata().clone();
    *    update.put("newItem","newItemValue");
    *    projectApi.setCommonInstanceMetadata("myProject", update);
    * </tt></pre>
    *
    * @param metadata      the metadata to set
    * @return an Operations resource. To check on the status of an operation, poll the Operations resource returned
    *         to you, and look for the status field.
    */
   @Named("Projects:setCommonInstanceMetadata")
   @POST
   @Path("/setCommonInstanceMetadata")
   @Produces(APPLICATION_JSON)
   Operation setCommonInstanceMetadata(@BinderParam(BindToJsonPayload.class) Metadata metadata);

   @Named("Projects:setUsageExportBucket")
   @POST
   @Path("/setUsageExportBucket")
   @Produces(APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Operation setUsageExportBucket(@PayloadParam("bucketName") String bucketName,
                                  @PayloadParam("reportNamePrefix") String reportNamePrefix);
}
