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
package org.jclouds.googlecloudstorage.blobstore.integration;

import static com.google.common.collect.Iterables.get;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.googlecloud.internal.TestProperties;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;

public class GoogleCloudStorageContainerIntegrationLiveTest extends BaseContainerIntegrationTest {

   public GoogleCloudStorageContainerIntegrationLiveTest() {
      provider = "google-cloud-storage";
   }

   @Override protected Properties setupProperties() {
      TestProperties.setGoogleCredentialsFromJson(provider);
      return TestProperties.apply(provider, super.setupProperties());
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testWithDetails() throws InterruptedException, IOException {
      String key = "hello";
      String containerName = getContainerName();
      try {
         addBlobToContainer(
                  containerName,
                  view.getBlobStore().blobBuilder(key).userMetadata(ImmutableMap.of("adrian", "powderpuff"))
                           .payload(TEST_STRING).contentType(MediaType.TEXT_PLAIN)
                           .contentMD5(Hashing.md5().newHasher().putString(TEST_STRING, Charsets.UTF_8).hash()).build());
         validateContent(containerName, key);

         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName,
                  maxResults(1).withDetails());

         BlobMetadata metadata = BlobMetadata.class.cast(get(container, 0));

         assert metadata.getContentMetadata().getContentType().startsWith("text/plain") : metadata.getContentMetadata()
                  .getContentType();
         assertEquals(metadata.getContentMetadata().getContentLength(), Long.valueOf(TEST_STRING.length()));
         assertEquals(metadata.getUserMetadata().get("adrian"), "powderpuff");
         checkMD5(metadata);
      } finally {
         returnContainer(containerName);
      }
   }

   /** Google Cloud Storage lists prefixes and objects in two different lists */
   @Override
   public void testListRootUsesDelimiter() throws InterruptedException {
      String containerName = getContainerName();
      try {
         String prefix = "rootdelimiter";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);
         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName,
                  new ListContainerOptions());
         assertNull(container.getNextMarker());
         assertEquals(container.size(), 15);
      } finally {
         returnContainer(containerName);
      }
   }

   @Override
   public void testDirectory() throws InterruptedException {
      // GoogleCloudStorage does not support directories, rather it supports prefixes which look like directories.
      throw new SkipException("directories are not supported in GoogleCloudStorage");
   }

   @Override
   public void testListMarkerAfterLastKey() throws Exception {
      throw new SkipException("cannot specify arbitrary markers");
   }

   @Override
   public void testContainerListWithPrefix() {
      throw new SkipException("Prefix option has not been plumbed down to GCS");
   }

   @Override
   public void testDelimiterList() {
      throw new SkipException("Prefix option has not been plumbed down to GCS");
   }
}
