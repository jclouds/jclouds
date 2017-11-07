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
package org.jclouds.openstack.swift.v1.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.io.Payloads.newByteSourcePayload;
import static org.testng.Assert.assertNotNull;

import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

@Test(groups = "live", testName = "DynamicLargeObjectApiLiveTest", singleThreaded = true)
public class DynamicLargeObjectApiLiveTest extends BaseSwiftApiLiveTest {

   private String containerName = getClass().getSimpleName();
   private static final ByteSource megOf1s = TestUtils.randomByteSource().slice(0, 1024 * 1024);
   private static final ByteSource megOf2s = TestUtils.randomByteSource().slice(0, 1024 * 1024);
   private String objectName = "myObject";
   private String name = "foo";

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : regions) {
         getApi().getContainerApi(regionId).create(containerName);
      }
   }

   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : regions) {
         deleteAllObjectsInContainer(regionId, containerName);
         getApi().getContainerApi(regionId).deleteIfEmpty(containerName);
      }
   }

   @SuppressWarnings("deprecation")
   @Test
   protected void assertReplaceManifest() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = getApi().getObjectApi(regionId, containerName);

         String etag1s = objectApi.put(name + "/1", newByteSourcePayload(megOf1s));
         awaitConsistency();
         assertMegabyteAndETagMatches(regionId, containerName, name + "/1", etag1s);

         String etag2s = objectApi.put(name + "/2", newByteSourcePayload(megOf2s));
         awaitConsistency();
         assertMegabyteAndETagMatches(regionId, containerName, name + "/2", etag2s);

         awaitConsistency();
         String etagOfEtags = getApi().getDynamicLargeObjectApi(regionId, containerName).putManifest(name,
               ImmutableMap.of("myfoo", "Bar"));

         assertNotNull(etagOfEtags);

         awaitConsistency();

         SwiftObject bigObject = getApi().getObjectApi(regionId, containerName).get(name);
         assertThat(bigObject.getPayload().getContentMetadata().getContentLength()).isEqualTo(Long.valueOf(2 * 1024L * 1024L));
         assertThat(bigObject.getMetadata()).isEqualTo(ImmutableMap.of("myfoo", "Bar"));
         // segments are visible
         assertThat(getApi().getContainerApi(regionId).get(containerName).getObjectCount()).isEqualTo(Long.valueOf(3));
      }
   }

   protected void assertMegabyteAndETagMatches(String regionId, String containerName, String name, String etag1s) {
      SwiftObject object1s = getApi().getObjectApi(regionId, containerName).get(name);
      assertThat(object1s.getETag()).isEqualTo(etag1s);
      assertThat(object1s.getPayload().getContentMetadata().getContentLength()).isEqualTo(Long.valueOf(1024L * 1024L));
   }
}
