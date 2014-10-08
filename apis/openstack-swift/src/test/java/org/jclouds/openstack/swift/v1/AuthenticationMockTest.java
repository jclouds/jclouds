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
package org.jclouds.openstack.swift.v1;

import static com.google.common.base.Charsets.UTF_8;
import static org.jclouds.openstack.swift.v1.features.AccountApiMockTest.accountResponse;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * @see KeystoneProperties#CREDENTIAL_TYPE
 */
@Test
public class AuthenticationMockTest extends BaseOpenStackMockTest<SwiftApi> {

   @DataProvider(name = "jclouds.keystone.credential-type")
   Object[][] credentialTypeToPostBody() {
      Object[][] credentialTypeToPostBody = new Object[2][2];
      credentialTypeToPostBody[0][0] = "apiAccessKeyCredentials";
      credentialTypeToPostBody[0][1] = "{\"auth\":{\"apiAccessKeyCredentials\":{\"accessKey\":\"joe\",\"secretKey\":\"letmein\"},\"tenantName\":\"jclouds\"}}";
      credentialTypeToPostBody[1][0] = "passwordCredentials";
      credentialTypeToPostBody[1][1] = "{\"auth\":{\"passwordCredentials\":{\"username\":\"joe\",\"password\":\"letmein\"},\"tenantName\":\"jclouds\"}}";
      return credentialTypeToPostBody;
   }

   @Test(dataProvider = "jclouds.keystone.credential-type")
   public void authenticateCredentialType(String credentialType, String expectedPost) throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(accountResponse()));

      try {
         Properties overrides = new Properties();
         overrides.setProperty("jclouds.keystone.credential-type", credentialType);

         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift", overrides);

         api.getAccountApi("DFW").get();

         assertEquals(server.getRequestCount(), 2);
         RecordedRequest authRequest = server.takeRequest();
         assertEquals(authRequest.getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(new String(authRequest.getBody(), UTF_8), expectedPost);
      } finally {
         server.shutdown();
      }
   }
}
