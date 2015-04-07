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
package org.jclouds.softlayer.parse;

import org.jclouds.softlayer.domain.ContainerVirtualGuestConfiguration;
import org.jclouds.softlayer.domain.ContainerVirtualGuestConfigurationOption;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.VirtualDiskImage;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;
import org.jclouds.softlayer.domain.VirtualGuestNetworkComponent;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

public class GetCreateObjectOptionsParseTest extends BaseSoftLayerParseTest<ContainerVirtualGuestConfiguration> {

   @Override
   public String resource() {
      return "/container_virtual_guest_configuration.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ContainerVirtualGuestConfiguration expected() {
      return ContainerVirtualGuestConfiguration.builder()
              .blockDevices(ContainerVirtualGuestConfigurationOption.builder()
                      .productItemPrice(ProductItemPrice.builder()
                              .hourlyRecurringFee(0)
                              .recurringFee("0")
                              .item(ProductItem.builder().description("25 GB (SAN)").build())
                              .build())
                      .template(VirtualGuest.builder()
                              .blockDevices(VirtualGuestBlockDevice.builder()
                                      .device("0")
                                      .diskImage(VirtualDiskImage.builder().capacity(25).build())
                                      .build())
                              .localDiskFlag(false)
                              .build())
                      .build())
              .datacenters(ContainerVirtualGuestConfigurationOption.builder()
                      .template(VirtualGuest.builder().datacenter(Datacenter.builder().name("ams01").build()).build())
                      .build())
              .memory(ContainerVirtualGuestConfigurationOption.builder()
                      .productItemPrice(ProductItemPrice.builder()
                              .hourlyRecurringFee(.02f)
                              .recurringFee("14")
                              .item(ProductItem.builder().description("1 GB").build())
                              .build())
                      .template(VirtualGuest.builder().maxMemory(1024).build())
                      .build())
              .networkComponents(ContainerVirtualGuestConfigurationOption.builder()
                      .productItemPrice(ProductItemPrice.builder()
                              .hourlyRecurringFee(0)
                              .recurringFee("0")
                              .item(ProductItem.builder().description("10 Mbps Public & Private Networks").build())
                              .build())
                      .template(VirtualGuest.builder().networkComponents(
                              VirtualGuestNetworkComponent.builder()
                                      .maxSpeed(10)
                                      .build())
                              .build())
                      .build())
              .operatingSystems(ContainerVirtualGuestConfigurationOption.builder()
                      .productItemPrice(ProductItemPrice.builder()
                              .hourlyRecurringFee(0)
                              .recurringFee("0")
                              .item(ProductItem.builder().description("CentOS 6.x - Minimal Install (64 bit)").build())
                              .build())
                      .template(VirtualGuest.builder()
                                              .operatingSystemReferenceCode("CENTOS_6_64")
                                              .build())
                      .build())
              .processors(ContainerVirtualGuestConfigurationOption.builder()
                      .productItemPrice(ProductItemPrice.builder()
                              .hourlyRecurringFee(.022f)
                              .recurringFee("15")
                              .item(ProductItem.builder().description("1 x 2.0 GHz Core").build())
                              .build())
                      .template(VirtualGuest.builder().startCpus(1).build())
                      .build())
              .build();
   }

}
