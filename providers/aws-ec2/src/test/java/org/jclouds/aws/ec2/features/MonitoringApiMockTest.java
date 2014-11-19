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

import static org.testng.Assert.assertFalse;

import java.util.Map;

import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "MonitoringApiMockTest", singleThreaded = true)
public class MonitoringApiMockTest extends BaseAWSEC2ApiMockTest {

   public void monitorInstancesInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/monitoring.xml");

      Map<String, MonitoringState> result = monitoringApi()
            .monitorInstancesInRegion(DEFAULT_REGION, "i-911444f0", "i-911444f1");

      assertFalse(result.isEmpty());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=MonitorInstances&InstanceId.0=i-911444f0&InstanceId.1=i-911444f1");
   }

   public void unmonitorInstancesInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/monitoring.xml");

      Map<String, MonitoringState> result = monitoringApi()
            .unmonitorInstancesInRegion(DEFAULT_REGION, "i-911444f0", "i-911444f1");

      assertFalse(result.isEmpty());
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=UnmonitorInstances&InstanceId.0=i-911444f0&InstanceId.1=i-911444f1");
   }

   private MonitoringApi monitoringApi() {
      return api().getMonitoringApi().get();
   }
}
