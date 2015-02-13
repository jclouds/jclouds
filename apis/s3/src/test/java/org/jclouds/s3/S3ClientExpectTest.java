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
package org.jclouds.s3;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.s3.domain.DeleteResult;
import org.jclouds.s3.internal.BaseS3ClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "S3ClientExpectTest")
public class S3ClientExpectTest extends BaseS3ClientExpectTest {

   @Test
   public void testBucketExistsReturnsTrueOn200AndFalseOn404() {
      
      HttpRequest bucketFooExists = HttpRequest.builder().method("HEAD").endpoint(
               URI.create("http://localhost/foo")).headers(
               ImmutableMultimap.<String, String> builder()
                  .put("Date", CONSTANT_DATE)
                  .put("Authorization", "AWS identity:lLD0mzo2bZPIWhxlFDZoT09MKUQ=")
                  .build()).build();
      
      S3Client clientWhenBucketExists = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(200).build());
      assert clientWhenBucketExists.bucketExists("foo");
      
      S3Client clientWhenBucketDoesntExist = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(404).build());
      assert !clientWhenBucketDoesntExist.bucketExists("foo");
      
   }

   @Test
   public void testDeleteMultipleObjects() {
      final String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<Delete>" +
         "<Object><Key>key1</Key></Object>" +
         "<Object><Key>key2</Key></Object>" +
         "</Delete>";

      final Payload requestPayload = Payloads.newStringPayload(request);
      requestPayload.getContentMetadata().setContentType("text/xml");
      requestPayload.getContentMetadata().setContentMD5(md5().hashString(request, UTF_8));

      final String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<DeleteResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n" +
         "  <Deleted>\n" +
         "    <Key>key1</Key>\n" +
         "  </Deleted>\n" +
         "  <Deleted>\n" +
         "    <Key>key1.1</Key>\n" +
         "  </Deleted>\n" +
         "  <Error>\n" +
         "    <Key>key2</Key>\n" +
         "    <Code>AccessDenied</Code>\n" +
         "    <Message>Access Denied</Message>\n" +
         "  </Error>\n" +
         "</DeleteResult>";

      final Payload responsePayload = Payloads.newStringPayload(response);
      responsePayload.getContentMetadata().setContentType("text/xml");

      S3Client client = requestSendsResponse(
         HttpRequest.builder()
                    .method("POST")
                    .endpoint("http://localhost/test?delete")
                    .addHeader("Date", CONSTANT_DATE)
                    .addHeader("Authorization", "AWS identity:XptAJrBvfz68TEfPkhXj4R58uvE=")
                    .payload(requestPayload)
                    .build(),
         HttpResponse.builder()
                     .statusCode(200)
                     .addHeader("x-amz-request-id", "7A84C3CD4437A4C0")
                     .addHeader("Date", CONSTANT_DATE)
                     .addHeader("ETag", "437b930db84b8079c2dd804a71936b5f")
                     .addHeader("Server", "AmazonS3")
                     .payload(responsePayload)
                     .build()
      );

      DeleteResult result = client.deleteObjects("test", ImmutableSet.of("key1", "key2"));
      assertNotNull(result, "result is null");

      assertEquals(result.getDeleted(), ImmutableSet.of("key1", "key1.1"));
      assertEquals(result.getErrors().size(), 1);

      assertEquals(result.getErrors().get("key2"), new DeleteResult.Error("AccessDenied", "Access Denied"));
   }
}
