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

import java.util.concurrent.TimeUnit;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusAware;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusPollingPredicate;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.util.Predicates2;

import com.google.common.base.Predicate;

public abstract class BaseProfitBricksLiveTest extends BaseApiLiveTest<ProfitBricksApi> {

   protected Predicate<String> dcWaitingPredicate;

   public BaseProfitBricksLiveTest() {
      provider = "profitbricks";
   }

   @Override
   protected void initialize() {
      super.initialize();
      this.dcWaitingPredicate = Predicates2.retry(
	      new ProvisioningStatusPollingPredicate(api, ProvisioningStatusAware.DATACENTER, ProvisioningState.AVAILABLE),
	      2l * 60l, 2l, TimeUnit.SECONDS);
   }

}
