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
package org.jclouds.profitbricks.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.ProvisioningState;

import com.google.common.base.Predicate;

/**
 * A custom predicate for waiting until a virtual resource satisfies the given expected status
 * <p>
 * Performing api requests on a datacenter that is not {@link ProvisioningState#AVAILABLE} is not allowed. On some
 * cases, the API user gets blocked from further requests, and will then need to contact tech support for api lock
 * release.
 */
public class ProvisioningStatusPollingPredicate implements Predicate<String> {

   private final ProfitBricksApi api;
   private final ProvisioningStatusAware domain;
   private final ProvisioningState expect;

   public ProvisioningStatusPollingPredicate(ProfitBricksApi api, ProvisioningStatusAware domain, ProvisioningState expect) {
      this.api = checkNotNull(api, "API null");
      this.expect = checkNotNull(expect, "Expected state null");
      this.domain = checkNotNull(domain, "Domain null");
   }

   @Override
   public boolean apply(String input) {
      checkNotNull(input, "Virtual item id can't be null.");
      switch (domain) {
         case DATACENTER:
            return expect == api.dataCenterApi().getDataCenterState(input);
         case SERVER:
            return expect == api.serverApi().getServer( input ).state();
         default:
            throw new IllegalArgumentException("Unknown domain '" + domain + "'");
      }
   }

}
