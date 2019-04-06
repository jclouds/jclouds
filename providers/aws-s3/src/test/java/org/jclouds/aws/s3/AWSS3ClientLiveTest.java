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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.aws.s3.blobstore.options.AWSS3PutObjectOptions.Builder.storageClass;
import static org.jclouds.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import com.google.common.collect.Iterables;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.rest.HttpClient;
import org.jclouds.s3.S3ClientLiveTest;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadata.StorageClass;
import org.jclouds.s3.domain.S3Object;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

   public void testPutWithStorageClass() throws Exception {
      String containerName = getContainerName();
      AWSS3Client s3Client = getApi();
      try {
         for (StorageClass storageClass : StorageClass.values()) {
            String blobName = "test-" + storageClass;
            BlobStore blobStore = view.getBlobStore();
            blobStore.createContainerInLocation(null, containerName);

            S3Object object = s3Client.newS3Object();
            object.getMetadata().setKey(blobName);
            object.setPayload("something");
            s3Client.putObject(containerName, object, storageClass(storageClass));

            ListBucketResponse response = s3Client.listBucket(containerName, withPrefix(blobName));

            ObjectMetadata metadata = response.iterator().next();
            assertThat(metadata.getStorageClass()).isEqualTo(storageClass);
         }

      } finally {
         returnContainer(containerName);
      }
   }

   /**
    * http://code.google.com/p/jclouds/issues/detail?id=992
    */
   public void testUseBucketWithUpperCaseName() throws Exception {
      String bucketName = CONTAINER_PREFIX + "-TestBucket";
      BlobStore store = view.getBlobStore();

       // As of March 1 2018, bucket names must be DNS compliant in all regions

      try {
         store.createContainerInLocation(null, bucketName);
         fail("Should have failed because mixed-case bucket names are invalid.");
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

   /**
    * Test signed get/put operations using signature v4. This is done by explicitly
    * using the "eu-central-1" region which only support signature v4.
    */
   public void testV4SignatureOps() throws InterruptedException {
       String containerName = getScratchContainerName() + "eu";
	   try {
           BlobStore blobStore = view.getBlobStore();
           Location location = Iterables.tryFind(blobStore.listAssignableLocations(),
               LocationPredicates.idEquals(Region.EU_CENTRAL_1)).orNull();
           assertNotNull(location);
           blobStore.createContainerInLocation(location, containerName);

           final HttpClient client = view.utils().http();
           String blobName = "test-blob";
           Blob blob = blobStore.blobBuilder(blobName).payload("something").build();

           // Signed put, no timeout.
           HttpRequest request = view.getSigner().signPutBlob(containerName, blob);
           assertNotNull(request);
           HttpResponse response = client.invoke(request);
           assertEquals(response.getStatusCode(), 200);

           // Signed get, no timeout.
           request = view.getSigner().signGetBlob(containerName, blobName);
           assertNotNull(request);
           response = client.invoke(request);
           assertEquals(response.getStatusCode(), 200);

           blobStore.removeBlob(containerName, blobName);

           // Signed put with timeout.
           request = view.getSigner().signPutBlob(containerName, blob, /*seconds=*/ 60);
           assertNotNull(request);
           response = client.invoke(request);
           assertEquals(response.getStatusCode(), 200);

           // Signed get with timeout.
           request = view.getSigner().signGetBlob(containerName, blobName, /*seconds=*/ 60);
           assertNotNull(request);
           response = client.invoke(request);
           assertEquals(response.getStatusCode(), 200);

           // Cleanup the container.
           blobStore.removeBlob(containerName, blobName);
       } finally {
           destroyContainer(containerName);
       }
   }
}
