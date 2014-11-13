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
package org.jclouds.googlecomputeengine.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.domain.Credentials;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "UseApiToResolveProjectSelfLinkMockTest", singleThreaded = true)
public class UseApiToResolveProjectNameMockTest extends BaseGoogleComputeEngineApiMockTest {
   private final String projectNumber = "761326798069";

   public void validClientEmail() throws Exception {
      server.enqueue(jsonResponse("/project.json"));

      URI projectSelfLink = fn().apply(new Credentials(projectNumber + "@developer.gserviceaccount.com", credential));

      assertEquals(projectSelfLink.toString(), url("/projects/party"));

      assertSent(server, "GET", "/projects/" + projectNumber);
   }

   public void validClientEmail_extendedUid() throws Exception {
      server.enqueue(jsonResponse("/project.json"));

      URI projectSelfLink = fn().apply(
            new Credentials(projectNumber + "-r5mljlln1rd4lrbhg75efgigp36m78j5@developer.gserviceaccount.com", credential));

      assertEquals(projectSelfLink.toString(), url("/projects/party"));

      assertSent(server, "GET", "/projects/" + projectNumber);
   }

   /**
    * We do not support just supplying the projectNumber, as this causes confusion when using oauth. OAuth JWT requires
    * the whole email for the ISS field. This is better than confusing users with instructions like "Use the email,
    * except if using bearer token. Then, you don't need the entire email, just put in the numeric id part of it."
    */
   public void justProjectIdIsInvalid() throws Exception {
      server.enqueue(jsonResponse("/project.json"));

      try {
         fn().apply(new Credentials(projectNumber, credential));
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals(e.getMessage(), String.format("Client email %s is malformed. Should be %s", projectNumber,
               new GoogleComputeEngineApiMetadata().getIdentityName()));
      }

      assertEquals(server.getRequestCount(), 0);
   }

   GoogleComputeEngineHttpApiModule.UseApiToResolveProjectName fn() {
      return builder().buildInjector().getInstance(GoogleComputeEngineHttpApiModule.UseApiToResolveProjectName.class);
   }
}
