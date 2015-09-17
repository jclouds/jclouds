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
package org.jclouds.docker.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit", testName = "ConfigTest")
public class ConfigTest {

   @Test
   public void testFromConfig() {
      Config origConfig = Config.builder()
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
         .build();
      Config newConfig = Config.builder().fromConfig(origConfig).build();
      assertThat(origConfig).isEqualTo(newConfig);
   }


   @Test
   public void testNullValuesPropagation() {
      Config config = Config.builder()
      .image("zettio/weave")
      .build();

      assertThat(config.cmd()).isNull();
      assertThat(config.entrypoint()).isNull();
      assertThat(config.env()).isNull();
      assertThat(config.hostname()).isNull();
      assertThat(config.domainname()).isNull();
      assertThat(config.workingDir()).isNull();
      assertThat(config.hostConfig()).isNull();
      assertThat(config.dns()).isNull();
      assertThat(config.dnsSearch()).isNull();
      assertThat(config.volumesFrom()).isNull();
   }
}
