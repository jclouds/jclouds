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

import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "PlacementGroupApiMockTest", singleThreaded = true)
public class PlacementGroupApiMockTest extends BaseAWSEC2ApiMockTest {

   public void describePlacementGroupsInRegionWithFilter() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_placement_groups.xml");

      PlacementGroup result = getOnlyElement(placementApi()
            .describePlacementGroupsInRegionWithFilter(DEFAULT_REGION, ImmutableMultimap.of("strategy", "cluster")));

      assertEquals(result.getName(), "XYZ-cluster");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribePlacementGroups&Filter.1.Name=strategy&Filter.1.Value.1=cluster");
   }

   public void describePlacementGroupsInRegionWithFilter_404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      assertEquals(placementApi().describePlacementGroupsInRegionWithFilter(DEFAULT_REGION,
                  ImmutableMultimap.of("strategy", "cluster")), ImmutableSet.of());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribePlacementGroups&Filter.1.Name=strategy&Filter.1.Value.1=cluster");
   }

   public void deletePlacementGroupInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      placementApi().deletePlacementGroupInRegion(DEFAULT_REGION, "name");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeletePlacementGroup&GroupName=name");
   }

   public void deletePlacementGroupInRegion_404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      placementApi().deletePlacementGroupInRegion(DEFAULT_REGION, "name");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeletePlacementGroup&GroupName=name");
   }

   public void createPlacementGroupInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      placementApi().createPlacementGroupInRegion(DEFAULT_REGION, "name");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreatePlacementGroup&Strategy=cluster&GroupName=name");
   }

   public void createPlacementGroupInRegion_strategy() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      placementApi().createPlacementGroupInRegion(DEFAULT_REGION, "name", "cluster");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreatePlacementGroup&GroupName=name&Strategy=cluster");
   }

   private PlacementGroupApi placementApi() {
      return api().getPlacementGroupApi().get();
   }
}
