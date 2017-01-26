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
package org.jclouds.packet.compute;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.compute.options.PacketTemplateOptions;
import org.jclouds.packet.domain.BillingCycle;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.Facility;
import org.jclouds.packet.domain.OperatingSystem;
import org.jclouds.packet.domain.Plan;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;

/**
 * defines the connection between the {@link org.jclouds.packet.PacketApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 */
@Singleton
public class PacketComputeServiceAdapter implements ComputeServiceAdapter<Device, Plan, OperatingSystem, Facility> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final PacketApi api;
   private final String projectId;

   @Inject
   PacketComputeServiceAdapter(PacketApi api, @Provider final Supplier<Credentials> creds) {
      this.api = api;
      this.projectId = creds.get().identity;
   }

   @Override
   public NodeAndInitialCredentials<Device> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {

      PacketTemplateOptions templateOptions = template.getOptions().as(PacketTemplateOptions.class);
      Map<String, String> features = templateOptions.getFeatures();
      BillingCycle billingCycle = BillingCycle.fromValue(templateOptions.getBillingCycle());
      boolean locked = templateOptions.isLocked();
      String userdata = templateOptions.getUserData();
      Set<String> tags = templateOptions.getTags();

      String plan = template.getHardware().getId();
      String facility = template.getLocation().getId();
      String operatingSystem = template.getImage().getId();

      Device device = api.deviceApi(projectId).create(
              Device.CreateDevice.builder()
                      .hostname(name)
                      .plan(plan)
                      .billingCycle(billingCycle.value())
                      .facility(facility)
                      .features(features)
                      .operatingSystem(operatingSystem)
                      .locked(locked)
                      .userdata(userdata)
                      .tags(tags)
                      .build()
              );

      // Any new servers you deploy to projects you are a collaborator on will have your project and personal SSH keys, if defined.
      // If no SSH keys are defined in your account, jclouds will generate one usiing CreateSshKeysThenCreateNodes 
      // so that it will add it to the device with the default mechanism.

      // Safe to pass null credentials here, as jclouds will default populate
      // the node with the default credentials from the image, or the ones in
      // the options, if provided.
      return new NodeAndInitialCredentials<Device>(device, device.id(), null);
   }

   @Override
   public Iterable<Plan> listHardwareProfiles() {
      return Iterables.filter(api.planApi().list().concat(), new Predicate<Plan>() {
         @Override
         public boolean apply(Plan input) {
            return input.line().equals("baremetal");
         }
      });
   }

   @Override
   public Iterable<OperatingSystem> listImages() {
      return api.operatingSystemApi().list().concat();
   }

   @Override
   public OperatingSystem getImage(final String id) {
      Optional<OperatingSystem> firstInterestingOperatingSystem = api
              .operatingSystemApi().list()
              .concat()
              .firstMatch(new Predicate<OperatingSystem>() {
                 @Override
                 public boolean apply(OperatingSystem input) {
                    return input.slug().equals(id);
                 }
              });
      if (!firstInterestingOperatingSystem.isPresent()) {
         throw new IllegalStateException("Cannot find image with the required slug " + id);
      }
      return firstInterestingOperatingSystem.get();
   }

   @Override
   public Iterable<Facility> listLocations() {
      return api.facilityApi().list().concat();
   }

   @Override
   public Device getNode(String id) {
      return api.deviceApi(projectId).get(id);
   }

   @Override
   public void destroyNode(String id) {
      api.deviceApi(projectId).delete(id);
   }

   @Override
   public void rebootNode(String id) {
      api.deviceApi(projectId).reboot(id);
   }

   @Override
   public void resumeNode(String id) {
      api.deviceApi(projectId).powerOn(id);
   }

   @Override
   public void suspendNode(String id) {
      api.deviceApi(projectId).powerOff(id);
   }

   @Override
   public Iterable<Device> listNodes() {
     return api.deviceApi(projectId).list().concat();
   }

   @Override
   public Iterable<Device> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<Device>() {
         @Override
         public boolean apply(Device device) {
            return contains(ids, String.valueOf(device.id()));
         }
      });
   }

}
