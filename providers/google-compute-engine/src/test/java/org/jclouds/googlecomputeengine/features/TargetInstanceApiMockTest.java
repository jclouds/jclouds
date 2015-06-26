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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.googlecomputeengine.domain.NewTargetInstance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetInstanceListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetInstanceTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "TargetInstanceApiMockTest", singleThreaded = true)
public class TargetInstanceApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public static String TARGET_INSTANCE_NAME = "target-instance-1";

   public void get() throws Exception {
      server.enqueue(jsonResponse("/target_instance_get.json"));

      assertEquals(targetInstanceApi().get(TARGET_INSTANCE_NAME),
            new ParseTargetInstanceTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/targetInstances/" + TARGET_INSTANCE_NAME);
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(targetInstanceApi().get(TARGET_INSTANCE_NAME));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/targetInstances/" + TARGET_INSTANCE_NAME);
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      NewTargetInstance options = new NewTargetInstance.Builder()
         .name("test-target-instance")
         .description("This is a test")
         .natPolicy("NO_NAT")
         .instance(URI.create(url("/projects/party/zones/us-central1-a/instances/instance-1")))
         .build();

      assertEquals(targetInstanceApi().create(options),
            new ParseOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/targetInstances",
            stringFromResource("/target_instance_insert.json"));
   }

   public void testDeleteImageResponseIs2xx()  throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(targetInstanceApi().delete(TARGET_INSTANCE_NAME),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/zones/us-central1-a/targetInstances/" + TARGET_INSTANCE_NAME);
   }

   public void delete() throws Exception {
      server.enqueue(response404());

      assertNull(targetInstanceApi().delete(TARGET_INSTANCE_NAME));
      assertSent(server, "DELETE", "/projects/party/zones/us-central1-a/targetInstances/" + TARGET_INSTANCE_NAME);
   }

   public void list() throws InterruptedException {
      server.enqueue(jsonResponse("/target_instance_list.json"));

      assertEquals(targetInstanceApi().list().next(), new ParseTargetInstanceListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/targetInstances");
   }

   public void listEmpty() throws InterruptedException {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(targetInstanceApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/targetInstances");
   }

   TargetInstanceApi targetInstanceApi() {
      return api().targetInstancesInZone("us-central1-a");
   }
}
