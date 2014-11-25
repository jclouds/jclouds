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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/** Parameter to {@linkplain org.jclouds.googlecomputeengine.features.InstanceApi#create(NewInstance)}. */
@AutoValue
public abstract class NewInstance {
   @AutoValue
   abstract static class NetworkInterface {
      abstract URI network();

      abstract List<AccessConfig.Type> accessConfigs();

      static NetworkInterface create(URI network) {
         return create(network, Arrays.asList(AccessConfig.Type.ONE_TO_ONE_NAT));
      }

      @SerializedNames({ "network", "accessConfigs" })
      static NetworkInterface create(URI network, List<AccessConfig.Type> accessConfigs) {
         return new AutoValue_NewInstance_NetworkInterface(network, accessConfigs);
      }

      NetworkInterface() {
      }
   }

   public abstract String name();

   public abstract URI machineType();

   public abstract List<NetworkInterface> networkInterfaces();

   public abstract List<AttachDisk> disks();

   @Nullable public abstract String description();

   public abstract Tags tags();

   /** Add metadata via {@link Metadata#items()}. */
   public abstract Metadata metadata();

   @Nullable public abstract Scheduling scheduling();

   /** Convenience for creating a new instance with only a boot disk and minimal parameters. */
   public static NewInstance create(String name, URI machineType, URI network, URI sourceImage) {
      return create(name, machineType, network, Arrays.asList(AttachDisk.newBootDisk(sourceImage)), null);
   }

   public static NewInstance create(String name, URI machineType, URI network, List<AttachDisk> disks, String description) {
      checkArgument(disks.get(0).boot(), "disk 0 must be a boot disk! %s", disks);
      boolean foundBoot = false;
      for (AttachDisk disk : disks) {
         if (disk.boot()) {
            checkArgument(!foundBoot, "There must be only one boot disk! %s", disks);
            foundBoot = true;
         }
      }
      return create(name, machineType, ImmutableList.of(NetworkInterface.create(network)), ImmutableList.copyOf(disks),
            description, Tags.create(), Metadata.create(), null);
   }

   @SerializedNames({ "name", "machineType", "networkInterfaces", "disks", "description", "tags", "metadata", "scheduling" })
   static NewInstance create(String name, URI machineType, List<NetworkInterface> networkInterfaces,
         List<AttachDisk> disks, String description, Tags tags, Metadata metadata, Scheduling scheduling) {
      return new AutoValue_NewInstance(name, machineType, networkInterfaces, disks, description, tags, metadata, scheduling);
   }

   NewInstance() {
   }
}
