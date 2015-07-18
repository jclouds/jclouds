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
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.Hypervisor;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.HypervisorDetails;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

@Test(groups = "live", testName = "HypervisorApiLiveTest")
public class HypervisorApiLiveTest extends BaseNovaApiLiveTest {

   @Test
   public void testList() throws Exception {

      Optional<? extends HypervisorApi> hypervisorApi = api.getHypervisorApi("RegionOne");
      if (hypervisorApi.isPresent()) {
         FluentIterable<? extends Hypervisor> hypervisors = hypervisorApi.get().list();

         for (Hypervisor hypervisor : hypervisors) {
            assertNotNull(hypervisor.getName());
            assertNotNull(hypervisor.getId(), "hypervisor: " + hypervisor.getName() + " has invalid id");
         }
      }
   }

   @Test
   public void testListInDetail() throws Exception {

      Optional<? extends HypervisorApi> hypervisorApi = api.getHypervisorApi("RegionOne");
      if (hypervisorApi.isPresent()) {
         FluentIterable<? extends HypervisorDetails> hypervisors = hypervisorApi.get().listInDetail();

         for (HypervisorDetails hypervisorDetails : hypervisors) {
            assertNotNull(hypervisorDetails.getId(), "Expected hypervisor id");
            assertNotNull(hypervisorDetails.getName(), "Expected hypervisor name");
            assertNotNull(hypervisorDetails.getCurrentWorkload(), "Expected CurrentWorkload");
            assertNotNull(hypervisorDetails.getDiskAvailableLeast(), "Expected Disk Available Least");
            assertNotNull(hypervisorDetails.getFreeDiskGb(), "Expected Free Disk Gb");
            assertNotNull(hypervisorDetails.getFreeRamMb(), "Expected Free Ram Mb");
            assertNotNull(hypervisorDetails.getHypervisorType(), "Expected Hypervisor Type");
            assertNotNull(hypervisorDetails.getHypervisorVersion(), "Expected Hypervisor Version");
            assertNotNull(hypervisorDetails.getLocalGb(), "Expected Local Gb");
            assertNotNull(hypervisorDetails.getLocalGbUsed(), "Expected Local Gb Used");
            assertNotNull(hypervisorDetails.getMemoryMb(), "Expected Memory Mb ");
            assertNotNull(hypervisorDetails.getMemoryMbUsed(), "Expected Memory Mb Used");
            assertNotNull(hypervisorDetails.getRunningVms(), "Expected Running Vms");
            assertNotNull(hypervisorDetails.getVcpus(), "Expected Vcpus");
            assertNotNull(hypervisorDetails.getVcpusUsed(), "Expected Vcpus Used");
            assertNotNull(hypervisorDetails.getCpuInfo(), "Eexpected Cpu Info");
         }
      }
   }
}
