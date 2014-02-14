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
package org.jclouds.rackspace.cloudfiles.v1.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.rackspace.cloudfiles.v1.domain.CDNContainer;
import org.jclouds.rackspace.cloudfiles.v1.internal.BaseCloudFilesApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jeremy Daggett
 */
@Test(groups = "live", testName = "CloudFilesCDNApiLiveTest")
public class CloudFilesCDNApiLiveTest extends BaseCloudFilesApiLiveTest {

   private String name = getClass().getSimpleName();

   public CloudFilesCDNApiLiveTest() {
      super();
   }
   
   public void testList() throws Exception {
      for (String regionId : regions) {
         CDNApi cdnApi = api.cdnApiInRegion(regionId);

         List<CDNContainer> cdnResponse = cdnApi.list().toList();
         assertNotNull(cdnResponse);
         for (CDNContainer cdnContainer : cdnResponse) {
            assertNotNull(cdnContainer.getName());
            assertTrue(cdnContainer.isEnabled());
            assertNotNull(cdnContainer.isLogRetentionEnabled());
            assertNotNull(cdnContainer.getTtl());
            assertNotNull(cdnContainer.getUri());
            assertNotNull(cdnContainer.getSslUri());
            assertNotNull(cdnContainer.getStreamingUri());
            assertNotNull(cdnContainer.getIosUri());
         }
      }
   }

   public void testListWithOptions() throws Exception {
      String lexicographicallyBeforeName = name.substring(0, name.length() - 1);
      for (String regionId : regions) {
         ListContainerOptions options = ListContainerOptions.Builder.marker(lexicographicallyBeforeName);
         CDNContainer cdnContainer = api.cdnApiInRegion(regionId).list(options).get(0);
         
         assertNotNull(cdnContainer.getName());
         assertTrue(cdnContainer.isEnabled());
         assertNotNull(cdnContainer.isLogRetentionEnabled());
         assertNotNull(cdnContainer.getTtl());
         assertNotNull(cdnContainer.getUri());
         assertNotNull(cdnContainer.getSslUri());
         assertNotNull(cdnContainer.getStreamingUri());
         assertNotNull(cdnContainer.getIosUri());
      }
   }

   public void testGet() throws Exception {
      for (String regionId : regions) {
         CDNContainer cdnContainer = api.cdnApiInRegion(regionId).get(name);
         assertNotNull(cdnContainer);
      }
   }

   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : regions) {
         api.containerApiInRegion(regionId).createIfAbsent(name, CreateContainerOptions.NONE);
         api.cdnApiInRegion(regionId).enable(name);
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : regions) {
         api.cdnApiInRegion(regionId).disable(name);
         api.containerApiInRegion(regionId).deleteIfEmpty(name);
      }
      super.tearDown();
   }
}
