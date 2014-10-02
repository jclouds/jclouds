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

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Info {

   public abstract int containers();

   public abstract int images();

   public abstract String driver();

   public abstract String executionDriver();

   public abstract String kernelVersion();

   public abstract int debug();

   public abstract int nFd();

   public abstract int nGoroutines();

   public abstract int nEventsListener();

   public abstract String initPath();

   public abstract String indexServerAddress();

   public abstract int memoryLimit();

   public abstract int swapLimit();

   public abstract int iPv4Forwarding();

   @SerializedNames(
           {"Containers", "Images", "Driver", "ExecutionDriver", "KernelVersion", "Debug", "NFd", "NGoroutines",
                   "NEventsListener", "InitPath", "IndexServerAddress", "MemoryLimit", "SwapLimit", "IPv4Forwarding"})
   public static Info create(int containers, int images, String driver, String executionDriver, String kernelVersion, int debug,
                             int nFd, int nGoroutines, int nEventsListener, String initPath, String indexServerAddress,
                             int memoryLimit, int swapLimit, int iPv4Forwarding) {
      return new AutoValue_Info(containers, images, driver, executionDriver, kernelVersion, debug, nFd, nGoroutines,
              nEventsListener, initPath, indexServerAddress, memoryLimit, swapLimit, iPv4Forwarding);
   }
}
