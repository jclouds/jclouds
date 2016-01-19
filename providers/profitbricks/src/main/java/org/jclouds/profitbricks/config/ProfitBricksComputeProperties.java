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
package org.jclouds.profitbricks.config;

public class ProfitBricksComputeProperties {

   public static final String POLL_PREDICATE_DATACENTER     = "jclouds.profitbricks.predicate.datacenter";
   public static final String POLL_PREDICATE_SNAPSHOT       = "jclouds.profitbricks.predicate.snapshot";

   public static final String TIMEOUT_DATACENTER_AVAILABLE  = "jclouds.profitbricks.timeout.datacenter-available";
   public static final String POLL_INITIAL_PERIOD           = "jclouds.profitbricks.poll-status.initial-period";
   public static final String POLL_MAX_PERIOD               = "jclouds.profitbricks.poll-status.poll.max-period";

   private ProfitBricksComputeProperties() {
      throw new AssertionError("Intentionally unimplemented");
   }

}
