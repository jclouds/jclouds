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
package org.jclouds.docker.compute.functions;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.HostConfig;
import org.jclouds.docker.domain.NetworkSettings;
import org.jclouds.docker.domain.Port;
import org.jclouds.docker.domain.State;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * Unit tests for the {@link org.jclouds.docker.compute.functions.ContainerToNodeMetadata} class.
 */
@Test(groups = "unit", testName = "ContainerToNodeMetadataTest")
public class ContainerToNodeMetadataTest {

   private LoginCredentials credentials;

   private ContainerToNodeMetadata function;

   private Container container;

   @BeforeMethod
   public void setup() {
      Config containerConfig = Config.builder()
              .hostname("6d35806c1bd2")
              .domainname("")
              .user("")
              .memory(0)
              .memorySwap(0)
              .cpuShares(0)
              .attachStdin(false)
              .attachStdout(false)
              .attachStderr(false)
              .exposedPorts(ImmutableMap.of("22/tcp", ImmutableMap.of()))
              .tty(false)
              .openStdin(false)
              .stdinOnce(false)
              .env(null)
              .cmd(ImmutableList.of("/usr/sbin/sshd", "-D"))
              .image("jclouds/ubuntu")
              .volumesFrom("")
              .workingDir("")
              .entrypoint(null)
              .networkDisabled(false)
              .build();
      State state = State.create( //
            3626, // pid
            true, // running
            0, // exitCode
            "2014-03-24T20:28:37.537659054Z", // startedAt
            "0001-01-01T00:00:00Z", // finishedAt
            false, // paused
            false  // restarting
      );
      container = Container.builder()
              .id("6d35806c1bd2b25cd92bba2d2c2c5169dc2156f53ab45c2b62d76e2d2fee14a9")
              .name("/hopeful_mclean")
              .created(new SimpleDateFormatDateService().iso8601DateParse("2014-03-22T07:16:45.784120972Z"))
              .path("/usr/sbin/sshd")
              .args(Arrays.asList("-D"))
              .config(containerConfig)
              .state(state)
              .image("af0f59f1c19eef9471c3b8c8d587c39b8f130560b54f3766931b37d76d5de4b6")
              .networkSettings(NetworkSettings.builder()
                      .ipAddress("172.17.0.2")
                      .ipPrefixLen(16)
                      .gateway("172.17.42.1")
                      .bridge("docker0")
                      .ports(ImmutableMap.<String, List<Map<String, String>>>of("22/tcp",
                              ImmutableList.<Map<String, String>>of(ImmutableMap.of("HostIp", "0.0.0.0", "HostPort",
                                      "49199"))))
                      .build())
              .resolvConfPath("/etc/resolv.conf")
              .driver("aufs")
              .execDriver("native-0.1")
              .volumes(ImmutableMap.<String, String>of())
              .volumesRW(ImmutableMap.<String, Boolean>of())
              .command("")
              .status("")
              .hostConfig(HostConfig.builder().publishAllPorts(true).build())
              .ports(ImmutableList.<Port>of())
              .build();
      ProviderMetadata providerMetadata = EasyMock.createMock(ProviderMetadata.class);
      expect(providerMetadata.getEndpoint()).andReturn("http://127.0.0.1:4243");
      replay(providerMetadata);

      GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

      Supplier<Map<String, ? extends Image>> images = new Supplier<Map<String, ? extends Image>>() {
         @Override
         public Map<String, ? extends Image> get() {
            OperatingSystem os = OperatingSystem.builder()
                    .description("Ubuntu 12.04 64bit")
                    .family(OsFamily.UBUNTU)
                    .version("12.04")
                    .is64Bit(true)
                    .build();

            return ImmutableMap.of("af0f59f1c19eef9471c3b8c8d587c39b8f130560b54f3766931b37d76d5de4b6",
                    new ImageBuilder()
                            .ids("af0f59f1c19eef9471c3b8c8d587c39b8f130560b54f3766931b37d76d5de4b6")
                            .name("ubuntu")
                            .description("Ubuntu 12.04 64bit")
                            .operatingSystem(os)
                            .status(Image.Status.AVAILABLE)
                            .build());
         }
      };

      Supplier<Set<? extends Location>> locations = new Supplier<Set< ? extends Location>>() {
         @Override
         public Set<? extends Location> get() {

            return ImmutableSet.of(
                    new LocationBuilder()
                            .id("docker")
                            .description("http://localhost:2375")
                            .scope(LocationScope.PROVIDER)
                            .build()
            );
         }
      };

      credentials = LoginCredentials.builder().user("foo").password("bar").build();

      function = new ContainerToNodeMetadata(providerMetadata, toPortableStatus(), namingConvention, images, locations);
   }

   private Function<State, NodeMetadata.Status> toPortableStatus() {
      StateToStatus function = EasyMock.createMock(StateToStatus.class);
         expect(function.apply(anyObject(State.class))).andReturn(NodeMetadata.Status.RUNNING);
         replay(function);
         return function;
   }

   public void testVirtualMachineToNodeMetadata() {
      Container mockContainer = mockContainer();

      NodeMetadata node = function.apply(mockContainer);

      verify(mockContainer);

      assertEquals(node.getId(), "6d35806c1bd2b25cd92bba2d2c2c5169dc2156f53ab45c2b62d76e2d2fee14a9");
      assertEquals(node.getGroup(), "hopeful_mclean");
      assertEquals(node.getImageId(), "af0f59f1c19eef9471c3b8c8d587c39b8f130560b54f3766931b37d76d5de4b6");
      assertEquals(node.getLoginPort(), 49199);
      assertEquals(node.getPrivateAddresses().size(), 1);
      assertEquals(node.getPublicAddresses().size(), 1);
   }

   private Container mockContainer() {
      Container mockContainer = EasyMock.createMock(Container.class);

      expect(mockContainer.id()).andReturn(container.id());
      expect(mockContainer.name()).andReturn(container.name());
      expect(mockContainer.config()).andReturn(container.config()).anyTimes();
      expect(mockContainer.networkSettings()).andReturn(container.networkSettings()).anyTimes();
      expect(mockContainer.state()).andReturn(container.state());
      expect(mockContainer.image()).andReturn(container.image()).anyTimes();
      replay(mockContainer);

      return mockContainer;
   }
}
