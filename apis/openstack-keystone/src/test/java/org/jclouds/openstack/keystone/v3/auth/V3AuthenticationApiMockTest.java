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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.keystone.auth.domain.ApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;
import org.jclouds.openstack.keystone.auth.domain.PasswordCredentials;
import org.jclouds.openstack.keystone.auth.domain.TenantOrDomainAndCredentials;
import org.jclouds.openstack.keystone.auth.domain.TokenCredentials;
import org.jclouds.openstack.keystone.v3.domain.Token;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "V3AuthenticationApiMockTest", singleThreaded = true)
public class V3AuthenticationApiMockTest extends BaseV3KeystoneApiMockTest {

   public void testAuthenticatePassword() throws InterruptedException {

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("unscoped")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password.json");
   }

   public void testAuthenticatePasswordScoped() throws InterruptedException {

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("projectId:1234567890")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password-scoped.json");
   }

   public void testAuthenticatePasswordProjectScopedIdDomainBackwardsCompat() throws InterruptedException {
      // See JCLOUDS-1414, before add of KeystoneProperties.PROJECT_DOMAIN,
      // TENANT_ID was not used as domain for project-scoped with id
      // => Unit test only for backward compatibility (is the same as
      // 'testAuthenticatePasswordScoped' with TENANT-ID in addition)

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("projectId:1234567890")
            .tenantOrDomainId("somethingShouldNotBeUsed")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password-scoped.json");
   }

   public void testAuthenticatePasswordProjectScopedNameDomainBackwardsCompat() throws InterruptedException {
      // See JCLOUDS-1414, before add of KeystoneProperties.PROJECT_DOMAIN,
      // domain-id of project-scoped could be filled with TENANT_ID
      // => Unit test only for backward compatibility

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("project:my-project")
            .tenantOrDomainId("default")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password-project-scoped-name-domain-backwards-compat.json");
   }

   public void testAuthenticatePasswordProjectScopedIdDomainId() throws InterruptedException {

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("projectId:42-project-42")
            .projectDomainId("42-domain-42")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password-project-scoped-id-domain-id.json");
   }

   public void testAuthenticatePasswordProjectScopedIdDomainName() throws InterruptedException {

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("projectId:42")
            .projectDomainName("default")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password-project-scoped-id-domain-name.json");
   }

   public void testAuthenticatePasswordProjectScopedNameDomainId() throws InterruptedException {

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("project:my-project")
            .projectDomainId("42")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password-project-scoped-name-domain-id.json");
   }

   public void testAuthenticatePasswordProjectScopedNameDomainName() throws InterruptedException {

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials
            .<PasswordCredentials> builder().tenantOrDomainName("domain").scope("project:my-project")
            .projectDomainName("default")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();

      checkTokenResult(credentials, "/v3/auth-password-project-scoped-name-domain-name.json");
   }

   public void testAuthenticateToken() throws InterruptedException {

      TenantOrDomainAndCredentials<TokenCredentials> credentials = TenantOrDomainAndCredentials
            .<TokenCredentials> builder().tenantOrDomainName("domain").scope("unscoped")
            .credentials(TokenCredentials.builder().id("token").build()).build();

      checkTokenResult(credentials, "/v3/auth-token.json");
   }

   public void testAuthenticateTokenScoped() throws InterruptedException {

      TenantOrDomainAndCredentials<TokenCredentials> credentials = TenantOrDomainAndCredentials
            .<TokenCredentials> builder().tenantOrDomainName("domain").scope("domain:mydomain")
            .credentials(TokenCredentials.builder().id("token").build()).build();

      checkTokenResult(credentials, "/v3/auth-token-scoped.json");
   }

   @SuppressWarnings("unchecked")
   private void checkTokenResult(TenantOrDomainAndCredentials<?> credentials, String json) throws InterruptedException {
      server.enqueue(jsonResponse("/v3/token.json"));

      AuthInfo authInfo = null;

      if (credentials.credentials() instanceof PasswordCredentials) {
         authInfo = authenticationApi
               .authenticatePassword((TenantOrDomainAndCredentials<PasswordCredentials>) credentials);
      } else if (credentials.credentials() instanceof TokenCredentials) {
         authInfo = authenticationApi.authenticateToken((TenantOrDomainAndCredentials<TokenCredentials>) credentials);
      } else if (credentials.credentials() instanceof ApiAccessKeyCredentials) {
         authInfo = authenticationApi
               .authenticateAccessKey((TenantOrDomainAndCredentials<ApiAccessKeyCredentials>) credentials);
      } else {
         throw new IllegalArgumentException(String.format("Unsupported authentication method with class: %s",
               credentials.credentials().getClass().getName()));
      }

      assertTrue(authInfo instanceof Token);
      assertEquals(authInfo, tokenFromResource("/v3/token.json"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/auth/tokens", stringFromResource(json));
   }

}
