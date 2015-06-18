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
package org.jclouds.googlecomputeengine.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTest;
import org.jclouds.googlecomputeengine.parse.ParseImageTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.jclouds.googlecomputeengine.parse.ParseNetworkTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ResourcesMockTest", singleThreaded = true)
public class ResourcesMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void testInstance() throws Exception {
      server.enqueue(jsonResponse("/instance_get.json"));

      Instance instance = resourceApi().instance(server.getUrl("/foo/bar").toURI());
      assertEquals(instance, new ParseInstanceTest().expected(url("/projects")));
      assertSent(server, "GET", "/foo/bar");
   }
   
   public void testInstanceReturns404() throws Exception {
      server.enqueue(response404());

      Instance instance = resourceApi().instance(server.getUrl("/foo/bar").toURI());
      assertNull(instance);
      assertSent(server, "GET", "/foo/bar");
   }
   
   public void testNetwork() throws Exception {
      server.enqueue(jsonResponse("/network_get.json"));

      Network network = resourceApi().network(server.getUrl("/foo/bar").toURI());
      assertEquals(network, new ParseNetworkTest().expected(url("/projects")));
      assertSent(server, "GET", "/foo/bar");
   }
   
   public void testNetworkReturns404() throws Exception {
      server.enqueue(response404());

      Network network = resourceApi().network(server.getUrl("/foo/bar").toURI());
      assertNull(network);
      assertSent(server, "GET", "/foo/bar");
   }

   public void testDisk() throws Exception {
      server.enqueue(jsonResponse("/disk_get.json"));

      Disk disk = resourceApi().disk(server.getUrl("/foo/bar").toURI());
      assertEquals(disk, new ParseDiskTest().expected(url("/projects")));
      assertSent(server, "GET", "/foo/bar");
   }

   public void testDiskReturns404() throws Exception {
      server.enqueue(response404());

      Disk disk = resourceApi().disk(server.getUrl("/foo/bar").toURI());
      assertNull(disk);
      assertSent(server, "GET", "/foo/bar");
   }

   public void testImage() throws Exception {
      server.enqueue(jsonResponse("/image_get.json"));

      Image image = resourceApi().image(server.getUrl("/foo/bar").toURI());
      assertEquals(image, new ParseImageTest().expected(url("/projects")));
      assertSent(server, "GET", "/foo/bar");
   }

   public void testImageReturns404() throws Exception {
      server.enqueue(response404());

      Image image = resourceApi().image(server.getUrl("/foo/bar").toURI());
      assertNull(image);
      assertSent(server, "GET", "/foo/bar");
   }

   public void testOperation() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      Operation operation = resourceApi().operation(server.getUrl("/foo/bar").toURI());
      assertEquals(operation, new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "GET", "/foo/bar");
   }
   
   public void testOperationReturns404() throws Exception {
      server.enqueue(response404());

      Operation operation = resourceApi().operation(server.getUrl("/foo/bar").toURI());
      assertNull(operation);
      assertSent(server, "GET", "/foo/bar");
   }
   
   public void testDelete() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      Operation operation = resourceApi().delete(server.getUrl("/foo/bar").toURI());
      assertEquals(operation, new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/foo/bar");
   }
   
   public void testDeleteReturns404() throws Exception {
      server.enqueue(response404());

      Operation operation = resourceApi().delete(server.getUrl("/foo/bar").toURI());
      assertNull(operation);
      assertSent(server, "DELETE", "/foo/bar");
   }
   
   public void testResetInstance() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      Operation operation = resourceApi().resetInstance(server.getUrl("/foo/bar").toURI());
      assertEquals(operation, new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/foo/bar/reset");
   }
   
   public void testStopInstance() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      Operation operation = resourceApi().stopInstance(server.getUrl("/foo/bar").toURI());
      assertEquals(operation, new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/foo/bar/stop");
   }
   
   public void testStartInstance() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      Operation operation = resourceApi().startInstance(server.getUrl("/foo/bar").toURI());
      assertEquals(operation, new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/foo/bar/start");
   }
   
   private Resources resourceApi() {
      return builder().build().utils().injector().getInstance(Resources.class);
   }
}

