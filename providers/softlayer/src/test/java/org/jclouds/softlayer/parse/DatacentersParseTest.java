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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class DatacentersParseTest extends BaseSoftLayerParseTest<Set<Datacenter>> {

   @Override
   public String resource() {
      return "/datacenter_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Datacenter> expected() {
      return ImmutableSet.of(
              Datacenter.builder()
                      .id(265592)
                      .longName("Amsterdam 1")
                      .name("ams01")
                      .build(),
              Datacenter.builder()
                      .id(3)
                      .longName("Dallas 1")
                      .name("dal01")
                      .build(),
              Datacenter.builder()
                      .id(154770)
                      .longName("Dallas 2")
                      .name("dal02")
                      .build(),
              Datacenter.builder()
                      .id(167092)
                      .longName("Dallas 4")
                      .name("dal04")
                      .build(),
              Datacenter.builder()
                      .id(138124)
                      .longName("Dallas 5")
                      .name("dal05")
                      .build(),
              Datacenter.builder()
                      .id(154820)
                      .longName("Dallas 6")
                      .name("dal06")
                      .build(),
              Datacenter.builder()
                      .id(142776)
                      .longName("Dallas 7")
                      .name("dal07")
                      .build(),
              Datacenter.builder()
                      .id(352392)
                      .longName("Dallas 8")
                      .name("dal08")
                      .build(),
              Datacenter.builder()
                      .id(352494)
                      .longName("Hong Kong 2")
                      .name("hkg02")
                      .build(),
              Datacenter.builder()
                      .id(142775)
                      .longName("Houston 2")
                      .name("hou02")
                      .build(),
              Datacenter.builder()
                      .id(168642)
                      .longName("San Jose 1")
                      .name("sjc01")
                      .build(),
              Datacenter.builder()
                      .id(18171)
                      .longName("Seattle")
                      .name("sea01")
                      .build(),
              Datacenter.builder()
                      .id(224092)
                      .longName("Singapore 1")
                      .name("sng01")
                      .build(),
              Datacenter.builder()
                      .id(37473)
                      .longName("Washington, DC")
                      .name("wdc01")
                      .build()
              );
   }
}
