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

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.CREATED;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotEquals;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.util.UUID;

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainUtils;
import org.jclouds.googlecloudstorage.domain.ResumableUpload;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiLiveTest;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.io.ByteSource;

public class ResumableUploadApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   private static final String BUCKET_NAME = "resumableuploadbucket" + UUID.randomUUID();
   private static final String UPLOAD_OBJECT_NAME = "jcloudslogo.jpg";
   private static final String CHUNKED_OBJECT_NAME = "jclouds.pdf";
   private static final int INCOMPLETE = 308;
   private static final long MIN_CHUNK_SIZE = 256 * 1024; // Min allowed size for a chunk

   private ResumableUploadApi api() {
      return api.getResumableUploadApi();
   }

   @BeforeClass
   private void createBucket() {
      BucketTemplate template = new BucketTemplate().name(BUCKET_NAME);
      Bucket bucket = api.getBucketApi().createBucket(PROJECT_NUMBER, template);
      assertNotNull(bucket);
   }

   @Test(groups = "live")
   public void testResumableJpegUpload() throws IOException {

      // Read Object
      long contentLength = MIN_CHUNK_SIZE * 4;
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, contentLength);

      // Initialize resumableUpload with metadata. ObjectTemaplete must provide the name
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      ObjectTemplate template = new ObjectTemplate();
      template.contentType("image/jpeg").addAcl(oacl).size(contentLength).name(UPLOAD_OBJECT_NAME)
               .contentLanguage("en").contentDisposition("attachment");

      ResumableUpload initResponse = api().initResumableUpload(BUCKET_NAME, "image/jpeg", contentLength, template);

      assertNotNull(initResponse);
      assertEquals(initResponse.getStatusCode().intValue(), OK.getStatusCode());
      assertNotNull(initResponse.getUploadId());

      String uploadId = initResponse.getUploadId();

      // Upload the payload
      ByteSourcePayload payload = Payloads.newByteSourcePayload(byteSource);
      ResumableUpload uploadResponse = api().upload(BUCKET_NAME, uploadId, "image/jpeg", byteSource.read().length + "",
               payload);

      assertEquals(uploadResponse.getStatusCode().intValue(), OK.getStatusCode());

      // CheckStatus
      ResumableUpload status = api().checkStatus(BUCKET_NAME, uploadId, "bytes */*");

      int code = status.getStatusCode();
      assertNotEquals(code, INCOMPLETE);
   }

   @Test(groups = "live")
   public void testResumableChunkedUpload() throws IOException, InterruptedException {

      // Read Object
      long contentLength = MIN_CHUNK_SIZE * 3;
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, contentLength);

      // Initialize resumableUpload with metadata. ObjectTemaplete must provide the name
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      ObjectTemplate template = new ObjectTemplate();
      template.contentType("application/pdf").addAcl(oacl).size(contentLength).name(CHUNKED_OBJECT_NAME)
               .contentLanguage("en").contentDisposition("attachment");

      ResumableUpload initResponse = api().initResumableUpload(BUCKET_NAME, "application/pdf", contentLength, template);

      assertNotNull(initResponse);
      assertEquals(initResponse.getStatusCode().intValue(), OK.getStatusCode());
      assertNotNull(initResponse.getUploadId());

      // Get the upload_id for the session
      String uploadId = initResponse.getUploadId();

      // Check the status first
      ResumableUpload status = api().checkStatus(BUCKET_NAME, uploadId, "bytes */*");
      int code = status.getStatusCode();
      assertEquals(code, INCOMPLETE);

      // Uploads in 2 chunks.
      long totalSize = byteSource.read().length;
      long offset = 0;
      // Size of the first chunk
      long chunkSize = MIN_CHUNK_SIZE * 2;

      // Uploading First chunk
      ByteSourcePayload payload = Payloads.newByteSourcePayload(byteSource.slice(offset, chunkSize));
      long length = byteSource.slice(offset, chunkSize).size();
      String Content_Range = DomainUtils.generateContentRange(0L, length, totalSize);
      ResumableUpload uploadResponse = api().chunkUpload(BUCKET_NAME, uploadId, "application/pdf", length,
               Content_Range, payload);

      int code2 = uploadResponse.getStatusCode();
      assertEquals(code2, INCOMPLETE);

      // Read uploaded length
      long lowerValue = uploadResponse.getRangeLowerValue();
      long uploaded = uploadResponse.getRangeUpperValue();

      assertThat(lowerValue).isEqualTo(0);
      assertThat(uploaded).isEqualTo(chunkSize - 1); // confirms chunk is totally uploaded

      long resumeLength = totalSize - (uploaded + 1);

      // 2nd chunk
      ByteSourcePayload payload2 = Payloads.newByteSourcePayload(byteSource.slice(uploaded + 1,
               byteSource.read().length - uploaded - 1));
      // Upload the 2nd chunk
      String Content_Range2 = DomainUtils.generateContentRange(uploaded + 1, totalSize - 1, totalSize);
      ResumableUpload resumeResponse = api().chunkUpload(BUCKET_NAME, uploadId, "application/pdf", resumeLength,
               Content_Range2, payload2);

      int code3 = resumeResponse.getStatusCode();
      assertThat(code3).isIn(OK.getStatusCode(), CREATED.getStatusCode()); // 200 or 201 if upload succeeded
   }

   @AfterClass
   private void deleteObjectsandBucket() {
      api.getObjectApi().deleteObject(BUCKET_NAME, UPLOAD_OBJECT_NAME);
      api.getObjectApi().deleteObject(BUCKET_NAME, CHUNKED_OBJECT_NAME);
      api.getBucketApi().deleteBucket(BUCKET_NAME);
   }
}
