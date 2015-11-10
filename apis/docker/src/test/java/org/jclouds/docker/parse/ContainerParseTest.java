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
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.HostConfig;
import org.jclouds.docker.domain.NetworkSettings;
import org.jclouds.docker.domain.State;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit")
public class ContainerParseTest extends BaseDockerParseTest<Container> {

   @Override
   public String resource() {
      return "/container.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Container expected() {
      return Container.builder()
              .id("6c9932f478bd761f32ddb54ed28ab42ab6fac6f2a279f561ea31503ee9d39524")
              .created(new SimpleDateFormatDateService().iso8601DateParse("2014-10-31T17:00:21.544197943Z"))
              .path("/home/weave/weaver")
              .name("/weave")
              .args(ImmutableList.of("-iface", "ethwe", "-wait", "5", "-name", "7a:63:a2:39:7b:0f"))
              .config(Config.builder()
                      .hostname("6c9932f478bd")
                      .env(ImmutableList.of("PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"))
                      .image("57e570db16baba1e8c0d6f3c15868ddb400f64ff76ec948e65c3ca3f15fb3587")
                      .domainname("")
                      .user("")
                      .cmd(ImmutableList.of("-name", "7a:63:a2:39:7b:0f"))
                      .entrypoint(ImmutableList.of("/home/weave/weaver", "-iface", "ethwe", "-wait", "5"))
                      .image("zettio/weave")
                      .workingDir("/home/weave")
                      .exposedPorts(ImmutableMap.of("6783/tcp", ImmutableMap.of(), "6783/udp", ImmutableMap.of()))
                      .build())
              .state(State.create(10357, true, 0, "2015-11-10T09:33:21.68146124Z", "0001-01-01T00:00:00Z", false, false, "running", false, false, ""))
              .image("57e570db16baba1e8c0d6f3c15868ddb400f64ff76ec948e65c3ca3f15fb3587")
              .networkSettings(NetworkSettings.builder()
                      .sandboxId("3ef128b055eb9ef62a6a2c281d97a2dfde5f47947d490f1dd2a81612611d961f")
                      .hairpinMode(false)
                      .linkLocalIPv6Address("")
                      .linkLocalIPv6PrefixLen(0)
                      .globalIPv6Address("")
                      .globalIPv6PrefixLen(0)
                      .ipv6Gateway("")
                      .sandboxKey("/var/run/docker/netns/3ef128b055eb")
                      .endpointId("9e8dcc0c8288938a923018fee0728cee8e6de7c01a5150738ee6e51c1caf8cf6")
                      .ipAddress("172.17.0.2")
                      .ipPrefixLen(16)
                      .gateway("172.17.0.1")
                      .bridge("")
                      .ports(ImmutableMap.<String, List<Map<String, String>>>of())
                      .macAddress("02:42:ac:11:00:02")
                      .networks(ImmutableMap.of(
                              "JCLOUDS_NETWORK", NetworkSettings.Details.create(
                                      "04268fbb4dc368b5a53bb1c3f89294a4f0c72095deb944db3c4efc6d6a439304",
                                      "172.19.0.1",
                                      "172.19.0.2",
                                      16,
                                      "",
                                      "",
                                      0,
                                      "02:42:ac:13:00:02"
                              ),
                              "bridge", NetworkSettings.Details.create(
                                      "9e8dcc0c8288938a923018fee0728cee8e6de7c01a5150738ee6e51c1caf8cf6",
                                      "172.17.0.1",
                                      "172.17.0.2",
                                      16,
                                      "",
                                      "",
                                      0,
                                      "02:42:ac:11:00:02"
                              )
                      ))
                      .build())
              .resolvConfPath("/var/lib/docker/containers/6c9932f478bd761f32ddb54ed28ab42ab6fac6f2a279f561ea31503ee9d39524/resolv.conf")
              .hostConfig(HostConfig.builder()
                      .containerIDFile("")
                      .portBindings(ImmutableMap.<String, List<Map<String, String>>>of(
                                      "6783/tcp", ImmutableList.<Map<String, String>>of(ImmutableMap.of("HostIp", "", "HostPort", "6783")),
                                      "6783/udp", ImmutableList.<Map<String, String>>of(ImmutableMap.of("HostIp", "", "HostPort", "6783")))
                      )
                      .dns(ImmutableList.of("8.8.8.8", "8.8.4.4"))
                      .extraHosts(ImmutableList.<String>of("extra:169.254.0.1"))
                      .privileged(true)
                      .networkMode("bridge")
                      .build())
              .driver("aufs")
              .execDriver("native-0.2")
              .hostnamePath("/var/lib/docker/containers/6c9932f478bd761f32ddb54ed28ab42ab6fac6f2a279f561ea31503ee9d39524/hostname")
              .hostsPath("/var/lib/docker/containers/6c9932f478bd761f32ddb54ed28ab42ab6fac6f2a279f561ea31503ee9d39524/hosts")
              .mountLabel("")
              .processLabel("")
              .build();
   }
}
