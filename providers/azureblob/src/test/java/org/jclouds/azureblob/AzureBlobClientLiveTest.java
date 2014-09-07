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
package org.jclouds.azureblob;

import static com.google.common.io.BaseEncoding.base16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.jclouds.azure.storage.options.ListOptions.Builder.includeMetadata;
import static org.jclouds.azureblob.options.CreateContainerOptions.Builder.withMetadata;
import static org.jclouds.azureblob.options.CreateContainerOptions.Builder.withPublicAccess;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.options.CopyBlobOptions;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.Payloads;
import org.jclouds.util.Strings2;
import org.jclouds.util.Throwables2;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteSource;

@Test(groups = "live", singleThreaded = true)
public class AzureBlobClientLiveTest extends BaseBlobStoreIntegrationTest {
   public AzureBlobClientLiveTest() {
      provider = "azureblob";
   }

   public AzureBlobClient getApi() {
      return view.unwrapApi(AzureBlobClient.class);
   }

   @Test
   public void testListContainers() throws Exception {
      Set<ContainerProperties> response = getApi().listContainers();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

   }

   String privateContainer;
   String publicContainer;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateContainer() throws Exception {
      boolean created = false;
      while (!created) {
         privateContainer = CONTAINER_PREFIX + new SecureRandom().nextInt();
         try {
            created = getApi().createContainer(privateContainer, withMetadata(ImmutableMultimap.of("foo", "bar")));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      Set<ContainerProperties> response = getApi().listContainers(includeMetadata());
      assert null != response;
      long containerCount = response.size();
      assertTrue(containerCount >= 1);
      ListBlobsResponse list = getApi().listBlobs(privateContainer);
      assertEquals(list.getUrl(), URI.create(String.format("https://%s.blob.core.windows.net/%s",
            view.unwrap().getIdentity(), privateContainer)));
      // TODO .. check to see the container actually exists
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreatePublicContainer() throws Exception {
      boolean created = false;
      while (!created) {
         publicContainer = CONTAINER_PREFIX + new SecureRandom().nextInt();
         try {
            created = getApi().createContainer(publicContainer, withPublicAccess(PublicAccess.BLOB));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      // TODO
      // URL url = new URL(String.format("http://%s.blob.core.windows.net/%s",
      // identity,
      // publicContainer));
      // Utils.toStringAndClose(url.openStream());
   }

   @Test(timeOut = 10 * 60 * 1000)
   public void testCreatePublicRootContainer() throws Exception {
      try {
         getApi().deleteRootContainer();
      } catch (ContainerNotFoundException e) {
         Thread.sleep(5000);
      } catch (AzureStorageResponseException htpe) {
         if (htpe.getResponse().getStatusCode() == 409) {// TODO look for
                                                         // specific message
            Thread.sleep(5000);
         } else {
            throw htpe;
         }
      }

      boolean created = false;
      while (!created) {
         try {
            created = getApi().createRootContainer();
         } catch (AzureStorageResponseException htpe) {
            if (htpe.getResponse().getStatusCode() == 409) {// TODO look for
                                                            // specific message
               Thread.sleep(5000);
               continue;
            } else {
               throw htpe;
            }
         }
      }
      ListBlobsResponse list = getApi().listBlobs();
      assertEquals(list.getUrl(), URI.create(String.format("https://%s.blob.core.windows.net/$root",
            view.unwrap().getIdentity())));
   }

   @Test
   public void testListContainersWithOptions() throws Exception {

      BoundedSet<ContainerProperties> response = getApi().listContainers(
            ListOptions.Builder.prefix(privateContainer).maxResults(1).includeMetadata());
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
      assertEquals(privateContainer, response.getPrefix());
      assertEquals(1, response.getMaxResults());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreatePublicRootContainer" })
   public void testDeleteRootContainer() throws Exception {
      getApi().deleteRootContainer();
      // TODO loop for up to 30 seconds checking if they are really gone
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer", "testCreatePublicContainer" })
   public void testListOwnedContainers() throws Exception {

      // Test default listing
      Set<ContainerProperties> response = getApi().listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the
      // containers already
      // exist, this will fail

      // Test listing with options
      response = getApi().listContainers(
            ListOptions.Builder.prefix(privateContainer.substring(0, privateContainer.length() - 1)).maxResults(1)
                  .includeMetadata());
      assertEquals(response.size(), 1);
      assertEquals(Iterables.getOnlyElement(response).getName(), privateContainer);
      assertEquals(Iterables.getOnlyElement(response).getMetadata(), ImmutableMap.of("foo", "bar"));

      response = getApi().listContainers(ListOptions.Builder.prefix(publicContainer).maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(Iterables.getOnlyElement(response).getName(), publicContainer);

   }

   @Test
   public void testDeleteOneContainer() throws Exception {
      getApi().deleteContainer("does-not-exist");
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testListOwnedContainers", "testObjectOperations" })
   public void testDeleteContainer() throws Exception {
      getApi().deleteContainer(privateContainer);
      getApi().deleteContainer(publicContainer);
      // TODO loop for up to 30 seconds checking if they are really gone
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer", "testCreatePublicContainer" })
   public void testObjectOperations() throws Exception {
      String data = "Here is my data";

      // Test PUT with string data, ETag hash, and a piece of metadata
      AzureBlob object = getApi().newBlob();
      object.getProperties().setName("object");
      object.setPayload(data);
      object.getProperties().getContentMetadata().setContentMD5(Hashing.md5().hashString(data, Charsets.UTF_8).asBytes());
      object.getProperties().getContentMetadata().setContentType("text/plain");
      object.getProperties().getMetadata().put("mykey", "metadata-value");
      byte[] md5 = object.getProperties().getContentMetadata().getContentMD5();
      String newEtag = getApi().putBlob(privateContainer, object);
      assertEquals(base16().lowerCase().encode(md5),
            base16().lowerCase().encode(object.getProperties().getContentMetadata().getContentMD5()));
      // Test HEAD of missing object
      assert getApi().getBlobProperties(privateContainer, "non-existent-object") == null;

      // Test HEAD of object
      BlobProperties metadata = getApi().getBlobProperties(privateContainer, object.getProperties().getName());
      assertEquals(metadata.getName(), object.getProperties().getName());
      assertEquals(metadata.getContentMetadata().getContentLength(), Long.valueOf(data.length()));
      assertEquals(metadata.getContentMetadata().getContentType(), "text/plain");
      assertEquals(base16().lowerCase().encode(metadata.getContentMetadata().getContentMD5()),
            base16().lowerCase().encode(object.getProperties().getContentMetadata().getContentMD5()));
      assertEquals(metadata.getETag(), newEtag);
      assertEquals(metadata.getMetadata().entrySet().size(), 1);
      assertEquals(metadata.getMetadata().get("mykey"), "metadata-value");

      // Test POST to update object's metadata
      Map<String, String> userMetadata = ImmutableMap.<String, String>builder()
            .put("new_metadata_1", "value-1")
            .put("new_metadata_2", "value-2")
            .build();
      String eTag = getApi().setBlobMetadata(privateContainer, object.getProperties().getName(), userMetadata);
      assertThat(eTag).isNotNull();

      // Azure ETag are timestamps not content hash
      String eTag2 = getApi().setBlobMetadata(privateContainer, object.getProperties().getName(), userMetadata);
      assertThat(eTag2).isNotNull().isNotEqualTo(eTag);

      // Test GET of missing object
      assert getApi().getBlob(privateContainer, "non-existent-object") == null;

      // Test GET of object (including updated metadata)
      AzureBlob getBlob = getApi().getBlob(privateContainer, object.getProperties().getName());
      assertEquals(Strings2.toStringAndClose(getBlob.getPayload().openStream()), data);
      assertEquals(getBlob.getProperties().getName(), object.getProperties().getName());
      assertEquals(getBlob.getPayload().getContentMetadata().getContentLength(), Long.valueOf(data.length()));
      assertEquals(getBlob.getProperties().getContentMetadata().getContentType(), "text/plain");
      assertEquals(base16().lowerCase().encode(md5),
            base16().lowerCase().encode(getBlob.getProperties().getContentMetadata().getContentMD5()));
      assertEquals(getBlob.getProperties().getMetadata().size(), 2);
      assertEquals(getBlob.getProperties().getMetadata().get("new_metadata_1"), "value-1");
      assertEquals(getBlob.getProperties().getMetadata().get("new_metadata_2"), "value-2");
      assertEquals(metadata.getMetadata().entrySet().size(), 1);
      assertEquals(metadata.getMetadata().get("mykey"), "metadata-value");

      // test listing
      ListBlobsResponse response = getApi().listBlobs(
            privateContainer,
            ListBlobsOptions.Builder
                  .prefix(object.getProperties().getName().substring(0, object.getProperties().getName().length() - 1))
                  .maxResults(1).includeMetadata());
      assertEquals(response.size(), 1);
      assertEquals(Iterables.getOnlyElement(response).getName(), object.getProperties().getName());
      assertEquals(Iterables.getOnlyElement(response).getMetadata().size(), 2);
      assertEquals(Iterables.getOnlyElement(response).getMetadata().get("new_metadata_1"), "value-1");
      assertEquals(Iterables.getOnlyElement(response).getMetadata().get("new_metadata_2"), "value-2");

      // Test PUT with invalid ETag (as if object's data was corrupted in
      // transit)
      String correctEtag = newEtag;
      String incorrectEtag = "0" + correctEtag.substring(1);
      object.getProperties().setETag(incorrectEtag);
      try {
         getApi().putBlob(privateContainer, object);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes(Charsets.UTF_8));
      object = getApi().newBlob();
      object.getProperties().setName("chunked-object");
      object.setPayload(bais);
      object.getPayload().getContentMetadata().setContentLength(Long.valueOf(data.getBytes().length));
      newEtag = getApi().putBlob(privateContainer, object);
      assertEquals(base16().lowerCase().encode(md5),
            base16().lowerCase().encode(getBlob.getProperties().getContentMetadata().getContentMD5()));

      // Test GET with options
      // Non-matching ETag
      try {
         getApi().getBlob(privateContainer, object.getProperties().getName(),
               GetOptions.Builder.ifETagDoesntMatch(newEtag));
      } catch (Exception e) {
         HttpResponseException httpEx = Throwables2.getFirstThrowableOfType(e, HttpResponseException.class);
         assert httpEx != null : "expected http exception, not " + e;
         assertEquals(httpEx.getResponse().getStatusCode(), 304);
      }

      // Matching ETag TODO this shouldn't fail!!!
      try {
         getBlob = getApi().getBlob(privateContainer, object.getProperties().getName(),
               GetOptions.Builder.ifETagMatches(newEtag));
         assertEquals(getBlob.getProperties().getETag(), newEtag);
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 412);
      }

      // Range
      // doesn't work per
      // http://social.msdn.microsoft.com/Forums/en-US/windowsazure/thread/479fa63f-51df-4b66-96b5-33ae362747b6
      // getBlob = getApi()
      // .getBlob(privateContainer, object.getProperties().getName(),
      // GetOptions.Builder.startAt(8)).get(120,
      // TimeUnit.SECONDS);
      // assertEquals(Utils.toStringAndClose((InputStream) getBlob.getData()),
      // data.substring(8));

      getApi().deleteBlob(privateContainer, "object");
      getApi().deleteBlob(privateContainer, "chunked-object");
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testBlockOperations() throws Exception {
      String blockContainer = CONTAINER_PREFIX + new SecureRandom().nextInt();
      String blockBlob = "myblockblob-" + new SecureRandom().nextInt();
      String A = "A";
      String B = "B";
      String C = "C";

      String blockIdA = BaseEncoding.base64().encode((blockBlob + "-" + A).getBytes());
      String blockIdB = BaseEncoding.base64().encode((blockBlob + "-" + B).getBytes());
      String blockIdC = BaseEncoding.base64().encode((blockBlob + "-" + C).getBytes());
      getApi().createContainer(blockContainer);
      getApi().putBlock(blockContainer, blockBlob, blockIdA, Payloads.newByteArrayPayload(A.getBytes()));
      getApi().putBlock(blockContainer, blockBlob, blockIdB, Payloads.newByteArrayPayload(B.getBytes()));
      getApi().putBlock(blockContainer, blockBlob, blockIdC, Payloads.newByteArrayPayload(C.getBytes()));
      getApi().putBlockList(blockContainer, blockBlob, Arrays.asList(blockIdA, blockIdB, blockIdC));
      ListBlobBlocksResponse blocks = getApi().getBlockList(blockContainer, blockBlob);
      assertEquals(3, blocks.getBlocks().size());
      assertEquals(blockIdA, blocks.getBlocks().get(0).getBlockName());
      assertEquals(blockIdB, blocks.getBlocks().get(1).getBlockName());
      assertEquals(blockIdC, blocks.getBlocks().get(2).getBlockName());
      assertEquals(1, blocks.getBlocks().get(0).getContentLength());
      assertEquals(1, blocks.getBlocks().get(1).getContentLength());
      assertEquals(1, blocks.getBlocks().get(2).getContentLength());
      getApi().deleteContainer(blockContainer);
   }

   @Test
   public void testGetSetACL() throws Exception {
      AzureBlobClient client = getApi();
      String blockContainer = CONTAINER_PREFIX + new SecureRandom().nextInt();
      client.createContainer(blockContainer);
      try {
         assertThat(client.getPublicAccessForContainer(blockContainer)).isEqualTo(PublicAccess.PRIVATE);

         setAndVerifyACL(client, blockContainer, PublicAccess.CONTAINER);
         setAndVerifyACL(client, blockContainer, PublicAccess.BLOB);
         setAndVerifyACL(client, blockContainer, PublicAccess.PRIVATE);
      } finally {
         client.deleteContainer(blockContainer);
      }
   }

   private static void setAndVerifyACL(AzureBlobClient client, String blockContainer, PublicAccess access)
         throws Exception {
      client.setPublicAccessForContainer(blockContainer, access);
      assertThat(client.getPublicAccessForContainer(blockContainer)).isEqualTo(access);
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer" })
   public void testCopyBlob() throws Exception {
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);

      // create blob
      AzureBlob object = getApi().newBlob();
      object.getProperties().setName("from");
      object.setPayload(byteSource.read());
      getApi().putBlob(privateContainer, object);

      // copy blob
      URI copySource = view.getSigner().signGetBlob(privateContainer, "from").getEndpoint();
      getApi().copyBlob(copySource, privateContainer, "to", CopyBlobOptions.NONE);

      // ensure copied blob matches original
      AzureBlob getBlob = getApi().getBlob(privateContainer, "to");
      assertEquals(ByteStreams2.toByteArrayAndClose(getBlob.getPayload().openStream()), byteSource.read());
      assertThat(getBlob.getProperties().getMetadata().isEmpty());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer" })
   public void testCopyBlobReplaceMetadata() throws Exception {
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);

      // create blob
      AzureBlob object = getApi().newBlob();
      object.getProperties().setName("from");
      object.setPayload(byteSource.read());
      getApi().putBlob(privateContainer, object);

      // copy blob
      URI copySource = view.getSigner().signGetBlob(privateContainer, "from").getEndpoint();
      Map<String, String> newMetadata = ImmutableMap.of("foo", "bar");
      getApi().copyBlob(copySource, privateContainer, "to", CopyBlobOptions.builder().overrideUserMetadata(newMetadata).build());

      // ensure copied blob matches original
      AzureBlob getBlob = getApi().getBlob(privateContainer, "to");
      assertEquals(ByteStreams2.toByteArrayAndClose(getBlob.getPayload().openStream()), byteSource.read());
      assertThat(getBlob.getProperties().getMetadata()).isEqualTo(newMetadata);
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer" })
   public void testCopyBlobIfModifiedSince() throws Exception {
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);

      // create blob
      AzureBlob object = getApi().newBlob();
      object.getProperties().setName("from");
      object.setPayload(byteSource.read());
      String eTag = getApi().putBlob(privateContainer, object);

      long now = System.currentTimeMillis();
      Date before = new Date(now - 1000 * 1000);
      Date after = new Date(now + 1000 * 1000);
      URI copySource = view.getSigner().signGetBlob(privateContainer, "from").getEndpoint();

      // failure case
      try {
         getApi().copyBlob(copySource, privateContainer, "to-if-modified-since", CopyBlobOptions.builder().ifModifiedSince(after).build());
         failBecauseExceptionWasNotThrown(AzureStorageResponseException.class);
      } catch (AzureStorageResponseException asre) {
         assertThat(asre.getResponse().getStatusCode()).as("status code").isEqualTo(412);
      }

      // success case
      getApi().copyBlob(copySource, privateContainer, "to-if-modified-since", CopyBlobOptions.builder().ifModifiedSince(before).build());
      AzureBlob getBlob = getApi().getBlob(privateContainer, "to-if-modified-since");
      assertEquals(ByteStreams2.toByteArrayAndClose(getBlob.getPayload().openStream()), byteSource.read());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer" })
   public void testCopyBlobIfUnmodifiedSince() throws Exception {
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);

      // create blob
      AzureBlob object = getApi().newBlob();
      object.getProperties().setName("from");
      object.setPayload(byteSource.read());
      String eTag = getApi().putBlob(privateContainer, object);

      long now = System.currentTimeMillis();
      Date before = new Date(now - 1000 * 1000);
      Date after = new Date(now + 1000 * 1000);
      URI copySource = view.getSigner().signGetBlob(privateContainer, "from").getEndpoint();

      // failure case
      try {
         getApi().copyBlob(copySource, privateContainer, "to-if-unmodifed-since", CopyBlobOptions.builder().ifUnmodifiedSince(before).build());
         failBecauseExceptionWasNotThrown(AzureStorageResponseException.class);
      } catch (AzureStorageResponseException asre) {
         assertThat(asre.getResponse().getStatusCode()).as("status code").isEqualTo(412);
      }

      // success case
      getApi().copyBlob(copySource, privateContainer, "to-if-unmodifed-since", CopyBlobOptions.builder().ifUnmodifiedSince(after).build());
      AzureBlob getBlob = getApi().getBlob(privateContainer, "to-if-unmodifed-since");
      assertEquals(ByteStreams2.toByteArrayAndClose(getBlob.getPayload().openStream()), byteSource.read());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer" })
   public void testCopyBlobIfMatch() throws Exception {
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);

      // create blob
      AzureBlob object = getApi().newBlob();
      object.getProperties().setName("from");
      object.setPayload(byteSource.read());
      String eTag = getApi().putBlob(privateContainer, object);
      String fakeETag = "0x8CEB669D794AFE2";

      URI copySource = view.getSigner().signGetBlob(privateContainer, "from").getEndpoint();

      // failure case
      try {
         getApi().copyBlob(copySource, privateContainer, "to-if-match", CopyBlobOptions.builder().ifMatch(fakeETag).build());
         failBecauseExceptionWasNotThrown(AzureStorageResponseException.class);
      } catch (AzureStorageResponseException asre) {
         assertThat(asre.getResponse().getStatusCode()).as("status code").isEqualTo(412);
      }

      // success case
      getApi().copyBlob(copySource, privateContainer, "to-if-match", CopyBlobOptions.builder().ifMatch(eTag).build());
      AzureBlob getBlob = getApi().getBlob(privateContainer, "to-if-match");
      assertEquals(ByteStreams2.toByteArrayAndClose(getBlob.getPayload().openStream()), byteSource.read());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer" })
   public void testCopyBlobIfNoneMatch() throws Exception {
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);

      // create blob
      AzureBlob object = getApi().newBlob();
      object.getProperties().setName("from");
      object.setPayload(byteSource.read());
      String eTag = getApi().putBlob(privateContainer, object);
      String fakeETag = "0x8CEB669D794AFE2";

      URI copySource = view.getSigner().signGetBlob(privateContainer, "from").getEndpoint();

      // failure case
      try {
         getApi().copyBlob(copySource, privateContainer, "to-if-none-match", CopyBlobOptions.builder().ifNoneMatch(eTag).build());
         failBecauseExceptionWasNotThrown(AzureStorageResponseException.class);
      } catch (AzureStorageResponseException asre) {
         assertThat(asre.getResponse().getStatusCode()).as("status code").isEqualTo(412);
      }

      // success case
      getApi().copyBlob(copySource, privateContainer, "to-if-none-match", CopyBlobOptions.builder().ifNoneMatch(fakeETag).build());
      AzureBlob getBlob = getApi().getBlob(privateContainer, "to-if-none-match");
      assertEquals(ByteStreams2.toByteArrayAndClose(getBlob.getPayload().openStream()), byteSource.read());
   }
}
