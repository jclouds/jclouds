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
package org.jclouds.profitbricks.compute;

import static org.jclouds.profitbricks.BaseProfitBricksLiveTest.testLocation;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.features.DataCenterApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ProfitBricksTemplateBuilderLiveTest", singleThreaded = true)
public class ProfitBricksTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   private static final String TEST_DC_NAME = "templateBuilderLiveTest-" + System.currentTimeMillis();

   private DataCenter dataCenter;

   public ProfitBricksTemplateBuilderLiveTest() {
      this.provider = "profitbricks";
   }

   @BeforeClass
   @Override
   public void setupContext() {
      super.setupContext();

      final DataCenterApi api = getDataCenterApi();
      final Predicate<String> predicate = getDataCenterPredicate();
      dataCenter = FluentIterable.from(api.getAllDataCenters()).firstMatch(new Predicate<DataCenter>() {

         @Override
         public boolean apply(DataCenter input) {
            boolean match = Objects.equals(input.name(), TEST_DC_NAME);
            if (match && input.location() == testLocation)
               return predicate.apply(input.id());
            return match;
         }
      }).or(new Supplier<DataCenter>() {

         @Override
         public DataCenter get() {
            DataCenter dataCenter = api.createDataCenter(
                    DataCenter.Request.creatingPayload(TEST_DC_NAME, testLocation));
            predicate.apply(dataCenter.id());

            return api.getDataCenter(dataCenter.id());
         }
      });
   }

   private Predicate<String> getDataCenterPredicate() {
      return view.utils().injector().getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(POLL_PREDICATE_DATACENTER)));
   }

   private DataCenterApi getDataCenterApi() {
      return view.unwrapApi(ProfitBricksApi.class).dataCenterApi();
   }

   @AfterClass(groups = {"integration", "live"}, alwaysRun = true)
   @Override
   protected void tearDownContext() {
      super.tearDownContext();
      if (dataCenter != null)
         getDataCenterApi().deleteDataCenter(dataCenter.id());
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.of();
   }

}
