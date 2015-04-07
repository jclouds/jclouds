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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.softlayer.domain.Address;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.Region;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class DatacenterParseTest extends BaseSoftLayerParseTest<Datacenter> {

   @Override
   public String resource() {
      return "/datacenter_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Datacenter expected() {
      return Datacenter.builder()
              .id(265592)
              .longName("Amsterdam 1")
              .name("ams01")
              .locationAddress(Address.builder()
                      .address("Paul van Vlissingenstraat 16")
                      .accountId(1)
                      .city("Amsterdam")
                      .contactName("SoftLayer")
                      .country("NL")
                      .description("Amsterdam - AMS01")
                      .id(3322)
                      .isActive(1)
                      .locationId(265592)
                      .postalCode("1096 BK")
                      .build())
              .regions(ImmutableSet.of(Region.builder().keyname("AMSTERDAM")
                              .description("AMS01 - Amsterdam - Western Europe")
                              .build()))
              .build();
   }
}
