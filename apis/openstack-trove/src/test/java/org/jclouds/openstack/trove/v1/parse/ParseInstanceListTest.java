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
package org.jclouds.openstack.trove.v1.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.Uris;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.openstack.trove.v1.domain.Flavor;
import org.jclouds.openstack.trove.v1.domain.Instance;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;


@Test(groups = "unit", testName = "ParseInstanceTest")
public class ParseInstanceListTest extends BaseSetParserTest<Instance> {

   @Override
   public String resource() {
      return "/instance_list.json";
   }

   /*
    * The instance needs to be comparable for this test to work 
    * */
   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("instances")
   public Set<Instance> expected() {
      return ImmutableSet
            .of(Instance.builder()
                  .id("098653ba-218b-47ce-936a-e0b749101f81")
                  .name("xml_rack_instance")
                  .size(2)
                  .flavor(
                          Flavor.builder()
                              .id(1)
                              .links(ImmutableList.of(
                                              Link.create(Relation.SELF, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/v1.0/1234/flavors/1").build() ),
                                              Link.create(Relation.BOOKMARK, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/flavors/1").build())))
                              .build()
                         )
                  .status(Instance.Status.ACTIVE)
                  .links(ImmutableList.of(
                          Link.create(Relation.SELF, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/v1.0/1234/instances/098653ba-218b-47ce-936a-e0b749101f81").build() ),
                          Link.create(Relation.BOOKMARK, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/instances/098653ba-218b-47ce-936a-e0b749101f81").build() )
                          ))
                  .build(),
                  Instance.builder()
                  .id("44b277eb-39be-4921-be31-3d61b43651d7")
                  .name("json_rack_instance")
                  .size(2)
                  .flavor(
                          Flavor.builder()
                              .id(1)
                              .links(ImmutableList.of(
                                              Link.create(Relation.SELF, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/v1.0/1234/flavors/1").build() ),
                                              Link.create(Relation.BOOKMARK, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/flavors/1").build())))
                              .build()
                         )
                  .status(Instance.Status.ACTIVE)
                  .links(ImmutableList.of(
                          Link.create(Relation.SELF, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/v1.0/1234/instances/44b277eb-39be-4921-be31-3d61b43651d7").build() ),
                          Link.create(Relation.BOOKMARK, Uris.uriBuilder("https://ord.databases.api.rackspacecloud.com/instances/44b277eb-39be-4921-be31-3d61b43651d7").build() )
                          ))
                  .build() 
                  );
   }
}
