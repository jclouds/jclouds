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
package org.jclouds.digitalocean2.compute;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.digitalocean2.config.DigitalOcean2RateLimitModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "DigitalOcean2ComputeServiceLiveTest")
public class DigitalOcean2ComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public DigitalOcean2ComputeServiceLiveTest() {
      provider = "digitalocean2";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> builder().addAll(super.setupModules()).add(new DigitalOcean2RateLimitModule())
            .build();
   }

   @Override
   public void testOptionToNotBlock() throws Exception {
      // DigitalOcean ComputeService implementation has to block until the node
      // is provisioned, to be able to return it.
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      // We encode the tags in the user data but the DigitalOcean API does not return it
   }

   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      // The DigitalOcean API does not return the user data
   }

}
