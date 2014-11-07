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
package org.jclouds.googlecomputeengine.domain.templates;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk;
import static org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk.Mode;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig.Type;
import org.jclouds.googlecomputeengine.domain.Instance.ServiceAccount;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/** Optional information for creating an instance. */
// TODO! this is dangerously similarly named to the InstanceTemplate resource!
public class InstanceTemplate {

   public static final class PersistentDisk {

      private final AttachedDisk.Type type = AttachedDisk.Type.PERSISTENT;
      private final AttachedDisk.Mode mode;
      private final URI source;
      private final String deviceName;
      private final boolean autoDelete;
      private final boolean boot;

      public PersistentDisk(AttachedDisk.Mode mode, URI source, String deviceName, boolean autoDelete, boolean boot) {
         this.mode = checkNotNull(mode, "mode");
         this.source = checkNotNull(source, "source");
         this.deviceName = deviceName;
         this.autoDelete = autoDelete;
         this.boot = boot;
      }

      public AttachedDisk.Mode mode() {
         return mode;
      }

      public URI source() {
         return source;
      }

      @Nullable public String deviceName() {
         return deviceName;
      }

      public boolean autoDelete() {
         return autoDelete;
      }

      public boolean boot() {
         return boot;
      }
   }

   public static final class NetworkInterface {

      private final URI network;
      private final String networkIP;
      private final List<AccessConfig> accessConfigs;

      public NetworkInterface(URI network, String networkIP, List<AccessConfig> accessConfigs) {
         this.network = network;
         this.networkIP = networkIP;
         this.accessConfigs = accessConfigs;
      }

      public URI network() {
         return network;
      }

      @Nullable public String networkIP() {
         return networkIP;
      }

      @Nullable public List<AccessConfig> accessConfigs() {
         return accessConfigs;
      }
   }

   private String name;
   private String description;
   private URI machineType;
   private URI image;
   private List<ServiceAccount> serviceAccounts = Lists.newArrayList();
   private List<PersistentDisk> disks = Lists.newArrayList();
   private List<NetworkInterface> networkInterfaces = Lists.newArrayList();
   private Map<String, String> metadata = Maps.newLinkedHashMap();

   /**
    * @see org.jclouds.googlecomputeengine.domain.Instance#name()
    */
   public String name() {
      return name;
   }

   public InstanceTemplate name(String name) {
      this.name = name;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Instance#description()
    */
   public String description() {
      return description;
   }

   public InstanceTemplate description(String description) {
      this.description = description;
      return this;
   }

   /**
    * @see Image#selfLink()
    */
   public URI image() {
      return image;
   }

   public InstanceTemplate image(URI image) {
      this.image = image;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Instance#machineType()
    */
   public URI machineType() {
      return machineType;
   }

   public InstanceTemplate machineType(URI machineType) {
      this.machineType = machineType;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Instance#disks()
    */
   public List<PersistentDisk> disks() {
      return disks;
   }

   public InstanceTemplate addDisk(Mode mode, URI source) {
      this.disks.add(new PersistentDisk(mode, source, null, false, false));
      return this;
   }

   public InstanceTemplate addDisk(Mode mode, URI source, boolean autoDelete) {
      this.disks.add(new PersistentDisk(mode, source, null, autoDelete, false));
      return this;
   }

   public InstanceTemplate addDisk(Mode mode, URI source, String deviceName, boolean autoDelete) {
      this.disks.add(new PersistentDisk(mode, source, deviceName, autoDelete, false));
      return this;
   }

   public InstanceTemplate addDisk(Mode mode, URI source, String deviceName, boolean autoDelete, boolean boot) {
      this.disks.add(new PersistentDisk(mode, source, deviceName, autoDelete, boot));
      return this;
   }

   public InstanceTemplate disks(List<PersistentDisk> disks) {
      this.disks = Lists.newArrayList();
      this.disks.addAll(checkNotNull(disks, "disks"));
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Instance#networkInterfaces()
    */
   public List<NetworkInterface> networkInterfaces() {
      return networkInterfaces;
   }

   public InstanceTemplate addNetworkInterface(URI network) {
      this.networkInterfaces.add(new NetworkInterface(network, null, null));
      return this;
   }

   public InstanceTemplate addNetworkInterface(URI network, Type type) {
      this.networkInterfaces
            .add(new NetworkInterface(network, null, ImmutableList.of(AccessConfig.create(null, type, null))));
      return this;
   }

   public InstanceTemplate addNetworkInterface(NetworkInterface networkInterface) {
      this.networkInterfaces.add(networkInterface);
      return this;
   }

   public InstanceTemplate networkInterfaces(List<NetworkInterface> networkInterfaces) {
      this.networkInterfaces = Lists.newArrayList(networkInterfaces);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Instance#metadata()
    */
   public Map<String, String> metadata() {
      return metadata;
   }

   public InstanceTemplate addMetadata(String key, String value) {
      this.metadata.put(checkNotNull(key, "key"), checkNotNull(value, "value of %", key));
      return this;
   }

   public InstanceTemplate metadata(Map<String, String> metadata) {
      this.metadata = Maps.newLinkedHashMap();
      this.metadata.putAll(checkNotNull(metadata, "metadata"));
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Instance#serviceAccounts()
    */
   public List<ServiceAccount> serviceAccounts() {
      return serviceAccounts;
   }

   public InstanceTemplate addServiceAccount(ServiceAccount serviceAccount) {
      this.serviceAccounts.add(checkNotNull(serviceAccount, "serviceAccount"));
      return this;
   }

   public InstanceTemplate serviceAccounts(List<ServiceAccount> serviceAccounts) {
      this.serviceAccounts = Lists.newArrayList();
      this.serviceAccounts.addAll(checkNotNull(serviceAccounts, "serviceAccounts"));
      return this;
   }

}
