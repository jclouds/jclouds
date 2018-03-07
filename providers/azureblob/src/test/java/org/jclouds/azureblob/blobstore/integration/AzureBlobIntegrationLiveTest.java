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
package org.jclouds.azureblob.blobstore.integration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.MediaType;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.blobstore.options.CopyOptions;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static com.google.common.hash.Hashing.md5;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test(groups = "live")
public class AzureBlobIntegrationLiveTest extends BaseBlobIntegrationTest {
   @Override
   protected long getMinimumMultipartBlobSize() {
      return view.getBlobStore().getMaximumMultipartPartSize() + 1;
   }

   public AzureBlobIntegrationLiveTest() {
      provider = "azureblob";
   }

   // TODO: Azure response has a quoted ETag but request requires unquoted ETag
   @Override
   public void testGetIfMatch() throws InterruptedException {
      throw new SkipException("not yet implemented");
   }

   public void testCreateBlobWithExpiry() throws InterruptedException {
      throw new SkipException("Expires header unsupported: http://msdn.microsoft.com/en-us/library/windowsazure/dd179404.aspx#Subheading3");
   }

   @Override
   @Test
   public void testPutObjectStream() throws InterruptedException, IOException, ExecutionException {
      throw new SkipException("Azure requires a Content-Length");
   }

   @Test(groups = { "integration", "live" })
   public void testSetBlobAccess() throws Exception {
      throw new SkipException("unsupported in Azure");
   }

   @Test(groups = { "integration", "live" }, expectedExceptions = UnsupportedOperationException.class)
   public void testPutBlobAccess() throws Exception {
      super.testPutBlobAccess();
   }

   @Test(groups = { "integration", "live" }, expectedExceptions = UnsupportedOperationException.class)
   public void testPutBlobAccessMultipart() throws Exception {
      super.testPutBlobAccessMultipart();
   }

   @Test(groups = { "integration", "live" })
   public void testSigningOfUppercaseMetadata() throws InterruptedException {
      String containerName = getContainerName();
      String blobName = "testSigningOfUppercaseMetadata";

      Blob blob = view.getBlobStore().blobBuilder(blobName)
          .userMetadata(ImmutableMap.of("B", "b", "a", "a"))
          .payload(TEST_STRING).contentType(MediaType.TEXT_PLAIN)
          .contentMD5(md5().hashString(TEST_STRING, Charsets.UTF_8))
          .build();

      try {
         assertNull(view.getBlobStore().blobMetadata(containerName, blobName));

         addBlobToContainer(containerName, blob);
         assertConsistencyAwareContainerSize(containerName, 1);

         view.getBlobStore().copyBlob(
             containerName, blobName, containerName, blobName,
             CopyOptions.builder().userMetadata(ImmutableMap.of("B", "b", "a", "a")).build()
         );

         Blob newObject = view.getBlobStore().getBlob(containerName, blobName);

         assertNotNull(newObject);
         assertEquals(newObject.getMetadata().getUserMetadata().get("b"), "b");
         assertEquals(newObject.getMetadata().getUserMetadata().get("a"), "a");

      } finally {
         returnContainer(containerName);
      }
   }
}
