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
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk.Type.PERSISTENT;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
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

   @AutoValue
   public abstract static class Disk {
      @AutoValue
      abstract static class InitializeParams {
         /** Override the default naming convention. */
         @Nullable public abstract String diskName();

         /** Set to use a size larger than the {@link #sourceImage()}. You need to repartition when set. */
         @Nullable public abstract Long diskSizeGb();

         /** The {@link org.jclouds.googlecomputeengine.domain.Image#selfLink() source image}. */
         public abstract URI sourceImage();

         static InitializeParams create(URI sourceImage) {
            return create(null, null, sourceImage);
         }

         @SerializedNames({ "diskName", "diskSizeGb", "sourceImage" })
         static InitializeParams create(String diskName, Long diskSizeGb, URI sourceImage) {
            return new AutoValue_NewInstance_Disk_InitializeParams(diskName, diskSizeGb, sourceImage);
         }

         InitializeParams() {
         }
      }

      public abstract AttachedDisk.Type type();

      /** Use an existingBootDisk {@link org.jclouds.googlecomputeengine.domain.Disk#selfLink() boot disk}. */
      @Nullable public abstract URI source();

      /** Set to automatically create a boot disk */
      @Nullable public abstract InitializeParams initializeParams();

      public abstract boolean boot();

      public abstract boolean autoDelete();

      public static Disk existingBootDisk(URI existingBootDisk) {
         return create(PERSISTENT, existingBootDisk, null, true, false);
      }

      public static Disk newBootDisk(URI sourceImage) {
         return create(PERSISTENT, null, InitializeParams.create(sourceImage), true, true);
      }

      public static Disk existingDisk(URI existingDisk) {
         return create(PERSISTENT, existingDisk, null, false, false);
      }

      @SerializedNames({ "type", "source", "initializeParams", "boot", "autoDelete" })
      static Disk create(AttachedDisk.Type type, URI source, InitializeParams initializeParams, boolean boot,
            boolean autoDelete) {
         return new AutoValue_NewInstance_Disk(type, source, initializeParams, boot, autoDelete);
      }

      Disk() {
      }
   }

   public abstract URI machineType();

   public abstract String name();

   public abstract List<NetworkInterface> networkInterfaces();

   public abstract List<Disk> disks();

   @Nullable public abstract String description();

   public abstract Tags tags();

   /** Add metadata via {@link Metadata#items()}. */
   public abstract Metadata metadata();

   public static NewInstance create(URI machineType, String name, URI network, Disk bootDisk, String description) {
      return create(machineType, name, network, Arrays.asList(checkNotNull(bootDisk, "bootDisk")), description);
   }

   public static NewInstance create(URI machineType, String name, URI network, List<Disk> disks, String description) {
      checkArgument(disks.get(0).boot(), "disk 0 must be a boot disk! %s", disks);
      boolean foundBoot = false;
      for (Disk disk : disks) {
         if (disk.boot()) {
            checkArgument(!foundBoot, "There must be only one boot disk! %s", disks);
            foundBoot = true;
         }
      }
      return create(machineType, name, ImmutableList.of(NetworkInterface.create(network)), ImmutableList.copyOf(disks),
            description, Tags.create(), Metadata.create());
   }

   @SerializedNames({ "machineType", "name", "networkInterfaces", "disks", "description", "tags", "metadata" })
   static NewInstance create(URI machineType, String name, List<NetworkInterface> networkInterfaces, List<Disk> disks,
         String description, Tags tags, Metadata metadata) {
      return new AutoValue_NewInstance(machineType, name, networkInterfaces, disks, description, tags, metadata);
   }

   NewInstance() {
   }
}
