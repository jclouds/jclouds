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
package org.jclouds.openstack.nova.v2_0.domain.regionscoped;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

/**
 * Hypervisor which shows id, name and other details
 */
@AutoValue
public abstract class HypervisorDetails {

   public abstract String getId();

   @Nullable
   public abstract String getName();

   @Nullable
   public abstract Integer getCurrentWorkload();

   @Nullable
   public abstract Integer getDiskAvailableLeast();

   @Nullable
   public abstract Integer getFreeDiskGb();

   @Nullable
   public abstract Integer getFreeRamMb();

   public abstract String getHypervisorType();

   public abstract int getHypervisorVersion();

   public abstract int getLocalGb();

   public abstract int getLocalGbUsed();

   public abstract int getMemoryMb();

   public abstract int getMemoryMbUsed();

   @Nullable
   public abstract Integer getRunningVms();

   public abstract int getVcpus();

   public abstract int getVcpusUsed();

   @Nullable
   public abstract String getCpuInfo();

   @SerializedNames({ "id", "hypervisor_hostname", "current_workload", "disk_available_least", "free_disk_gb", "free_ram_mb", "hypervisor_type",
         "hypervisor_version", "local_gb", "local_gb_used", "memory_mb", "memory_mb_used", "running_vms", "vcpus", "vcpus_used",
         "cpu_info" })
   private static HypervisorDetails create(String id, String name, int currentWorkload, int diskAvailableLeast, int freeDiskGb, int freeRamMb,
         String hypervisorType, int hypervisorVersion, int localGb, int localGbUsed, int memoryMb, int memoryMbUsed, int runningVms,
         int vcpus, int vcpusUsed, String cpuInfo) {
      return new AutoValue_HypervisorDetails(id, name, currentWorkload, diskAvailableLeast, freeDiskGb, freeRamMb, hypervisorType, hypervisorVersion,
            localGb, localGbUsed, memoryMb, memoryMbUsed, runningVms, vcpus, vcpusUsed, cpuInfo);
   }

}
