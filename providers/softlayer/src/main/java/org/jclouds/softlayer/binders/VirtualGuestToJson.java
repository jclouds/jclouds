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
package org.jclouds.softlayer.binders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;
import org.jclouds.softlayer.domain.VirtualGuestNetworkComponent;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Converts a VirtualGuest into a json string valid for creating a CCI via softlayer api
 * The string is set into the payload of the HttpRequest
 * 
 */
public class VirtualGuestToJson implements Binder {

   private final Json json;

   @Inject
   public VirtualGuestToJson(Json json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof VirtualGuest);
      VirtualGuest virtualGuest = VirtualGuest.class.cast(checkNotNull(input, "input"));
      request.setPayload(buildJson(virtualGuest));
      return request;
   }

   /**
    * Builds a Json string suitable for sending to the softlayer api
    *
    * @param virtualGuest
    * @return String
    */
   String buildJson(VirtualGuest virtualGuest) {
      TemplateObject templateObject = null;
      String hostname = checkNotNull(virtualGuest.getHostname(), "hostname");
      String domain = checkNotNull(virtualGuest.getDomain(), "domain");
      int startCpus = checkNotNull(virtualGuest.getStartCpus(), "startCpus");
      int maxMemory = checkNotNull(virtualGuest.getMaxMemory(), "maxMemory");
      boolean localDiskFlag = checkNotNull(virtualGuest.isLocalDiskFlag(), "localDiskFlag");
      String datacenterName = checkNotNull(virtualGuest.getDatacenter().getName(), "datacenterName");
      Set<NetworkComponent> networkComponents = getNetworkComponents(virtualGuest);
      if (virtualGuest.getOperatingSystem() != null) {
         String operatingSystemReferenceCode = checkNotNull(virtualGuest.getOperatingSystem()
                 .getOperatingSystemReferenceCode(), "operatingSystemReferenceCode");
         templateObject = new TemplateObject(hostname, domain, startCpus, maxMemory, true,
                 operatingSystemReferenceCode, null, localDiskFlag, new Datacenter(datacenterName), networkComponents,
                 getBlockDevices(virtualGuest));
      } else if(virtualGuest.getVirtualGuestBlockDeviceTemplateGroup() != null) {
         String globalIdentifier = checkNotNull(virtualGuest.getVirtualGuestBlockDeviceTemplateGroup()
                 .getGlobalIdentifier(), "blockDeviceTemplateGroup.globalIdentifier");
         templateObject = new TemplateObject(hostname, domain, startCpus, maxMemory, true, null,
                 new BlockDeviceTemplateGroup(globalIdentifier), localDiskFlag, new Datacenter(datacenterName),
                 networkComponents, null);
      }
      return json.toJson(ImmutableMap.of("parameters", ImmutableList.of(templateObject)));
   }

   private Set<BlockDevice> getBlockDevices(VirtualGuest virtualGuest) {
      if (virtualGuest.getVirtualGuestBlockDevices() == null) {
         return null;
      }
      ImmutableSortedSet.Builder<BlockDevice> blockDevices = ImmutableSortedSet.orderedBy(new BlockDevicesComparator());
      for (VirtualGuestBlockDevice blockDevice : virtualGuest.getVirtualGuestBlockDevices()) {
         blockDevices.add(new BlockDevice(blockDevice.getDevice(), blockDevice.getVirtualDiskImage().getCapacity()));
      }
      return blockDevices.build();
   }

   private Set<NetworkComponent> getNetworkComponents(VirtualGuest virtualGuest) {
      if (virtualGuest.getVirtualGuestNetworkComponents() == null) {
         return null;
      }
      ImmutableSet.Builder networkComponents = ImmutableSet.builder();
      for (VirtualGuestNetworkComponent networkComponent : virtualGuest.getVirtualGuestNetworkComponents()) {
         networkComponents.add(new NetworkComponent(networkComponent.getSpeed()));
      }
      return networkComponents.build();
   }

   private static class TemplateObject {
      private final String hostname;
      private final String domain;
      private final int startCpus;
      private final int maxMemory;
      private final boolean hourlyBillingFlag;
      private final BlockDeviceTemplateGroup blockDeviceTemplateGroup;
      private final String operatingSystemReferenceCode;
      private final boolean localDiskFlag;
      private final Datacenter datacenter;
      private final Set<NetworkComponent> networkComponents;
      private final Set<BlockDevice> blockDevices;

      private TemplateObject(String hostname, String domain, int startCpus, int maxMemory, boolean hourlyBillingFlag,
                         String operatingSystemReferenceCode, BlockDeviceTemplateGroup blockDeviceTemplateGroup,
                         boolean localDiskFlag, Datacenter datacenter, Set<NetworkComponent> networkComponents,
                         Set<BlockDevice> blockDevices) {
         this.hostname = hostname;
         this.domain = domain;
         this.startCpus = startCpus;
         this.maxMemory = maxMemory;
         this.hourlyBillingFlag = hourlyBillingFlag;
         this.operatingSystemReferenceCode = operatingSystemReferenceCode;
         this.blockDeviceTemplateGroup = blockDeviceTemplateGroup;
         this.localDiskFlag = localDiskFlag;
         this.datacenter = datacenter;
         this.networkComponents = networkComponents;
         this.blockDevices = blockDevices;
      }
   }

   private class Datacenter {
      private String name;

      private Datacenter(String name) {
         this.name = name;
      }
   }

   private class NetworkComponent {
      private int maxSpeed;

      private NetworkComponent(int maxSpeed) {
         this.maxSpeed = maxSpeed;
      }
   }

   private class BlockDevice {
      private String device;
      private DiskImage diskImage;

      public String getDevice() {
         return device;
      }

      public DiskImage getDiskImage() {
         return diskImage;
      }

      private BlockDevice(String device, float diskImageCapacity) {
         this.device = device;
         this.diskImage = new DiskImage(diskImageCapacity);
      }
   }

   private class DiskImage {
      private float capacity;

      private DiskImage(float capacity) {
         this.capacity = capacity;
      }
   }

   private class BlockDeviceTemplateGroup {
      private String globalIdentifier;

      private BlockDeviceTemplateGroup(String globalIdentifier) {
         this.globalIdentifier = globalIdentifier;
      }
   }

   private class BlockDevicesComparator implements Comparator<BlockDevice> {

         @Override
         public int compare(BlockDevice b1, BlockDevice b2) {
            return Integer.valueOf(b1.getDevice()).compareTo(Integer.valueOf(b2.getDevice()));
         }
   }

}
