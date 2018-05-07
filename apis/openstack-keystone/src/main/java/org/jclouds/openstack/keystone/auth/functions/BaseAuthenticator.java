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

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.PROJECT_DOMAIN_ID;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.PROJECT_DOMAIN_NAME;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.REQUIRES_TENANT;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.SCOPE;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.TENANT_ID;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.TENANT_NAME;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;
import org.jclouds.openstack.keystone.auth.domain.TenantOrDomainAndCredentials;
import org.jclouds.openstack.keystone.v3.domain.Auth.Scope;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class BaseAuthenticator<C> implements Function<Credentials, AuthInfo> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(TENANT_NAME)
   protected String defaultTenantName;

   @Inject(optional = true)
   @Named(TENANT_ID)
   protected String defaultTenantId;

   @Inject(optional = true)
   @Named(REQUIRES_TENANT)
   protected boolean requiresTenant;

   @Inject(optional = true)
   @Named(SCOPE)
   protected String scope = Scope.UNSCOPED;

   @Inject(optional = true)
   @Named(PROJECT_DOMAIN_NAME)
   protected String projectDomainName;

   @Inject(optional = true)
   @Named(PROJECT_DOMAIN_ID)
   protected String projectDomainId;

   @PostConstruct
   public void checkPropertiesAreCompatible() {
      checkState(defaultTenantName == null || defaultTenantId == null, "you cannot specify both %s and %s", TENANT_NAME,
            TENANT_ID);
      checkState(projectDomainName == null || projectDomainId == null, "you cannot specify both %s and %s",
            PROJECT_DOMAIN_NAME, PROJECT_DOMAIN_ID);
   }

   @Override
   public AuthInfo apply(Credentials input) {
      String tenantName = defaultTenantName;
      String usernameOrAccessKey = input.identity;
      String passwordOrSecretKeyOrToken = input.credential;

      if (defaultTenantName == null && input.identity.indexOf(':') != -1) {
         tenantName = input.identity.substring(0, input.identity.lastIndexOf(':'));
         usernameOrAccessKey = input.identity.substring(input.identity.lastIndexOf(':') + 1);
      }

      if (defaultTenantId == null && tenantName == null && requiresTenant) {
         throw new IllegalArgumentException(String.format(
               "current configuration is set to [%s]. Unless you set [%s] or [%s], you must prefix your identity with 'tenantName:'",
               REQUIRES_TENANT, TENANT_NAME, TENANT_ID));
      }

      C creds = createCredentials(usernameOrAccessKey, passwordOrSecretKeyOrToken);
      TenantOrDomainAndCredentials<C> credsWithTenant = TenantOrDomainAndCredentials.<C> builder()
            .tenantOrDomainId(defaultTenantId).tenantOrDomainName(tenantName).scope(scope)
            .projectDomainName(projectDomainName).projectDomainId(projectDomainId).credentials(creds).build();

      return authenticate(credsWithTenant);
   }

   public abstract C createCredentials(String identity, String credential);

   public abstract AuthInfo authenticate(TenantOrDomainAndCredentials<C> credentials);

}
