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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.URI;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedAttributes;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseServerDetailsStatesTest")
public class ParseServerDetailsStatesTest extends BaseSetParserTest<Server> {

   @Override
   public String resource() {
      return "/server_list_details_states.json";
   }

   @Override
   @SelectJson("servers")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Server> expected() {
      return ImmutableSet.<Server>of(
            Server.builder()
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://openstack:8774/v2/4e1900cf21924a098709c23480e157c0/servers/56d51a88-0066-4976-91b6-d1b453be603f")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/servers/56d51a88-0066-4976-91b6-d1b453be603f"))
                  )
                  .image(Resource.builder()
                        .id("e3f84189-964e-4dc3-8ac6-832c2b7553d4")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/images/e3f84189-964e-4dc3-8ac6-832c2b7553d4")))
                        .build())
                  .flavor(Resource.builder()
                        .id("6")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/flavors/6")))
                        .build())
                  .id("56d51a88-0066-4976-91b6-d1b453be603f")
                  .userId("08ba127f0d6842279f9db8e8bc6977e9")
                  .status(Status.BUILD)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:54:59Z"))
                  .hostId("0bc453b1c10348e9dc398fed7a5b06f996964ae1643fe460a85a23d8")
                  .name("machine_5")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:54:57Z"))
                  .tenantId("4e1900cf21924a098709c23480e157c0")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("building").powerState(0).taskState("spawning")
                        .build())
                  .diskConfig("MANUAL")
                  .availabilityZone("nova")
                  .extendedAttributes(
                        ServerExtendedAttributes.builder()
                              .instanceName("instance-0000000b")
                              .hostName("rdohavana.localdomain")
                              .hypervisorHostName("rdohavana.localdomain").build()
                  ).build(),
            Server.builder()
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://openstack:8774/v2/4e1900cf21924a098709c23480e157c0/servers/3bc8ab03-52e7-4d2b-ba88-73f9ecadf003")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/servers/3bc8ab03-52e7-4d2b-ba88-73f9ecadf003"))
                  )
                  .image(Resource.builder()
                        .id("e3f84189-964e-4dc3-8ac6-832c2b7553d4")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/images/e3f84189-964e-4dc3-8ac6-832c2b7553d4")))
                        .build())
                  .flavor(Resource.builder()
                        .id("6")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/flavors/6")))
                        .build())
                  .id("3bc8ab03-52e7-4d2b-ba88-73f9ecadf003")
                  .userId("08ba127f0d6842279f9db8e8bc6977e9")
                  .status(Status.ACTIVE)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:52:21Z"))
                  .hostId("0bc453b1c10348e9dc398fed7a5b06f996964ae1643fe460a85a23d8")
                  .name("machine_4")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:51:53Z"))
                  .tenantId("4e1900cf21924a098709c23480e157c0")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("active").powerState(1).build())
                  .diskConfig("MANUAL")
                  .availabilityZone("nova")
                  .extendedAttributes(
                        ServerExtendedAttributes.builder()
                              .instanceName("instance-00000009")
                              .hostName("rdohavana.localdomain")
                              .hypervisorHostName("rdohavana.localdomain").build()
                  )
                  .addresses(ImmutableMultimap.<String, Address>builder()
                              .putAll("public", Address.createV4("172.24.4.232")).build()
                  ).build(),
            Server.builder()
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://openstack:8774/v2/4e1900cf21924a098709c23480e157c0/servers/cad76945-8851-489a-99e1-f1049e02c769")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/servers/cad76945-8851-489a-99e1-f1049e02c769"))
                  )
                  .image(Resource.builder()
                        .id("e3f84189-964e-4dc3-8ac6-832c2b7553d4")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/images/e3f84189-964e-4dc3-8ac6-832c2b7553d4")))
                        .build())
                  .flavor(Resource.builder()
                        .id("6")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/flavors/6")))
                        .build())
                  .id("cad76945-8851-489a-99e1-f1049e02c769")
                  .userId("08ba127f0d6842279f9db8e8bc6977e9")
                  .status(Status.SHELVED_OFFLOADED)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:38:05Z"))
                  .name("machine_3")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:33:27Z"))
                  .tenantId("4e1900cf21924a098709c23480e157c0")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("shelved_offloaded").powerState(4).build())
                  .diskConfig("MANUAL")
                  .availabilityZone("nova")
                  .extendedAttributes(
                        ServerExtendedAttributes.builder()
                              .instanceName("instance-00000006").build()
                  )
                  .addresses(ImmutableMultimap.<String, Address>builder()
                              .putAll("public", Address.createV4("172.24.4.229")).build()
                  ).build(),
            Server.builder()
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://openstack:8774/v2/4e1900cf21924a098709c23480e157c0/servers/89142a4f-f58c-4205-8571-65f4a2be2bc9")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/servers/89142a4f-f58c-4205-8571-65f4a2be2bc9"))
                  )
                  .image(Resource.builder()
                        .id("e3f84189-964e-4dc3-8ac6-832c2b7553d4")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/images/e3f84189-964e-4dc3-8ac6-832c2b7553d4")))
                        .build())
                  .flavor(Resource.builder()
                        .id("6")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/flavors/6")))
                        .build())
                  .id("89142a4f-f58c-4205-8571-65f4a2be2bc9")
                  .userId("08ba127f0d6842279f9db8e8bc6977e9")
                  .status(Status.RESCUE)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:36:05Z"))
                  .hostId("0bc453b1c10348e9dc398fed7a5b06f996964ae1643fe460a85a23d8")
                  .name("machine_2")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:31:19Z"))
                  .tenantId("4e1900cf21924a098709c23480e157c0")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("rescued").powerState(1).build())
                  .diskConfig("MANUAL")
                  .availabilityZone("nova")
                  .extendedAttributes(
                        ServerExtendedAttributes.builder()
                              .instanceName("instance-00000005")
                              .hostName("rdohavana.localdomain")
                              .hypervisorHostName("rdohavana.localdomain").build()
                  )
                  .addresses(ImmutableMultimap.<String, Address>builder()
                              .putAll("public", Address.createV4("172.24.4.227")).build()
                  ).build(),
            Server.builder()
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://openstack:8774/v2/4e1900cf21924a098709c23480e157c0/servers/fac50d26-bb38-455f-ad92-eba790187c00")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/servers/fac50d26-bb38-455f-ad92-eba790187c00"))
                  )
                  .image(Resource.builder()
                        .id("e3f84189-964e-4dc3-8ac6-832c2b7553d4")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/images/e3f84189-964e-4dc3-8ac6-832c2b7553d4")))
                        .build())
                  .flavor(Resource.builder()
                        .id("6")
                        .links(Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://openstack:8774/4e1900cf21924a098709c23480e157c0/flavors/6")))
                        .build())
                  .id("fac50d26-bb38-455f-ad92-eba790187c00")
                  .userId("08ba127f0d6842279f9db8e8bc6977e9")
                  .status(Status.SHUTOFF)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:35:26Z"))
                  .hostId("0bc453b1c10348e9dc398fed7a5b06f996964ae1643fe460a85a23d8")
                  .name("machine_1")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2014-03-19T11:28:56Z"))
                  .tenantId("4e1900cf21924a098709c23480e157c0")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("stopped").powerState(4).build())
                  .diskConfig("MANUAL")
                  .availabilityZone("nova")
                  .extendedAttributes(
                        ServerExtendedAttributes.builder()
                              .instanceName("instance-00000004")
                              .hostName("rdohavana.localdomain")
                              .hypervisorHostName("rdohavana.localdomain").build()
                  )
                  .addresses(ImmutableMultimap.<String, Address>builder()
                              .putAll("public", Address.createV4("172.24.4.228")).build()
                  ).build()
      );
   }


   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
