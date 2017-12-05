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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.auth.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v3.domain.Token;
import org.jclouds.openstack.keystone.v3.domain.User;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides access to the Keystone Authentication API.
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Identity.class)
@Path("/auth")
public interface AuthApi {

   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog).
    */
   @Named("token:get")
   @GET
   @SelectJson("token")
   @Path("/tokens")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   @Headers(keys = "X-Subject-Token", values = "{token}")
   Token get(@PathParam("token") String token);

   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog).
    */
   @Named("token:getuser")
   @GET
   @SelectJson("user")
   @Path("/tokens")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   @Headers(keys = "X-Subject-Token", values = "{token}")
   User getUserOfToken(@PathParam("token") String token);

   /**
    * Validate a token. This is a high-performance variant of the #getToken() call that does not return any further
    * information.
    */
   @Named("token:check")
   @HEAD
   @Path("/tokens")
   @Headers(keys = "X-Subject-Token", values = "{token}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean isValid(@PathParam("token") String token);

}
