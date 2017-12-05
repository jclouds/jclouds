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
package org.jclouds.openstack.keystone.v3.auth;

import java.io.Closeable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.auth.AuthenticationApi;
import org.jclouds.openstack.keystone.auth.domain.PasswordCredentials;
import org.jclouds.openstack.keystone.auth.domain.TenantOrDomainAndCredentials;
import org.jclouds.openstack.keystone.auth.domain.TokenCredentials;
import org.jclouds.openstack.keystone.v3.binders.BindPasswordAuthToJsonPayload;
import org.jclouds.openstack.keystone.v3.binders.BindTokenAuthToJsonPayload;
import org.jclouds.openstack.keystone.v3.domain.Token;
import org.jclouds.openstack.keystone.v3.parsers.ParseTokenFromHttpResponse;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.inject.name.Named;

/**
 * Provides access to the OpenStack Keystone Authentication API.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Path("/auth/tokens")
public interface V3AuthenticationApi extends AuthenticationApi, Closeable {

   @Named("token:create")
   @POST
   @ResponseParser(ParseTokenFromHttpResponse.class)
   @MapBinder(BindPasswordAuthToJsonPayload.class)
   @Override
   Token authenticatePassword(TenantOrDomainAndCredentials<PasswordCredentials> credentials);

   @Named("token:create")
   @POST
   @ResponseParser(ParseTokenFromHttpResponse.class)
   @MapBinder(BindTokenAuthToJsonPayload.class)
   @Override
   Token authenticateToken(TenantOrDomainAndCredentials<TokenCredentials> credentials);

}
