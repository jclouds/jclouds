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
package org.jclouds.oauth.v2;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

import org.jclouds.oauth.v2.config.Authentication;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.handlers.OAuthTokenBinder;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;

/**
 * Provides access to OAuth via REST api.
 * <p/>
 * Usually this is not directly used by a client, which instead specifies
 * OAuthAuthenticator as a request filter, which in turn uses this class to
 * perform token requests.
 * 
 * @see OAuthAsyncApi
 */
@Endpoint(Authentication.class)
public interface OAuthApi extends Closeable {

   /**
    * Authenticates/Authorizes access to a resource defined in TokenRequest
    * against an OAuth v2 authentication/authorization server.
    * 
    * @param tokenRequest
    *           specified the principal and the required permissions
    * @return a Token object with the token required to access the resource
    *         along with its expiration time
    * @throws AuthorizationException
    *            if the principal cannot be authenticated or has no permissions
    *            for the specifed resources.
    */
   @Named("authenticate")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   Token authenticate(@BinderParam(OAuthTokenBinder.class) TokenRequest tokenRequest) throws AuthorizationException;

}
