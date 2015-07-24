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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.SecuritySshKey;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;
import org.jclouds.softlayer.domain.VirtualGuestNetworkComponent;
import org.jclouds.softlayer.domain.internal.BlockDevice;
import org.jclouds.softlayer.domain.internal.BlockDeviceTemplateGroup;
import org.jclouds.softlayer.domain.internal.Datacenter;
import org.jclouds.softlayer.domain.internal.NetworkComponent;
import org.jclouds.softlayer.domain.internal.NetworkVlan;
import org.jclouds.softlayer.domain.internal.PrimaryBackendNetworkComponent;
import org.jclouds.softlayer.domain.internal.PrimaryNetworkComponent;
import org.jclouds.softlayer.domain.internal.TemplateObject;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Converts a VirtualGuest into a json string valid for creating a CCI via softlayer api
 * The string is set into the payload of the HttpRequest
 *
 */
public class VirtualGuestToJson implements Binder {

   public static final String USER_DATA_KEY = "value";
   private static final String SSH_KEY_ID = "id";
   protected Json json;

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
      TemplateObject.Builder templateObjectBuilder = TemplateObject.builder();
      String hostname = checkNotNull(virtualGuest.getHostname(), "hostname");
      String domain = checkNotNull(virtualGuest.getDomain(), "domain");
      int startCpus = checkNotNull(virtualGuest.getStartCpus(), "startCpus");
      int maxMemory = checkNotNull(virtualGuest.getMaxMemory(), "maxMemory");
      boolean hourlyBillingFlag = virtualGuest.isHourlyBillingFlag();
      boolean localDisk = virtualGuest.isLocalDiskFlag();

      String datacenterName = checkNotNull(virtualGuest.getDatacenter().getName(), "datacenterName");
      Set<VirtualGuestNetworkComponent> virtualGuestNetworkComponents = virtualGuest.getVirtualGuestNetworkComponents();
      Set<NetworkComponent> networkComponents = Sets.newHashSet();
      if (virtualGuestNetworkComponents != null) {
         networkComponents = FluentIterable.from(virtualGuestNetworkComponents)
                 .transform(new Function<VirtualGuestNetworkComponent, NetworkComponent>() {
                    @Override
                    public NetworkComponent apply(VirtualGuestNetworkComponent virtualGuestNetworkComponent) {
                       return new NetworkComponent(virtualGuestNetworkComponent.getSpeed());
                    }
                 }).toSet();
      }

      templateObjectBuilder.hostname(hostname)
                           .domain(domain)
                           .startCpus(startCpus)
                           .maxMemory(maxMemory)
                           .hourlyBillingFlag(hourlyBillingFlag)
                           .localDiskFlag(localDisk)
                           .dedicatedAccountHostOnlyFlag(virtualGuest.isDedicatedAccountHostOnly())
                           .privateNetworkOnlyFlag(virtualGuest.isPrivateNetworkOnly())
                           .datacenter(new Datacenter(datacenterName));

      if (!networkComponents.isEmpty()) {
         templateObjectBuilder.networkComponents(networkComponents);
      }
      if (virtualGuest.getOperatingSystem() != null) {
         String operatingSystemReferenceCode = checkNotNull(virtualGuest.getOperatingSystem()
                 .getOperatingSystemReferenceCode(), "operatingSystemReferenceCode");
         templateObjectBuilder.operatingSystemReferenceCode(operatingSystemReferenceCode)
                              .blockDevices(getBlockDevices(virtualGuest));
      } else if (virtualGuest.getVirtualGuestBlockDeviceTemplateGroup() != null) {
         String globalIdentifier = checkNotNull(virtualGuest.getVirtualGuestBlockDeviceTemplateGroup()
                 .getGlobalIdentifier(), "blockDeviceTemplateGroup.globalIdentifier");
         templateObjectBuilder.blockDeviceTemplateGroup(new BlockDeviceTemplateGroup(globalIdentifier));
      }

      if (virtualGuest.getPrimaryNetworkComponent() != null) {
         templateObjectBuilder.primaryNetworkComponent(new PrimaryNetworkComponent(new NetworkVlan(virtualGuest
                 .getPrimaryNetworkComponent().getNetworkVlan().getId())));
      }

      if (virtualGuest.getPrimaryBackendNetworkComponent() != null) {
         templateObjectBuilder.primaryBackendNetworkComponent(new PrimaryBackendNetworkComponent(new NetworkVlan(virtualGuest
                 .getPrimaryBackendNetworkComponent().getNetworkVlan().getId())));
      }

      if (virtualGuest.getPostInstallScriptUri() != null) {
         templateObjectBuilder.postInstallScriptUri(virtualGuest.getPostInstallScriptUri());
      }

      if (virtualGuest.getVirtualGuestAttribute() != null) {
         templateObjectBuilder.userData(ImmutableSet.<Map<String, String>>of(ImmutableMap.of(USER_DATA_KEY,
                 virtualGuest.getVirtualGuestAttribute().getValue())));
      }

      if (virtualGuest.getSshKeys() != null) {
         Set<Map<String, Integer>> sshKeys = Sets.newHashSet();
         for (SecuritySshKey securitySshKey : virtualGuest.getSshKeys()) {
            sshKeys.add(ImmutableMap.of(SSH_KEY_ID, securitySshKey.getId()));
         }
         templateObjectBuilder.sshKeys(sshKeys);
      }

      return json.toJson(ImmutableMap.of("parameters", ImmutableList.of(templateObjectBuilder.build())));
   }

   private List<BlockDevice> getBlockDevices(VirtualGuest virtualGuest) {
      if (virtualGuest.getVirtualGuestBlockDevices() == null) {
         return null;
      }
      List<BlockDevice> blockDevices = Lists.newArrayList();
      for (VirtualGuestBlockDevice blockDevice : virtualGuest.getVirtualGuestBlockDevices()) {
         blockDevices.add(new BlockDevice(blockDevice.getDevice(), blockDevice.getVirtualDiskImage().getCapacity()));
      }
      Collections.sort(blockDevices, new BlockDevicesComparator());
      return ImmutableList.copyOf(blockDevices);
   }

   public class BlockDevicesComparator implements Comparator<BlockDevice> {

      @Override
      public int compare(BlockDevice b1, BlockDevice b2) {
         return Integer.valueOf(b1.getDevice()).compareTo(Integer.valueOf(b2.getDevice()));
      }
   }

}
