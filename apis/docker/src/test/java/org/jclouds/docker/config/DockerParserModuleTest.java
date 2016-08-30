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
package org.jclouds.docker.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.NetworkSettings;
import org.jclouds.docker.domain.Port;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

/**
 * Unit tests for the {@link org.jclouds.docker.config.DockerParserModule} class.
 */
@Test(groups = "unit", testName = "DockerParserModuleTest")
public class DockerParserModuleTest {

   private Json json = Guice.createInjector(new GsonModule(), new DockerParserModule()).getInstance(Json.class);

   public void testContainerWithVolumesNull() {
      Container container = json.fromJson("{ \"Id\": \"foo\", \"Volumes\": null }", Container.class);
      assertNotNull(container);
      assertEquals(container.id(), "foo");
      assertEquals(container.volumes(), ImmutableMap.of());
   }

   public void port() {
      // Note IP, not Ip
      String text = "{\"IP\":\"0.0.0.0\",\"PrivatePort\":4567,\"PublicPort\":49155,\"Type\":\"tcp\"}";
      Port port = Port.create("0.0.0.0", 4567, 49155, "tcp");
      assertEquals(json.fromJson(text, Port.class), port);
      assertEquals(json.toJson(port), text);
   }

   public void networkSettings() {
      String text = "{" +
              "\"Bridge\":\"\"," +
              "\"SandboxID\":\"3ef128b055eb9ef62a6a2c281d97a2dfde5f47947d490f1dd2a81612611d961f\"," +
              "\"HairpinMode\":false," +
              "\"LinkLocalIPv6Address\":\"\"," +
              "\"LinkLocalIPv6PrefixLen\":0," +
              "\"Ports\":{}," +
              "\"SandboxKey\":\"/var/run/docker/netns/3ef128b055eb\"," +
              "\"SecondaryIPAddresses\":[]," +
              "\"SecondaryIPv6Addresses\":[]," +
              "\"EndpointID\":\"9e8dcc0c8288938a923018fee0728cee8e6de7c01a5150738ee6e51c1caf8cf6\"," +
              "\"Gateway\":\"172.17.0.1\"," +
              "\"GlobalIPv6Address\":\"\"," +
              "\"GlobalIPv6PrefixLen\":0," +
              "\"IPAddress\":\"172.17.0.2\"," +
              "\"IPPrefixLen\":16," +
              "\"IPv6Gateway\":\"\"," +
              "\"MacAddress\":\"02:42:ac:11:00:02\"," +
              "\"Networks\":{" +
              "\"bridge\":{" +
              "\"EndpointID\":\"9e8dcc0c8288938a923018fee0728cee8e6de7c01a5150738ee6e51c1caf8cf6\"," +
              "\"Gateway\":\"172.17.0.1\"," +
              "\"IPAddress\":\"172.17.0.2\"," +
              "\"IPPrefixLen\":16," +
              "\"IPv6Gateway\":\"\"," +
              "\"GlobalIPv6Address\":\"\"," +
              "\"GlobalIPv6PrefixLen\":0," +
              "\"MacAddress\":\"02:42:ac:11:00:02\"" +
              "}" +
              "}" +
              "}";
      NetworkSettings settings = NetworkSettings.create(
              "", // Bridge
              "3ef128b055eb9ef62a6a2c281d97a2dfde5f47947d490f1dd2a81612611d961f", // SandboxID
              false, // HairpinMode
              "", // LinkLocalIPv6Address
              0, // LinkLocalIPv6PrefixLen
              ImmutableMap.<String, List<Map<String, String>>> of(), // Ports
              "/var/run/docker/netns/3ef128b055eb", // SandboxKey
              null, // SecondaryIPAddresses
              null, // SecondaryIPv6Addresses
              "9e8dcc0c8288938a923018fee0728cee8e6de7c01a5150738ee6e51c1caf8cf6", // EndpointID
              "172.17.0.1", // Gateway
              "", // GlobalIPv6Address
              0, // GlobalIPv6PrefixLen
              "172.17.0.2", // IPAddress
              16, // IPPrefixLen
              "", // IPv6Gateway
              "02:42:ac:11:00:02", // MacAddress
              ImmutableMap.of(
                      "bridge", NetworkSettings.Details.create(
                              "9e8dcc0c8288938a923018fee0728cee8e6de7c01a5150738ee6e51c1caf8cf6", // EndpointID
                              "172.17.0.1", // Gateway
                              "172.17.0.2", // IPAddress
                              16, // IPPrefixLen
                              "", // IPv6Gateway
                              "", // GlobalIPv6Address
                              0, // GlobalIPv6PrefixLen
                              "02:42:ac:11:00:02" // MacAddress
                     )
              ),
              null // PortMapping
      );

      assertEquals(json.fromJson(text, NetworkSettings.class), settings);
      assertEquals(json.toJson(settings), text);
   }
}
