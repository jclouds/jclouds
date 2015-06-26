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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;

import javax.ws.rs.Consumes;

import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.Address.Status;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseAddressListTest")
public class ParseAddressListTest extends BaseGoogleComputeEngineParseTest<ListPage<Address>> {

   @Override
   public String resource() {
      return "/address_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<Address> expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public ListPage<Address> expected(String baseUrl) {
      Address address1 = new ParseAddressTest().expected(baseUrl);
      Address address2 = Address.create( //
            "4881363978908129158", // id
            URI.create(baseUrl + "/party/regions/us-central1/addresses/test-ip2"), // selfLink
            "test-ip2", // name
            parse("2013-07-26T14:08:21.552-07:00"), //creationTimestamp
            "", // description
            Status.RESERVED, // status
            null, // users
            URI.create(baseUrl + "/party/regions/us-central1"), // region
            "173.255.118.115" // address
      );
      return ForwardingListPage.create( //
            ImmutableList.of(address1, address2), // items
            null // nextPageToken
      );
   }
}
