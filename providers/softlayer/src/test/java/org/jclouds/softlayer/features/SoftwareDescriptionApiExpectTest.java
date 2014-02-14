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
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.softlayer.parse.GetAllObjectsResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests annotation parsing of {@code DatacenterAsyncClient}
 */
@Test(groups = "unit")
public class SoftwareDescriptionApiExpectTest extends BaseSoftLayerApiExpectTest {

   public void testGetAllObjectsWhenResponseIs2xx() {

      HttpRequest getAllObjectsRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Software_Description/getAllObjects?objectMask=id%3Bname%3Bversion%3BoperatingSystem%3BlongDescription%3BreferenceCode")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();
      HttpResponse getAllObjectsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/software_description_list.json")).build();
      SoftwareDescriptionApi api = requestSendsResponse(getAllObjectsRequest, getAllObjectsResponse).getSoftwareDescriptionApi();
      assertEquals(api.getAllObjects(), new GetAllObjectsResponseTest().expected());
   }

   public void testGetAllObjectsWhenResponseIs4xx() {

      HttpRequest getAllObjectsRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Software_Description/getAllObjects?objectMask=id%3Bname%3Bversion%3BoperatingSystem%3BlongDescription%3BreferenceCode")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();
      HttpResponse getAllObjectsResponse = HttpResponse.builder().statusCode(404).build();
      SoftwareDescriptionApi api = requestSendsResponse(getAllObjectsRequest, getAllObjectsResponse).getSoftwareDescriptionApi();
      assertTrue(Iterables.isEmpty(api.getAllObjects()));
   }

}
