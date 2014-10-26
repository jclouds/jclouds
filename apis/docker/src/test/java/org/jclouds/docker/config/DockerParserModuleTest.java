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
      String text = "{\"IPAddress\":\"XX.XX.206.98\",\"IPPrefixLen\":27,\"Gateway\":\"XX.XX.206.105\",\"Bridge\":\"public\",\"Ports\":{}}";
      NetworkSettings settings = NetworkSettings.create("XX.XX.206.98", 27, "XX.XX.206.105", "public", null, null);
      assertEquals(json.fromJson(text, NetworkSettings.class), settings);
      assertEquals(json.toJson(settings), text);
   }
}
