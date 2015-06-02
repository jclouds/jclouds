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

import java.util.List;
import java.util.UUID;

import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiLiveTest;
import org.testng.annotations.Test;

public class DefaultObjectAccessControlsApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   protected static final String BUCKET_NAME = "jcloudsdefaultoacltest" + UUID.randomUUID();

   private DefaultObjectAccessControlsApi api() {
      return api.getDefaultObjectAccessControlsApi();
   }

   private void createBucket(String BucketName) {
      BucketTemplate template = new BucketTemplate().name(BucketName);
      Bucket response = api.getBucketApi().createBucket(PROJECT_NUMBER, template);
      assertNotNull(response);
   }

   @Test(groups = "live")
   public void testCreateDefaultObjectAcl() {
      createBucket(BUCKET_NAME);
      ObjectAccessControlsTemplate template = ObjectAccessControlsTemplate.create("allUsers", ObjectRole.READER);

      ObjectAccessControls response = api().createDefaultObjectAccessControls(BUCKET_NAME, template);

      assertNotNull(response);
      assertEquals(response.entity(), "allUsers");
      assertEquals(response.role(), ObjectRole.READER);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateDefaultObjectAcl")
   public void testUpdateDefaultObjectAcl() {
      ObjectAccessControls defaultObjectAcl = ObjectAccessControls.builder().bucket(BUCKET_NAME)
               .entity("allUsers").role(ObjectRole.OWNER).build();
      ObjectAccessControls response = api().updateDefaultObjectAccessControls(BUCKET_NAME, "allUsers", defaultObjectAcl);

      assertNotNull(response);
      assertEquals(response.entity(), "allUsers");
      assertEquals(response.role(), ObjectRole.OWNER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateDefaultObjectAcl")
   public void testGetDefaultObjectAcl() {
      ObjectAccessControls response = api().getDefaultObjectAccessControls(BUCKET_NAME, "allUsers");

      assertNotNull(response);
      assertEquals(response.entity(), "allUsers");
      assertEquals(response.role(), ObjectRole.OWNER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateDefaultObjectAcl")
   public void testListDefaultObjectAcl() {
      List<ObjectAccessControls> response = api().listDefaultObjectAccessControls(BUCKET_NAME);
      assertNotNull(response);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateDefaultObjectAcl")
   public void testPatchDefaultObjectAcl() {
      ObjectAccessControls defaultObjectAcl = ObjectAccessControls.builder().bucket(BUCKET_NAME)
               .entity("allUsers").role(ObjectRole.READER).build();
      ObjectAccessControls response = api().patchDefaultObjectAccessControls(BUCKET_NAME, "allUsers", defaultObjectAcl);

      assertNotNull(response);
      assertEquals(response.entity(), "allUsers");
      assertEquals(response.role(), ObjectRole.READER);
   }

   @Test(groups = "live", dependsOnMethods = "testPatchDefaultObjectAcl")
   public void testDeleteBucketAcl() {
      api().deleteDefaultObjectAccessControls(BUCKET_NAME, "allUsers");
      deleteBucket(BUCKET_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testDeleteBucketAcl")
   public void testDeleteNotExistingBucketAccessControls() {
      api().deleteDefaultObjectAccessControls(BUCKET_NAME, "allUsers");
   }

   private void deleteBucket(String bucketName) {
      api.getBucketApi().deleteBucket(bucketName);
   }
}
