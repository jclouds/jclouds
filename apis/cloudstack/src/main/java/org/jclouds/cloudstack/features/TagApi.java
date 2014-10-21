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
package org.jclouds.cloudstack.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;
import java.util.Set;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Tag;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateTagsOptions;
import org.jclouds.cloudstack.options.DeleteTagsOptions;
import org.jclouds.cloudstack.options.ListTagsOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://cloudstack.apache.org/docs/api/apidocs-4.3/TOC_Root_Admin.html" />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface TagApi {

   /**
    * Lists tags
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return tags matching query, or empty set, if no tags are found
    */
   @Named("listTags")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listTags", "true" })
   @SelectJson("tag")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Tag> listTags(ListTagsOptions... options);

   /**
    * Creates one or more tags on the specified resources.
    *
    * @param options arguments
    * @return an asynchronous job structure
    */
   @Named("createTags")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "createTags")
   @Unwrap
   AsyncCreateResponse createTags(CreateTagsOptions options);

   /**
    * Deletes one or more tags from the specified resources.
    *
    * @param options arguments
    * @return an asynchronous job structure
    */
   @Named("deleteTags")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "deleteTags")
   @Unwrap
   AsyncCreateResponse deleteTags(DeleteTagsOptions options);

}
