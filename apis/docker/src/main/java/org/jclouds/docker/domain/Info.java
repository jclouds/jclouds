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
package org.jclouds.docker.domain;

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Info {

   public abstract int containers();

   public abstract int debug();

   public abstract String driver();

   public abstract List<List<String>> driverStatus();

   public abstract String executionDriver();

   public abstract int iPv4Forwarding();

   public abstract int images();

   public abstract String indexServerAddress();

   public abstract String initPath();

   public abstract String initSha1();

   public abstract String kernelVersion();

   public abstract int memoryLimit();

   public abstract int nEventsListener();

   public abstract int nFd();

   public abstract int nGoroutines();

   public abstract String operatingSystem();

   public abstract int swapLimit();

   Info() {
   }

   @SerializedNames({
                   "Containers", "Debug", "Driver", "DriverStatus", "ExecutionDriver", "IPv4Forwarding", "Images",
                   "IndexServerAddress", "InitPath", "InitSha1", "KernelVersion", "MemoryLimit", "NEventsListener",
                   "NFd", "NGoroutines", "OperatingSystem", "SwapLimit"
   })
   public static Info create(int containers, int debug, String driver, List<List<String>> driverStatus,
                             String executionDriver, int iPv4Forwarding, int images, String indexServerAddress,
                             String initPath, String initSha1, String kernelVersion, int memoryLimit,
                             int nEventsListener, int nFd, int nGoroutines, String operatingSystem, int swapLimit) {
      return new AutoValue_Info(containers, debug, driver, driverStatus, executionDriver, iPv4Forwarding, images,
              indexServerAddress, initPath, initSha1, kernelVersion, memoryLimit, nEventsListener, nFd, nGoroutines,
              operatingSystem, swapLimit);
   }
}
