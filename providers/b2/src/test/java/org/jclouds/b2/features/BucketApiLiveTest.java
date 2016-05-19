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
package org.jclouds.labs.b2.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.util.Random;

import org.jclouds.labs.b2.domain.Bucket;
import org.jclouds.labs.b2.domain.BucketList;
import org.jclouds.labs.b2.domain.BucketType;
import org.jclouds.labs.b2.internal.BaseB2ApiLiveTest;
import org.testng.annotations.Test;

public final class BucketApiLiveTest extends BaseB2ApiLiveTest {
   private static final String BUCKET_NAME = "jcloudstestbucket" + new Random().nextInt(Integer.MAX_VALUE);

   @Test(groups = "live")
   public void testCreateBucket() {
      BucketApi bucketApi = api.getBucketApi();

      Bucket response = bucketApi.createBucket(BUCKET_NAME, BucketType.ALL_PRIVATE);
      try {
         assertThat(response.bucketName()).isEqualTo(BUCKET_NAME);
         assertThat(response.bucketType()).isEqualTo(BucketType.ALL_PRIVATE);
      } finally {
         response = bucketApi.deleteBucket(response.bucketId());
         assertThat(response.bucketName()).isEqualTo(BUCKET_NAME);
         assertThat(response.bucketType()).isEqualTo(BucketType.ALL_PRIVATE);
      }
   }

   @Test(groups = "live")
   public void testDeleteAlreadyDeletedBucket() {
      BucketApi bucketApi = api.getBucketApi();

      Bucket response = bucketApi.createBucket(BUCKET_NAME, BucketType.ALL_PRIVATE);
      response = bucketApi.deleteBucket(response.bucketId());

      response = bucketApi.deleteBucket(response.bucketId());
      assertThat(response).isNull();
   }

   @Test(groups = "live")
   public void testDeleteInvalidBucketId() {
      BucketApi bucketApi = api.getBucketApi();

      try {
         bucketApi.deleteBucket("4a48fe8875c6214145260818");
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         assertThat(iae.getMessage()).isEqualTo("bucketId not valid for account");
      }
   }

   @Test(groups = "live")
   public void testUpdateBucket() {
      BucketApi bucketApi = api.getBucketApi();

      Bucket response = bucketApi.createBucket(BUCKET_NAME, BucketType.ALL_PRIVATE);
      try {
         response = bucketApi.updateBucket(response.bucketId(), BucketType.ALL_PUBLIC);
         assertThat(response.bucketName()).isEqualTo(BUCKET_NAME);
         assertThat(response.bucketType()).isEqualTo(BucketType.ALL_PUBLIC);
      } finally {
         response = bucketApi.deleteBucket(response.bucketId());
         assertThat(response.bucketName()).isEqualTo(BUCKET_NAME);
      }
   }

   @Test(groups = "live")
   public void testListBuckets() {
      BucketApi bucketApi = api.getBucketApi();

      Bucket response = bucketApi.createBucket(BUCKET_NAME, BucketType.ALL_PRIVATE);
      try {
         boolean found = false;
         BucketList buckets = bucketApi.listBuckets();
         for (Bucket bucket : buckets.buckets()) {
            if (bucket.bucketName().equals(BUCKET_NAME)) {
               assertThat(response.bucketType()).isEqualTo(BucketType.ALL_PRIVATE);
               found = true;
            }
         }
         assertThat(found).isTrue();
      } finally {
         response = bucketApi.deleteBucket(response.bucketId());
         assertThat(response.bucketName()).isEqualTo(BUCKET_NAME);
      }
   }
}
