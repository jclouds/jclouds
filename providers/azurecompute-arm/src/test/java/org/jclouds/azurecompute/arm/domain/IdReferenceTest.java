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
package org.jclouds.azurecompute.arm.domain;

import static org.jclouds.azurecompute.arm.domain.IdReference.extractName;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractResourceGroup;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "IdReferenceTest")
public class IdReferenceTest {

   @Test
   public void testExtractResourceGroup() {
      assertEquals(extractResourceGroup(null), null);
      assertEquals(extractResourceGroup(""), null);
      assertEquals(
            extractResourceGroup("/subscriptions/subscription/resourceGroups/jclouds-northeurope/providers/Microsoft.Compute/virtualMachines/resources-8c5"),
            "jclouds-northeurope");
      assertEquals(extractResourceGroup("/subscriptions/subscription/resourceGroups/jclouds-west"), "jclouds-west");
      assertEquals(extractResourceGroup("/resourceGroups/jclouds-west2"), "jclouds-west2");
      assertEquals(
            extractResourceGroup("/resourceGroups/jclouds-northeurope2/providers/Microsoft.Compute/virtualMachines/resources-8c5"),
            "jclouds-northeurope2");
      assertEquals(extractResourceGroup("resourceGroups/jclouds-west2"), null);
      assertEquals(
            extractResourceGroup("resourceGroups/jclouds-northeurope2/providers/Microsoft.Compute/virtualMachines/resources-8c5"),
            null);
      assertEquals(
            extractResourceGroup("/subscriptions/subscription/providers/Microsoft.Compute/virtualMachines/resources-8c5"),
            null);
      assertEquals(
            extractResourceGroup("/subscriptions/subscription/resourceGroups//jclouds-northeurope/providers/Microsoft.Compute/virtualMachines/resources-8c5"),
            null);
   }

   @Test
   public void testExtractName() {
      assertEquals(extractName(null), null);
      assertEquals(extractName(""), "");
      assertEquals(extractName("foo"), "foo");
      assertEquals(extractName("/foo/bar"), "bar");
      assertEquals(extractName("/foo/bar/"), "bar");
      assertEquals(extractName("/foo/bar////"), "bar");
      assertEquals(extractName("/foo///bar////"), "bar");
      assertEquals(extractName("////bar"), "bar");
   }
}
