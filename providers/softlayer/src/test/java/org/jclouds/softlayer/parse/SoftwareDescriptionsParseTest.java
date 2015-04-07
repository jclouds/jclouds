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

import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class SoftwareDescriptionsParseTest extends BaseSoftLayerParseTest<Set<SoftwareDescription>> {

   @Override
   public String resource() {
      return "/software_description_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<SoftwareDescription> expected() {
      return ImmutableSet.of(
              SoftwareDescription.builder()
                      .version("6.0-64 LAMP for CCI")
                      .referenceCode("CENTOS_6_64")
                      .operatingSystem(1)
                      .longDescription("CentOS / CentOS / 6.0-64 LAMP for CCI")
                      .build(),
              SoftwareDescription.builder()
                      .version("WEB 64 bit")
                      .referenceCode("WIN_2008-WEB-R2_64")
                      .operatingSystem(1)
                      .longDescription("Microsoft / Windows 2008 FULL WEB 64 bit R2 / WEB 64 bit")
                      .build(),
              SoftwareDescription.builder()
                      .version("12.04-32 Minimal for CCI")
                      .referenceCode("UBUNTU_12_32")
                      .operatingSystem(1)
                      .longDescription("Ubuntu / Ubuntu / 12.04-32 Minimal for CCI")
                      .build()
              );
   }
}
