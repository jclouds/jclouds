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
package org.jclouds.aws.s3;

import static org.jclouds.aws.s3.blobstore.options.AWSS3PutOptions.Builder.storageClass;
import static org.jclouds.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.domain.Location;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.S3ClientLiveTest;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadata.StorageClass;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code S3Client}
 */
@Test(groups = "live", singleThreaded = true, testName = "AWSS3ClientLiveTest")
public class AWSS3ClientLiveTest extends S3ClientLiveTest {
   public AWSS3ClientLiveTest() {
      provider = "aws-s3";
   }

   @Override
   public AWSS3Client getApi() {
      return view.unwrapApi(AWSS3Client.class);
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
   }

   public void testPutWithReducedRedundancyStorage() throws InterruptedException {
      String containerName = getContainerName();
      try {
         String blobName = "test-rrs";
         BlobStore blobStore = view.getBlobStore();
         blobStore.createContainerInLocation(null, containerName);

         Blob blob = blobStore.blobBuilder(blobName).payload("something").build();
         blobStore.putBlob(containerName, blob,
            storageClass(StorageClass.REDUCED_REDUNDANCY));

         S3Client s3Client = view.unwrapApi(S3Client.class);
         ListBucketResponse response = s3Client.listBucket(containerName, withPrefix(blobName));

         ObjectMetadata metadata = response.iterator().next();
         assertEquals(metadata.getStorageClass(), StorageClass.REDUCED_REDUNDANCY);

      } finally {
         returnContainer(containerName);
      }
   }

   /**
    * http://code.google.com/p/jclouds/issues/detail?id=992
    */
   public void testUseBucketWithUpperCaseName() throws Exception {
      String bucketName = CONTAINER_PREFIX + "-TestBucket";
      String blobName = "TestBlob.txt";
      StorageMetadata container = null;
      BlobStore store = view.getBlobStore();

      // Create and use a valid bucket name with uppercase characters in the bucket name (US regions only)
      try {
         store.createContainerInLocation(null, bucketName);

         for (StorageMetadata metadata : store.list()) {
            if (metadata.getName().equals(bucketName)) {
               container = metadata;
               break;
            }
         }

         assertNotNull(container);

         store.putBlob(bucketName, store.blobBuilder(blobName)
                                          .payload("This is a test!")
                                          .contentType("text/plain")
                                          .build());

         assertNotNull(store.getBlob(bucketName, blobName));
      } finally {
         if (container != null) {
            store.deleteContainer(bucketName);
         }
      }

      // Try to create the same bucket successfully created above in one of the non-US regions to ensure an error is
      // encountered as expected.
      Location location = null;

      for (Location pLocation : store.listAssignableLocations()) {
         if (!ImmutableSet.of(Region.US_STANDARD, Region.US_EAST_1, Region.US_WEST_1, Region.US_WEST_2)
            .contains(pLocation.getId())) {
            location = pLocation;
            break;
         }
      }

      try {
         store.createContainerInLocation(location, bucketName);
         fail("Should had failed because in non-US regions, mixed-case bucket names are invalid.");
      } catch (AWSResponseException e) {
         assertEquals("InvalidBucketName", e.getError().getCode());
      }
   }

   public void testDirectoryEndingWithSlash() throws InterruptedException {
	   String containerName = getContainerName();
	   try {
		   BlobStore blobStore = view.getBlobStore();
		   blobStore.createDirectory(containerName, "someDir");

		   // According to the S3 documentation, a directory is nothing but a blob
		   // whose name ends with a '/'. So let's try to remove the blob we just
		   // created.
		   blobStore.removeBlob(containerName, "someDir/");

		   // The directory "someDir" shouldn't exist since we removed it. If this
		   // test succeeds, it confirms that a directory (or folder) is nothing
		   // but a blob with a name ending in '/'.
		   assertEquals(blobStore.directoryExists(containerName, "someDir"), false);
	   } finally {
		   returnContainer(containerName);
	   }
   }
}
