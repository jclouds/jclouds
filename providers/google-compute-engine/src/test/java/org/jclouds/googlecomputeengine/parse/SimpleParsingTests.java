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

import static org.jclouds.googlecomputeengine.compute.strategy.CreateNodesWithGroupEncodedIntoNameThenAddToSet.simplifyPorts;
import static org.jclouds.googlecomputeengine.compute.strategy.CreateNodesWithGroupEncodedIntoNameThenAddToSet.nameFromNetworkString;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import static org.testng.Assert.assertEquals;


@Test(groups = "unit", testName = "SimpleParsingTests", singleThreaded = true)
public class SimpleParsingTests {

   public void testOnePort(){
      int[] ports = {22};
      List<String> output = simplifyPorts(ports);

      assertEquals(ImmutableList.of("22"), output);
   }

   public void testBasic(){
      int[] ports = {1, 2, 3, 4};
      List<String> output = simplifyPorts(ports);

      assertEquals(ImmutableList.of("1-4"), output);
   }

   public void testComplex(){
      int[] ports = {3, 1, 5, 2, 1002, 17, 1001, 22, 80, 1000};
      List<String> output = simplifyPorts(ports);

      assertEquals(ImmutableList.of("1-3", "5", "17", "22", "80", "1000-1002"), output);
   }

   public void testEmpty(){
      int[] ports = {};
      List<String> output = simplifyPorts(ports);

      assertEquals(null, output);
   }

   public void testEndSingle(){
      int[] ports = {1, 2, 3, 4, 7};
      List<String> output = simplifyPorts(ports);

      assertEquals(ImmutableList.of("1-4", "7"), output);
   }

   public void testNetworkFromString(){
      String network = "https://www.googleapis.com/compute/v1/projects/project/global/networks/network";
      assertEquals("network", nameFromNetworkString(network));

      network = "projects/project/global/networks/network";
      assertEquals("network", nameFromNetworkString(network));

      network = "global/networks/default";
      assertEquals("default", nameFromNetworkString(network));

      network = "default";
      assertEquals("default", nameFromNetworkString(network));
   }
}
