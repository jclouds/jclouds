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

import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.STORAGE_FULLCONTROL_SCOPE;
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.STORAGE_READONLY_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Projection;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Role;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiExpectTest;
import org.jclouds.googlecloudstorage.options.GetBucketOptions;
import org.jclouds.googlecloudstorage.options.ListOptions;
import org.jclouds.googlecloudstorage.options.UpdateBucketOptions;
import org.jclouds.googlecloudstorage.parse.BucketUpdateTest;
import org.jclouds.googlecloudstorage.parse.NoAclBucketTest;
import org.jclouds.googlecloudstorage.parse.NoAclBucketListTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class BucketApiExpectTest extends BaseGoogleCloudStorageApiExpectTest {

   private static final String EXPECTED_TEST_BUCKET = "bhashbucket";
   private static final String EXPECTED_TEST_PROJECT_NUMBER = "1082289308625";

   private static final HttpRequest GET_BUCKET_REQUEST = HttpRequest.builder().method("GET")
            .endpoint("https://www.googleapis.com/storage/v1/b/bhashbucket").addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN).build();

   private static final HttpRequest GET_BUCKET_REQUEST_WITHOPTIONS = HttpRequest.builder().method("GET")
            .endpoint("https://www.googleapis.com/storage/v1/b/bhashbucket")
            .addQueryParam("ifMetagenerationNotMatch", "100").addQueryParam("projection", "full")
            .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

   private final HttpResponse BUCKET_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/noAcl_bucket.json")).build();

   public static final HttpRequest LIST_BUCKET_REQUEST = HttpRequest.builder().method("GET")
            .endpoint("https://www.googleapis.com/storage/v1/b").addQueryParam("project", EXPECTED_TEST_PROJECT_NUMBER)
            .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpRequest LIST_BUCKET_REQUEST_WITHOPTIONS = HttpRequest.builder().method("GET")
            .endpoint("https://www.googleapis.com/storage/v1/b").addQueryParam("project", EXPECTED_TEST_PROJECT_NUMBER)
            .addQueryParam("maxResults", "2").addQueryParam("pageToken", "jcloudtestbucket500")
            .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

   private final HttpResponse LIST_BUCKET_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/noAcl_bucket_list.json")).build();

   // Test getBucket without options
   public void testGetBucketWithNoOptionsResponseIs2xx() throws Exception {

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_READONLY_SCOPE), TOKEN_RESPONSE,
               GET_BUCKET_REQUEST, BUCKET_RESPONSE).getBucketApi();

      assertEquals(api.getBucket(EXPECTED_TEST_BUCKET), new NoAclBucketTest().expected());
   }

   public void testGetBucketResponseIs4xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_READONLY_SCOPE), TOKEN_RESPONSE,
               GET_BUCKET_REQUEST, getResponse).getBucketApi();

      assertNull("404", api.getBucket(EXPECTED_TEST_BUCKET));
   }

   // Test getBucket with options
   public void testGetBucketWithOptionsResponseIs2xx() throws Exception {

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_READONLY_SCOPE), TOKEN_RESPONSE,
               GET_BUCKET_REQUEST_WITHOPTIONS, BUCKET_RESPONSE).getBucketApi();

      GetBucketOptions options = new GetBucketOptions().ifMetagenerationNotMatch(Long.valueOf(100)).projection(
               Projection.FULL);
      assertEquals(api.getBucket(EXPECTED_TEST_BUCKET, options), new NoAclBucketTest().expected());
   }

   // Test listBucket without options
   public void testListBucketWithNoOptionsResponseIs2xx() throws Exception {

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               LIST_BUCKET_REQUEST, LIST_BUCKET_RESPONSE).getBucketApi();

      assertEquals(api.listBucket(EXPECTED_TEST_PROJECT_NUMBER), new NoAclBucketListTest().expected());

   }

   public void testListBucketWithOptionsResponseIs2xx() throws Exception {

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               LIST_BUCKET_REQUEST_WITHOPTIONS, LIST_BUCKET_RESPONSE).getBucketApi();

      ListOptions options = new ListOptions().maxResults(2).pageToken("jcloudtestbucket500");

      assertEquals(api.listBucket(EXPECTED_TEST_PROJECT_NUMBER, options), new NoAclBucketListTest().expected());

   }

   public void testListBucketResponseIs4xx() throws Exception {
      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               LIST_BUCKET_REQUEST, listResponse).getBucketApi();

      assertNull(api.listBucket(EXPECTED_TEST_PROJECT_NUMBER));
   }

   // Test createBucket without options
   public void testCreateBucketWithNoOptionsResponseIs2xx() throws Exception {

      HttpRequest createRequest = HttpRequest
               .builder()
               .method("POST")
               .endpoint("https://www.googleapis.com/storage/v1/b")
               .addHeader("Accept", "application/json")
               .addQueryParam("project", EXPECTED_TEST_PROJECT_NUMBER)
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/bucket_insert_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               createRequest, BUCKET_RESPONSE).getBucketApi();

      BucketTemplate template = new BucketTemplate().name("bhashbucket");

      assertEquals(api.createBucket(EXPECTED_TEST_PROJECT_NUMBER, template), new NoAclBucketTest().expected());

   }

   public void testUpdateBucketWithNoOptionsResponseIs2xx() throws Exception {

      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(EXPECTED_TEST_BUCKET)
               .entity("allAuthenticatedUsers").role(Role.OWNER).build();

      BucketTemplate template = new BucketTemplate().name(EXPECTED_TEST_BUCKET).addAcl(bucketacl);

      HttpRequest updateRequest = HttpRequest
               .builder()
               .method("PUT")
               .endpoint("https://www.googleapis.com/storage/v1/b/" + EXPECTED_TEST_BUCKET)
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/bucket_update_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/bucket_update_response.json")).build();

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               updateRequest, updateResponse).getBucketApi();

      assertEquals(api.updateBucket(EXPECTED_TEST_BUCKET, template), new BucketUpdateTest().expected());
   }

   public void testUpdateBucketWithOptionsResponseIs2xx() throws Exception {

      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(EXPECTED_TEST_BUCKET)
               .entity("allAuthenticatedUsers").role(Role.OWNER).build();
      UpdateBucketOptions options = new UpdateBucketOptions().projection(Projection.NO_ACL).ifMetagenerationNotMatch(
               Long.valueOf(100));
      BucketTemplate template = new BucketTemplate().name(EXPECTED_TEST_BUCKET).addAcl(bucketacl);

      HttpRequest updateRequest = HttpRequest
               .builder()
               .method("PUT")
               .endpoint("https://www.googleapis.com/storage/v1/b/" + EXPECTED_TEST_BUCKET)
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .addQueryParam("projection", Projection.NO_ACL.toString())
               .addQueryParam("ifMetagenerationNotMatch", "100")
               .payload(payloadFromResourceWithContentType("/bucket_update_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/bucket_update_response.json")).build();

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               updateRequest, updateResponse).getBucketApi();

      assertEquals(api.updateBucket(EXPECTED_TEST_BUCKET, template, options), new BucketUpdateTest().expected());
   }

   public void testPatchBucketWithNoOptionsResponseIs2xx() throws Exception {

      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(EXPECTED_TEST_BUCKET)
               .entity("allAuthenticatedUsers").role(Role.OWNER).build();

      BucketTemplate template = new BucketTemplate().name(EXPECTED_TEST_BUCKET).addAcl(bucketacl);

      HttpRequest patchRequest = HttpRequest
               .builder()
               .method("PATCH")
               .endpoint("https://www.googleapis.com/storage/v1/b/" + EXPECTED_TEST_BUCKET)
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/bucket_update_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse patchResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/bucket_update_response.json")).build();

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE, patchRequest,
               patchResponse).getBucketApi();

      assertEquals(api.patchBucket(EXPECTED_TEST_BUCKET, template), new BucketUpdateTest().expected());
   }

   public void testPatchBucketWithOptionsResponseIs2xx() throws Exception {

      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(EXPECTED_TEST_BUCKET)
               .entity("allAuthenticatedUsers").role(Role.OWNER).build();
      UpdateBucketOptions options = new UpdateBucketOptions().projection(Projection.NO_ACL).ifMetagenerationNotMatch(
               Long.valueOf(100));
      BucketTemplate template = new BucketTemplate().name(EXPECTED_TEST_BUCKET).addAcl(bucketacl);

      HttpRequest patchRequest = HttpRequest
               .builder()
               .method("PUT")
               .endpoint("https://www.googleapis.com/storage/v1/b/" + EXPECTED_TEST_BUCKET)
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .addQueryParam("projection", Projection.NO_ACL.toString())
               .addQueryParam("ifMetagenerationNotMatch", "100")
               .payload(payloadFromResourceWithContentType("/bucket_update_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse patchResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/bucket_update_response.json")).build();

      BucketApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE, patchRequest,
               patchResponse).getBucketApi();

      assertEquals(api.updateBucket(EXPECTED_TEST_BUCKET, template, options), new BucketUpdateTest().expected());
   }
}
