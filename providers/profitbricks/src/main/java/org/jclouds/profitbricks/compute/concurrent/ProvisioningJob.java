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
package org.jclouds.profitbricks.compute.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;

import java.util.concurrent.Callable;

import javax.inject.Named;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ProvisioningJob implements Callable {

   public interface Factory {

      ProvisioningJob create(String group, Supplier<Object> operation);
   }

   private final Predicate<String> waitDataCenterUntilReady;
   private final String group;
   private final Supplier<Object> operation;

   @Inject
   ProvisioningJob(@Named(POLL_PREDICATE_DATACENTER) Predicate<String> waitDataCenterUntilReady,
           @Assisted String group, @Assisted Supplier<Object> operation) {
      this.waitDataCenterUntilReady = waitDataCenterUntilReady;
      this.group = checkNotNull(group, "group cannot be null");
      this.operation = checkNotNull(operation, "operation cannot be null");
   }

   @Override
   public Object call() throws Exception {
      waitDataCenterUntilReady.apply(group);
      Object obj = operation.get();
      waitDataCenterUntilReady.apply(group);

      return obj;
   }

   public String getGroup() {
      return group;
   }
}
