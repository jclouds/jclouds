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
package org.jclouds.docker.compute.options;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Config.Builder;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Unit tests for the {@link DockerTemplateOptions} class.
 */
@Test(groups = "unit", testName = "DockerTemplateOptionsTest")
public class DockerTemplateOptionsTest {

   @Test
   public void testHostname() {
      TemplateOptions options = DockerTemplateOptions.Builder.hostname("hostname");
      assertEquals(options.as(DockerTemplateOptions.class).getHostname(), "hostname");
   }

   @Test
   public void testMemory() {
      TemplateOptions options = DockerTemplateOptions.Builder.memory(1024);
      assertEquals(options.as(DockerTemplateOptions.class).getMemory(), Integer.valueOf(1024));
   }

   @Test
   public void testCpuShares() {
      TemplateOptions options = DockerTemplateOptions.Builder.cpuShares(2);
      assertEquals(options.as(DockerTemplateOptions.class).getCpuShares(), Integer.valueOf(2));
   }

   @Test
   public void testVolumes() {
      TemplateOptions options = DockerTemplateOptions.Builder.volumes(ImmutableMap.of("/tmp", "/tmp"));
      assertEquals(options.as(DockerTemplateOptions.class).getVolumes(), ImmutableMap.of("/tmp", "/tmp"));
   }

   @Test
   public void testDns() {
      TemplateOptions options = DockerTemplateOptions.Builder.dns("8.8.8.8", "8.8.4.4");
      assertEquals(options.as(DockerTemplateOptions.class).getDns(), ImmutableList.of("8.8.8.8", "8.8.4.4"));
   }

   @Test
   public void testEntrypoint() {
      TemplateOptions options = DockerTemplateOptions.Builder.entrypoint("/bin/sh", "-c");
      assertEquals(options.as(DockerTemplateOptions.class).getEntrypoint(), ImmutableList.of("/bin/sh", "-c"));
   }

   @Test
   public void testCommands() {
      TemplateOptions options = DockerTemplateOptions.Builder.commands("chmod 666 /etc/*", "rm -rf /var/run");
      assertEquals(options.as(DockerTemplateOptions.class).getCommands(), ImmutableList.of("chmod 666 /etc/*", "rm -rf /var/run"));
   }

   @Test
   public void testEnv() {
      TemplateOptions options = DockerTemplateOptions.Builder.env(ImmutableList.of("HOST=abc", "PORT=1234"));
      assertEquals(options.as(DockerTemplateOptions.class).getEnv(), ImmutableList.of("HOST=abc", "PORT=1234"));
   }

   @Test
   public void testPortBindings() {
      TemplateOptions options = DockerTemplateOptions.Builder.portBindings(ImmutableMap.<Integer, Integer>builder().put(8443,  443).put(8080, 80).build());
      assertEquals(options.as(DockerTemplateOptions.class).getPortBindings(), ImmutableMap.<Integer, Integer>builder().put(8443,  443).put(8080, 80).build());
   }


   @Test
   public void testNetworkMode() {
      TemplateOptions options = DockerTemplateOptions.Builder.networkMode("host");
      assertEquals(options.as(DockerTemplateOptions.class).getNetworkMode(), "host");
   }

   @Test
   public void testPrivilegedDefaultFalse() {
      TemplateOptions options = DockerTemplateOptions.Builder.memory(2);
      assertEquals(options.as(DockerTemplateOptions.class).getPrivileged(), false);
   }

   @Test
   public void testPrivileged() {
      TemplateOptions options = DockerTemplateOptions.Builder.privileged(true);
      assertEquals(options.as(DockerTemplateOptions.class).getPrivileged(), true);
   }

   @Test
   public void testConfigBuilder() {
      Builder builder = Config.builder().memory(1024)
            .cpuShares(100).cmd(ImmutableList.<String> of("/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"))
            .env(ImmutableList.<String> of("JAVA_HOME=/opt/jdk-1.8", "MGMT_USER=admin",
                  "MGMT_PASSWORD=Schroedinger's Cat"));
      TemplateOptions options = DockerTemplateOptions.Builder.configBuilder(builder);
      Builder builderInOpts = options.as(DockerTemplateOptions.class).getConfigBuilder();
      assertNotNull(builderInOpts);
      Config configFromOptions = builderInOpts.build();
      assertEquals(configFromOptions, builder.build());
      assertEquals(configFromOptions.env(), ImmutableList.<String> of("JAVA_HOME=/opt/jdk-1.8", "MGMT_USER=admin",
                  "MGMT_PASSWORD=Schroedinger's Cat"));
   }

   @Test
   public void testNonDockerOptions() {
      TemplateOptions options = DockerTemplateOptions.Builder.userMetadata(ImmutableMap.of("key", "value")).cpuShares(1);
      assertEquals(options.as(DockerTemplateOptions.class).getUserMetadata(), ImmutableMap.of("key", "value"));
      assertEquals(options.as(DockerTemplateOptions.class).getCpuShares(), Integer.valueOf(1));
   }

   @Test
   public void testMultipleOptions() {
      TemplateOptions options = DockerTemplateOptions.Builder.memory(512).cpuShares(4);
      assertEquals(options.as(DockerTemplateOptions.class).getMemory(), Integer.valueOf(512));
      assertEquals(options.as(DockerTemplateOptions.class).getCpuShares(), Integer.valueOf(4));
   }

   @Test
   public void testCopyTo() {
      DockerTemplateOptions options = DockerTemplateOptions.Builder
            .memory(512)
            .cpuShares(4)
            .entrypoint("entry", "point")
            .commands("test", "abc")
            .portBindings(
                  ImmutableMap.<Integer, Integer> builder()
                        .put(8443, 443).build())
            .hostname("hostname")
            .networkMode("host")
            .userMetadata(ImmutableMap.of("key", "value"))
            .env(ImmutableList.of("HOST=abc", "PORT=1234"))
            .dns("8.8.8.8", "8.8.4.4")
            .volumes(ImmutableMap.of("/tmp", "/tmp"));
      DockerTemplateOptions optionsCopy = new DockerTemplateOptions();
      options.copyTo(optionsCopy);
      assertEquals(optionsCopy, options);
   }

}
