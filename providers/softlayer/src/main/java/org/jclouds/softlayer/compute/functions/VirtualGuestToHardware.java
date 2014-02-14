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
package org.jclouds.softlayer.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;

import javax.inject.Singleton;

@Singleton
public class VirtualGuestToHardware implements Function<VirtualGuest, Hardware> {

   @Override
   public Hardware apply(final VirtualGuest from) {
      HardwareBuilder builder = new HardwareBuilder().ids(from.getId() + "")
              .name(from.getHostname())
              .hypervisor("XenServer")
              .processors(ImmutableList.of(new Processor(from.getStartCpus(), 2)))
              .ram(from.getMaxMemory());

      if (from.getVirtualGuestBlockDevices() != null) {
         builder.volumes(
                 FluentIterable.from(from.getVirtualGuestBlockDevices()).filter(new Predicate<VirtualGuestBlockDevice>() {
                    @Override
                    public boolean apply(VirtualGuestBlockDevice input) {
                       return input.getMountType().equals("Disk");
                    }
                 })
                         .transform(new Function<VirtualGuestBlockDevice, Volume>() {
                            @Override
                            public Volume apply(VirtualGuestBlockDevice item) {
                               float volumeSize = item.getVirtualDiskImage().getCapacity();
                               return new VolumeImpl(
                                       item.getId() + "",
                                       from.isLocalDiskFlag() ? Volume.Type.LOCAL : Volume.Type.SAN,
                                       volumeSize, null, item.getBootableFlag() == 1, false);
                            }
                         }).toSet());
      }
      return builder.build();
   }
}
