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
package org.jclouds.ec2.suppliers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.jclouds.ec2.internal.BaseEC2ApiMockTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "DescribeAvailabilityZonesInRegionMockTest", singleThreaded = true)
public class DescribeAvailabilityZonesInRegionMockTest extends BaseEC2ApiMockTest {

   public void onlySendsRequestsToConfiguredRegions() throws Exception {
      enqueueRegions("us-east-1");
      enqueueXml("us-east-1", "/availabilityZones.xml");

      Map<String, Supplier<Set<String>>> result = new DescribeAvailabilityZonesInRegion(api(),
            supplyRegionIds("us-east-1")).get();

      assertEquals(result.size(), 1);
      assertEquals(result.get("us-east-1").get(),
            ImmutableSet.of("us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d"));

      assertPosted("us-east-1", "Action=DescribeRegions");
      assertPosted("us-east-1", "Action=DescribeAvailabilityZones");
   }

   public void failsOnAuthorizationErrorToAnyRegion() throws Exception {
      enqueueRegions("us-east-1", "eu-central-1");
      enqueueXml("us-east-1", "/availabilityZones.xml");
      enqueue("eu-central-1", new MockResponse().setResponseCode(401));

      DescribeAvailabilityZonesInRegion supplier = new DescribeAvailabilityZonesInRegion(api(),
            supplyRegionIds("us-east-1", "eu-central-1"));

      try {
         supplier.get();
         fail();
      } catch (AuthorizationException e){

      }

      assertPosted("us-east-1", "Action=DescribeRegions");
      assertPosted("us-east-1", "Action=DescribeAvailabilityZones");
      assertPosted("eu-central-1", "Action=DescribeAvailabilityZones");
   }

   private static Supplier<Set<String>> supplyRegionIds(String... regionIds) {
      return Suppliers.<Set<String>>ofInstance(ImmutableSet.copyOf(regionIds));
   }
}
