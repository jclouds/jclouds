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
package org.jclouds.ec2.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "SecurityGroupApiExpectTest")
public class SecurityGroupApiExpectTest extends BaseEC2ApiExpectTest<EC2Api> {

   HttpRequest filter = HttpRequest.builder().method("POST")
           .endpoint("https://ec2.us-east-1.amazonaws.com/")
           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
           .addFormParam("Action", "DescribeSecurityGroups")
           .addFormParam("Filter.1.Name", "owner-id")
           .addFormParam("Filter.1.Value.1", "993194456877")
           .addFormParam("Signature", "zk8EEWkG9Hi0bBLPueF9WdTUKapxQqUXgyJTxeZHXBc%3D")
           .addFormParam("SignatureMethod", "HmacSHA256")
           .addFormParam("SignatureVersion", "2")
           .addFormParam("Timestamp", "2012-04-16T15%3A54%3A08.897Z")
           .addFormParam("Version", "2010-08-31")
           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testFilterWhenResponseIs2xx() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResourceWithContentType("/describe_securitygroups_extension_single.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filter, filterResponse);

      SecurityGroup group = getOnlyElement(apiWhenExist.getSecurityGroupApi().get().describeSecurityGroupsInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("owner-id", "993194456877")
                      .build()));

      assertEquals(group.getId(), "sg-3c6ef654");
   }

   public void testFilterWhenResponseIs404() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenNotExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filter, filterResponse);

      assertEquals(apiWhenNotExist.getSecurityGroupApi().get().describeSecurityGroupsInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("owner-id", "993194456877")
                      .build()),
              ImmutableSet.of());
   }
}
