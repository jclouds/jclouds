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
package org.jclouds.softlayer.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.softlayer.parse.GetDatacenterResponseTest;
import org.jclouds.softlayer.parse.ListDatacentersResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests annotation parsing of {@code DatacenterAsyncClient}
 */
@Test(groups = "unit")
public class DatacenterApiExpectTest extends BaseSoftLayerApiExpectTest {

   public void testListDatacentersWhenResponseIs2xx() {

      HttpRequest listDatacentersRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Location_Datacenter/Datacenters?objectMask=locationAddress%3Bregions")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listDatacentersResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/datacenter_list.json")).build();

      DatacenterApi api = requestSendsResponse(listDatacentersRequest, listDatacentersResponse).getDatacenterApi();

      assertEquals(api.listDatacenters(),
              new ListDatacentersResponseTest().expected());
   }

   public void testListDatacenterWhenResponseIs4xx() {

      HttpRequest listDatacentersRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Location_Datacenter/Datacenters?objectMask=locationAddress%3Bregions")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listDatacentersResponse = HttpResponse.builder().statusCode(404).build();

      DatacenterApi api = requestSendsResponse(listDatacentersRequest, listDatacentersResponse).getDatacenterApi();

      assertTrue(Iterables.isEmpty(api.listDatacenters()));
   }

   public void testGetDatacenterWhenResponseIs2xx() {

      HttpRequest getDatacenterRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Location_Datacenter/265592?objectMask=locationAddress%3Bregions")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getDatacenterResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/datacenter_get.json")).build();

      DatacenterApi api = requestSendsResponse(getDatacenterRequest, getDatacenterResponse).getDatacenterApi();

      assertEquals(api.getDatacenter(265592),
              new GetDatacenterResponseTest().expected());
   }

   public void testGetDatacenterWhenResponseIs4xx() {

      HttpRequest getDatacenterRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Location_Datacenter/265592?objectMask=locationAddress%3Bregions")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getDatacenterResponse = HttpResponse.builder().statusCode(404).build();

      DatacenterApi api = requestSendsResponse(getDatacenterRequest, getDatacenterResponse).getDatacenterApi();

      assertNull(api.getDatacenter(265592));
   }
}
