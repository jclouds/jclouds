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
package org.jclouds.openstack.keystone.auth.functions;

import static org.jclouds.openstack.keystone.auth.config.CredentialTypes.TOKEN_CREDENTIALS;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.openstack.keystone.auth.AuthenticationApi;
import org.jclouds.openstack.keystone.auth.config.CredentialType;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;
import org.jclouds.openstack.keystone.auth.domain.TenantOrDomainAndCredentials;
import org.jclouds.openstack.keystone.auth.domain.TokenCredentials;

@CredentialType(TOKEN_CREDENTIALS)
@Singleton
public class AuthenticateTokenCredentials extends BaseAuthenticator<TokenCredentials> {

   private final AuthenticationApi auth;

   @Inject
   AuthenticateTokenCredentials(AuthenticationApi auth) {
      this.auth = auth;
   }

   @Override
   public TokenCredentials createCredentials(String identity, String credential) {
      return TokenCredentials.builder().id(credential).build();
   }

   @Override
   public AuthInfo authenticate(TenantOrDomainAndCredentials<TokenCredentials> credentials) {
      return auth.authenticateToken(credentials);
   }
}
