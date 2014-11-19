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
import static org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions.Builder.from;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import org.jclouds.aws.ec2.domain.Spot;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;

   @Test(groups = "unit", testName = "SpotInstanceApiMockTest", singleThreaded = true)
   public class SpotInstanceApiMockTest extends BaseAWSEC2ApiMockTest {

      public void describeSpotInstanceRequestsInRegionWithFilter() throws Exception {
         enqueueRegions(DEFAULT_REGION);
         enqueueXml(DEFAULT_REGION, "/describe_spot_instance.xml");

         SpotInstanceRequest result = getOnlyElement(spotApi()
               .describeSpotInstanceRequestsInRegionWithFilter(DEFAULT_REGION,
                     ImmutableMultimap.of("instance-id", "i-ef308e8e")));

         assertEquals(result.getId(), "sir-1ede0012");

         assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
         assertPosted(DEFAULT_REGION, "Action=DescribeSpotInstanceRequests&Filter.1.Name=instance-id&Filter.1.Value.1=i-ef308e8e");
      }

      public void describeSpotInstanceRequestsInRegionWithFilter_404() throws Exception {
         enqueueRegions(DEFAULT_REGION);
         enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

         assertEquals(spotApi().describeSpotInstanceRequestsInRegionWithFilter(DEFAULT_REGION,
               ImmutableMultimap.of("instance-id", "i-ef308e8e")), ImmutableSet.of());

         assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
         assertPosted(DEFAULT_REGION, "Action=DescribeSpotInstanceRequests&Filter.1.Name=instance-id&Filter.1.Value.1=i-ef308e8e");
      }

      public void cancelSpotInstanceRequestsInRegion() throws Exception {
         enqueueRegions(DEFAULT_REGION);
         enqueue(DEFAULT_REGION, new MockResponse());

         spotApi().cancelSpotInstanceRequestsInRegion(DEFAULT_REGION, "sir-f4d44212");

         assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
         assertPosted(DEFAULT_REGION, "Action=CancelSpotInstanceRequests&SpotInstanceRequestId.1=sir-f4d44212");
      }

      public void cancelSpotInstanceRequestsInRegion_404() throws Exception {
         enqueueRegions(DEFAULT_REGION);
         enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

         spotApi().cancelSpotInstanceRequestsInRegion(DEFAULT_REGION, "sir-f4d44212");

         assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
         assertPosted(DEFAULT_REGION, "Action=CancelSpotInstanceRequests&SpotInstanceRequestId.1=sir-f4d44212");
      }

      public void describeSpotPriceHistoryInRegion() throws Exception {
         enqueueRegions(DEFAULT_REGION);
         enqueueXml(DEFAULT_REGION, "/describe_spot_price_history.xml");

         Set<Spot> result = spotApi().describeSpotPriceHistoryInRegion(DEFAULT_REGION);

         assertEquals(result.size(), 3);

         assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
         assertPosted(DEFAULT_REGION, "Action=DescribeSpotPriceHistory");
      }

      public void describeSpotPriceHistoryInRegion_404() throws Exception {
         enqueueRegions(DEFAULT_REGION);
         enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

         Set<Spot> result = spotApi().describeSpotPriceHistoryInRegion(DEFAULT_REGION);

         assertTrue(result.isEmpty());

         assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
         assertPosted(DEFAULT_REGION, "Action=DescribeSpotPriceHistory");
      }

      public void describeSpotPriceHistoryInRegionOptions() throws Exception {
         enqueueRegions(DEFAULT_REGION);
         enqueueXml(DEFAULT_REGION, "/describe_spot_price_history.xml");

         Date from = new Date(12345678910l);
         Date to = new Date(1234567891011l);

         Set<Spot> result = spotApi().describeSpotPriceHistoryInRegion(DEFAULT_REGION,
               from(from).to(to).productDescription("description").instanceType("m1.small"));

         assertEquals(result.size(), 3);

         assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
         assertPosted(DEFAULT_REGION, "Action=DescribeSpotPriceHistory&StartTime=1970-05-23T21%3A21%3A18.910Z&EndTime=2009-02-13T23%3A31%3A31.011Z&ProductDescription=description&InstanceType.1=m1.small");
      }

      private SpotInstanceApi spotApi() {
         return api().getSpotInstanceApi().get();
      }
}
