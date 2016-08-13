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
package org.jclouds.compute.domain.internal;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.util.AutomaticHardwareIdSpec;
import org.jclouds.domain.Location;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.jclouds.compute.util.AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder;
import static org.jclouds.compute.util.AutomaticHardwareIdSpec.isAutomaticId;
import static org.jclouds.compute.util.AutomaticHardwareIdSpec.parseId;

public class ArbitraryCpuRamTemplateBuilderImpl extends TemplateBuilderImpl {
   @Inject
   protected ArbitraryCpuRamTemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> hardwares,
         Supplier<Location> defaultLocation, @Named("DEFAULT") Provider<TemplateOptions> optionsProvider,
         @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider) {
      super(locations, images, hardwares, defaultLocation, optionsProvider, defaultTemplateProvider);
   }

   protected Hardware automaticHardware(double cores, int ram, Optional<Float> diskSize) {
      HardwareBuilder builder = new HardwareBuilder();
      if (diskSize.isPresent() && diskSize.get() > 0.0f) {
            builder.volume(new VolumeImpl(diskSize.get(), true, true));
      }
      return builder
            .id(automaticHardwareIdSpecBuilder(cores, ram, diskSize).toString())
            .ram(ram)
            .processor(new Processor(cores, 1.0))
            .build();
   }

   protected Hardware findHardwareWithId(Set<? extends Hardware> hardwaresToSearch) {
      try {
         return super.findHardwareWithId(hardwaresToSearch);
      } catch (NoSuchElementException ex) {
         if (isAutomaticId(hardwareId)) {
            AutomaticHardwareIdSpec spec = parseId(hardwareId);
            return automaticHardware(spec.getCores(), spec.getRam(), spec.getDisk());
         }
         else {
            throw ex;
         }
      }
   }

   protected Hardware resolveHardware(Set<? extends Hardware> hardwarel, final Iterable<? extends Image> images) {
      try {
         return super.resolveHardware(hardwarel, images);
      }
      catch (NoSuchElementException ex) {
         if (minCores <= 0 || minRam == 0 || minDisk < 0) {
            throw new IllegalArgumentException("No hardware profile matching the given criteria was found. If you" +
                    " want to use exact values, please set the minCores, minRam and minDisk to positive values.");
         }
         return automaticHardware(minCores, minRam, minDisk == 0 ? Optional.<Float>absent() : Optional.of((float)minDisk));
      }
   }

}
