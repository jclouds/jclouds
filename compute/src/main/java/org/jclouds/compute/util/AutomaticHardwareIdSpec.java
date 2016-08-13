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
package org.jclouds.compute.util;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;

import java.util.Map;

public class AutomaticHardwareIdSpec {

   private double cores;
   private int ram;
   private Optional<Float> disk = Optional.absent();

   public static boolean isAutomaticId(String id) {
      return id.startsWith("automatic:");
   }

   public static AutomaticHardwareIdSpec parseId(String hardwareId) {
      AutomaticHardwareIdSpec spec = new AutomaticHardwareIdSpec();
      String hardwareSpec = hardwareId.substring(10);
      Map<String, String> specValues = Splitter.on(';')
            .trimResults()
            .omitEmptyStrings()
            .withKeyValueSeparator('=')
            .split(hardwareSpec);
      if (!specValues.containsKey("ram") || !specValues.containsKey("cores")) {
         throw new IllegalArgumentException(String.format("Omitted keys on hardwareId: %s. Please set number " +
               "of cores and ram amount.", hardwareId));
      }
      if (specValues.containsKey("disk")) {
         float disk = Float.parseFloat(specValues.get("disk"));
         if (disk > 0.0f) {
            spec.disk = Optional.of(disk);
         }
         else {
            throw new IllegalArgumentException(String.format("Invalid disk value: %s", hardwareId));
         }
      }
      spec.ram = Integer.parseInt(specValues.get("ram"));
      spec.cores = Double.parseDouble(specValues.get("cores"));
      return spec;
   }

   public static AutomaticHardwareIdSpec automaticHardwareIdSpecBuilder(double cores, int ram, Optional<Float> disk) {
      AutomaticHardwareIdSpec spec = new AutomaticHardwareIdSpec();
      if (cores <= 0 || ram == 0) {
         throw new IllegalArgumentException(String.format("Omitted or wrong minCores and minRam. If you want to" +
               " use exact values, please set the minCores and minRam values: cores=%s, ram=%s", cores, ram));
      }
      if (disk.isPresent() && disk.get() <= 0.0f) {
         throw new IllegalArgumentException(String.format("Invalid disk value: %.0f", disk.get()));
      }
      spec.disk = disk;
      spec.cores = cores;
      spec.ram = ram;
      return spec;
   }

   @Override
   public String toString() {
      if (disk.isPresent()) {
         return String.format("automatic:cores=%s;ram=%s;disk=%.0f", cores, ram, disk.get().floatValue());
      }
      else {
         return String.format("automatic:cores=%s;ram=%s", cores, ram);
      }
   }

   public double getCores() {
      return cores;
   }

   public int getRam() {
      return ram;
   }

   public Optional<Float> getDisk() {
      return disk;
   }
}
