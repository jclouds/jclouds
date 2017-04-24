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
package org.jclouds.azurecompute.arm.compute.functions;

import static org.jclouds.azurecompute.arm.compute.domain.LocationAndName.fromLocationAndName;

import java.util.Set;

import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

public class VMHardwareToHardware implements Function<VMHardware, Hardware> {

   private final Supplier<Set<? extends Location>> locations;

   @Inject
   VMHardwareToHardware(@Memoized final Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public Hardware apply(VMHardware from) {
      final HardwareBuilder builder = new HardwareBuilder()
              .name(from.name())
              .providerId(from.name())
              .id(fromLocationAndName(from.location(), from.name()).slashEncode())
              .processors(ImmutableList.of(new Processor(from.numberOfCores(), 2)))
              .ram(from.memoryInMB())
              .location(FluentIterable.from(locations.get())
                      .firstMatch(LocationPredicates.idEquals(from.location()))
                      .get());

      // No id or providerId from Azure
      if (from.resourceDiskSizeInMB() != null) {
         builder.volume(new VolumeBuilder()
                 .size(Float.valueOf(from.resourceDiskSizeInMB()))
                 .type(Volume.Type.LOCAL)
                 .build());
      }
      if (from.osDiskSizeInMB() != null) {
         builder.volume(new VolumeBuilder()
                 .size(Float.valueOf(from.osDiskSizeInMB()))
                 .type(Volume.Type.LOCAL)
                 .build());
      }

      ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();
      metadata.put("maxDataDiskCount", String.valueOf(from.maxDataDiskCount()));
      builder.userMetadata(metadata.build());

      return builder.build();
   }

}
