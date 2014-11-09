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

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.Atomics;

public final class CreateNetworkIfNeeded implements Function<NetworkAndAddressRange, Network> {
   private final GoogleComputeEngineApi api;
   private final Predicate<AtomicReference<Operation>> operationDone;

   @Inject CreateNetworkIfNeeded(GoogleComputeEngineApi api, Predicate<AtomicReference<Operation>> operationDone) {
      this.api = api;
      this.operationDone = operationDone;
   }

   @Override
   public Network apply(NetworkAndAddressRange input) {
      checkNotNull(input, "input");

      Network nw = api.networks().get(input.name());
      if (nw != null) {
         return nw;
      }

      if (input.gateway() != null) {
         AtomicReference<Operation> operation = Atomics.newReference(api.networks()
               .createInIPv4RangeWithGateway(input.name(), input.rangeIPv4(), input.gateway()));
         operationDone.apply(operation);

         checkState(operation.get().httpErrorStatusCode() == null,
               "Could not insert network, operation failed" + operation);
      } else {
         AtomicReference<Operation> operation = Atomics
               .newReference(api.networks().createInIPv4Range(input.name(), input.rangeIPv4()));
         operationDone.apply(operation);

         checkState(operation.get().httpErrorStatusCode() == null,
               "Could not insert network, operation failed" + operation);
      }
      return checkNotNull(api.networks().get(input.name()), "no network with name %s was found",
            input.name());
   }
}
