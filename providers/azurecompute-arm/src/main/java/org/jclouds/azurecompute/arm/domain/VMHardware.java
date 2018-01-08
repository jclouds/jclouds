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
package org.jclouds.azurecompute.arm.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

/**
 * A VM Size that is available in a region for a given subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt269440.aspx" >api</a>
 */
@AutoValue
public abstract class VMHardware {

   /**
    * The name of the VM size.
    */
   public abstract String name();

   /**
    * The number of cores that are available in the VM size.
    */
   public abstract Integer numberOfCores();

   /**
    * Specifies the size in MB of the OS Disk.
    */
   public abstract Integer osDiskSizeInMB();

   /**
    * The size of the resource disk.
    */
   public abstract Integer resourceDiskSizeInMB();

   /**
    * Specifies the available RAM in MB.
    */
   public abstract Integer memoryInMB();

   /**
    * Specifies the maximum number of data disks that can be attached to the VM size.
    */
   public abstract Integer maxDataDiskCount();

   /**
    * Specifies the location of the HW resource
    */
   public abstract String location();

   @SerializedNames({ "name", "numberOfCores", "osDiskSizeInMB", "resourceDiskSizeInMB", "memoryInMB", "maxDataDiskCount", "location"})
   public static VMHardware create(String name, Integer numberOfCores, Integer osDiskSizeInMB, Integer resourceDiskSizeInMB, Integer memoryInMB, Integer maxDataDiskCount, String location) {

      return new AutoValue_VMHardware(name, numberOfCores, osDiskSizeInMB, resourceDiskSizeInMB, memoryInMB, maxDataDiskCount, location);
   }
}
