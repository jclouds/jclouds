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
package org.jclouds.aws.ec2.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.compute.internal.BaseAWSEC2ComputeServiceExpectTest;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "PlacementGroupApiExpectTest")
public class PlacementGroupApiExpectTest extends BaseAWSEC2ComputeServiceExpectTest {

   HttpRequest filter = HttpRequest.builder().method("POST")
           .endpoint("https://ec2.us-east-1.amazonaws.com/")
           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
           .addFormParam("Action", "DescribePlacementGroups")
           .addFormParam("Filter.1.Name", "strategy")
           .addFormParam("Filter.1.Value.1", "cluster")
           .addFormParam("Signature", "SaA7Un1BE3m9jIEKyjXNdQPzFh/QAJSCebvKXiwUEK0%3D")
           .addFormParam("SignatureMethod", "HmacSHA256")
           .addFormParam("SignatureVersion", "2")
           .addFormParam("Timestamp", "2012-04-16T15%3A54%3A08.897Z")
           .addFormParam("Version", "2012-06-01")
           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testFilterWhenResponseIs2xx() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResourceWithContentType("/describe_placement_groups.xml", "text/xml")).build();

      AWSEC2Api apiWhenExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filter, filterResponse)
              .getContext().unwrapApi(AWSEC2Api.class);

      PlacementGroup group = getOnlyElement(apiWhenExist.getPlacementGroupApi().get().describePlacementGroupsInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("strategy", "cluster")
                      .build()));

      assertEquals(group.getName(), "XYZ-cluster");
   }

   public void testFilterWhenResponseIs404() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      AWSEC2Api apiWhenNotExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filter, filterResponse)
              .getContext().unwrapApi(AWSEC2Api.class);

      assertEquals(apiWhenNotExist.getPlacementGroupApi().get().describePlacementGroupsInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("strategy", "cluster")
                      .build()),
              ImmutableSet.of());
   }
}
