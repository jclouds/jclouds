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
import com.google.common.collect.Lists;
import java.util.List;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.IpBlock.PublicIp;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "IpBlockResponseHandlerTest")
public class IpBlockResponseHandlerTest extends BaseResponseHandlerTest<IpBlock> {

   @Override
   protected ParseSax<IpBlock> createParser() {
      return factory.create(injector.getInstance(IpBlockResponseHandler.class));
   }

   @Test
   public void testParseResponseFromGetIpBlock() {
      ParseSax<IpBlock> parser = createParser();

      IpBlock actual = parser.parse(payloadFromResource("/ipblock/ipblock.xml"));
      assertNotNull(actual, "Parsed content returned null");
      List<String> emptyIpList = Lists.newArrayList();

      IpBlock expected = IpBlock.builder()
              .id("qwertyui-qwer-qwer-qwer-qwertyyuiiop")
              .location(Location.US_LAS)
              .publicIps(ImmutableList.<PublicIp>of(
                              PublicIp.builder()
                              .ip("ip")
                              .nicId("nic-id")
                              .build(),
                              PublicIp.builder()
                              .ip("ip")
                              .nicId("nic-id")
                              .build()))
              .ips(emptyIpList)
              .build();
      assertEquals(actual, expected);
   }
}
