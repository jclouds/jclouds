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
package org.jclouds.azurecompute.arm.config;

import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.RESOURCE;

import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.v2.config.OAuthConfigFactory;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AzureOAuthConfigFactory implements OAuthConfigFactory {
   private final OAuthScopes scopes;
   
   @Named(AUDIENCE)
   @Inject(optional = true)
   private String audience;
   
   @Named(RESOURCE)
   @Inject(optional = true)
   private String resource;

   @Inject
   AzureOAuthConfigFactory(OAuthScopes scopes) {
      this.scopes = scopes;
   }

   @Override
   public OAuthConfig forRequest(HttpRequest input) {
      OAuthResource customResource = null;
      if (input instanceof GeneratedHttpRequest) {
         GeneratedHttpRequest request = (GeneratedHttpRequest) input;
         customResource = request.getInvocation().getInvokable().getAnnotation(OAuthResource.class);
         if (customResource == null) {
            customResource = request.getInvocation().getInvokable().getDeclaringClass()
                  .getAnnotation(OAuthResource.class);
         }
      }
      String oauthResource = customResource != null ? customResource.value() : resource;
      return OAuthConfig.create(scopes.forRequest(input), audience, oauthResource);
   }
}
