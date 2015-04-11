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

import static com.google.common.hash.Hashing.md5;
import static org.jclouds.io.Payloads.newByteArrayPayload;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceETagDoesntMatch;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceETagMatches;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceModifiedSince;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceUnmodifiedSince;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.overrideAcl;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.overrideMetadataWith;
import static org.jclouds.s3.options.PutObjectOptions.Builder.withAcl;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.Payload;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.DeleteResult;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.util.Strings2;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import com.google.common.io.ByteSource;

@Test(groups = { "integration", "live" })
public class S3ClientLiveTest extends BaseBlobStoreIntegrationTest {
   public static final String TEST_ACL_ID = "1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c";
   public static final String TEST_ACL_EMAIL = "james@misterm.org";
   public static final String DEFAULT_OWNER_ID = "abc123";
   private static final ByteSource oneHundredOneConstitutions = TestUtils.randomByteSource().slice(0, 5 * 1024 * 1024 + 1);

   public S3ClientLiveTest() {
      this.provider = "s3";
   }
   
   public S3Client getApi() {
      return view.unwrapApi(S3Client.class);
   }

   /**
    * this method overrides containerName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyNotFound() throws Exception {
      assert getApi().deleteBucketIfEmpty("dbienf");
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyButHasContents() throws Exception {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, "test");
         assert !getApi().deleteBucketIfEmpty(containerName);
      } finally {
         returnContainer(containerName);
      }
   }

   protected URL getObjectURL(String containerName, String key) throws Exception {
      URL url = new URL(String.format("http://%s.%s/%s", containerName, URI.create(endpoint).getHost(), key));
      return url;
   }

   public void testPutCannedAccessPolicyPublic() throws Exception {
      String containerName = getContainerName();
      try {
         String key = "hello";
         S3Object object = getApi().newS3Object();
         object.getMetadata().setKey(key);
         object.setPayload(TEST_STRING);
         getApi().putObject(containerName, object,

         withAcl(CannedAccessPolicy.PUBLIC_READ));

         URL url = this.getObjectURL(containerName, key);
         Strings2.toStringAndClose(url.openStream());
      } finally {
         returnContainer(containerName);
      }

   }

   public void testCopyCannedAccessPolicyPublic() throws Exception {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         addBlobToContainer(containerName, sourceKey);
         validateContent(containerName, sourceKey);

         getApi().copyObject(containerName, sourceKey, destinationContainer, destinationKey,
                  overrideAcl(CannedAccessPolicy.PUBLIC_READ));

         validateContent(destinationContainer, destinationKey);

         URL url = getObjectURL(destinationContainer, destinationKey);
         Strings2.toStringAndClose(url.openStream());

      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

   String sourceKey = "apples";
   String destinationKey = "pears";

   public void testPublicWriteOnObject() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      final String publicReadWriteObjectKey = "public-read-write-acl";
      final String containerName = getContainerName();
      try {
         S3Object object = getApi().newS3Object();
         object.getMetadata().setKey(publicReadWriteObjectKey);
         object.setPayload("");
         // Public Read-Write object
         getApi()
                  .putObject(containerName, object,
                           new PutObjectOptions().withAcl(CannedAccessPolicy.PUBLIC_READ_WRITE));

         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = getApi().getObjectACL(containerName, publicReadWriteObjectKey);
                  assertEquals(acl.getGrants().size(), 3);
                  assertEquals(acl.getPermissions(GroupGranteeURI.ALL_USERS).size(), 2);
                  assertNotNull(acl.getOwner());
                  String ownerId = acl.getOwner().getId();
                  assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.WRITE));
                  assertFalse(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ_ACP));
                  assertFalse(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.WRITE_ACP));
                  assertFalse(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.FULL_CONTROL));
               } catch (Exception e) {
                  Throwables.propagateIfPossible(e);
               }
            }
         });
      } finally {
         returnContainer(containerName);
      }

   }

   public void testUpdateObjectACL() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerName = getContainerName();
      try {
         String objectKey = "private-acl";

         // Private object
         addBlobToContainer(containerName, objectKey);
         AccessControlList acl = getApi().getObjectACL(containerName, objectKey);
         String ownerId = acl.getOwner().getId();

         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(getApi().putObjectACL(containerName, objectKey, acl));

         // Confirm that the updated ACL has stuck.
         acl = getApi().getObjectACL(containerName, objectKey);
         checkGrants(acl);

         /*
          * Revoke all of owner's permissions!
          */
         acl.revokeAllPermissions(new CanonicalUserGrantee(ownerId));
         if (!ownerId.equals(TEST_ACL_ID))
            acl.revokeAllPermissions(new CanonicalUserGrantee(TEST_ACL_ID));
         assertEquals(acl.getGrants().size(), 1);
         // Only public read permission should remain...
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));

         // Update the object's ACL settings
         assertTrue(getApi().putObjectACL(containerName, objectKey, acl));

         // Confirm that the updated ACL has stuck
         acl = getApi().getObjectACL(containerName, objectKey);
         assertEquals(acl.getGrants().size(), 1);
         assertEquals(acl.getPermissions(ownerId).size(), 0);
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      } finally {
         returnContainer(containerName);
      }

   }

   public void testPrivateAclIsDefaultForObject() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String privateObjectKey = "private-acl";
      String containerName = getContainerName();
      try {
         // Private object
         addBlobToContainer(containerName, privateObjectKey);
         AccessControlList acl = getApi().getObjectACL(containerName, privateObjectKey);

         assertEquals(acl.getGrants().size(), 1);
         assertNotNull(acl.getOwner());
         String ownerId = acl.getOwner().getId();
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      } finally {
         returnContainer(containerName);
      }

   }

   public void testPublicReadOnObject() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      final String publicReadObjectKey = "public-read-acl";
      final String containerName = getContainerName();
      try {
         S3Object object = getApi().newS3Object();
         object.getMetadata().setKey(publicReadObjectKey);
         object.setPayload("");
         getApi().putObject(containerName, object, new PutObjectOptions().withAcl(CannedAccessPolicy.PUBLIC_READ));

         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = getApi().getObjectACL(containerName, publicReadObjectKey);

                  assertEquals(acl.getGrants().size(), 2);
                  assertEquals(acl.getPermissions(GroupGranteeURI.ALL_USERS).size(), 1);
                  assertNotNull(acl.getOwner());
                  String ownerId = acl.getOwner().getId();
                  assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));
               } catch (Exception e) {
                  Throwables.propagateIfPossible(e);
               }
            }
         });

      } finally {
         returnContainer(containerName);
      }

   }

   protected String addBlobToContainer(String sourceContainer, String key) {
      S3Object sourceObject = getApi().newS3Object();
      sourceObject.getMetadata().setKey(key);
      sourceObject.getMetadata().getContentMetadata().setContentType("text/xml");
      sourceObject.setPayload(TEST_STRING);
      return getApi().putObject(sourceContainer, sourceObject);
   }

   protected S3Object validateObject(String sourceContainer, String key) throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
      assertConsistencyAwareContainerSize(sourceContainer, 1);
      S3Object newObject = getApi().getObject(sourceContainer, key);
      assert newObject != null;
      assertEquals(Strings2.toStringAndClose(newObject.getPayload().openStream()), TEST_STRING);
      return newObject;
   }

   public void testMetadataWithCacheControlAndContentDisposition() throws Exception {
      String key = "hello";

      S3Object object = getApi().newS3Object();
      object.getMetadata().setKey(key);
      object.setPayload(TEST_STRING);
      object.getMetadata().setCacheControl("no-cache");
      object.getMetadata().getContentMetadata().setContentDisposition("attachment; filename=hello.txt");
      String containerName = getContainerName();
      try {
         getApi().putObject(containerName, object);

         S3Object newObject = validateObject(containerName, key);
         assertCacheControl(newObject, "no-cache");
         assertEquals(newObject.getMetadata().getContentMetadata().getContentDisposition(),
                  "attachment; filename=hello.txt");
      } finally {
         returnContainer(containerName);
      }
   }

   protected void assertCacheControl(S3Object newObject, String string) {
      assert newObject.getMetadata().getCacheControl().indexOf(string) != -1 : newObject.getMetadata()
               .getCacheControl();
   }

   protected void assertContentEncoding(S3Object newObject, String string) {
      assert newObject.getPayload().getContentMetadata().getContentEncoding().indexOf(string) != -1 : newObject
               .getPayload().getContentMetadata().getContentEncoding();
      assert newObject.getMetadata().getContentMetadata().getContentEncoding().indexOf(string) != -1 : newObject
               .getMetadata().getContentMetadata().getContentEncoding();
   }

   @Test(groups = { "integration", "live" })
   public void testMetadataContentEncoding() throws Exception {
      String key = "hello";

      S3Object object = getApi().newS3Object();
      object.getMetadata().setKey(key);
      object.setPayload(TEST_STRING);
      object.getMetadata().getContentMetadata().setContentEncoding("x-compress");
      String containerName = getContainerName();
      try {
         getApi().putObject(containerName, object);
         S3Object newObject = validateObject(containerName, key);
         assertContentEncoding(newObject, "x-compress");
      } finally {
         returnContainer(containerName);
      }
   }

   public void testCopyObject() throws Exception {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();

      try {
         addToContainerAndValidate(containerName, sourceKey);

         getApi().copyObject(containerName, sourceKey, destinationContainer, destinationKey);

         validateContent(destinationContainer, destinationKey);
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   protected String addToContainerAndValidate(String containerName, String sourceKey) throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
      String etag = addBlobToContainer(containerName, sourceKey);
      validateContent(containerName, sourceKey);
      return etag;
   }

   public void testCopyIfModifiedSince() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         Date before = new Date(System.currentTimeMillis() - 10 * 1000);
         addToContainerAndValidate(containerName, sourceKey + "mod");
         Date after = new Date(System.currentTimeMillis() + 10 * 1000);

         getApi().copyObject(containerName, sourceKey + "mod", destinationContainer, destinationKey,
                  ifSourceModifiedSince(before));
         validateContent(destinationContainer, destinationKey);

         // Sleep since Amazon returns 200 if the date is in the future:
         // https://forums.aws.amazon.com/message.jspa?messageID=325930
         TimeUnit.SECONDS.sleep(20);
         try {
            getApi().copyObject(containerName, sourceKey + "mod", destinationContainer, destinationKey,
                     ifSourceModifiedSince(after));
            fail("should have thrown HttpResponseException");
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   public void testCopyIfUnmodifiedSince() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         Date before = new Date(System.currentTimeMillis() - 10 * 1000);
         addToContainerAndValidate(containerName, sourceKey + "un");
         Date after = new Date(System.currentTimeMillis() + 10 * 1000);

         getApi().copyObject(containerName, sourceKey + "un", destinationContainer, destinationKey,
                  ifSourceUnmodifiedSince(after));
         validateContent(destinationContainer, destinationKey);

         try {
            getApi().copyObject(containerName, sourceKey + "un", destinationContainer, destinationKey,
                     ifSourceUnmodifiedSince(before));
            fail("should have thrown HttpResponseException");
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

   public void testCopyIfMatch() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         String goodETag = addToContainerAndValidate(containerName, sourceKey);

         getApi().copyObject(containerName, sourceKey, destinationContainer, destinationKey,
                  ifSourceETagMatches(goodETag));
         validateContent(destinationContainer, destinationKey);

         try {
            getApi().copyObject(containerName, sourceKey, destinationContainer, destinationKey,
                     ifSourceETagMatches("setsds"));
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

   public void testCopyIfNoneMatch() throws IOException, InterruptedException, ExecutionException, TimeoutException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         String goodETag = addToContainerAndValidate(containerName, sourceKey);

         getApi().copyObject(containerName, sourceKey, destinationContainer, destinationKey,
                  ifSourceETagDoesntMatch("asfasdf"));
         validateContent(destinationContainer, destinationKey);

         try {
            getApi().copyObject(containerName, sourceKey, destinationContainer, destinationKey,
                     ifSourceETagDoesntMatch(goodETag));
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

   public void testCopyWithMetadata() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         addToContainerAndValidate(containerName, sourceKey);

         Map<String, String> metadata = Maps.newHashMap();
         metadata.put("adrian", "cole");

         getApi().copyObject(containerName, sourceKey, destinationContainer, destinationKey,
                  overrideMetadataWith(metadata));

         validateContent(destinationContainer, destinationKey);

         ObjectMetadata objectMeta = getApi().headObject(destinationContainer, destinationKey);

         assertEquals(objectMeta.getUserMetadata(), metadata);
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   public void testMultipartSynchronously() throws InterruptedException, IOException {
      HashCode oneHundredOneConstitutionsMD5 = oneHundredOneConstitutions.hash(md5());
      String containerName = getContainerName();
      S3Object object = null;
      try {
         String key = "constitution.txt";
         String uploadId = getApi().initiateMultipartUpload(containerName,
                  ObjectMetadataBuilder.create().key(key).contentMD5(oneHundredOneConstitutionsMD5.asBytes()).build());
         byte[] buffer = oneHundredOneConstitutions.read();
         assertEquals(oneHundredOneConstitutions.size(), (long) buffer.length);

         Payload part1 = newByteArrayPayload(buffer);
         part1.getContentMetadata().setContentLength((long) buffer.length);
         part1.getContentMetadata().setContentMD5(oneHundredOneConstitutionsMD5);

         String eTagOf1 = null;
         try {
            eTagOf1 = getApi().uploadPart(containerName, key, 1, uploadId, part1);
         } catch (KeyNotFoundException e) {
            // note that because of eventual consistency, the upload id may not be present yet
            // we may wish to add this condition to the retry handler

            // we may also choose to implement ListParts and wait for the uploadId to become
            // available there.
            eTagOf1 = getApi().uploadPart(containerName, key, 1, uploadId, part1);
         }

         String eTag = getApi().completeMultipartUpload(containerName, key, uploadId, ImmutableMap.of(1, eTagOf1));

         assert !eTagOf1.equals(eTag);

         object = getApi().getObject(containerName, key);
         assertEquals(ByteStreams2.toByteArrayAndClose(object.getPayload().openStream()), buffer);

         // noticing amazon does not return content-md5 header or a parsable ETag after a multi-part
         // upload is complete:
         // https://forums.aws.amazon.com/thread.jspa?threadID=61344
         assertEquals(object.getPayload().getContentMetadata().getContentMD5(), null);
         assertEquals(getApi().headObject(containerName, key).getContentMetadata().getContentMD5(), null);

      } finally {
         if (object != null)
            object.getPayload().close();
         returnContainer(containerName);
      }
   }

   public void testDeleteMultipleObjects() throws InterruptedException {
      String container = getContainerName();
      try {
         ImmutableSet.Builder<String> builder = ImmutableSet.builder();
         for (int i = 0; i < 5; i++) {
            String key = UUID.randomUUID().toString();

            Blob blob = view.getBlobStore().blobBuilder(key).payload("").build();
            view.getBlobStore().putBlob(container, blob);

            builder.add(key);
         }

         Set<String> keys = builder.build();
         DeleteResult result = getApi().deleteObjects(container, keys);

         assertTrue(result.getDeleted().containsAll(keys));
         assertEquals(result.getErrors().size(), 0);

         for (String key : keys) {
            assertConsistencyAwareBlobDoesntExist(container, key);
         }

      } finally {
         returnContainer(container);
      }
   }

   private void checkGrants(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();

      assertEquals(acl.getGrants().size(), 4, acl.toString());

      assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL), acl.toString());
      assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      assertTrue(acl.hasPermission(ownerId, Permission.WRITE_ACP), acl.toString());
      // EmailAddressGrantee is replaced by a CanonicalUserGrantee, so we cannot test by email addr
      assertTrue(acl.hasPermission(TEST_ACL_ID, Permission.READ_ACP), acl.toString());
   }

   private void addGrantsToACL(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();
      acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.READ);
      acl.addPermission(new EmailAddressGrantee(TEST_ACL_EMAIL), Permission.READ_ACP);
      acl.addPermission(new CanonicalUserGrantee(ownerId), Permission.WRITE_ACP);
   }

}
