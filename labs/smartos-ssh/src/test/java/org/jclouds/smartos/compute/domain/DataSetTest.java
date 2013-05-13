/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.smartos.compute.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "DataSetTest")
public class DataSetTest {

   @Test
   public void testParse() throws IOException {
      // Response from console from a 'dsadm list'
      InputStream is = getClass().getResourceAsStream("dsadm-list-response.txt");

      BufferedReader r = new BufferedReader(new InputStreamReader(is));
      String line = r.readLine(); // skip line
      ImmutableList.Builder<DataSet> resultBuilder = ImmutableList.builder();
      while ((line = r.readLine()) != null) {
         DataSet ds = DataSet.builder().fromDsadmString(line).build();

         resultBuilder.add(ds);
      }
      List<DataSet> dataSetList = resultBuilder.build();

      Assert.assertEquals(10, dataSetList.size());

      Assert.assertEquals(UUID.fromString("c0ffee88-883e-47cf-80d1-ad71cc872180"), dataSetList.get(0).getUuid());
      Assert.assertEquals("nrm:nrm:realtime-jenkins:1.7", dataSetList.get(0).getUrn());

   }
}
