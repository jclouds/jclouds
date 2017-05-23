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

import java.io.IOException;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

@Test(groups = "live", testName = "DynamicLargeObjectApiLiveTest", singleThreaded = true)
public class DynamicLargeObjectApiLiveTest extends BaseSwiftApiLiveTest {

   private String defaultName = getClass().getSimpleName();
   private static final ByteSource megOf1s = TestUtils.randomByteSource().slice(0, 1024 * 1024);
   private static final ByteSource megOf2s = TestUtils.randomByteSource().slice(0, 1024 * 1024);
   private String objectName = "myObject";

   @Test
   public void testReplaceManifest() throws Exception {
      for (String regionId : regions) {
         assertReplaceManifest(regionId, defaultName);
         uploadLargeFile(regionId);
      }
   }

   @SuppressWarnings("deprecation")
   @Test
   public void uploadLargeFile(String regionId) throws IOException, InterruptedException {
      long total_size = 0;
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      BlobStore blobStore = ctx.getBlobStore();
      String defaultContainerName = getContainerName();
      // configure the blobstore to use multipart uploading of the file
      for (int partNumber = 0; partNumber < 3; partNumber++) {
         String objName = String.format("%s/%s/%s", objectName, "dlo", partNumber);
         String data = "data" + partNumber;
         ByteSource payload = ByteSource.wrap(data.getBytes(Charsets.UTF_8));
         Blob blob = blobStore.blobBuilder(objName)
               .payload(payload)
               .build();
         String etag = blobStore.putBlob(defaultContainerName, blob);
         assertNotNull(etag);
         total_size += data.length();
      }
      getApi().getDynamicLargeObjectApi(regionId, defaultContainerName).putManifest(objectName,
            ImmutableMap.of("myfoo", "Bar"));
      SwiftObject bigObject = getApi().getObjectApi(regionId, defaultContainerName).get(objectName);
      assertThat(bigObject.getETag()).isEqualTo("54bc1337d7a51660c40db39759cc1944");
      assertThat(bigObject.getPayload().getContentMetadata().getContentLength()).isEqualTo(total_size);
      assertThat(getApi().getContainerApi(regionId).get(defaultContainerName).getObjectCount()).isEqualTo(Long.valueOf(4));
   }

   @SuppressWarnings("deprecation")
   protected void assertReplaceManifest(String regionId, String name) throws InterruptedException {
      String containerName = getContainerName();
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

   protected void assertMegabyteAndETagMatches(String regionId, String containerName, String name, String etag1s) {
      SwiftObject object1s = getApi().getObjectApi(regionId, containerName).get(name);
      assertThat(object1s.getETag()).isEqualTo(etag1s);
      assertThat(object1s.getPayload().getContentMetadata().getContentLength()).isEqualTo(Long.valueOf(1024L * 1024L));
   }

   protected void deleteAllObjectsInContainerDLO(String regionId, final String containerName) {
       ObjectList objects = getApi().getObjectApi(regionId, containerName).list(new ListContainerOptions());
      if (objects == null) {
         return;
      }
      for (SwiftObject object : objects) {
         String name = containerName + "/" + object.getName();
         getApi().getObjectApi(regionId, containerName).delete(name);
      }
   }
}
