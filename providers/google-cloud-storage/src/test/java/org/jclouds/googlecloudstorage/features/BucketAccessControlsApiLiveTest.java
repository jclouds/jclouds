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
package org.jclouds.googlecloudstorage.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.UUID;

import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Role;
import org.jclouds.googlecloudstorage.domain.ListBucketAccessControls;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiLiveTest;
import org.testng.annotations.Test;

public class BucketAccessControlsApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   protected static final String BUCKET_NAME = "jcloudstestbucketacl" + UUID.randomUUID();

   private BucketAccessControlsApi api() {
      return api.getBucketAccessControlsApi();
   }

   private void createBucket(String BucketName) {
      BucketTemplate template = new BucketTemplate().name(BucketName);
      Bucket response = api.getBucketApi().createBucket(PROJECT_NUMBER, template);
      assertNotNull(response);
   }

   @Test(groups = "live")
   public void testCreateBucketAcl() {
      createBucket(BUCKET_NAME);
      BucketAccessControls bucketAcl = BucketAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(Role.READER).build();
      BucketAccessControls response = api().createBucketAccessControls(BUCKET_NAME, bucketAcl);

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
   }

   @Test(groups = "live", dependsOnMethods = "testCreateBucketAcl")
   public void testUpdateBucketAcl() {
      BucketAccessControls bucketAcl = BucketAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(Role.WRITER).build();
      BucketAccessControls response = api().updateBucketAccessControls(BUCKET_NAME, "allUsers", bucketAcl);

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
      assertEquals(response.getRole(), Role.WRITER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBucketAcl")
   public void testGetBucketAcl() {
      BucketAccessControls response = api().getBucketAccessControls(BUCKET_NAME, "allUsers");

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
      assertEquals(response.getRole(), Role.WRITER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBucketAcl")
   public void testListBucketAcl() {
      ListBucketAccessControls response = api().listBucketAccessControls(BUCKET_NAME);

      assertNotNull(response);
      assertEquals(response.getKind(), Kind.BUCKET_ACCESS_CONTROLS);
      assertNotNull(response.getItems());
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBucketAcl")
   public void testPatchBucketAcl() {
      BucketAccessControls bucketAcl = BucketAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(Role.READER).build();
      BucketAccessControls response = api().patchBucketAccessControls(BUCKET_NAME, "allUsers", bucketAcl);

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
      assertEquals(response.getRole(), Role.READER);
   }

   @Test(groups = "live", dependsOnMethods = "testPatchBucketAcl")
   public void testDeleteBucketAcl() {
      api().deleteBucketAccessControls(BUCKET_NAME, "allUsers");
      deleteBucket(BUCKET_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testDeleteBucketAcl")
   public void testDeleteNotExistingBucketAccessControls() {
      api().deleteBucketAccessControls(BUCKET_NAME, "allUsers");
   }

   private void deleteBucket(String BucketName) {
      api.getBucketApi().deleteBucket(BucketName);
   }
}
