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
package org.jclouds.openstack.keystone.v2_0.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
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
@Path("/tokens/{token}")
public interface TokenApi {

   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog)/
    *
    * @return the requested information
    */
   @Named("token:get")
   @GET
   @SelectJson("token")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Token get(@PathParam("token") String token);

   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog)/
    *
    * @return the requested information
    */
   @Named("token:getuser")
   @GET
   @SelectJson("user")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   User getUserOfToken(@PathParam("token") String token);

   /**
    * Validate a token. This is a high-performance variant of the #getToken() call that does not return any further
    * information.
    *
    * @return true if the token is valid
    */
   @Named("token:valid")
   @HEAD
   @Fallback(FalseOnNotFoundOr404.class)
   boolean isValid(@PathParam("token") String token);

   /**
    * List all endpoints for a token
    * <p/>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/988672 )
    *
    * @return the set of endpoints
    */
   @Named("token:listEndpoints")
   @GET
   @SelectJson("endpoints")
   @Path("/endpoints")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Endpoint> listEndpointsForToken(@PathParam("token") String token);
}
