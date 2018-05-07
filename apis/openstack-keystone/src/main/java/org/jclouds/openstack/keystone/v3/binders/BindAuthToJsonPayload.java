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
package org.jclouds.openstack.keystone.v3.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.tryFind;
import static org.jclouds.openstack.keystone.v3.domain.Auth.Scope.DOMAIN;
import static org.jclouds.openstack.keystone.v3.domain.Auth.Scope.DOMAIN_ID;
import static org.jclouds.openstack.keystone.v3.domain.Auth.Scope.PROJECT;
import static org.jclouds.openstack.keystone.v3.domain.Auth.Scope.PROJECT_ID;
import static org.jclouds.openstack.keystone.v3.domain.Auth.Scope.UNSCOPED;

import java.util.Map;
import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.openstack.keystone.auth.domain.TenantOrDomainAndCredentials;
import org.jclouds.openstack.keystone.v3.domain.Auth;
import org.jclouds.openstack.keystone.v3.domain.Auth.DomainIdScope;
import org.jclouds.openstack.keystone.v3.domain.Auth.DomainScope;
import org.jclouds.openstack.keystone.v3.domain.Auth.Id;
import org.jclouds.openstack.keystone.v3.domain.Auth.Name;
import org.jclouds.openstack.keystone.v3.domain.Auth.ProjectIdScope;
import org.jclouds.openstack.keystone.v3.domain.Auth.ProjectIdScope.ProjectId;
import org.jclouds.openstack.keystone.v3.domain.Auth.ProjectScope;
import org.jclouds.openstack.keystone.v3.domain.Auth.ProjectScope.ProjectName;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public abstract class BindAuthToJsonPayload<T> extends BindToJsonPayload implements MapBinder {

   private static final Set<String> SCOPE_PREFIXES = ImmutableSet.of(PROJECT, PROJECT_ID, DOMAIN, DOMAIN_ID);

   protected BindAuthToJsonPayload(Json jsonBinder) {
      super(jsonBinder);
   }

   protected abstract Auth buildAuth(TenantOrDomainAndCredentials<T> credentials, Object scope);

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;

      Optional<Object> authentication = tryFind(gRequest.getInvocation().getArgs(),
            instanceOf(TenantOrDomainAndCredentials.class));
      checkArgument(authentication.isPresent(), "no credentials found in the api call arguments");

      @SuppressWarnings("unchecked")
      TenantOrDomainAndCredentials<T> credentials = (TenantOrDomainAndCredentials<T>) authentication.get();
      Object scope = parseScope(credentials);
      Auth auth = buildAuth(credentials, scope);

      R authRequest = super.bindToRequest(request, ImmutableMap.of("auth", auth));
      authRequest.getPayload().setSensitive(true);

      return authRequest;
   }

   private Object parseScope(TenantOrDomainAndCredentials<T> credentials) {
      String scope = credentials.scope();
      // If there is no prefix, assume an unscoped authentication
      if (!scope.contains(":")) {
         checkArgument(scope.equals(UNSCOPED), "Invalid scope: %s", scope);
         return UNSCOPED;
      }
      // Otherwise, parse if it is a project or domain scope
      String[] parts = scope.split(":");
      checkArgument(parts.length == 2, "Invalid scope: %s", scope);
      checkArgument(SCOPE_PREFIXES.contains(parts[0]), "Scope prefix should be: %s", SCOPE_PREFIXES);

      if (PROJECT.equals(parts[0])) {
         return ProjectScope.create(ProjectName.create(parts[1], parseProjectDomain(credentials, true)));
      } else if (PROJECT_ID.equals(parts[0])) {
         // tenant (name/id) was never used as domain for project-id; so try to
         // keep backward compatibility
         return ProjectIdScope.create(ProjectId.create(parts[1], parseProjectDomain(credentials, false)));
      } else if (DOMAIN.equals(parts[0])) {
         return DomainScope.create(Name.create(parts[1]));
      } else {
         return DomainIdScope.create(Id.create(parts[1]));
      }
   }

   private Object parseProjectDomain(TenantOrDomainAndCredentials<T> credentials, boolean useTenantAsDefaultDomain) {
      // Before 'projectDomainName'/'projectDomainId' support,
      // 'tenantOrDomainId' was used as domain (id) for project-scoped by name,
      // but not by id, so 'useTenantAsDefaultDomain' flag allows to manage that
      Object domainScope = null;
      if (useTenantAsDefaultDomain && credentials.tenantOrDomainId() != null) {
         domainScope = Id.create(credentials.tenantOrDomainId());
      } else if (credentials.projectDomainName() != null) {
         domainScope = Name.create(credentials.projectDomainName());
      } else if (credentials.projectDomainId() != null) {
         domainScope = Id.create(credentials.projectDomainId());
      } else if (useTenantAsDefaultDomain) {
         domainScope = Name.create(credentials.tenantOrDomainName());
      }
      return domainScope;
   }
}
