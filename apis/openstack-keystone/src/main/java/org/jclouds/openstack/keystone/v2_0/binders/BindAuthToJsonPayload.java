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
package org.jclouds.openstack.keystone.v2_0.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.tryFind;
import static org.jclouds.openstack.keystone.auth.config.CredentialTypes.findCredentialType;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.openstack.keystone.auth.config.CredentialType;
import org.jclouds.openstack.keystone.auth.domain.TenantOrDomainAndCredentials;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@Singleton
public class BindAuthToJsonPayload extends BindToJsonPayload implements MapBinder {
   @Inject
   public BindAuthToJsonPayload(Json jsonBinder) {
      super(jsonBinder);
   }

   protected TenantOrDomainAndCredentials<?> findCredentialsInArgs(GeneratedHttpRequest gRequest) {
      Optional<Object> credentials = tryFind(gRequest.getInvocation().getArgs(), instanceOf(TenantOrDomainAndCredentials.class));
      return credentials.isPresent() ? (TenantOrDomainAndCredentials<?>) credentials.get() : null;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      Builder<String, Object> builder = ImmutableMap.builder();

      TenantOrDomainAndCredentials<?> credentials = findCredentialsInArgs(gRequest);
      if (credentials != null) {
         CredentialType credentialType = findCredentialType(credentials.credentials().getClass());
         checkArgument(credentialType != null, "the given credentials must be annotated with @CredentialType");

         builder.put(credentialType.value(), credentials.credentials());

         // TODO: is tenantName permanent? or should we switch to tenantId at
         // some point. seems most tools still use tenantName
         if (credentials != null) {
            if (!Strings.isNullOrEmpty(credentials.tenantOrDomainId()))
               builder.put("tenantId", credentials.tenantOrDomainId());
            else if (!Strings.isNullOrEmpty(credentials.tenantOrDomainName()))
               builder.put("tenantName", credentials.tenantOrDomainName());
         }
      }

      R authRequest = super.bindToRequest(request, ImmutableMap.of("auth", builder.build()));
      authRequest.getPayload().setSensitive(true);
      return authRequest;
   }

}
