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
package org.jclouds.googlecomputeengine.functions;

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
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.internal.NetworkAndAddressRange;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;

@Singleton
public class CreateNetworkIfNeeded implements Function<NetworkAndAddressRange, Network> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final GoogleComputeEngineApi api;
   protected final Supplier<String> userProject;
   private final Predicate<AtomicReference<Operation>> operationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject
   public CreateNetworkIfNeeded(GoogleComputeEngineApi api,
                                @UserProject Supplier<String> userProject,
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

      Network nw = api.getNetworkApiForProject(userProject.get()).get(input.getName());
      if (nw != null) {
         return nw;
      }

      if (input.getGateway().isPresent()) {
         AtomicReference<Operation> operation = Atomics.newReference(api.getNetworkApiForProject(userProject
                 .get()).createInIPv4RangeWithGateway(input.getName(), input.getIpV4Range(), input.getGateway().get()));
         retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
                 MILLISECONDS).apply(operation);

         checkState(!operation.get().getHttpError().isPresent(), "Could not create network, operation failed" + operation);
      } else {
         AtomicReference<Operation> operation = Atomics.newReference(api.getNetworkApiForProject(userProject
                 .get()).createInIPv4Range(input.getName(), input.getIpV4Range()));
         retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
                 MILLISECONDS).apply(operation);

         checkState(!operation.get().getHttpError().isPresent(), "Could not create network, operation failed" + operation);
      }
      return checkNotNull(api.getNetworkApiForProject(userProject.get()).get(input.getName()),
                 "no network with name %s was found", input.getName());
   }
}
