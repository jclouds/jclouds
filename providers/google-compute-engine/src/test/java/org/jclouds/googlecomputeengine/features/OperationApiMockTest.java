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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationListTest;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneOperationTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "OperationApiMockTest", singleThreaded = true)
public class OperationApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      assertEquals(operationApi().get(URI.create(url("/projects/party/global/operations/operation-1354084865060"))),
            new ParseGlobalOperationTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/operations/operation-1354084865060");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(operationApi().get(URI.create(url("/projects/party/global/operations/operation-1354084865060"))));
      assertSent(server, "GET", "/projects/party/global/operations/operation-1354084865060");
   }

   public void delete() throws Exception {
      server.enqueue(new MockResponse().setBody(""));

      operationApi().delete(URI.create(url("/projects/party/global/operations/operation-1352178598164-4cdcc9d031510-4aa46279")));
      assertSent(server, "DELETE", "/projects/party/global/operations/operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      operationApi().delete(URI.create(url("/projects/party/global/operations/operation-1352178598164-4cdcc9d031510-4aa46279")));
      assertSent(server, "DELETE", "/projects/party/global/operations/operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/global_operation_list.json"));

      assertEquals(operationApi().list().next(), new ParseGlobalOperationListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/operations");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(operationApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/operations");
   }

   public void listPage_options() throws Exception {
      server.enqueue(jsonResponse("/global_operation_list.json"));

      assertEquals(operationApi().listPage("CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcGVyYXRpb24tMTM1Mj" +
            "I0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz",
            filter("status eq done").maxResults(3)), new ParseGlobalOperationListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/operations?pageToken=CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcG" +
                      "VyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz&" +
                      "filter=status%20eq%20done&maxResults=3");
   }

   private ListPage<Operation> regionList() {
      return ForwardingListPage.create( //
            ImmutableList.of(new ParseRegionOperationTest().expected(url("/projects"))), // items
            null // nextPageToken
      );
   }

   public void listInRegion() throws Exception {
      server.enqueue(jsonResponse("/region_operation_list.json"));

      assertEquals(operationApi().listInRegion("us-central1").next(), regionList());
      assertSent(server, "GET", "/projects/party/regions/us-central1/operations");
   }

   public void listInRegion_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(operationApi().listInRegion("us-central1").hasNext());
      assertSent(server, "GET", "/projects/party/regions/us-central1/operations");
   }

   public void listPageInRegion() throws Exception {
      server.enqueue(jsonResponse("/region_operation_list.json"));

      assertEquals(operationApi().listPageInRegion("us-central1", "CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcGVyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz",
            filter("status eq done").maxResults(3)).toString(), regionList().toString());
      assertSent(server, "GET", "/projects/party/regions/us-central1/operations?pageToken=CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcG" +
                  "VyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz&" +
                  "filter=" +
                  "status%20eq%20done&" +
                  "maxResults=3");
   }

   private ListPage<Operation> zoneList() {
      return ForwardingListPage.create( //
            ImmutableList.of(new ParseZoneOperationTest().expected(url("/projects"))), // items
            null // nextPageToken
      );
   }

   public void listInZone() throws Exception {
      server.enqueue(jsonResponse("/zone_operation_list.json"));

      assertEquals(operationApi().listInZone("us-central1-a").next(), zoneList());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations");
   }

   public void listInZone_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(operationApi().listInZone("us-central1-a").hasNext());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations");
   }

   public void listPageInZone() throws Exception {
      server.enqueue(jsonResponse("/zone_operation_list.json"));

      assertEquals(operationApi().listPageInZone("us-central1-a",
            "CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcGVyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz",
            filter("status eq done").maxResults(3)).toString(), zoneList().toString());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations?pageToken=CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcG" +
                  "VyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz&" +
                  "filter=" +
                  "status%20eq%20done&" +
                  "maxResults=3");
   }

   OperationApi operationApi(){
      return api().operations();
   }
}
