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
package org.jclouds.googlecomputeengine.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource.Kind;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseAddressListTest extends BaseGoogleComputeEngineParseTest<ListPage<Address>> {

   @Override
   public String resource() {
      return "/address_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Address> expected() {
      return ListPage.<Address>builder()
              .kind(Kind.ADDRESS_LIST)
              .id("projects/myproject/regions/us-central1/addresses")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/addresses"))
              .items(ImmutableSet.of(new ParseAddressTest().expected(),
                      Address.builder()
                              .id("4881363978908129158")
                              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2013-07-26T14:08:21.552-07:00"))
                              .status("RESERVED")
                              .region(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1"))
                              .name("test-ip2")
                              .description("")
                              .address("173.255.118.115")
                              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/addresses/test-ip2"))
                              .build())
              ).build();
   }
}
