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

import org.jclouds.softlayer.domain.NetworkVlan;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestNetworkComponent;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class VirtualGuestFilteredParseTest extends BaseSoftLayerParseTest<VirtualGuest> {

   @Override
   public String resource() {
      return "/virtual_guest_get_filtered.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public VirtualGuest expected() {
      return VirtualGuest.builder()
                              .id(3001812)
                              .primaryBackendNetworkComponent(VirtualGuestNetworkComponent.builder()
                                      .networkId(123456)
                                      .networkVlan(NetworkVlan.builder()
                                              .id(1234)
                                              .name("abc")
                                              .build())
                                      .build())
                              .build();
   }
}
