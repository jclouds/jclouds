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
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;

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

   @Override
   @Test(dataProvider = "onlyIfAutomaticHardwareSupported", groups = {"integration", "live"})
   public void testCreateNodeWithCustomHardware() throws Exception {
      Template template = buildTemplate(templateBuilder()
              .hardwareId("automatic:cores=2;ram=2048;disk=10"));
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group + "custom", 1, template));
         assertThat(node.getHardware().getRam()).isEqualTo(2048);
         assertThat(node.getHardware().getProcessors().get(0).getCores()).isEqualTo(2);
         assertThat(node.getHardware().getVolumes().get(0).getSize()).isEqualTo(10);
         assertThat(node.getHardware().getId()).isEqualTo("automatic:cores=2;ram=2048;disk=10");
      }
      finally {
         client.destroyNodesMatching(inGroup(group + "custom"));
      }
   }

   @Test(dataProvider = "onlyIfAutomaticHardwareSupported", groups = {"integration", "live"})
   public void testCreateNodeWithCustomHardwareUsingMins() throws Exception {
      Template template = buildTemplate(templateBuilder()
           .minCores(2).minRam(2048).minDisk(10));
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group + "custom", 1, template));
         assertThat(node.getHardware().getRam()).isEqualTo(2048);
         assertThat(node.getHardware().getProcessors().get(0).getCores()).isEqualTo(2);
         assertThat(node.getHardware().getVolumes().get(0).getSize()).isEqualTo(10);
         assertThat(node.getHardware().getId()).isEqualTo("cpu=2,ram=2048,disk=10");
      }
      finally {
         client.destroyNodesMatching(inGroup(group + "custom"));
      }
   }

}
