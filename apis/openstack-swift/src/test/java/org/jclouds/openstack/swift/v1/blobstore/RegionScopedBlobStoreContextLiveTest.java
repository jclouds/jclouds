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
package org.jclouds.openstack.swift.v1.blobstore;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.HttpClient;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteSource;
import com.google.common.hash.Hashing;
import com.google.common.net.MediaType;
import com.google.common.net.HttpHeaders;

@Test(groups = "live")
public class RegionScopedBlobStoreContextLiveTest extends BaseBlobStoreIntegrationTest {

   public RegionScopedBlobStoreContextLiveTest() {
      provider = "openstack-swift";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      return props;
   }

   @Test
   public void testRegionsAreNotEmpty() {
      assertFalse(RegionScopedBlobStoreContext.class.cast(view).getConfiguredRegions().isEmpty());
   }

   @Test
   public void testLocationsMatch() {
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      for (String regionId : ctx.getConfiguredRegions()) {
         Set<? extends Location> locations = ctx.getBlobStore(regionId).listAssignableLocations();
         assertEquals(locations.size(), 1, "expected one region " + regionId + " " + locations);
         Location location = locations.iterator().next();
         assertEquals(location.getId(), regionId, "region id " + regionId + " didn't match getId(): " + location);
      }
   }

   @Test
   public void testListBlobs() throws InterruptedException, ExecutionException {
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      for (String regionId : ctx.getConfiguredRegions()) {
         ctx.getBlobStore(regionId).list();
      }
   }

   @Test
   public void testSign() throws InterruptedException, ExecutionException,
         IOException {
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      for (String regionId : ctx.getConfiguredRegions()) {
         BlobStore region = ctx.getBlobStore(regionId);
         PageSet<? extends StorageMetadata> containers = region.list();
         if (containers.isEmpty()) {
            continue;
         }
         String containerName = Iterables.getLast(containers).getName();

         final ByteSource input = ByteSource.wrap("str".getBytes());
         final HttpClient client = ctx.utils().http();

         // test signed put
         String blobName = "test-" + UUID.randomUUID();
         Blob blob2 = region.blobBuilder(blobName).forSigning()
               .contentLength(input.size())
               .contentMD5(input.hash(Hashing.md5()).asBytes())
               .contentType(MediaType.OCTET_STREAM.toString()).build();
         BlobRequestSigner signer = ctx.getSigner(regionId);
         HttpResponse response;
         try {
            HttpRequest putRequest;
            putRequest = signer.signPutBlob(containerName, blob2, 600);
            MutableContentMetadata metadata = blob2.getMetadata()
                  .getContentMetadata();
            HttpRequest.Builder<?> putRequestBuilder = putRequest.toBuilder()
                  .addHeader(HttpHeaders.CONTENT_TYPE,
                        metadata.getContentType());
            putRequestBuilder.addHeader(HttpHeaders.CONTENT_LENGTH,
                  String.valueOf(input.size()));
            putRequestBuilder.payload(input);
            putRequest = putRequestBuilder.build();
            Payload payload = Payloads.newPayload(input.read());
            putRequest.setPayload(payload);
            assertNotNull(putRequest, "regionId=" + regionId + ", container="
                  + containerName + ", blob=" + blobName);
            response = client.invoke(putRequest);
            if (response.getStatusCode() != 200
                  && response.getStatusCode() != 201) {
               fail("Signed PUT expected to return 200 or 201 but returned "
                     + response.getStatusCode());
            }
         } catch (Exception e) {
            fail("Failed signed put test: " + e);
         }

         // test signed get
         try {
            HttpRequest getRequest = signer.signGetBlob(containerName,
                  blobName);
            assertNotNull(getRequest, "regionId=" + regionId + ", container="
                  + containerName + ", blob=" + blobName);
            response = client.invoke(getRequest);
            if (response.getStatusCode() != 200) {
               fail("Signed GET expected to return 200 but returned "
                     + response.getStatusCode());
            }
            Payload payload = response.getPayload();
            assertEquals(ByteStreams2.toByteArrayAndClose(payload.openStream()), input.read(),
                  "Data with signed GET not identical to what was put");
         } catch (Exception e) {
            fail("Failed signed GET test: " + e);
         }
      }
   }
}
