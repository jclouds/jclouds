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
package org.jclouds.openstack.keystone.v1_1;

import java.io.Closeable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v1_1.binders.BindCredentialsToJsonPayload;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides access to the Keystone v1.1 Service API.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Path("/v1.1")
public interface AuthenticationClient extends Closeable {

   /**
    * Authenticate to generate a token.
    *
    * @return access with token
    */
   @POST
   @SelectJson("auth")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/auth")
   @MapBinder(BindCredentialsToJsonPayload.class)
   Auth authenticate(@PayloadParam("username") String username, @PayloadParam("key") String key);
}
