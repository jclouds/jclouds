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
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineScopes.COMPUTE_READONLY_SCOPE;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticationFilter.class)
@Path("/aggregated")
@Consumes(APPLICATION_JSON)
@OAuthScopes(COMPUTE_READONLY_SCOPE)
public interface AggregatedListApi {

   /**
    * Retrieves the list of machine type resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("MachineTypes:aggregatedList")
   @GET
   @Path("/machineTypes")
   ListPage<MachineType> pageOfMachineTypes(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfMachineTypes(String, ListOptions) */
   @Named("MachineTypes:aggregatedList")
   @GET
   @Path("/machineTypes")
   @Transform(MachineTypePages.class)
   Iterator<ListPage<MachineType>> machineTypes();

   /** @see #pageOfMachineTypes(String, ListOptions) */
   @Named("MachineTypes:aggregatedList")
   @GET
   @Path("/machineTypes")
   @Transform(MachineTypePages.class)
   Iterator<ListPage<MachineType>> machineTypes(ListOptions options);

   static final class MachineTypePages extends BaseToIteratorOfListPage<MachineType, MachineTypePages> {
      private final GoogleComputeEngineApi api;

      @Inject MachineTypePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<MachineType>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<MachineType>>() {
            @Override public ListPage<MachineType> apply(String pageToken) {
               return api.aggregatedList().pageOfMachineTypes(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of instance resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Instances:aggregatedList")
   @GET
   @Path("/instances")
   ListPage<Instance> pageOfInstances(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfInstances(String, ListOptions) */
   @Named("Instances:aggregatedList")
   @GET
   @Path("/instances")
   @Transform(InstancePages.class)
   Iterator<ListPage<Instance>> instances();

   /** @see #pageOfInstances(String, ListOptions) */
   @Named("Instances:aggregatedList")
   @GET
   @Path("/instances")
   @Transform(InstancePages.class)
   Iterator<ListPage<Instance>> instances(ListOptions options);

   static final class InstancePages extends BaseToIteratorOfListPage<Instance, InstancePages> {
      private final GoogleComputeEngineApi api;

      @Inject InstancePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Instance>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Instance>>() {
            @Override public ListPage<Instance> apply(String pageToken) {
               return api.aggregatedList().pageOfInstances(pageToken, options);
            }
         };
      }
   }
}
