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

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "TempAuthMockTest", singleThreaded = true)
public class TempAuthMockTest {

   private MockWebServer swiftServer;
   private MockWebServer tempAuthServer;


   public void testTempAuthRequest() throws Exception {
      tempAuthServer.enqueue(new MockResponse().setResponseCode(204)
            .addHeader("X-Auth-Token", "token")
            .addHeader("X-Storage-Url", swiftServer.getUrl("").toString()));

      swiftServer.enqueue(new MockResponse().setBody("[{\"name\":\"test_container_1\",\"count\":2,\"bytes\":78}]"));

      SwiftApi api = api(tempAuthServer.getUrl("").toString());

      // Region name is derived from the swift server host.
      assertEquals(api.getConfiguredRegions(), ImmutableSet.of(tempAuthServer.getHostName()));

      assertTrue(api.getContainerApi(tempAuthServer.getHostName()).list().iterator().hasNext());

      RecordedRequest auth = tempAuthServer.takeRequest();
      assertEquals(auth.getMethod(), "GET");
      assertEquals(auth.getHeader("X-Storage-User"), "user");
      assertEquals(auth.getHeader("X-Storage-Pass"), "password");

      // list request went to the destination specified in X-Storage-Url.
      RecordedRequest listContainers = swiftServer.takeRequest();
      assertEquals(listContainers.getMethod(), "GET");
      assertEquals(listContainers.getPath(), "/");
      assertEquals(listContainers.getHeader("Accept"), APPLICATION_JSON);
      assertEquals(listContainers.getHeader("X-Auth-Token"), "token");
   }

   private SwiftApi api(String authUrl) throws IOException {
      Properties overrides = new Properties();
      overrides.setProperty(CREDENTIAL_TYPE, "tempAuthCredentials");
      return ContextBuilder.newBuilder(new SwiftApiMetadata())
            .credentials("user", "password")
            .endpoint(authUrl)
            .overrides(overrides)
            .modules(ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor())))
            .buildApi(SwiftApi.class);
   }

   @BeforeMethod
   public void start() throws IOException {
      tempAuthServer = new MockWebServer();
      tempAuthServer.play();

      swiftServer = new MockWebServer();
      swiftServer.play();
   }

   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      tempAuthServer.shutdown();
      swiftServer.shutdown();
   }
}
