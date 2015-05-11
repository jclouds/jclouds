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
package org.jclouds.softlayer.domain;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@Test(groups = "unit")
public class ContainerVirtualGuestConfigurationTest {
   @Test
   public void testGetVirtualGuestOperatingSystemsWithMissingReferenceCode() {
      // Malformed response leading to failure:
      // {
      //    "itemPrice": {
      //        "hourlyRecurringFee": ".131",
      //        "item": {
      //            "description": "Windows Server 2012 Datacenter Edition (64bit)"
      //        },
      //        "recurringFee": "90.63"
      //    },
      //    "template": {
      //        "id": null
      //        missing operatingSystemReferenceCode entry!
      //    }
      // },

      ContainerVirtualGuestConfigurationOption osValid = ContainerVirtualGuestConfigurationOption.builder()
            .productItemPrice(ProductItemPrice.builder()
                    .hourlyRecurringFee(0)
                    .recurringFee("0")
                    .item(ProductItem.builder().description("CentOS - Latest").build())
                    .build())
            .template(VirtualGuest.builder().operatingSystemReferenceCode("CENTOS_LATEST").build())
            .build();
      ContainerVirtualGuestConfigurationOption osInvalid = ContainerVirtualGuestConfigurationOption.builder()
            .productItemPrice(ProductItemPrice.builder()
                    .hourlyRecurringFee(0.131f)
                    .recurringFee("90.63")
                    .item(ProductItem.builder().description("Windows Server 2012 Datacenter Edition (64bit)").build())
                    .build())
            .template(VirtualGuest.builder().build())
            .build();
      ContainerVirtualGuestConfiguration conf = ContainerVirtualGuestConfiguration.builder()
         .blockDevices(ImmutableSet.<ContainerVirtualGuestConfigurationOption>of())
         .datacenters(ImmutableSet.<ContainerVirtualGuestConfigurationOption>of())
         .memory(ImmutableSet.<ContainerVirtualGuestConfigurationOption>of())
         .networkComponents(ImmutableSet.<ContainerVirtualGuestConfigurationOption>of())
         .operatingSystems(ImmutableSet.<ContainerVirtualGuestConfigurationOption>of(osValid, osInvalid))
         .processors(ImmutableSet.<ContainerVirtualGuestConfigurationOption>of())
         .build();
      Set<OperatingSystem> virtualGuestOperatingSystems = conf.getVirtualGuestOperatingSystems();
      OperatingSystem os = Iterables.getOnlyElement(virtualGuestOperatingSystems);
      assertEquals(os.getOperatingSystemReferenceCode(), osValid.getTemplate().getOperatingSystemReferenceCode());
   }
}
