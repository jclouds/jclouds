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
package org.jclouds.openstack.nova.v2_0.extensions;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.Hypervisor;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.HypervisorDetails;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "HypervisorApiMockTest")
public class HypervisorApiMockTest extends BaseOpenStackMockTest<NovaApi> {

   private static Map<String, MockWebServer> servers = new ConcurrentHashMap<String, MockWebServer>();

   @BeforeMethod
   public void setupMockServer(Method method) throws IOException {
      servers.put(method.getName(), mockOpenStackServer());
      servers.get(method.getName()).enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
   }

   @AfterMethod
   public void tearDownMockServer(Method method) throws IOException {
      servers.get(method.getName()).shutdown();
   }

   public void testWhenNamespaceInExtensionsListHypervisorPresent(Method method) throws Exception {
      MockWebServer server = servers.get(method.getName());
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list_full.json"))));

      NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
      assertEquals(novaApi.getConfiguredRegions(), ImmutableSet.of("RegionOne", "RegionTwo", "RegionThree"));

      Optional<HypervisorApi> hypervisorApi = novaApi.getHypervisorApi("RegionOne");

      assertTrue(hypervisorApi.isPresent());

      assertRequests(server, 2, null);
   }

   public void testWhenNamespaceNotInExtensionsListHypervisorPresent(Method method) throws Exception {
      MockWebServer server = servers.get(method.getName());
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));

      NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
      assertEquals(novaApi.getConfiguredRegions(), ImmutableSet.of("RegionOne", "RegionTwo", "RegionThree"));

      Optional<HypervisorApi> hypervisorApi = novaApi.getHypervisorApi("RegionOne");

      assertFalse(hypervisorApi.isPresent());

      assertRequests(server, 2, null);
   }

   public void testListHypervisor(Method method) throws Exception {
      MockWebServer server = servers.get(method.getName());
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list_full.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/hypervisor_list.json"))));

      NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
      assertEquals(novaApi.getConfiguredRegions(), ImmutableSet.of("RegionOne", "RegionTwo", "RegionThree"));

      FluentIterable<? extends Hypervisor> hypervisors = novaApi.getHypervisorApi("RegionOne").get().list();

      Optional<? extends Hypervisor> hypervisor = hypervisors.first();

      assertTrue(hypervisor.isPresent(), "Couldn't find hypervisor");
      assertEquals(hypervisor.get().getId(), "1", "Expected hypervisor id to be 1 but it was: " + hypervisor.get().getId());
      assertEquals(hypervisor.get().getName(), "os-compute1", "Expected hypervisor name to be os-compute1 but it was: " + hypervisor.get().getName());

      assertRequests(server, 3, "/os-hypervisors");
   }

   public void testListHypervisorWhenResponseIs404(Method method) throws Exception {
      MockWebServer server = servers.get(method.getName());
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list_full.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
      assertEquals(novaApi.getConfiguredRegions(), ImmutableSet.of("RegionOne", "RegionTwo", "RegionThree"));

      assertTrue(novaApi.getHypervisorApi("RegionOne").get().list().isEmpty());

      assertRequests(server, 3, "/os-hypervisors");
   }

   public void testListInDetail(Method method) throws Exception {
      MockWebServer server = servers.get(method.getName());
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list_full.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/hypervisor_details.json"))));

      NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
      assertEquals(novaApi.getConfiguredRegions(), ImmutableSet.of("RegionOne", "RegionTwo", "RegionThree"));

      FluentIterable<? extends HypervisorDetails> hypervisors = novaApi.getHypervisorApi("RegionOne").get().listInDetail();

      Optional<? extends HypervisorDetails> hypervisorDetailsOptional = hypervisors.first();

      assertTrue(hypervisorDetailsOptional.isPresent(), "Couldn't find Hypervisor Details");
      HypervisorDetails hypervisorDetails = hypervisorDetailsOptional.get();
      assertEquals(hypervisorDetails.getId(), "1", "Expected hypervisor id to be 1 but it was: " + hypervisorDetails.getId());
      assertEquals(hypervisorDetails.getName(), "os-compute1",
            "Expected hypervisor name to be os-compute1 but it was: " + hypervisorDetails.getName());
      assertEquals(hypervisorDetails.getCurrentWorkload(), Integer.valueOf(0),
            "Expected CurrentWorkload to be 0 but it was: " + hypervisorDetails.getCurrentWorkload());
      assertEquals(hypervisorDetails.getDiskAvailableLeast(), Integer.valueOf(131),
            "Expected Disk Available Least to be 131 but it was: " + hypervisorDetails.getDiskAvailableLeast());
      assertEquals(hypervisorDetails.getFreeDiskGb(), Integer.valueOf(144),
            "Expected Free Disk Gb to be 144 but it was: " + hypervisorDetails.getFreeDiskGb());
      assertEquals(hypervisorDetails.getFreeRamMb(), Integer.valueOf(12911),
            "Expected Free Ram Mb to be 12911 but it was: " + hypervisorDetails.getFreeRamMb());
      assertEquals(hypervisorDetails.getHypervisorType(), "QEMU",
            "Expected Hypervisor Type to be QEMU but it was: " + hypervisorDetails.getHypervisorType());
      assertEquals(hypervisorDetails.getHypervisorVersion(), 2000000,
            "Expected Hypervisor Version to be 2000000 but it was: " + hypervisorDetails.getHypervisorVersion());
      assertEquals(hypervisorDetails.getLocalGb(), 195, "Expected Local Gb to be 195 but it was: " + hypervisorDetails.getLocalGb());
      assertEquals(hypervisorDetails.getLocalGbUsed(), 51, "Expected Local Gb Used to be 51 but it was: " + hypervisorDetails.getLocalGbUsed());
      assertEquals(hypervisorDetails.getMemoryMb(), 20079, "Expected Memory Mb to be 20079 but it was: " + hypervisorDetails.getMemoryMb());
      assertEquals(hypervisorDetails.getMemoryMbUsed(), 7168,
            "Expected Memory Mb Used to be 7168 but it was: " + hypervisorDetails.getMemoryMbUsed());
      assertEquals(hypervisorDetails.getRunningVms(), Integer.valueOf(2),
            "Expected Running Vms to be 2 but it was: " + hypervisorDetails.getRunningVms());
      assertEquals(hypervisorDetails.getVcpus(), 16, "Expected Vcpus to be 16 but it was: " + hypervisorDetails.getVcpus());
      assertEquals(hypervisorDetails.getVcpusUsed(), 5, "Expected Vcpus Used to be 5 but it was: " + hypervisorDetails.getVcpusUsed());
      assertEquals(hypervisorDetails.getCpuInfo(),
            "{\"vendor\": \"Intel\", \"model\": \"Westmere\", \"arch\": \"x86_64\", \"features\": [\"pge\", \"avx\", \"clflush\", \"sep\", "
                  + "\"syscall\", \"vme\", \"tsc\", \"xsave\", \"vmx\", \"cmov\", \"ssse3\", \"pat\", \"lm\", \"msr\", \"nx\", \"fxsr\", \"sse4.1\", "
                  + "\"pae\", \"sse4.2\", \"pclmuldq\", \"mmx\", \"osxsave\", \"cx8\", \"mce\", \"de\", \"aes\", \"ht\", \"pse\", \"lahf_lm\","
                  + " \"popcnt\", \"mca\", \"apic\", \"sse\", \"ds\", \"pni\", \"rdtscp\", \"sse2\", \"ss\", \"hypervisor\", \"pcid\", \"fpu\","
                  + " \"cx16\", \"pse36\", \"mtrr\", \"x2apic\"], \"topology\": {\"cores\": 4, \"threads\": 1, \"sockets\": 1}}",
            "Unexpected Cpu Info it was: " + hypervisorDetails.getCpuInfo());

      assertRequests(server, 3, "/os-hypervisors/detail");
   }

   public void testListInDetailWhenResponseIs404(Method method) throws Exception {
      MockWebServer server = servers.get(method.getName());
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list_full.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
      assertEquals(novaApi.getConfiguredRegions(), ImmutableSet.of("RegionOne", "RegionTwo", "RegionThree"));

      assertTrue(novaApi.getHypervisorApi("RegionOne").get().listInDetail().isEmpty());

      assertRequests(server, 3, "/os-hypervisors/detail");
   }

   private void assertRequests(MockWebServer server, int requestCount, String requestPath) throws InterruptedException {
      assertEquals(server.getRequestCount(), requestCount);
      assertAuthentication(server);
      assertExtensions(server, "/v2/da0d12be20394afb851716e10a49e4a7");
      if (requestPath != null) {
         assertRequest(server.takeRequest(), "GET", "/v2/da0d12be20394afb851716e10a49e4a7" + requestPath);
      }
   }
}
