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

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig.Type;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling;
import org.jclouds.googlecomputeengine.domain.Instance.ServiceAccount;
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
      @Nullable abstract URI subnetwork();

      abstract List<AccessConfig> accessConfigs();

      static NetworkInterface create(URI network) {
         return create(network, Arrays.asList(AccessConfig.create(null, Type.ONE_TO_ONE_NAT, null)));
      }

      static NetworkInterface create(URI network, URI subnetwork) {
         return create(network, subnetwork,
             Arrays.asList(AccessConfig.create(null, Type.ONE_TO_ONE_NAT, null)));
      }

      @SerializedNames({ "network", "accessConfigs" })
      static NetworkInterface create(URI network, List<AccessConfig> accessConfigs) {
         return new AutoValue_NewInstance_NetworkInterface(network, null, accessConfigs);
      }

      @SerializedNames({ "network", "subnetwork", "accessConfigs" })
      static NetworkInterface create(URI network, URI subnetwork, List<AccessConfig> accessConfigs) {
         return new AutoValue_NewInstance_NetworkInterface(network, subnetwork, accessConfigs);
      }

      NetworkInterface() {
      }
   }

   public abstract String name();

   public abstract URI machineType();

   @Nullable public abstract Boolean canIpForward();

   public abstract List<NetworkInterface> networkInterfaces();

   public abstract List<AttachDisk> disks();

   @Nullable public abstract String description();

   public abstract Tags tags();

   /** Add metadata via {@link Metadata#items()}. */
   public abstract Metadata metadata();

   @Nullable public abstract List<ServiceAccount> serviceAccounts();

   @Nullable public abstract Scheduling scheduling();

   /** Convenience for creating a new instance with only a boot disk and minimal parameters. */
   public static NewInstance create(String name, URI machineType, URI network, URI sourceImage) {
      return create(name, machineType, network, Arrays.asList(AttachDisk.newBootDisk(sourceImage)), null, null);
   }

   public static NewInstance create(String name, URI machineType, URI network, URI subnetwork, URI sourceImage) {
      return create(name, machineType, network, subnetwork, Arrays.asList(AttachDisk.newBootDisk(sourceImage)), null,
              null);
   }

   public static NewInstance create(String name, URI machineType, URI network, List<AttachDisk> disks,
                                    @Nullable String description, @Nullable Tags tags) {
      return create(name, machineType, network, null, disks, description, tags);
   }

   public static NewInstance create(String name, URI machineType, URI network, @Nullable URI subnetwork,
                                    List<AttachDisk> disks, @Nullable String description, @Nullable Tags tags) {
      checkArgument(disks.get(0).boot(), "disk 0 must be a boot disk! %s", disks);
      boolean foundBoot = false;
      for (AttachDisk disk : disks) {
         if (disk.boot()) {
            checkArgument(!foundBoot, "There must be only one boot disk! %s", disks);
            foundBoot = true;
         }
      }
      return create(name, machineType, null, ImmutableList.of(NetworkInterface.create(network)), ImmutableList.copyOf(disks),
            description, tags != null ? tags : Tags.create(), Metadata.create(), null, null);
   }

   @SerializedNames({ "name", "machineType", "canIpForward", "networkInterfaces", "disks", "description",
                      "tags", "metadata", "serviceAccounts", "scheduling" })
   static NewInstance create(String name, URI machineType, Boolean canIpForward,
                             List<NetworkInterface> networkInterfaces, List<AttachDisk> disks, String description,
                             Tags tags, Metadata metadata, List<ServiceAccount> serviceAccounts, Scheduling scheduling) {
      return new AutoValue_NewInstance(name, machineType, canIpForward, networkInterfaces, disks, description,
                                       tags, metadata, serviceAccounts, scheduling);
   }

   NewInstance() {
   }

   public static class Builder {
      private String name;
      private URI machineType;
      private Boolean canIpForward;
      private List<NetworkInterface> networkInterfaces;
      private List<AttachDisk> disks;
      private String description;
      private Tags tags;
      private Metadata metadata;
      private List<ServiceAccount> serviceAccounts;
      private Scheduling scheduling;

      public Builder(String name, URI machineType, URI network, List<AttachDisk> disks) {
         checkNotNull(name, "NewInstance name cannot be null");
         this.name = name;
         this.machineType = machineType;
         this.networkInterfaces = ImmutableList.of(NetworkInterface.create(network));
         this.disks = disks;
      }

      public Builder(String name, URI machineType, URI network, URI subnetwork, List<AttachDisk> disks) {
         checkNotNull(name, "NewInstance name cannot be null");
         this.name = name;
         this.machineType = machineType;
         this.networkInterfaces = ImmutableList.of(NetworkInterface.create(network, subnetwork));
         this.disks = disks;
      }

      public Builder(String name, URI machineType, URI network, URI sourceImage) {
         checkNotNull(name, "NewInstance name cannot be null");
         this.name = name;
         this.machineType = machineType;
         this.networkInterfaces = ImmutableList.of(NetworkInterface.create(network));
         this.disks = Arrays.asList(AttachDisk.newBootDisk(sourceImage));
      }

      public Builder(String name, URI machineType, URI network, URI subnetwork, URI sourceImage) {
         checkNotNull(name, "NewInstance name cannot be null");
         this.name = name;
         this.machineType = machineType;
         this.networkInterfaces = ImmutableList.of(NetworkInterface.create(network, subnetwork));
         this.disks = Arrays.asList(AttachDisk.newBootDisk(sourceImage));
      }

      public Builder canIpForward(Boolean canIpForward){
         this.canIpForward = canIpForward;
         return this;
      }

      public Builder description(String description){
         this.description = description;
         return this;
      }

      public Builder tags(Tags tags){
         this.tags = tags;
         return this;
      }

      public Builder metadata(Metadata metadata){
         this.metadata = metadata;
         return this;
      }

      /**
       * A list of service accounts, with their specified scopes, authorized for this instance.
       * Service accounts generate access tokens that can be accessed through the metadata server
       * and used to authenticate applications on the instance.
       * Note: to add scopes to the default service account on the VM you can use 'default' as
       * a keyword for email.
       */
      public Builder serviceAccounts(List<ServiceAccount> serviceAccounts){
         this.serviceAccounts = serviceAccounts;
         return this;
      }

      public Builder scheduling(Scheduling scheduling){
         this.scheduling = scheduling;
         return this;
      }

      public NewInstance build() {
         return create(name, machineType, canIpForward, networkInterfaces, disks, description, tags != null ? tags : Tags.create(),
                       metadata != null ? metadata : Metadata.create(), serviceAccounts, scheduling);
      }
   }
}
