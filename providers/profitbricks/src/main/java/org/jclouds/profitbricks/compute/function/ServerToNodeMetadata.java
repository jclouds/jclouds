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
package org.jclouds.profitbricks.compute.function;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

import java.util.List;
import java.util.Set;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.util.InetAddresses2.IsPrivateIPAddress;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   private final Function<Storage, Volume> fnVolume;
   private final Supplier<Set<? extends Location>> locations;
   private final Function<List<Nic>, List<String>> fnCollectIps;
   private final ProfitBricksApi api;

   private final GroupNamingConvention groupNamingConvention;

   @Inject
   ServerToNodeMetadata(Function<Storage, Volume> fnVolume,
           @Memoized Supplier<Set<? extends Location>> locations,
           ProfitBricksApi api,
           GroupNamingConvention.Factory groupNamingConvention) {
      this.fnVolume = fnVolume;
      this.locations = locations;
      this.api = api;
      this.groupNamingConvention = groupNamingConvention.createWithoutPrefix();
      this.fnCollectIps = new Function<List<Nic>, List<String>>() {
         @Override
         public List<String> apply(List<Nic> in) {
            List<String> ips = Lists.newArrayListWithExpectedSize(in.size());
            for (Nic nic : in)
               ips.addAll(nic.ips());
            return ips;
         }
      };
   }

   @Override
   public NodeMetadata apply(final Server server) {
      checkNotNull(server, "Null server");
      // Location is not populated in the datacenter on a server response
      DataCenter dataCenter = api.dataCenterApi().getDataCenter(server.dataCenter().id());
      Location location = find(locations.get(), idEquals(dataCenter.location().getId()));

      float size = 0f;
      List<Volume> volumes = Lists.newArrayList();
      List<Storage> storages = server.storages();
      if (storages != null)
         for (Storage storage : storages) {
            size += storage.size();
            volumes.add(fnVolume.apply(storage));
         }

      // Build hardware
      String id = String.format("cpu=%d,ram=%d,disk=%.0f", server.cores(), server.ram(), size);
      Hardware hardware = new HardwareBuilder()
              .ids(id)
              .name(id)
              .ram(server.ram())
              .processor(new Processor(server.cores(), 1d))
              .hypervisor("kvm")
              .volumes(volumes)
              .location(location)
              .build();

      // Collect ips
      List<String> addresses = fnCollectIps.apply(server.nics());

      // Build node
      NodeMetadataBuilder nodeBuilder = new NodeMetadataBuilder();
      nodeBuilder.ids(server.id())
              .group(groupNamingConvention.extractGroup(server.name()))
              .hostname(server.hostname())
              .name(server.name())
              .backendStatus(server.state().toString())
              .status(mapStatus(server.status()))
              .hardware(hardware)
              .operatingSystem(mapOsType(server.osType()))
              .location(location)
              .privateAddresses(Iterables.filter(addresses, IsPrivateIPAddress.INSTANCE))
              .publicAddresses(Iterables.filter(addresses, not(IsPrivateIPAddress.INSTANCE)));

      return nodeBuilder.build();
   }

   static NodeMetadata.Status mapStatus(Server.Status status) {
      if (status == null)
         return NodeMetadata.Status.UNRECOGNIZED;
      switch (status) {
         case SHUTDOWN:
         case SHUTOFF:
         case PAUSED:
            return NodeMetadata.Status.SUSPENDED;
         case RUNNING:
            return NodeMetadata.Status.RUNNING;
         case BLOCKED:
            return NodeMetadata.Status.PENDING;
         case CRASHED:
            return NodeMetadata.Status.ERROR;
         default:
            return NodeMetadata.Status.UNRECOGNIZED;
      }
   }

   static OperatingSystem mapOsType(OsType osType) {
      if (osType != null)
         switch (osType) {
            case WINDOWS:
               return OperatingSystem.builder()
                       .description(OsFamily.WINDOWS.value())
                       .family(OsFamily.WINDOWS)
                       .build();
            case LINUX:
               return OperatingSystem.builder()
                       .description(OsFamily.LINUX.value())
                       .family(OsFamily.LINUX)
                       .build();
         }
      return OperatingSystem.builder()
              .description(OsFamily.UNRECOGNIZED.value())
              .family(OsFamily.UNRECOGNIZED)
              .build();
   }

}
