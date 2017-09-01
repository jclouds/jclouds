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

import static com.google.common.io.BaseEncoding.base64;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.DestinationPredefinedAcl;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Projection;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.googlecloudstorage.domain.ListPageWithPrefixes;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.RewriteResponse;
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
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public class ObjectApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   private static final String BUCKET_NAME = "jcloudsobjectoperations" + UUID.randomUUID();
   private static final String BUCKET_NAME2 = "jcloudsobjectdestination" + UUID.randomUUID();
   private static final String UPLOAD_OBJECT_NAME = "objectOperation.txt";
   private static final String UPLOAD_OBJECT_NAME2 = "jcloudslogo.jpg";
   private static final String MULTIPART_UPLOAD_OBJECT = "multipart_related.jpg";
   private static final String COPIED_OBJECT_NAME = "copyofObjectOperation.txt";
   private static final String COPIED_OBJECT_NAME2 = "copyObjectWithMeta.txt";
   private static final String COMPOSED_OBJECT = "ComposedObject1.txt";
   private static final String COMPOSED_OBJECT2 = "ComposedObject2.json";
   private static final String NONEXISTENT_OBJECT_NAME = "noSuchObject.txt";
   private static final String REWRITE_OBJECT_NAME = "rewriteObject.txt";

   private PayloadEnclosing testPayload;
   private Long RANDOM_LONG = 100L;

   private Long metageneration;
   private Long generation;
   private String md5Hash;
   private String crc32c;

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

      PayloadEnclosing payload = new PayloadEnclosingImpl(byteSourcePayload);
      payload.getPayload().getContentMetadata().setContentLength(contentLength);

      this.testPayload = payload;

      InsertObjectOptions options = new InsertObjectOptions().name(UPLOAD_OBJECT_NAME);

      GoogleCloudStorageObject gcsObject = api().simpleUpload(BUCKET_NAME, "text/plain",
               payload.getPayload().getContentMetadata().getContentLength(), payload.getPayload(), options);

      assertNotNull(gcsObject);
      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testSimpleUpload")
   public void testRewrite() throws IOException {
      GoogleCloudStorageObject gcsObject = api().getObject(BUCKET_NAME, UPLOAD_OBJECT_NAME);
      System.out.println(gcsObject);

      RewriteResponse response = api().rewriteObjects(BUCKET_NAME, REWRITE_OBJECT_NAME, BUCKET_NAME, UPLOAD_OBJECT_NAME);
      assertNotNull(response);
      assertTrue(response.done());
      assertEquals(response.objectSize(), 512);
      assertEquals(response.totalBytesRewritten(), 512);
      assertEquals(response.rewriteToken(), null);
      assertNotNull(response.resource());
   }

   @Test(groups = "live", dependsOnMethods = "testRewrite")
   public void testDownload() throws IOException {
      PayloadEnclosing impl = api().download(BUCKET_NAME, UPLOAD_OBJECT_NAME);
      ContentMetadata meta = impl.getPayload().getContentMetadata();
      assertNotNull(impl);
      assertNotNull(impl.getPayload());
      assertNotNull(meta);
      assertEquals(meta.getContentType(), "text/plain");

      testPayload.getPayload().getContentMetadata().setContentDisposition("attachment");

      assertEquals(ByteStreams2.toByteArrayAndClose(impl.getPayload().openStream()),
               ByteStreams2.toByteArrayAndClose(testPayload.getPayload().openStream()));
   }

   @Test(groups = "live", dependsOnMethods = "testSimpleUpload")
   public void testSimpleJpegUpload() throws IOException {
      long contentLength = 2 * 1024L;
      ByteSource testSource = TestUtils.randomByteSource().slice(0, contentLength);
      ByteSourcePayload payload = Payloads.newByteSourcePayload(testSource);

      InsertObjectOptions options = new InsertObjectOptions().name(UPLOAD_OBJECT_NAME2);

      GoogleCloudStorageObject gcsObject = api().simpleUpload(BUCKET_NAME, "image/jpeg", contentLength, payload, options);

      assertNotNull(gcsObject);
      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME2);

      // This is a client side validation of md5
      md5Hash = base64().encode(testSource.hash(Hashing.md5()).asBytes());
      // TODO: crc32c = without making a compile dep on guava 18.

      checkHashCodes(gcsObject);
   }

   @Test(groups = "live", dependsOnMethods = "testSimpleUpload")
   public void testGetObject() {
      GoogleCloudStorageObject gcsObject = api().getObject(BUCKET_NAME, UPLOAD_OBJECT_NAME);

      assertNotNull(gcsObject);

      metageneration = gcsObject.metageneration();
      generation = gcsObject.generation();

      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME);
      assertEquals(gcsObject.contentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testGetObject")
   public void testGetObjectWithOptions() {
      GetObjectOptions options = new GetObjectOptions().ifGenerationMatch(generation)
               .ifMetagenerationMatch(metageneration).ifGenerationNotMatch(generation + 1).projection(Projection.FULL);

      GoogleCloudStorageObject gcsObject = api().getObject(BUCKET_NAME, UPLOAD_OBJECT_NAME, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME);
      assertEquals(gcsObject.contentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testGetObject")
   public void testCopyObject() throws IOException {
      GoogleCloudStorageObject gcsObject = api().copyObject(BUCKET_NAME2, COPIED_OBJECT_NAME, BUCKET_NAME, UPLOAD_OBJECT_NAME);

      assertNotNull(gcsObject);
      assertEquals(gcsObject.bucket(), BUCKET_NAME2);
      assertEquals(gcsObject.name(), COPIED_OBJECT_NAME);
      assertEquals(gcsObject.contentType(), "text/plain");

      // Test for data

      PayloadEnclosing impl = api().download(BUCKET_NAME2, COPIED_OBJECT_NAME);
      assertNotNull(impl);
      assertEquals(ByteStreams2.toByteArrayAndClose(impl.getPayload().openStream()),
               ByteStreams2.toByteArrayAndClose(testPayload.getPayload().openStream()));

   }

   @Test(groups = "live", dependsOnMethods = "testGetObject")
   public void testCopyObjectWithUpdatedMetadata() throws IOException {
      String METADATA_KEY = "key1";
      String METADATA_VALUE = "value1";

      ObjectTemplate template = new ObjectTemplate().contentLanguage("fr").contentType("text/plain")
               .contentDisposition("attachment").customMetadata(METADATA_KEY, METADATA_VALUE);

      GoogleCloudStorageObject gcsObject = api().copyObject(BUCKET_NAME2, COPIED_OBJECT_NAME2, BUCKET_NAME, UPLOAD_OBJECT_NAME, template);

      assertNotNull(gcsObject);
      assertEquals(gcsObject.bucket(), BUCKET_NAME2);
      assertEquals(gcsObject.name(), COPIED_OBJECT_NAME2);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.contentType(), "text/plain");
      assertEquals(gcsObject.metadata().get(METADATA_KEY), METADATA_VALUE);
      assertEquals(gcsObject.contentLanguage(), "fr");
      // Test for data

      PayloadEnclosing impl = api().download(BUCKET_NAME2, COPIED_OBJECT_NAME2);
      assertNotNull(impl);
      assertEquals(ByteStreams2.toByteArrayAndClose(impl.getPayload().openStream()),
               ByteStreams2.toByteArrayAndClose(testPayload.getPayload().openStream()));
   }

   @Test(groups = "live", dependsOnMethods = "testCopyObject")
   public void testCopyObjectWithOptions() {
      CopyObjectOptions options = new CopyObjectOptions().ifSourceGenerationMatch(generation)
               .ifSourceMetagenerationMatch(metageneration).projection(Projection.FULL);

      GoogleCloudStorageObject gcsObject = api()
               .copyObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME, BUCKET_NAME, UPLOAD_OBJECT_NAME, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME2);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME);
      assertEquals(gcsObject.contentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testCopyObjectWithOptions")
   public void testComposeObject() {
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      ObjectTemplate destination = new ObjectTemplate().contentType("text/plain").addAcl(oacl);
      List<GoogleCloudStorageObject> sourceList = Lists.newArrayList();
      sourceList.add(api().getObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME));
      sourceList.add(api().getObject(BUCKET_NAME2, COPIED_OBJECT_NAME));

      ComposeObjectTemplate requestTemplate = ComposeObjectTemplate.builder()
         .fromGoogleCloudStorageObject(sourceList)
         .destination(destination)
         .build();

      GoogleCloudStorageObject gcsObject = api().composeObjects(BUCKET_NAME2, COMPOSED_OBJECT, requestTemplate);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME2);
      assertEquals(gcsObject.name(), COMPOSED_OBJECT);
      assertEquals(gcsObject.contentType(), "text/plain");
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObject")
   public void testComposeObjectWithOptions() {
      ObjectTemplate destination = new ObjectTemplate().contentType(MediaType.APPLICATION_JSON);
      List<GoogleCloudStorageObject> sourceList = Lists.newArrayList();
      sourceList.add(api().getObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME));
      sourceList.add(api().getObject(BUCKET_NAME2, COPIED_OBJECT_NAME));

      ComposeObjectTemplate requestTemplate =  ComposeObjectTemplate.builder()
         .fromGoogleCloudStorageObject(sourceList)
         .destination(destination)
         .build();
      ComposeObjectOptions options = new ComposeObjectOptions().destinationPredefinedAcl(
               DestinationPredefinedAcl.BUCKET_OWNER_READ).ifMetagenerationNotMatch(RANDOM_LONG);

      GoogleCloudStorageObject gcsObject = api().composeObjects(BUCKET_NAME2, COMPOSED_OBJECT2, requestTemplate, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME2);
      assertEquals(gcsObject.name(), COMPOSED_OBJECT2);
      assertEquals(gcsObject.contentType(), MediaType.APPLICATION_JSON);
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObjectWithOptions")
   public void listObjects() {
      ListPageWithPrefixes<GoogleCloudStorageObject> list = api().listObjects(BUCKET_NAME);

      assertNotNull(list);
      assertEquals(list.get(0) instanceof GoogleCloudStorageObject, true);
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObjectWithOptions")
   public void testListObjectsWithOptions() {
      ListObjectOptions options = new ListObjectOptions().maxResults(1);
      ListPageWithPrefixes<GoogleCloudStorageObject> list = api().listObjects(BUCKET_NAME, options);

      while (list.nextPageToken() != null) {
         assertNotNull(list);
         assertEquals(list.get(0) instanceof GoogleCloudStorageObject, true);
         assertEquals(list.size(), 1);

         options = new ListObjectOptions().maxResults(1).pageToken(list.nextPageToken());
         list = api().listObjects(BUCKET_NAME, options);
      }
   }

   @Test(groups = "live", dependsOnMethods = "testComposeObjectWithOptions")
   public void testUpdateObject() {
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      ObjectTemplate template = new ObjectTemplate().addAcl(oacl).contentType("image/jpeg");
      GoogleCloudStorageObject gcsObject = api().updateObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.contentType(), "image/jpeg");
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
      GoogleCloudStorageObject gcsObject = api().updateObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.contentType(), "image/jpeg");
      assertNotNull(gcsObject.metadata());
      assertNotNull(gcsObject.metadata().get(METADATA_KEY), METADATA_VALUE);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateObjectWithOptions")
   public void testPatchObject() {
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.READER).build();

      ObjectTemplate template = new ObjectTemplate().addAcl(oacl).contentType("image/jpeg");
      GoogleCloudStorageObject gcsObject = api().patchObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.contentType(), "image/jpeg");
   }

   @Test(groups = "live", dependsOnMethods = "testPatchObject")
   public void testPatchObjectsWithOptions() {
      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      UpdateObjectOptions options = new UpdateObjectOptions().ifMetagenerationNotMatch(RANDOM_LONG)
               .ifGenerationNotMatch(RANDOM_LONG);


      ObjectTemplate template = new ObjectTemplate().addAcl(oacl).contentType("image/jpeg")
               .contentDisposition("attachment");
      GoogleCloudStorageObject gcsObject = api().patchObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2, template, options);

      assertNotNull(gcsObject);
      assertNotNull(gcsObject.acl());
      assertEquals(gcsObject.bucket(), BUCKET_NAME);
      assertEquals(gcsObject.name(), UPLOAD_OBJECT_NAME2);
      assertEquals(gcsObject.contentType(), "image/jpeg");
      assertEquals(gcsObject.contentDisposition(), "attachment");
   }

   @Test(groups = "live", dependsOnMethods = "testPatchObjectsWithOptions")
   public void testMultipartJpegUpload() throws IOException {
      long contentLength = 32 * 1024L;
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, contentLength);
      ByteSourcePayload payload = Payloads.newByteSourcePayload(byteSource);
      PayloadEnclosing payloadImpl = new PayloadEnclosingImpl(payload);

      ObjectTemplate template = new ObjectTemplate();

      ObjectAccessControls oacl = ObjectAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(ObjectRole.OWNER).build();

      // This would trigger server side validation of md5
      md5Hash = base64().encode(byteSource.hash(Hashing.md5()).asBytes());
      // TODO: crc32c = without making a compile dep on guava 18

      template.contentType("image/jpeg").addAcl(oacl).size(contentLength).name(MULTIPART_UPLOAD_OBJECT)
               .contentLanguage("en").contentDisposition("attachment").md5Hash(md5Hash)
               .storageClass(StorageClass.NEARLINE)
               .customMetadata("custommetakey1", "custommetavalue1").crc32c(crc32c)
               .customMetadata(ImmutableMap.of("Adrian", "powderpuff"));

      GoogleCloudStorageObject gcsObject = api().multipartUpload(BUCKET_NAME, template, payloadImpl.getPayload());

      assertThat(gcsObject.bucket()).isEqualTo(BUCKET_NAME);
      assertThat(gcsObject.name()).isEqualTo(MULTIPART_UPLOAD_OBJECT);
      checkHashCodes(gcsObject);

      assertThat(gcsObject.metadata()).contains(entry("custommetakey1", "custommetavalue1"),
               entry("Adrian", "powderpuff")).doesNotContainKey("adrian");

      gcsObject = api().getObject(BUCKET_NAME, MULTIPART_UPLOAD_OBJECT, null);

      assertThat(gcsObject).isNotNull();
      assertThat(gcsObject.storageClass()).isEqualTo(StorageClass.NEARLINE);

      PayloadEnclosing impl = api().download(BUCKET_NAME, MULTIPART_UPLOAD_OBJECT);

      assertThat(ByteStreams2.toByteArrayAndClose(impl.getPayload().openStream())).isEqualTo(
               ByteStreams2.toByteArrayAndClose(payloadImpl.getPayload().openStream()));
   }

   private void checkHashCodes(GoogleCloudStorageObject gcsObject) {
      assertEquals(gcsObject.md5Hash(), md5Hash);
      if (crc32c != null) {
         assertEquals(gcsObject.crc32c(), crc32c);
      }
   }

   @Test(groups = "live", dependsOnMethods = "testMultipartJpegUpload")
   public void testDeleteObject() {
      assertTrue(api().deleteObject(BUCKET_NAME2, UPLOAD_OBJECT_NAME));
      assertTrue(api().deleteObject(BUCKET_NAME2, COMPOSED_OBJECT2));
      assertTrue(api().deleteObject(BUCKET_NAME2, COMPOSED_OBJECT));
      assertTrue(api().deleteObject(BUCKET_NAME2, COPIED_OBJECT_NAME));
      assertTrue(api().deleteObject(BUCKET_NAME2, COPIED_OBJECT_NAME2));
      assertFalse(api().deleteObject(BUCKET_NAME, UPLOAD_OBJECT_NAME));
      assertTrue(api().deleteObject(BUCKET_NAME, UPLOAD_OBJECT_NAME2));
      assertTrue(api().deleteObject(BUCKET_NAME, MULTIPART_UPLOAD_OBJECT));
      assertTrue(api().deleteObject(BUCKET_NAME, REWRITE_OBJECT_NAME));
      assertFalse(api().deleteObject(BUCKET_NAME, NONEXISTENT_OBJECT_NAME));
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
