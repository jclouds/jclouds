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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Projection;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Role;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.ListPage;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.domain.internal.BucketCors;
import org.jclouds.googlecloudstorage.domain.internal.Logging;
import org.jclouds.googlecloudstorage.domain.internal.Versioning;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiLiveTest;
import org.jclouds.googlecloudstorage.options.DeleteBucketOptions;
import org.jclouds.googlecloudstorage.options.GetBucketOptions;
import org.jclouds.googlecloudstorage.options.InsertBucketOptions;
import org.jclouds.googlecloudstorage.options.UpdateBucketOptions;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class BucketApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   private static final String BUCKET_NAME = "jcloudtestbucket" + (int) (Math.random() * 10000);

   private static final String BUCKET_NAME_WITHOPTIONS = "jcloudtestbucketoptions" + (int) (Math.random() * 10000);

   private static final String LOG_BUCKET_NAME = "jcloudtestbucket" + (int) (Math.random() * 10000);

   private Long metageneration;

   private BucketApi api() {
      return api.getBucketsApi();
   }

   @Test(groups = "live")
   public void testCreateBucket() {

      BucketTemplate logTemplate = new BucketTemplate().name(LOG_BUCKET_NAME);
      Bucket logResponse = api().createBuckets(PROJECT_NUMBER, logTemplate);
      assertNotNull(logResponse);

      BucketAccessControls acl = BucketAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers").role(Role.OWNER)
               .build();
      DefaultObjectAccessControls oac = DefaultObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();
      BucketCors bucketCors = BucketCors.builder().addOrigin("http://example.appspot.com").addMethod("GET").addMethod("HEAD")
               .addResponseHeader("x-meta-goog-custom").maxAgeSeconds(10).build();
      Versioning version = Versioning.builder().enalbled(true).build();

      Logging log = new Logging(LOG_BUCKET_NAME, BUCKET_NAME);

      BucketTemplate template = new BucketTemplate().name(BUCKET_NAME).addAcl(acl)
               .addDefaultObjectAccessControls(oac).versioning(version).location(Location.US_CENTRAL2).logging(log)
               .storageClass(StorageClass.DURABLE_REDUCED_AVAILABILITY).addCORS(bucketCors);

      Bucket response = api().createBuckets(PROJECT_NUMBER, template);

      assertNotNull(response);
      assertNotNull(response.getCors());
      assertEquals(response.getKind(), Kind.BUCKET);
      assertEquals(response.getName(), BUCKET_NAME);
      assertEquals(response.getLocation(), Location.US_CENTRAL2);
      assertTrue(response.getVersioning().isEnabled());
   }

   @Test(groups = "live")
   public void testCreateBucketWithOptions() {

      DefaultObjectAccessControls oac = DefaultObjectAccessControls.builder().bucket(BUCKET_NAME_WITHOPTIONS)
               .entity("allUsers").role(ObjectRole.OWNER).build();
      BucketCors bucketCors = BucketCors.builder().addOrigin("http://example.appspot.com").addMethod("GET").addMethod("HEAD")
               .addResponseHeader("x-meta-goog-custom").maxAgeSeconds(10).build();
      Versioning version = Versioning.builder().enalbled(true).build();

      BucketTemplate template = new BucketTemplate().name(BUCKET_NAME_WITHOPTIONS)
               .addDefaultObjectAccessControls(oac).versioning(version).location(Location.US_CENTRAL2)
               .storageClass(StorageClass.DURABLE_REDUCED_AVAILABILITY).addCORS(bucketCors);

      InsertBucketOptions options = new InsertBucketOptions().projection(Projection.FULL);

      Bucket response = api().createBuckets(PROJECT_NUMBER, template, options);

      assertNotNull(response);
      assertNotNull(response.getCors());
      assertEquals(response.getKind(), Kind.BUCKET);
      assertEquals(response.getName(), BUCKET_NAME_WITHOPTIONS);
      assertEquals(response.getLocation(), Location.US_CENTRAL2);
      assertTrue(response.getVersioning().isEnabled());
   }

   @Test(groups = "live", dependsOnMethods = "testCreateBucket")
   public void testUpdateBucket() {
      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(BUCKET_NAME)
               .entity("allAuthenticatedUsers").role(Role.OWNER).build();
      BucketTemplate template = new BucketTemplate().name(BUCKET_NAME).addAcl(bucketacl);
      Bucket response = api().updateBuckets(BUCKET_NAME, template);

      assertNotNull(response);
      assertEquals(response.getName(), BUCKET_NAME);
      assertNotNull(response.getAcl());
   }

   @Test(groups = "live", dependsOnMethods = "testCreateBucketWithOptions")
   public void testUpdateBucketWithOptions() {
      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(BUCKET_NAME_WITHOPTIONS)
               .entity("allAuthenticatedUsers").role(Role.OWNER).build();
      UpdateBucketOptions options = new UpdateBucketOptions().projection(Projection.FULL);
      BucketTemplate template = new BucketTemplate().name(BUCKET_NAME_WITHOPTIONS).addAcl(bucketacl);
      Bucket response = api().updateBuckets(BUCKET_NAME_WITHOPTIONS, template, options);

      assertNotNull(response);

      metageneration = response.getMetageneration();

      assertEquals(response.getName(), BUCKET_NAME_WITHOPTIONS);
      assertNotNull(response.getAcl());
   }

   @Test(groups = "live", dependsOnMethods = "testCreateBucket")
   public void testGetBucket() {
      Bucket response = api().getBuckets(BUCKET_NAME);

      assertNotNull(response);
      assertEquals(response.getName(), BUCKET_NAME);
      assertEquals(response.getKind(), Kind.BUCKET);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBucketWithOptions")
   public void testGetBucketWithOptions() {
      GetBucketOptions options = new GetBucketOptions().ifMetagenerationMatch(metageneration);
      Bucket response = api().getBuckets(BUCKET_NAME_WITHOPTIONS, options);

      assertNotNull(response);
      assertEquals(response.getName(), BUCKET_NAME_WITHOPTIONS);
      assertEquals(response.getKind(), Kind.BUCKET);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateBucket")
   public void testListBucket() {
      ListPage<Bucket> bucket = api().listBuckets(PROJECT_NUMBER);

      Iterator<Bucket> pageIterator = bucket.iterator();
      assertTrue(pageIterator.hasNext());

      Bucket singlePageIterator = pageIterator.next();
      List<Bucket> bucketAsList = Lists.newArrayList(singlePageIterator);

      assertNotNull(singlePageIterator);
      assertSame(bucketAsList.size(), 1);

   }

   @Test(groups = "live", dependsOnMethods = "testCreateBucket")
   public void testPatchBucket() {
      Logging logging = new Logging(LOG_BUCKET_NAME, BUCKET_NAME);
      BucketTemplate template = new BucketTemplate().name(BUCKET_NAME).logging(logging);

      Bucket response = api().patchBuckets(BUCKET_NAME, template);

      assertNotNull(response);
      assertEquals(response.getName(), BUCKET_NAME);
      assertEquals(response.getLogging().getLogBucket(), LOG_BUCKET_NAME);
   }

   @Test(groups = "live", dependsOnMethods = { "testListBucket", "testGetBucket", "testUpdateBucket" })
   public void testDeleteBucket() {
      HttpResponse response = api().deleteBuckets(BUCKET_NAME);
      HttpResponse response2 = api().deleteBuckets(LOG_BUCKET_NAME);

      assertNotNull(response);
      assertEquals(response.getStatusCode(), 204);
      assertNotNull(response2);
      assertEquals(response2.getStatusCode(), 204);

   }

   @Test(groups = "live", dependsOnMethods = { "testGetBucketWithOptions" })
   public void testDeleteBucketWithOptions() {

      DeleteBucketOptions options = new DeleteBucketOptions().ifMetagenerationMatch(metageneration)
               .ifMetagenerationNotMatch(metageneration + 1);

      HttpResponse response = api().deleteBuckets(BUCKET_NAME_WITHOPTIONS, options);

      assertNotNull(response);
      assertEquals(response.getStatusCode(), 204);
   }

}
