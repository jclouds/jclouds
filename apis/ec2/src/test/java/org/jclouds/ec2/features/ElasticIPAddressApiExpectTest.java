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
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @see org.jclouds.ec2.features.ElasticIPAddressApi
 */
@Test(groups = "unit")
public class ElasticIPAddressApiExpectTest extends BaseEC2ApiExpectTest<EC2Api> {

   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(Constants.PROPERTY_API_VERSION, "2010-08-31");
      return props;
   }

   HttpRequest filter =
           HttpRequest.builder()
                   .method("POST")
                   .endpoint("https://ec2.us-east-1.amazonaws.com/")
                   .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                   .payload(BaseRestApiExpectTest.payloadFromStringWithContentType(
                           "Action=DescribeAddresses" +
                                   "&Filter.1.Name=instance-id" +
                                   "&Filter.1.Value.1=i-f15ebb98" +
                                   "&Signature=dJbTUsBGHSrarQQAwmLm8LLI255R/lzdE7ZcYJucOzI%3D" +
                                   "&SignatureMethod=HmacSHA256" +
                                   "&SignatureVersion=2" +
                                   "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                   "&Version=2010-08-31" +
                                   "&AWSAccessKeyId=identity",
                           "application/x-www-form-urlencoded"))
                   .build();

   public void testFilterWhenResponseIs2xx() throws Exception {

      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResourceWithContentType("/describe_addresses_single.xml", "text/xml")).build();


      EC2Api apiWhenExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse,
              filter, filterResponse);

      PublicIpInstanceIdPair address = getOnlyElement(apiWhenExist.getElasticIPAddressApi()
              .get().describeAddressesInRegionWithFilter("us-east-1",
                      ImmutableMultimap.<String, String>builder()
                              .put("instance-id", "i-f15ebb98")
                              .build()));
      assertNotNull(address, "address should not be null");

      assertEquals(address.getPublicIp(), "67.202.55.255");
   }

   public void testFilterWhenResponseIs404() throws Exception {

      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse,
              filter, filterResponse);

      assertEquals(apiWhenDontExist.getElasticIPAddressApi()
              .get().describeAddressesInRegionWithFilter("us-east-1",
                      ImmutableMultimap.<String, String>builder()
                              .put("instance-id", "i-f15ebb98")
                              .build()), ImmutableSet.of());
   }

}
