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
package org.jclouds.googlecomputeengine.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.util.Predicates2.retry;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;

public class CreateNetworkIfNeeded implements Function<NetworkAndAddressRange, Network> {
   @Resource @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   private final GoogleComputeEngineApi api;
   private final Supplier<String> userProject;
   private final Predicate<AtomicReference<Operation>> operationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject CreateNetworkIfNeeded(GoogleComputeEngineApi api, @UserProject Supplier<String> userProject,
         @Named("global") Predicate<AtomicReference<Operation>> operationDonePredicate,
         @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
         @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout) {
      this.api = checkNotNull(api, "api");
      this.userProject = checkNotNull(userProject, "userProject");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
            "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
            "operation completed check timeout");
      this.operationDonePredicate = checkNotNull(operationDonePredicate, "operationDonePredicate");
   }

   @Override
   public Network apply(NetworkAndAddressRange input) {
      checkNotNull(input, "input");

      Network nw = api.getNetworkApi(userProject.get()).get(input.name());
      if (nw != null) {
         return nw;
      }

      if (input.gateway() != null) {
         AtomicReference<Operation> operation = Atomics.newReference(api.getNetworkApi(userProject.get())
               .createInIPv4RangeWithGateway(input.name(), input.rangeIPv4(), input.gateway()));
         retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS)
               .apply(operation);

         checkState(operation.get().httpErrorStatusCode() == null,
               "Could not insert network, operation failed" + operation);
      } else {
         AtomicReference<Operation> operation = Atomics
               .newReference(api.getNetworkApi(userProject.get()).createInIPv4Range(input.name(), input.rangeIPv4()));
         retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS)
               .apply(operation);

         checkState(operation.get().httpErrorStatusCode() == null,
               "Could not insert network, operation failed" + operation);
      }
      return checkNotNull(api.getNetworkApi(userProject.get()).get(input.name()), "no network with name %s was found",
            input.name());
   }
}
