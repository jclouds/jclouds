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
package org.jclouds.profitbricks;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_PREDICATE_SNAPSHOT;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.features.DataCenterApi;
import org.jclouds.profitbricks.features.ServerApi;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.features.NicApi;

public abstract class BaseProfitBricksLiveTest extends BaseApiLiveTest<ProfitBricksApi> {

   public static final Location testLocation = Location.US_LAS;

   private Predicate<String> dataCenterAvailable;
   private Predicate<String> snapshotAvailable;
   private Predicate<String> serverRunning;
   private Predicate<String> serverSuspended;

   public BaseProfitBricksLiveTest() {
      provider = "profitbricks";
   }

   @Override
   protected ProfitBricksApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      dataCenterAvailable = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(POLL_PREDICATE_DATACENTER)));
      snapshotAvailable = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(POLL_PREDICATE_SNAPSHOT)));
      serverRunning = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(TIMEOUT_NODE_RUNNING)));
      serverSuspended = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(TIMEOUT_NODE_SUSPENDED)));

      return injector.getInstance(ProfitBricksApi.class);
   }

   protected void assertDataCenterAvailable(DataCenter dataCenter) {
      assertDataCenterAvailable(dataCenter.id());
   }

   protected void assertDataCenterAvailable(String dataCenterId) {
      assertTrue(dataCenterAvailable.apply(dataCenterId),
              String.format("Datacenter %s wasn't available in the configured timeout", dataCenterId));
   }
   
   protected void assertSnapshotAvailable(String snapshotId){
      assertTrue(snapshotAvailable.apply(snapshotId),
              String.format("Snapshot %s wasn't available in the configured timeout", snapshotId));
   }

   protected void assertNodeRunning(String serverId) {
      assertTrue(serverRunning.apply(serverId), String.format("Server %s did not start in the configured timeout", serverId));
   }

   protected void assertNodeSuspended(String serverId) {
      assertTrue(serverSuspended.apply(serverId), String.format("Server %s did not stop in the configured timeout", serverId));
   }

   protected DataCenter findOrCreateDataCenter(final String name) {
      DataCenterApi dataCenterApi = api.dataCenterApi();

      return FluentIterable.from(dataCenterApi.getAllDataCenters()).firstMatch(new Predicate<DataCenter>() {

         @Override
         public boolean apply(DataCenter input) {
            boolean match = Objects.equals(input.name(), name);
            if (match && input.location() == testLocation)
               assertDataCenterAvailable(input);

            return match;
         }
      }).or(new Supplier<DataCenter>() {

         @Override
         public DataCenter get() {
            DataCenter dataCenter = api.dataCenterApi().createDataCenter(
                    DataCenter.Request.creatingPayload(name, testLocation));
            assertDataCenterAvailable(dataCenter);

            return api.dataCenterApi().getDataCenter(dataCenter.id());
         }
      });
   }

   protected Server findOrCreateServer(final DataCenter dataCenter) {
      return FluentIterable.from(dataCenter.servers()).firstMatch(new Predicate<Server>() {

         @Override
         public boolean apply(Server input) {
            return input.state() == ProvisioningState.AVAILABLE;
         }
      }).or(new Supplier<Server>() {

         @Override
         public Server get() {
            ServerApi serverApi = api.serverApi();
            String name = String.format("server-%d", dataCenter.servers().size());
            String createdServerId = serverApi.createServer(
                    Server.Request.creatingBuilder()
                    .dataCenterId(dataCenter.id())
                    .name(name)
                    .cores(1)
                    .ram(256)
                    .build());
            assertDataCenterAvailable(dataCenter);
            assertNodeRunning(createdServerId);

            return serverApi.getServer(createdServerId);
         }
      });
   }

   protected Nic findOrCreateNic(final DataCenter dataCenter) {
      final NicApi nicApi = api.nicApi();
      final List<Nic> nics = nicApi.getAllNics();

      return FluentIterable.from(nics).firstMatch(new Predicate<Nic>() {

         @Override
         public boolean apply(Nic input) {
            return Objects.equals(input.dataCenterId(), dataCenter.id())
                    && input.state() == ProvisioningState.AVAILABLE;
         }
      }).or(new Supplier<Nic>() {

         @Override
         public Nic get() {
            Server server = findOrCreateServer(dataCenter);
            String name = String.format("%s-nic-%d", server.name(), nics.size());
            String nicId = nicApi.createNic(Nic.Request.creatingBuilder()
                    .name(name)
                    .lanId(1)
                    .serverId(server.id())
                    .build());
            assertDataCenterAvailable(dataCenter);

            return nicApi.getNic(nicId);
         }
      });
   }

   protected void destroyDataCenter(final DataCenter dataCenter) {
      boolean success = api.dataCenterApi().deleteDataCenter(dataCenter.id());
      assertTrue(success, "DataCenter wasn't deleted");
   }
}
