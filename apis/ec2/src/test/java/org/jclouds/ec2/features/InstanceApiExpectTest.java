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
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.jclouds.util.Strings2;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @see InstanceApi
 */
@Test(groups = "unit")
public class InstanceApiExpectTest extends BaseEC2ApiExpectTest<EC2Api> {

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
                           "Action=DescribeInstances" +
                                   "&Filter.1.Name=key-name" +
                                   "&Filter.1.Value.1=" + Strings2.urlEncode("adriancole.ec21") +
                                   "&Signature=%2B2ktAljlAPNUMAJUFh3poQrTvwcwWytuQFBg/ktKdTc%3D" +
                                   "&SignatureMethod=HmacSHA256" +
                                   "&SignatureVersion=2" +
                                   "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                   "&Version=2010-08-31" +
                                   "&AWSAccessKeyId=identity",
                           "application/x-www-form-urlencoded"))
                   .build();

   public void testFilterWhenResponseIs2xx() throws Exception {

      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResourceWithContentType("/describe_instances_running.xml", "text/xml")).build();


      EC2Api apiWhenExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse,
              filter, filterResponse);

      RunningInstance instance = getOnlyElement(getOnlyElement(apiWhenExist.getInstanceApi().get().describeInstancesInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("key-name", "adriancole.ec21")
                      .build())));
      assertNotNull(instance, "Instance should not be null");

      Assert.assertEquals(instance.getId(), "i-0799056f");
   }

   public void testFilterWhenResponseIs404() throws Exception {

      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse,
              filter, filterResponse);

      Assert.assertEquals(apiWhenDontExist.getInstanceApi().get().describeInstancesInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("key-name", "adriancole.ec21")
                      .build()), ImmutableSet.of());
   }

}
