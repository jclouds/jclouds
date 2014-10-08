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

import static org.jclouds.openstack.swift.v1.features.AccountApiMockTest.accountResponse;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_TEMPORARY_URL_KEY;
import static org.testng.Assert.assertEquals;

import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "TemporaryUrlSignerMockTest")
public class TemporaryUrlSignerMockTest extends BaseOpenStackMockTest<SwiftApi> {

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "accountApi")
   public void whenAccountApiIsNull() {
      TemporaryUrlSigner.checkApiEvery(null, 10000);
   }

   public void whenAccountApiHasKey() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(accountResponse().addHeader(ACCOUNT_TEMPORARY_URL_KEY, "mykey")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         String signature = TemporaryUrlSigner.checkApiEvery(api.getAccountApi("DFW"), 10000)
               .sign("GET", "/v1/AUTH_account/container/object", 1323479485l);

         assertEquals(signature, "d9fc2067e52b06598421664cf6610bfc8fc431f6");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "HEAD /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9 HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = ".*returned a null temporaryUrlKey!")
   public void whenAccountApiDoesntHaveKey() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(accountResponse()));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         TemporaryUrlSigner.checkApiEvery(api.getAccountApi("DFW"), 10000)
            .sign("GET", "/v1/AUTH_account/container/object", 1323479485l);
      } finally {
         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "HEAD /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9 HTTP/1.1");
         server.shutdown();
      }
   }
}
