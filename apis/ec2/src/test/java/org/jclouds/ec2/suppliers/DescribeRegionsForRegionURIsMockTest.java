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

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.ec2.internal.BaseEC2ApiMockTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

@Test(groups = "unit", testName = "DescribeRegionsForRegionURIsMockTest", singleThreaded = true)
public class DescribeRegionsForRegionURIsMockTest extends BaseEC2ApiMockTest {

   public void buildsUrlsForEachRegion() throws Exception {
      enqueueRegions("us-east-1", "eu-central-1");

      Map<String, Supplier<URI>> result = supplier(new Properties()).get();

      assertEquals(result.size(), 2);
      assertNotNull(result.get("us-east-1").get());
      assertNotNull(result.get("eu-central-1").get());

      assertPosted("us-east-1", "Action=DescribeRegions");
   }

   public void honorsRegionWhitelist() throws Exception {
      enqueueRegions("us-east-1", "eu-central-1");

      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_REGIONS, "us-east-1");

      Map<String, Supplier<URI>> result = supplier(overrides).get();

      assertEquals(result.size(), 1);
      assertNotNull(result.get("us-east-1").get());

      assertPosted("us-east-1", "Action=DescribeRegions");
   }

   private DescribeRegionsForRegionURIs supplier(Properties overrides) {
      return builder(overrides).buildInjector().getInstance(DescribeRegionsForRegionURIs.class);
   }
}
