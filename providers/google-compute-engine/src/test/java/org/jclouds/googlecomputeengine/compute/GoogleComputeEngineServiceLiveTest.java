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
package org.jclouds.googlecomputeengine.compute;

import static com.google.common.collect.Iterables.contains;
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredentialFromPemFile;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

@Test(groups = "live", singleThreaded = true)
public class GoogleComputeEngineServiceLiveTest extends BaseComputeServiceLiveTest {

   protected static final String DEFAULT_ZONE_NAME = "us-central1-a";

   public GoogleComputeEngineServiceLiveTest() {
      provider = "google-compute-engine";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setCredentialFromPemFile(props, provider + ".credential");
      return props;
   }

   public void testListHardwareProfiles() throws Exception {
      GoogleComputeEngineApi api = client.getContext().unwrapApi(GoogleComputeEngineApi.class);
      Supplier<String> userProject = context.utils().injector().getInstance(Key.get(new TypeLiteral<Supplier<String>>() {
      }, UserProject.class));
      ImmutableSet.Builder<String> deprecatedMachineTypes = ImmutableSet.builder();
      for (MachineType machine : api.getMachineTypeApiForProject(userProject.get())
              .listInZone(DEFAULT_ZONE_NAME).concat()) {
         if (machine.getDeprecated().isPresent()) {
            deprecatedMachineTypes.add(machine.getId());
         }
      }
      ImmutableSet<String> deprecatedMachineTypeIds = deprecatedMachineTypes.build();
      Set<? extends Hardware> hardwareProfiles = client.listHardwareProfiles();
      System.out.println(hardwareProfiles.size());
      for (Hardware hardwareProfile : hardwareProfiles) {
         System.out.println(hardwareProfile);
         assertFalse(contains(deprecatedMachineTypeIds, hardwareProfile.getId()));
      }
   }

   /**
    * Nodes may have additional metadata entries (particularly they may have an "sshKeys" entry)
    */
   protected void checkUserMetadataInNodeEquals(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assertTrue(node.getUserMetadata().keySet().containsAll(userMetadata.keySet()));
   }

   // do not run until the auth exception problem is figured out.
   @Test(enabled = false)
   @Override
   public void testCorrectAuthException() throws Exception {
   }

   // reboot is not supported by GCE
   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
   }

   // suspend/Resume is not supported by GCE
   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodesByIds() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Test(enabled = true, dependsOnMethods = {"testListNodes", "testGetNodesWithDetails", "testListNodesByIds"})
   @Override
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
