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
package org.jclouds.profitbricks.http.parser.ipblock;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.IpBlock.PublicIp;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "IpBlockListResponseHandlerTest")
public class IpBlockListResponseHandlerTest extends BaseResponseHandlerTest<List<IpBlock>> {

   @Override
   protected ParseSax<List<IpBlock>> createParser() {
      return factory.create(injector.getInstance(IpBlockListResponseHandler.class));
   }

   @Test
   public void testParseResponseFromGetAllIpBlock() {
      ParseSax<List<IpBlock>> parser = createParser();

      List<IpBlock> actual = parser.parse(payloadFromResource("/ipblock/ipblocks.xml"));
      assertNotNull(actual, "Parsed content returned null");

      List<IpBlock> expected = ImmutableList.<IpBlock>of(
              IpBlock.builder()
              .id("block-id-1")
              .location(Location.US_LAS)
              .publicIps(ImmutableList.<PublicIp>of(
                              PublicIp.builder()
                              .ip("10.0.0.2")
                              .nicId("nic-id-1")
                              .build(),
                              PublicIp.builder()
                              .ip("10.0.0.3")
                              .nicId("nic-id-2")
                              .build()))
              .build(),
              IpBlock.builder()
              .id("block-id-2")
              .location(Location.US_LAS)
              .publicIps(ImmutableList.<PublicIp>of(
                              PublicIp.builder()
                              .ip("10.0.0.4")
                              .build(),
                              PublicIp.builder()
                              .ip("10.0.0.5")
                              .nicId("nic-id-4")
                              .build()))
              .build()
      );

      assertEquals(actual, expected);
   }

}
