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
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.googlecloud.internal.TestProperties;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true)
public class GoogleComputeEngineServiceLiveTest extends BaseComputeServiceLiveTest {

   protected static final String DEFAULT_ZONE_NAME = "us-central1-a";

   public GoogleComputeEngineServiceLiveTest() {
      provider = "google-compute-engine";
   }

   @Override protected Properties setupProperties() {
      TestProperties.setGoogleCredentialsFromJson(provider);
      return TestProperties.apply(provider, super.setupProperties());
   }

   public void testListHardwareProfiles() throws Exception {
      GoogleComputeEngineApi api = client.getContext().unwrapApi(GoogleComputeEngineApi.class);
      ImmutableSet.Builder<String> deprecatedMachineTypes = ImmutableSet.builder();
      for (MachineType machine : api.machineTypesInZone(DEFAULT_ZONE_NAME).list().next()) {
         if (machine.deprecated() != null) {
            deprecatedMachineTypes.add(machine.id());
         }
      }
      ImmutableSet<String> deprecatedMachineTypeIds = deprecatedMachineTypes.build();
      for (Hardware hardwareProfile : client.listHardwareProfiles()) {
         assertFalse(contains(deprecatedMachineTypeIds, hardwareProfile.getId()));
      }
   }

   public void testCreatePreemptibleNodeWithSsd() throws Exception {
      String group = this.group + "ssd";
      try {
         TemplateOptions options = client.templateOptions();

         options.as(GoogleComputeEngineTemplateOptions.class).bootDiskType("pd-ssd").preemptible(true);

         // create a node
         Set<? extends NodeMetadata> nodes =
               client.createNodesInGroup(group, 1, options);
         assertEquals(nodes.size(), 1, "One node should have been created");

         // Verify the disk on the instance is an ssd.
         NodeMetadata node = Iterables.get(nodes, 0);
         GoogleComputeEngineApi api = client.getContext().unwrapApi(GoogleComputeEngineApi.class);
         Instance instance = api.instancesInZone(node.getLocation().getId()).get(node.getName());
         Disk disk = api.disksInZone(node.getLocation().getId()).get(toName(instance.disks().get(0).source()));
         assertTrue(disk.type().toString().endsWith("pd-ssd"));
         assertTrue(instance.scheduling().preemptible());

      } finally {
         client.destroyNodesMatching(inGroup(group));
      }
   }
   /**
    * Nodes may have additional metadata entries (particularly they may have an "sshKeys" entry)
    */
   protected void checkUserMetadataInNodeEquals(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assertTrue(node.getUserMetadata().keySet().containsAll(userMetadata.keySet()));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   @Override
   public void testCorrectAuthException() throws Exception {
      ComputeServiceContext context = null;
      try {
         String credential = toStringAndClose(getClass().getResourceAsStream("/test"));
         Properties overrides = setupProperties();
         overrides.setProperty(provider + ".identity", "000000000000@developer.gserviceaccount.com");
         overrides.setProperty(provider + ".credential", credential);
         context = newBuilder()
               .modules(ImmutableSet.of(getLoggingModule(), credentialStoreModule))
               .overrides(overrides).build(ComputeServiceContext.class);
         context.getComputeService().listNodes();
      } catch (AuthorizationException e) {
         throw e;
      } catch (RuntimeException e) {
         e.printStackTrace();
         throw e;
      } finally {
         if (context != null)
            context.close();
      }
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      Set<String> nodeTags = node.getTags();
      for (String tag : tags){
         assert nodeTags.contains(tag) : String.format("node tags did not match %s %s node %s:", tags, nodeTags, node);
      }
   }

   private static String toName(URI link) {
      String path = link.getPath();
      return path.substring(path.lastIndexOf('/') + 1);
   }

   @Override
   protected void checkVolumes(Hardware hardware) {
      // Hardware profiles might not have volumes.
   }

   @Override
   @Test(dataProvider = "onlyIfAutomaticHardwareSupported", groups = {"integration", "live"})
   public void testCreateNodeWithCustomHardware() throws Exception {
      Template template = buildTemplate(templateBuilder()
            .hardwareId("automatic:cores=2;ram=4096"));
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group + "custom", 1, template));
         assertThat(node.getHardware().getRam()).isEqualTo(4096);
         assertThat(node.getHardware().getProcessors().get(0).getCores()).isEqualTo(2);
         assertThat(node.getHardware().getId()).isEqualTo(node.getLocation().getDescription() + "/machineTypes/custom-2-4096");
      }
      finally {
         client.destroyNodesMatching(inGroup(group + "custom"));
      }
   }

}
