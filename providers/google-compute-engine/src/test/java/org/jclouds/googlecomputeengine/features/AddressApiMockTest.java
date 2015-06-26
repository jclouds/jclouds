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
import static org.testng.AssertJUnit.assertNull;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.AddressCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseAddressListTest;
import org.jclouds.googlecomputeengine.parse.ParseAddressTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "AddressApiExpectTest", singleThreaded = true)
public class AddressApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception{
      server.enqueue(jsonResponse("/address_get.json"));

      assertEquals(addressApi().get("test-ip1"), new ParseAddressTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/us-central1/addresses/test-ip1");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(addressApi().get("test-ip1"));
      assertSent(server, "GET", "/projects/party/regions/us-central1/addresses/test-ip1");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      assertEquals(addressApi().create("test-ip1"),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/addresses",
            stringFromResource("/address_insert.json"));
   }

   public void insert_options() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      AddressCreationOptions options = new AddressCreationOptions.Builder("address-with-options")
         .description("This is a test").address("1.1.1.1").build();
      assertEquals(addressApi().create(options),
            new ParseRegionOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/regions/us-central1/addresses",
            "{\"name\": \"address-with-options\",\"description\":\"This is a test\",\"address\":\"1.1.1.1\"}");
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      assertEquals(addressApi().delete("test-ip1"),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/regions/us-central1/addresses/test-ip1");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(addressApi().delete("test-ip1"));
      assertSent(server, "DELETE", "/projects/party/regions/us-central1/addresses/test-ip1");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/address_list.json"));

      assertEquals(addressApi().list().next(), new ParseAddressListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/us-central1/addresses");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(addressApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/regions/us-central1/addresses");
   }

   AddressApi addressApi(){
      return api().addressesInRegion("us-central1");
   }
}
