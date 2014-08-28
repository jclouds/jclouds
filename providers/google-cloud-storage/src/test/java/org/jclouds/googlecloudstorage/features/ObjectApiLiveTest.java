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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.DestinationPredefinedAcl;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Projection;
import org.jclouds.googlecloudstorage.domain.GCSObject;
import org.jclouds.googlecloudstorage.domain.ListPage;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ComposeObjectTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiLiveTest;
import org.jclouds.googlecloudstorage.options.ComposeObjectOptions;
import org.jclouds.googlecloudstorage.options.CopyObjectOptions;
import org.jclouds.googlecloudstorage.options.DeleteObjectOptions;
import org.jclouds.googlecloudstorage.options.GetObjectOptions;
import org.jclouds.googlecloudstorage.options.InsertObjectOptions;
import org.jclouds.googlecloudstorage.options.ListObjectOptions;
import org.jclouds.googlecloudstorage.options.UpdateObjectOptions;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public class ObjectApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   private static final String BUCKET_NAME = "jcloudsobjectoperations" + UUID.randomUUID();
   private static final String BUCKET_NAME2 = "jcloudobjectdestination" + UUID.randomUUID();
   private static final String UPLOAD_OBJECT_NAME = "objectOperation.txt";
   private static final String UPLOAD_OBJECT_NAME2 = "jcloudslogo.jpg";
   private static final String MULTIPART_UPLOAD_OBJECT = "multipart_related.jpg";
   private static final String COPIED_OBJECT_NAME = "copyofObjectOperation.txt";
   private static final String COMPOSED_OBJECT = "ComposedObject1.txt";
   private static final String COMPOSED_OBJECT2 = "ComposedObject2.json";

   private PayloadEnclosingImpl testPayload;
   private Long RANDOM_LONG = 100L;

   private Long metageneration;
   private Long generation;
   private HashCode hcMd5;
   private HashCode hcCrc32c;

   private ObjectApi api() {
      return api.getObjectApi();
   }

   // Create the buckets
   @BeforeClass
   private void createBucket() {
      BucketTemplate template = new BucketTemplate().name(BUCKET_NAME);
      Bucket bucket = api.getBucketApi().createBucket(PROJECT_NUMBER, template);
      assertNotNull(bucket);

      BucketTemplate template2 = new BucketTemplate().name(BUCKET_NAME2);
      Bucket bucket2 = api.getBucketApi().createBucket(PROJECT_NUMBER, template2);
      assertNotNull(bucket2);
   }

   // Object Operations
   @Test(groups = "live")
   public void testSimpleUpload() throws IOException {
      long contentLength = 512L;
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, contentLength);
      ByteSourcePayload byteSourcePayload = Payloads.newByteSourcePayload(byteSource);

      PayloadEnclosingImpl payload = new PayloadEnclosingImpl();
      payload.setPayload(byteSourcePayload);
      payload.getPayload().getContentMetadata().setContentLength(contentLength);

      this.testPayload = payload;

      InsertObjectOptions options = new InsertObjectOptions().name(UPLOAD_OBJECT_NAME);

      GCSObject gcsObject = api().simpleUpload(BUCKET_NAME, "text/plain",
               payload.getPayload().getContentMetadata().getContentLength(), payload.getPayload(), options);

      assertNotNull(gcsObject);
      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testSimpleUpload")
   public void testDownload() throws IOException {
      PayloadEnclosingImpl impl = api().download(BUCKET_NAME, UPLOAD_OBJECT_NAME);
      ContentMetadata meta = impl.getPayload().getContentMetadata();
      assertNotNull(impl);
      assertNotNull(impl.getPayload());
      assertNotNull(meta);
      assertEquals(meta.getContentType(), "text/plain");

      testPayload.getPayload().getContentMetadata().setContentDisposition("attachement");

      assertEquals(ByteStreams2.toByteArrayAndClose(impl.getPayload().openStream()),
               ByteStreams2.toByteArrayAndClose(testPayload.getPayload().openStream()));
   }

   @Test(groups = "live", dependsOnMethods = "testSimpleUpload")
   public void testSimpleJpegUpload() throws IOException {
      long contentLength = 2 * 1024L;
      ByteSource testSource = TestUtils.randomByteSource().slice(0, contentLength);
      ByteSourcePayload payload = Payloads.newByteSourcePayload(testSource);

      InsertObjectOptions options = new InsertObjectOptions().name(UPLOAD_OBJECT_NAME2);

      GCSObject gcsObject = api().simpleUpload(BUCKET_NAME, "image/jpeg", contentLength, payload, options);

      assertNotNull(gcsObject);
      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME2);

      // This is a client side validation of md5
      HashFunction hf = Hashing.md5();
      hcMd5 = hf.newHasher().putBytes(testSource.read()).hash();

      assertEquals(gcsObject.getMd5HashCode(), hcMd5);

      // crc32c validation
      HashFunction hfCrc32c = Hashing.crc32c();
      hcCrc32c = hfCrc32c.newHasher().putBytes(testSource.read()).hash();

      assertEquals(gcsObject.getCrc32cHashcode(), hcCrc32c);
   }

   @Test(groups = "live", dependsOnMethods = "testSimpleUpload")
   public void testGetObject() {
      GCSObject gcsObject = api().getObject(BUCKET_NAME, UPLOAD_OBJECT_NAME);

      assertNotNull(gcsObject);

      metageneration = gcsObject.getMetageneration();
      generation = gcsObject.getGeneration();

      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME);
      assertEquals(gcsObject.getContentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testGetObject")
   public void testGetObjectWithOptions() {
      GetObjectOptions options = new GetObjectOptions().ifGenerationMatch(generation)
               .ifMetagenerationMatch(metageneration).ifGenerationNotMatch(generation + 1).projection(Projection.FULL);

      GCSObject gcsObject = api().getObject(BUCKET_NAME, UPLOAD_OBJECT_NAME, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME);
      assertEquals(gcsObject.getContentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testGetObject")
   public void testCopyObject() throws IOException {
      GCSObject gcsObject = api().copyObject(BUCKET_NAME2, COPIED_OBJECT_NAME, BUCKET_NAME, UPLOAD_OBJECT_NAME);

      assertNotNull(gcsObject);
      assertEquals(gcsObject.getBucket(), BUCKET_NAME2);
      assertEquals(gcsObject.getName(), COPIED_OBJECT_NAME);
      assertEquals(gcsObject.getContentType(), "text/plain");

      // Test for data

      PayloadEnclosingImpl impl = api().download(BUCKET_NAME2, COPIED_OBJECT_NAME);
      assertNotNull(impl);
      assertEquals(ByteStreams2.toByteArrayAndClose(impl.getPayload().openStream()),
               ByteStreams2.toByteArrayAndClose(testPayload.getPayload().openStream()));

   }

   @Test(groups = "live", dependsOnMethods = "testCopyObject")
   public void testCopyObjectWithOptions() {
      CopyObjectOptions options = new CopyObjectOptions().ifSourceGenerationMatch(generation)
               .ifSourceMetagenerationMatch(metageneration).projection(Projection.FULL);

      GCSObject gcsObject = api()
               .copyObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME, BUCKET_NAME, UPLOAD_OBJECT_NAME, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME2);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME);
      assertEquals(gcsObject.getContentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testCopyObjectWithOptions")
   public void testComposeObject() {
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      ObjectTemplate destination = new ObjectTemplate().contentType("text/plain").addAcl(oacl);
      Set<GCSObject> sourceList = Sets.newHashSet();
      sourceList.add(api().getObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME));
      sourceList.add(api().getObject(BUCKET_NAME2, COPIED_OBJECT_NAME));

      ComposeObjectTemplate requestTemplate = new ComposeObjectTemplate().sourceObjects(sourceList).destination(
               destination);

      GCSObject gcsObject = api().composeObjects(BUCKET_NAME2, COMPOSED_OBJECT, requestTemplate);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME2);
      assertEquals(gcsObject.getName(), COMPOSED_OBJECT);
      assertEquals(gcsObject.getContentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObject")
   public void testComposeObjectWithOptions() {
      ObjectTemplate destination = new ObjectTemplate().contentType(MediaType.APPLICATION_JSON);
      Set<GCSObject> sourceList = Sets.newHashSet();
      sourceList.add(api().getObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME));
      sourceList.add(api().getObject(BUCKET_NAME2, COPIED_OBJECT_NAME));

      ComposeObjectTemplate requestTemplate = new ComposeObjectTemplate().sourceObjects(sourceList).destination(
               destination);

      ComposeObjectOptions options = new ComposeObjectOptions().destinationPredefinedAcl(
               DestinationPredefinedAcl.BUCKET_OWNER_READ).ifMetagenerationNotMatch(RANDOM_LONG);

      GCSObject gcsObject = api().composeObjects(BUCKET_NAME2, COMPOSED_OBJECT2, requestTemplate, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME2);
      assertEquals(gcsObject.getName(), COMPOSED_OBJECT2);
      assertEquals(gcsObject.getContentType(), MediaType.APPLICATION_JSON);
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObjectWithOptions")
   public void listObjects() {
      ListPage<GCSObject> list = api().listObjects(BUCKET_NAME);

      assertNotNull(list);
      assertEquals(list.get(0) instanceof GCSObject, true);
      assertEquals(list.getKind(), Kind.OBJECTS);
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObjectWithOptions")
   public void testListObjectsWithOptions() {
      ListObjectOptions options = new ListObjectOptions().maxResults(1);
      ListPage<GCSObject> list = api().listObjects(BUCKET_NAME, options);

      while (list.nextMarker().isPresent()) {
         assertNotNull(list);
         assertEquals(list.get(0) instanceof GCSObject, true);
         assertEquals(list.size(), 1);
         assertEquals(list.getKind(), Kind.OBJECTS);

         options = new ListObjectOptions().maxResults(1).pageToken(list.getNextPageToken());
         list = api().listObjects(BUCKET_NAME, options);
      }
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObjectWithOptions")
   public void testUpdateObject() {

      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      ObjectTemplate template = new ObjectTemplate().addAcl(oacl).contentType("image/jpeg");
      GCSObject gcsObject = api().updateObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.getContentType(), "image/jpeg");
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateObject")
   public void testUpdateObjectWithOptions() {
      String METADATA_KEY = "key1";
      String METADATA_VALUE = "value1";

      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      UpdateObjectOptions options = new UpdateObjectOptions().ifMetagenerationNotMatch(RANDOM_LONG)
               .ifGenerationNotMatch(RANDOM_LONG);

      ObjectTemplate template = new ObjectTemplate().addAcl(oacl).contentType("image/jpeg")
               .contentDisposition("attachment").customMetadata(METADATA_KEY, METADATA_VALUE);
      GCSObject gcsObject = api().updateObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.getContentType(), "image/jpeg");
      assertNotNull(gcsObject.getAllMetadata());
      assertNotNull(gcsObject.getAllMetadata().get(METADATA_KEY), METADATA_VALUE);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateObjectWithOptions")
   public void testPatchObject() {
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.READER).build();

      ObjectTemplate template = new ObjectTemplate().addAcl(oacl).contentType("image/jpeg");
      GCSObject gcsObject = api().patchObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.getContentType(), "image/jpeg");
   }

   @Test(groups = "live", dependsOnMethods = "testPatchObject")
   public void testPatchObjectsWithOptions() {
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      UpdateObjectOptions options = new UpdateObjectOptions().ifMetagenerationNotMatch(RANDOM_LONG)
               .ifGenerationNotMatch(RANDOM_LONG);

      ObjectTemplate template = new ObjectTemplate().addAcl(oacl).contentType("image/jpeg")
               .contentDisposition("attachment");
      GCSObject gcsObject = api().patchObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.getAcl());
      assertEquals(gcsObject.getBucket(), BUCKET_NAME);
      assertEquals(gcsObject.getName(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.getContentType(), "image/jpeg");
      assertEquals(gcsObject.getContentDisposition(), "attachment");
   }

   @Test(groups = "live", dependsOnMethods = "testPatchObjectsWithOptions")
   public void testMultipartJpegUpload() throws IOException {
      long contentLength = 32 * 1024L;
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, contentLength);
      ByteSourcePayload payload = Payloads.newByteSourcePayload(byteSource);
      PayloadEnclosingImpl payloadImpl = new PayloadEnclosingImpl(payload);

      ObjectTemplate template = new ObjectTemplate();

      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      // This would trigger server side validation of md5
      hcMd5 = byteSource.hash(Hashing.md5());

      // This would trigger server side validation of crc32c
      hcCrc32c = byteSource.hash(Hashing.crc32c());

      template.contentType("image/jpeg").addAcl(oacl).size(contentLength).name(MULTIPART_UPLOAD_OBJECT)
               .contentLanguage("en").contentDisposition("attachment").md5Hash(hcMd5)
               .customMetadata("custommetakey1", "custommetavalue1").crc32c(hcCrc32c)
               .customMetadata(ImmutableMap.of("Adrian", "powderpuff"));

      GCSObject gcsObject = api().multipartUpload(BUCKET_NAME, template, payloadImpl.getPayload());

      assertThat(gcsObject.getBucket()).isEqualTo(BUCKET_NAME);
      assertThat(gcsObject.getName()).isEqualTo(MULTIPART_UPLOAD_OBJECT);
      assertThat(gcsObject.getMd5HashCode()).isEqualTo(hcMd5);
      assertThat(gcsObject.getCrc32cHashcode()).isEqualTo(hcCrc32c);

      assertThat(gcsObject.getAllMetadata()).contains(entry("custommetakey1", "custommetavalue1"),
               entry("Adrian", "powderpuff")).doesNotContainKey("adrian");

      PayloadEnclosingImpl impl = api().download(BUCKET_NAME, MULTIPART_UPLOAD_OBJECT);

      assertThat(ByteStreams2.toByteArrayAndClose(impl.getPayload().openStream())).isEqualTo(
               ByteStreams2.toByteArrayAndClose(payloadImpl.getPayload().openStream()));
   }

   @Test(groups = "live", dependsOnMethods = "testMultipartJpegUpload")
   public void testDeleteObject() {
      api().deleteObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME);
      api().deleteObject(BUCKET_NAME2, COMPOSED_OBJECT2);
      api().deleteObject(BUCKET_NAME2, COMPOSED_OBJECT);
      api().deleteObject(BUCKET_NAME2, COPIED_OBJECT_NAME);
      api().deleteObject(BUCKET_NAME, UPLOAD_OBJECT_NAME);
      api().deleteObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2);
      api().deleteObject(BUCKET_NAME, MULTIPART_UPLOAD_OBJECT);
   }

   @Test(groups = "live", dependsOnMethods = "testPatchObjectsWithOptions")
   public void testDeleteObjectWithOptions() {
      DeleteObjectOptions options = new DeleteObjectOptions().ifGenerationMatch(generation).ifMetagenerationMatch(
               metageneration);
      api().deleteObject(BUCKET_NAME, UPLOAD_OBJECT_NAME, options);
   }

   @AfterClass
   private void deleteBucket() {
      api.getBucketApi().deleteBucket(BUCKET_NAME);
      api.getBucketApi().deleteBucket(BUCKET_NAME2);
   }
}
