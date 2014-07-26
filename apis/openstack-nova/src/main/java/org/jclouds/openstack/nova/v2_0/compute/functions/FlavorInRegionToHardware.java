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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.FlavorInRegion;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * A function for transforming the nova specific FlavorInRegion object to the generic Hardware object.
 */
public class FlavorInRegionToHardware implements Function<FlavorInRegion, Hardware> {

   private final Supplier<Map<String, Location>> locationIndex;

   @Inject
   public FlavorInRegionToHardware(Supplier<Map<String, Location>> locationIndex) {
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
   }

   @Override
   public Hardware apply(FlavorInRegion flavorInRegion) {
      Location location = locationIndex.get().get(flavorInRegion.getRegion());
      checkState(location != null, "location %s not in locationIndex: %s", flavorInRegion.getRegion(), locationIndex.get());
      Flavor flavor = flavorInRegion.getFlavor();
      return new HardwareBuilder().id(flavorInRegion.slashEncode()).providerId(flavor.getId()).name(flavor.getName())
               .ram(flavor.getRam()).processor(new Processor(flavor.getVcpus(), 1.0)).volume(
                        new VolumeImpl(Float.valueOf(flavor.getDisk()), true, true)).location(location).build();
   }
}
