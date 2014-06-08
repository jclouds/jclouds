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
package org.jclouds.googlecomputeengine.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.net.URI;
import java.util.Map;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.MachineTypeInZone;
import org.jclouds.googlecomputeengine.domain.SlashEncodedIds;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Transforms a google compute domain specific machine type to a generic Hardware object.
 */
public class MachineTypeInZoneToHardware implements Function<MachineTypeInZone, Hardware> {

   private final Supplier<Map<URI, ? extends Location>> locations;

   @Inject
   public MachineTypeInZoneToHardware(@Memoized Supplier<Map<URI, ? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public Hardware apply(final MachineTypeInZone input) {
      Iterable<? extends Location> zonesForMachineType = filter(locations.get().values(), new Predicate<Location>() {
         @Override
         public boolean apply(Location l) {
            return l.getId().equals(input.getMachineType().getZone());
         }
      });

      Location location = checkNotNull(getOnlyElement(zonesForMachineType),
              "location for %s",
              input.getMachineType().getZone());

      // TODO Figure out a robust way to deal with machineTypes with imageSizeGb==0 rather than just blocking them.
      return new HardwareBuilder()
              .id(SlashEncodedIds.fromTwoIds(input.getMachineType().getZone(), input.getMachineType().getName()).slashEncode())
              .location(location)
              .name(input.getMachineType().getName())
              .hypervisor("kvm")
              .processor(new Processor(input.getMachineType().getGuestCpus(), 1.0))
              .providerId(input.getMachineType().getId())
              .ram(input.getMachineType().getMemoryMb())
              .uri(input.getMachineType().getSelfLink())
              .userMetadata(ImmutableMap.of("imageSpaceGb", Integer.toString(input.getMachineType().getImageSpaceGb())))
              .volumes(collectVolumes(input.getMachineType()))
              .supportsImage(input.getMachineType().getImageSpaceGb() > 0
                      ? Predicates.<Image>alwaysTrue()
                      : Predicates.<Image>alwaysFalse())
              .build();
   }

   private Iterable<Volume> collectVolumes(MachineType input) {
      ImmutableSet.Builder<Volume> volumes = ImmutableSet.builder();
      for (MachineType.ScratchDisk disk : input.getScratchDisks()) {
         volumes.add(new VolumeBuilder()
                 .type(Volume.Type.LOCAL)
                 .size(new Integer(disk.getDiskGb()).floatValue())
                 .bootDevice(true)
                 .durable(false).build());
      }
      return volumes.build();
   }
}
