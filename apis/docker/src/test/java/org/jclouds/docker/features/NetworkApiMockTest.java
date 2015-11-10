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
package org.jclouds.docker.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import java.util.Map;

import org.jclouds.docker.DockerApi;
import org.jclouds.docker.config.DockerParserModule;
import org.jclouds.docker.domain.Network;
import org.jclouds.docker.internal.BaseDockerMockTest;
import org.jclouds.docker.parse.NetworkParseTest;
import org.jclouds.docker.parse.NetworksParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link NetworkApi} class.
 */
@Test(groups = "unit", testName = "NetworkApiMockTest")
public class NetworkApiMockTest extends BaseDockerMockTest {

   public void testListNetworks() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/networks.json")));
      NetworkApi api = api(DockerApi.class, server.getUrl("/").toString()).getNetworkApi();
      try {
         assertEquals(api.listNetworks(), new NetworksParseTest().expected());
         assertSent(server, "GET", "/networks");
      } finally {
         server.shutdown();
      }
   }

   public void testListNonexistentNetworks() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = api(DockerApi.class, server.getUrl("/").toString()).getNetworkApi();
      try {
         assertEquals(api.listNetworks(), ImmutableList.of());
         assertSent(server, "GET", "/networks");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/network.json")));
      NetworkApi api = api(DockerApi.class, server.getUrl("/").toString(), new DockerParserModule()).getNetworkApi();
      String networkId = "b03d4cd15b76f8876110615cdeed15eadf77c9beb408d62f1687dcc69192cd6d";
      try {
         assertEquals(api.inspectNetwork(networkId), new NetworkParseTest().expected());
         assertSent(server, "GET", "/networks/" + networkId);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/network-creation.json")));
      NetworkApi api = api(DockerApi.class, server.getUrl("/").toString()).getNetworkApi();

      Map<String, String> options = ImmutableMap.<String, String> builder()
              .put("com.docker.network.bridge.default_bridge", "true")
              .put("com.docker.network.bridge.enable_icc", "true")
              .put("com.docker.network.bridge.enable_ip_masquerade", "true")
              .put("com.docker.network.bridge.host_binding_ipv4", "0.0.0.0")
              .put("com.docker.network.bridge.name", "docker0")
              .put("com.docker.network.driver.mtu", "1500")
              .build();

      Network network = Network.create(
              "isolated_nw", // Name
              null, // Id
              "bridge", // Driver
              null, // Scope
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
              options);

      try {
         Network created = api.createNetwork(network);
         assertNotNull(created);
         assertThat(created.id()).isEqualTo("22be93d5babb089c5aab8dbc369042fad48ff791584ca2da2100db837a1c7c30");
         assertSent(server, "POST", "/networks/create", "{\"Name\":\"isolated_nw\",\"Scope\":\"bridge\",\"IPAM\":{\"Driver\":\"default\",\"Config\":[{\"Subnet\":\"172.17.0.0/16\"}]},\"Containers\":{\"39b69226f9d79f5634485fb236a23b2fe4e96a0a94128390a7fbbcc167065867\":{\"EndpointID\":\"ed2419a97c1d9954d05b46e462e7002ea552f216e9b136b80a7db8d98b442eda\",\"MacAddress\":\"02:42:ac:11:00:02\",\"IPv4Address\":\"172.17.0.2/16\",\"IPv6Address\":\"\"}},\"Options\":{\"com.docker.network.bridge.default_bridge\":\"true\",\"com.docker.network.bridge.enable_icc\":\"true\",\"com.docker.network.bridge.enable_ip_masquerade\":\"true\",\"com.docker.network.bridge.host_binding_ipv4\":\"0.0.0.0\",\"com.docker.network.bridge.name\":\"docker0\",\"com.docker.network.driver.mtu\":\"1500\"}}");
      } finally {
         server.shutdown();
      }
   }

   public void testRemoveNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(204));
      NetworkApi api = api(DockerApi.class, server.getUrl("/").toString()).getNetworkApi();
      String networkId = "6d35806c1bd2b25cd92bba2d2c2c5169dc2156f53ab45c2b62d76e2d2fee14a9";
      try {
         api.removeNetwork(networkId);
         assertSent(server, "DELETE", "/networks/" + networkId);
      } finally {
         server.shutdown();
      }
   }

   public void testConnectContainerToNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(200));
      NetworkApi api = api(DockerApi.class, server.getUrl("/").toString()).getNetworkApi();
      try {
         api.connectContainerToNetwork("123456789", "containerName");
         assertSent(server, "POST", "/networks/123456789/connect", "{ \"Container\": \"containerName\" }");
      } finally {
         server.shutdown();
      }
   }

   public void testDisconnectContainerFromNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(200));
      NetworkApi api = api(DockerApi.class, server.getUrl("/").toString()).getNetworkApi();
      try {
         api.disconnectContainerFromNetwork("123456789", "containerName");
         assertSent(server, "POST", "/networks/123456789/disconnect", "{ \"Container\": \"containerName\" }");
      } finally {
         server.shutdown();
      }
   }

}
