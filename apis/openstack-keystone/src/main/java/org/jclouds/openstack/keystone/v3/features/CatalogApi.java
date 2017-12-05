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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.openstack.keystone.auth.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v3.domain.Endpoint;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides access to the Keystone Admin API.
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(AuthenticateRequest.class)
@org.jclouds.rest.annotations.Endpoint(Identity.class)
public interface CatalogApi {

   /**
    * List all endpoints for a token.
    * <p>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/988672 )
    */
   @Named("endpoints:list")
   @GET
   @SelectJson("endpoints")
   @Path("/endpoints")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Endpoint> endpoints();
}
