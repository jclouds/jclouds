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
package org.jclouds.hpcloud.objectstorage.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import org.jclouds.hpcloud.services.HPExtensionServiceType;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.services.ServiceType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

@Test(groups = "unit")
public class HPCloudObjectStorageEndpointModuleTest {

   private final String apiVersion = "1.0";
   private final RegionIdToURISupplier.Factory mockFactory = createStrictMock(RegionIdToURISupplier.Factory.class);
   private final RegionIdToURISupplier.Factory mockCDNFactory = createStrictMock(RegionIdToURISupplier.Factory.class);
   private final RegionIdToURISupplier mockSupplier = createStrictMock(RegionIdToURISupplier.class);

   /**
    * Setup the expectations for our mock factory to return 3 region urls keyed
    * by test region names
    */
   @BeforeTest
   public void setup() {
      Map<String, Supplier<URI>> endpoints = Maps.newHashMap();

      try {
         endpoints.put("region1", Suppliers.ofInstance(new URI("http://region1.example.org/")));
         endpoints.put("region2", Suppliers.ofInstance(new URI("http://region2.example.org/")));
         endpoints.put("region3", Suppliers.ofInstance(new URI("http://region3.example.org/")));
      } catch (URISyntaxException ex) {
         fail("static test Strings do not parse to URI: " + ex.getMessage());
      }

      expect(mockSupplier.get())
         .andReturn(endpoints)
         .anyTimes();
      expect(mockFactory.createForApiTypeAndVersion(ServiceType.OBJECT_STORE, null))
         .andReturn(mockSupplier)
         .anyTimes();
      expect(mockCDNFactory.createForApiTypeAndVersion(HPExtensionServiceType.CDN, apiVersion))
         .andReturn(mockSupplier)
         .anyTimes();

      replay(mockSupplier);
      replay(mockFactory);
      replay(mockCDNFactory);
   }

   @Test
   public void testObjectStorageRegion() {
      final HPCloudObjectStorageHttpApiModule.HPCloudObjectStorageEndpointModule moduleToTest = new HPCloudObjectStorageHttpApiModule.HPCloudObjectStorageEndpointModule();

      for (int i = 1; i <= 3; i++) {
         Supplier<URI> resultingSupplier = moduleToTest.provideStorageUrl(mockFactory, apiVersion, String.format("region%1$s", i));
         assertNotNull(resultingSupplier);
         URI resultingUri = resultingSupplier.get();
         assertNotNull(resultingUri);

         assertEquals(resultingUri.toString(),
               String.format("http://region%1$s.example.org/", i));
      }
   }

   @Test
   public void testCDNRegion() {
      final HPCloudObjectStorageHttpApiModule moduleToTest = new HPCloudObjectStorageHttpApiModule();

      for (int i = 1; i <= 3; i++) {
         Supplier<URI> resultingSupplier = moduleToTest.provideCDNUrl(mockCDNFactory, apiVersion, String.format("region%1$s", i));
         assertNotNull(resultingSupplier);
         URI resultingUri = resultingSupplier.get();
         assertNotNull(resultingUri);

         assertEquals(resultingUri.toString(),
               String.format("http://region%1$s.example.org/", i));
      }
   }

   /**
    * Test that specifying an undefined region will return null
    */
   @Test
   public void testObjectStorageUndefinedRegion() {
      final HPCloudObjectStorageHttpApiModule.HPCloudObjectStorageEndpointModule moduleToTest = new HPCloudObjectStorageHttpApiModule.HPCloudObjectStorageEndpointModule();

      Supplier<URI> resultingSupplier = moduleToTest.provideStorageUrl(mockFactory, apiVersion, "region-that-dne");
      assertNotNull(resultingSupplier);
      URI resultingUri = resultingSupplier.get();
      assertNull(resultingUri);
   }

   @Test
   public void testCDNUndefinedRegion() {
      final HPCloudObjectStorageHttpApiModule moduleToTest = new HPCloudObjectStorageHttpApiModule();

      Supplier<URI> resultingSupplier = moduleToTest.provideCDNUrl(mockCDNFactory, apiVersion, "region-that-dne");
      assertNotNull(resultingSupplier);
      URI resultingUri = resultingSupplier.get();
      assertNull(resultingUri);
   }
}
