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
      server.enqueue(jsonResponse("/v3/token.json"));

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials.<PasswordCredentials> builder()
            .tenantOrDomainName("domain")
            .scope("unscoped")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();
      
      AuthInfo authInfo = authenticationApi.authenticatePassword(credentials);

      assertTrue(authInfo instanceof Token);
      assertEquals(authInfo, tokenFromResource("/v3/token.json"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/auth/tokens", stringFromResource("/v3/auth-password.json"));
   }
   
   public void testAuthenticatePasswordScoped() throws InterruptedException {
      server.enqueue(jsonResponse("/v3/token.json"));

      TenantOrDomainAndCredentials<PasswordCredentials> credentials = TenantOrDomainAndCredentials.<PasswordCredentials> builder()
            .tenantOrDomainName("domain")
            .scope("projectId:1234567890")
            .credentials(PasswordCredentials.builder().username("identity").password("credential").build()).build();
      
      AuthInfo authInfo = authenticationApi.authenticatePassword(credentials);

      assertTrue(authInfo instanceof Token);
      assertEquals(authInfo, tokenFromResource("/v3/token.json"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/auth/tokens", stringFromResource("/v3/auth-password-scoped.json"));
   }

   public void testAuthenticateToken() throws InterruptedException {
      server.enqueue(jsonResponse("/v3/token.json"));

      TenantOrDomainAndCredentials<TokenCredentials> credentials = TenantOrDomainAndCredentials.<TokenCredentials> builder()
            .tenantOrDomainName("domain")
            .scope("unscoped")
            .credentials(TokenCredentials.builder().id("token").build()).build();
      
      AuthInfo authInfo = authenticationApi.authenticateToken(credentials);

      assertTrue(authInfo instanceof Token);
      assertEquals(authInfo, tokenFromResource("/v3/token.json"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/auth/tokens", stringFromResource("/v3/auth-token.json"));
   }
   
   public void testAuthenticateTokenScoped() throws InterruptedException {
      server.enqueue(jsonResponse("/v3/token.json"));

      TenantOrDomainAndCredentials<TokenCredentials> credentials = TenantOrDomainAndCredentials.<TokenCredentials> builder()
            .tenantOrDomainName("domain")
            .scope("domain:mydomain")
            .credentials(TokenCredentials.builder().id("token").build()).build();
      
      AuthInfo authInfo = authenticationApi.authenticateToken(credentials);

      assertTrue(authInfo instanceof Token);
      assertEquals(authInfo, tokenFromResource("/v3/token.json"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/auth/tokens", stringFromResource("/v3/auth-token-scoped.json"));
   }

}
