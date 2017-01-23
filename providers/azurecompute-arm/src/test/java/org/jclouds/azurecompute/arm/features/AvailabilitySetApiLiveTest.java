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
package org.jclouds.azurecompute.arm.features;

import static com.google.common.collect.Iterables.any;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.UUID;

import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.AvailabilitySet.AvailabilitySetProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

@Test(groups = "live", testName = "AvailabilitySetApiLiveTest", singleThreaded = true)
public class AvailabilitySetApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String asName;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      asName = "jclouds-" + RAND;
   }

   @Test
   public void deleteAvailabilitySetDoesNotExist() {
      assertNull(api().delete(UUID.randomUUID().toString()));
   }

   @Test
   public void createAvailabilitySet() {
      AvailabilitySetProperties props = AvailabilitySetProperties.builder().platformUpdateDomainCount(2)
            .platformFaultDomainCount(3).build();
      AvailabilitySet as = api().createOrUpdate(asName, LOCATION, null, props);

      assertNotNull(as);
      assertEquals(as.name(), asName);
   }

   @Test(dependsOnMethods = "createAvailabilitySet")
   public void getAvailabilitySet() {
      assertNotNull(api().get(asName));
   }
   
   @Test(dependsOnMethods = "createAvailabilitySet")
   public void listAvailabilitySet() {
      assertTrue(any(api().list(), new Predicate<AvailabilitySet>() {
         @Override
         public boolean apply(AvailabilitySet input) {
            return asName.equals(input.name());
         }
      }));
   }
   
   @Test(dependsOnMethods = "createAvailabilitySet")
   public void updateAvailabilitySet() {
      AvailabilitySet as = api().get(asName);
      as = api().createOrUpdate(asName, LOCATION, ImmutableMap.of("foo", "bar"), as.properties());

      assertNotNull(as);
      assertTrue(as.tags().containsKey("foo"));
      assertEquals(as.tags().get("foo"), "bar");
   }
   
   @Test(dependsOnMethods = { "getAvailabilitySet", "listAvailabilitySet", "updateAvailabilitySet" })
   public void deleteAvailabilitySet() {
      URI uri = api().delete(asName);
      assertResourceDeleted(uri);
   }

   private AvailabilitySetApi api() {
      return api.getAvailabilitySetApi(resourceGroupName);
   }

}
