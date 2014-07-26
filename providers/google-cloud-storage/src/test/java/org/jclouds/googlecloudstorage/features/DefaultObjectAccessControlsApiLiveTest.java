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
import org.jclouds.googlecloudstorage.domain.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.ListDefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiLiveTest;
import org.testng.annotations.Test;

public class DefaultObjectAccessControlsApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   protected static final String BUCKET_NAME = "jcloudtestbucketdefaultoacl" + UUID.randomUUID();

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
      DefaultObjectAccessControlsTemplate template = new DefaultObjectAccessControlsTemplate().entity("allUsers").role(
               ObjectRole.READER);

      DefaultObjectAccessControls response = api().createDefaultObjectAccessControls(BUCKET_NAME, template);

      assertNotNull(response);
      assertEquals(response.getEntity(), "allUsers");
      assertEquals(response.getRole(), ObjectRole.READER);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateDefaultObjectAcl")
   public void testUpdateDefaultObjectAcl() {
      DefaultObjectAccessControls defaultObjectAcl = DefaultObjectAccessControls.builder().bucket(BUCKET_NAME)
               .entity("allUsers").role(ObjectRole.OWNER).build();
      DefaultObjectAccessControls response = api().updateDefaultObjectAccessControls(BUCKET_NAME, "allUsers",
               defaultObjectAcl);

      assertNotNull(response);
      assertEquals(response.getEntity(), "allUsers");
      assertEquals(response.getRole(), ObjectRole.OWNER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateDefaultObjectAcl")
   public void testGetDefaultObjectAcl() {
      DefaultObjectAccessControls response = api().getDefaultObjectAccessControls(BUCKET_NAME, "allUsers");

      assertNotNull(response);
      assertEquals(response.getEntity(), "allUsers");
      assertEquals(response.getRole(), ObjectRole.OWNER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateDefaultObjectAcl")
   public void testListDefaultObjectAcl() {
      ListDefaultObjectAccessControls response = api().listDefaultObjectAccessControls(BUCKET_NAME);

      assertNotNull(response);
      assertEquals(response.getKind(), Kind.OBJECT_ACCESS_CONTROLS);
      assertNotNull(response.getItems());
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateDefaultObjectAcl")
   public void testPatchDefaultObjectAcl() {
      DefaultObjectAccessControls defaultObjectAcl = DefaultObjectAccessControls.builder().bucket(BUCKET_NAME)
               .entity("allUsers").role(ObjectRole.READER).build();
      DefaultObjectAccessControls response = api().patchDefaultObjectAccessControls(BUCKET_NAME, "allUsers",
               defaultObjectAcl);

      assertNotNull(response);
      assertEquals(response.getEntity(), "allUsers");
      assertEquals(response.getRole(), ObjectRole.READER);
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
