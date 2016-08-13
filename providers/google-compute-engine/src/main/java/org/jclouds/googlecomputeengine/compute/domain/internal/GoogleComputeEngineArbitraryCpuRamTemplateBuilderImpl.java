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
package org.jclouds.googlecomputeengine.compute.domain.internal;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.ArbitraryCpuRamTemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.net.URI;
import java.util.Set;

public class GoogleComputeEngineArbitraryCpuRamTemplateBuilderImpl extends ArbitraryCpuRamTemplateBuilderImpl {
   @Inject
   protected GoogleComputeEngineArbitraryCpuRamTemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends org.jclouds.compute.domain.Image>> images,
         @Memoized Supplier<Set<? extends Hardware>> hardwares, Supplier<Location> defaultLocation,
         @Named("DEFAULT") Provider<TemplateOptions> optionsProvider,
         @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider) {
      super(locations, images, hardwares, defaultLocation, optionsProvider, defaultTemplateProvider);
   }

   @Override
   protected Hardware automaticHardware(double cores, int ram, Optional<Float> disk) {
      if (location == null) {
         location = defaultLocation.get();
      }
      String uri = location.getDescription() + "/machineTypes/custom-" + (int)cores + "-" + ram;
      return new HardwareBuilder()
            .id(uri)
            .ram(ram)
            .processor(new Processor((int)cores, 1.0))
            .providerId(uri)
            .uri(URI.create(uri))
            .build();
   }

}
