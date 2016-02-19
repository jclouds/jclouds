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
package org.jclouds.profitbricks.compute.config;

import static org.jclouds.profitbricks.internal.BaseProfitBricksMockTest.mockWebServer;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.compute.config.ProfitBricksComputeServiceContextModule.DataCenterProvisioningStatePredicate;
import org.jclouds.profitbricks.compute.config.ProfitBricksComputeServiceContextModule.ServerStatusPredicate;
import org.jclouds.profitbricks.compute.config.ProfitBricksComputeServiceContextModule.SnapshotProvisioningStatePredicate;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Snapshot;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import org.jclouds.util.Predicates2;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;


/**
 * Test class for {@link DataCenterProvisioningStatePredicate} and {@link ServerStatusPredicate}
 */
@Test(groups = "unit", testName = "ProvisioningStatusPollingPredicateTest")
public class StatusPredicateTest extends BaseProfitBricksMockTest {

   @Test
   public void testDataCenterPredicate() throws Exception {
      MockWebServer server = mockWebServer();

      byte[] payloadInProcess = payloadFromResource("/datacenter/datacenter-state-inprocess.xml");
      byte[] payloadAvailable = payloadFromResource("/datacenter/datacenter-state.xml");

      // wait 3 times
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadAvailable));

      server.enqueue(new MockResponse().setBody(payloadAvailable));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));

      Predicate<String> waitUntilAvailable = Predicates2.retry(
              new DataCenterProvisioningStatePredicate(pbApi, ProvisioningState.AVAILABLE),
              30l, 1l, TimeUnit.SECONDS);

      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      try {
         waitUntilAvailable.apply(id);
         ProvisioningState finalState = pbApi.dataCenterApi().getDataCenterState(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertEquals(finalState, ProvisioningState.AVAILABLE);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testServerPredicate() throws Exception {
      MockWebServer server = mockWebServer();

      byte[] payloadInProcess = payloadFromResource("/server/server-state-inprocess.xml");
      byte[] payloadAvailable = payloadFromResource("/server/server.xml");

      // wait 3 times
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadAvailable));

      server.enqueue(new MockResponse().setBody(payloadAvailable));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));

      Predicate<String> waitUntilAvailable = Predicates2.retry(
              new ServerStatusPredicate(pbApi, Server.Status.RUNNING),
              30l, 1l, TimeUnit.SECONDS);

      String id = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";
      try {
         waitUntilAvailable.apply(id);
         Server remoteServer = pbApi.serverApi().getServer(id);
         assertEquals(remoteServer.status(), Server.Status.RUNNING);
         assertRequestHasCommonProperties(server.takeRequest());
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testSnapshotPredicate() throws Exception {
      MockWebServer server = mockWebServer();

      byte[] payloadInProcess = payloadFromResource("/snapshot/snapshot-state-inprocess.xml");
      byte[] payloadAvailable = payloadFromResource("/snapshot/snapshot.xml");

      // wait 3 times
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadAvailable));

      server.enqueue(new MockResponse().setBody(payloadAvailable));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));

      Predicate<String> waitUntilAvailable = Predicates2.retry(
              new SnapshotProvisioningStatePredicate(pbApi, ProvisioningState.AVAILABLE),
              30l, 1l, TimeUnit.SECONDS);

      String id = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";
      try {
         waitUntilAvailable.apply(id);
         Snapshot snapshot = pbApi.snapshotApi().getSnapshot(id);
         assertEquals(snapshot.state(), ProvisioningState.AVAILABLE);
         assertRequestHasCommonProperties(server.takeRequest());
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

}
