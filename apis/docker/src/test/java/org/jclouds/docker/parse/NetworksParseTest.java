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
package org.jclouds.docker.parse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.docker.domain.Network;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit")
public class NetworksParseTest extends BaseDockerParseTest<List<Network>> {

   @Override
   public String resource() {
      return "/networks.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public List<Network> expected() {
      return ImmutableList.of(

              Network.create(
                      "bridge", // Name
                      "f2de39df4171b0dc801e8002d1d999b77256983dfc63041c0f34030aa3977566", // Id
                      "local", // Scope
                      "bridge", // Driver
                      Network.IPAM.create(
                              "default", // driver
                              ImmutableList.of(Network.IPAM.Config.create("172.17.0.0/16", null, null)) // config
                      ),
                      ImmutableMap.of("39b69226f9d79f5634485fb236a23b2fe4e96a0a94128390a7fbbcc167065867",
                              Network.Details.create(
                                      "ed2419a97c1d9954d05b46e462e7002ea552f216e9b136b80a7db8d98b442eda", //endpointId
                                      "02:42:ac:11:00:02", // MAC
                                      "172.17.0.2/16", // ipv4address
                                      "" // ipv6address
                              )
                      ),
                      ImmutableMap.<String, String> builder()
                              .put("com.docker.network.bridge.default_bridge", "true")
                              .put("com.docker.network.bridge.enable_icc", "true")
                              .put("com.docker.network.bridge.enable_ip_masquerade", "true")
                              .put("com.docker.network.bridge.host_binding_ipv4", "0.0.0.0")
                              .put("com.docker.network.bridge.name", "docker0")
                              .put("com.docker.network.driver.mtu", "1500")
                              .build()
              ),
              Network.create(
                      "none", // Name
                      "e086a3893b05ab69242d3c44e49483a3bbbd3a26b46baa8f61ab797c1088d794", // Id
                      "local", // Scope
                      "null", // Driver
                      Network.IPAM.create(
                              "default", // driver
                              ImmutableList.<Network.IPAM.Config>of() // config
                      ),
                      ImmutableMap.<String, Network.Details> of(),
                      ImmutableMap.<String, String> of()
              ),
              Network.create(
                      "host", // Name
                      "13e871235c677f196c4e1ecebb9dc733b9b2d2ab589e30c539efeda84a24215e", // Id
                      "local", // Scope
                      "host", // Driver
                      Network.IPAM.create(
                              "default", // driver
                              ImmutableList.<Network.IPAM.Config>of() // config
                      ),
                      ImmutableMap.<String, Network.Details> of(),
                      ImmutableMap.<String, String> of()
              )
      );
   }

}
