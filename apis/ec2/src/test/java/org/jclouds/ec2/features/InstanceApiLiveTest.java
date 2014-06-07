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

import static com.google.common.collect.Iterables.getFirst;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code EC2Api}
 */
@Test(groups = "live", singleThreaded = true, testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseComputeServiceContextLiveTest {
   public InstanceApiLiveTest() {
      provider = "ec2";
   }

   private EC2Api ec2Api;
   private InstanceApi client;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      ec2Api = view.unwrapApi(EC2Api.class);
      client = ec2Api.getInstanceApi().get();
   }

   @Test
   void testDescribeInstances() {
      for (String region : ec2Api.getConfiguredRegions()) {
         Set<? extends Reservation<? extends RunningInstance>> allResults = client.describeInstancesInRegion(region);
         assertNotNull(allResults);
      }
   }

   @Test
   void testFilterInstances() {
      for (String region : view.unwrapApi(EC2Api.class).getAvailabilityZoneAndRegionApi().get().describeRegions().keySet()) {
         Set<? extends Reservation<? extends RunningInstance>> allResults = client.describeInstancesInRegion(region);
         assertNotNull(allResults);

         if (!allResults.isEmpty())  {
            RunningInstance instance = getFirst(getFirst(allResults, null), null);

            assertNotNull(instance);

            Set<? extends Reservation<? extends RunningInstance>> filterResults = client.describeInstancesInRegionWithFilter(region,
                    ImmutableMultimap.<String, String>builder()
                            .put("key-name", instance.getKeyName())
                            .build());

            assertNotNull(filterResults);
            assertTrue(!filterResults.isEmpty(), "No results found for filter, but there should be.");

         }
      }
   }

   @Test(expectedExceptions = AWSResponseException.class)
   void testInvalidFilterInstances() {
      for (String region : view.unwrapApi(EC2Api.class).getAvailabilityZoneAndRegionApi().get().describeRegions().keySet()) {
         Set<? extends Reservation<? extends RunningInstance>> filterResults = client.describeInstancesInRegionWithFilter(region,
                 ImmutableMultimap.<String, String>builder()
                         .put("invalid-key", "some-value")
                         .build());

      }
   }}
