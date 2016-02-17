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
package org.jclouds.openstack.nova.v2_0.parse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Test(groups = "unit", testName = "ParseServerWithAddressExtensionsTest")
public class ParseServerWithAddressExtensionsTest extends BaseItemParserTest<Server> {

   @Override
   public String resource() {
      return "/server_details_with_address_ext.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public Server expected() {
      return Server
         .builder()
         .id("0bdc3a8d-3a96-4ccc-bb40-715537a7df7b")
         .tenantId("cac29c920a6149aabe499757b6ba81c7")
         .userId("ed15e338032f4a2c85b7fa80e40b9917")
         .name("cloudts-f07")
         .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2016-02-17T14:48:00Z"))
         .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2016-02-17T14:46:13Z"))
         .hostId("18a9cd55f76c520dcad6c31d5b2b8f4c921979629c0274c0f7d2de39")
         .status(Status.ACTIVE)
         .image(
            Resource
               .builder()
               .id("9a9f496a-f5c2-4286-81a4-98189a48777a")
               .links(
                  Link.create(
                     Relation.BOOKMARK,
                     URI.create("http://openstack:8774/cac29c920a6149aabe499757b6ba81c7/images/9a9f496a-f5c2-4286-81a4-98189a48777a")))
               .build())
         .flavor(
            Resource
               .builder()
               .id("3")
               .links(
                  Link.create(
                     Relation.BOOKMARK,
                     URI.create("http://openstack:8774/cac29c920a6149aabe499757b6ba81c7/flavors/3")))
                .build())
         .links(
            Link.create(
               Relation.SELF,
               URI.create("http://openstack:8774/v2/cac29c920a6149aabe499757b6ba81c7/servers/0bdc3a8d-3a96-4ccc-bb40-715537a7df7b")),
            Link.create(
               Relation.BOOKMARK,
               URI.create("http://openstack:8774/cac29c920a6149aabe499757b6ba81c7/servers/0bdc3a8d-3a96-4ccc-bb40-715537a7df7b")))
         .metadata(ImmutableMap.<String, String>of("jclouds-group", "cloudts"))
         .addresses(ImmutableMultimap.<String, Address>builder()
            .putAll("jenkins",
               Address.builder().version(4).addr("172.16.130.24").macAddr("fa:16:3e:bf:82:43").type("fixed").build(),
               Address.builder().version(4).addr("10.8.54.75").macAddr("fa:16:3e:bf:82:43").type("floating").build())
            .build())
         .diskConfig("MANUAL")
         .configDrive("")
         .availabilityZone("nova")
         .accessIPv4("")
         .accessIPv6("")
         .keyName("jenkins")
         .extendedStatus(ServerExtendedStatus.builder().vmState("active").powerState(1).build())
         .build();
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
