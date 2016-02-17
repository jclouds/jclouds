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

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true, testName = "ProfitBricksComputeServiceLiveTest")
public class ProfitBricksComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public ProfitBricksComputeServiceLiveTest() {
      provider = "profitbricks";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   public void testOptionToNotBlock() throws Exception {
      // ProfitBricks implementation intentionally blocks until the node is 'AVAILABLE'
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      // ProfitBricks doesn't support tags
   }

   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      // ProfitBricks doesn't support user metadata
   }

   @Override
   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      // ProfitBricks doesn't support hostname
   }

   @Override
   protected void checkOsMatchesTemplate(NodeMetadata node) {
      // Not enough description from API to match template
   }

}
