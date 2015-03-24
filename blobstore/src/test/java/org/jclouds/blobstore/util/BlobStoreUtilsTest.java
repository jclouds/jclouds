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
package org.jclouds.blobstore.util;

import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.jclouds.blobstore.util.BlobStoreUtils.getNameFor;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.List;

import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

@Test(groups = "unit")
public class BlobStoreUtilsTest {

   public void testGetKeyForAzureS3AndRackspace() {
      GeneratedHttpRequest request = requestForEndpointAndArgs(
            "https://jclouds.blob.core.windows.net/adriancole-blobstore0/five",
            ImmutableList.<Object> of("adriancole-blobstore0", "five"));
      assertEquals(getNameFor(request), "five");
   }

   public void testGetKeyForAtmos() {
      GeneratedHttpRequest request = requestForEndpointAndArgs(
            "https://storage4.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22/adriancole-blobstore0/four",
            ImmutableList.<Object> of("adriancole-blobstore0/four"));
      assertEquals(getNameFor(request), "four");
   }

   public void testReadOnlyBlobStore() {
      BlobStoreContext context = ContextBuilder.newBuilder("transient").build(BlobStoreContext.class);
      try {
         BlobStore rwBlobStore = context.getBlobStore();
         BlobStore roBlobStore = ReadOnlyBlobStore.newReadOnlyBlobStore(rwBlobStore);
         String containerName = "name";
         rwBlobStore.createContainerInLocation(null, containerName);
         Blob blob = rwBlobStore.blobBuilder("blob")
               .payload(new byte[0])
               .build();
         rwBlobStore.putBlob(containerName, blob);
         try {
            roBlobStore.putBlob(containerName, blob);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
         } catch (UnsupportedOperationException uoe) {
            // expected
         }
      } finally {
         context.close();
      }
   }

   GeneratedHttpRequest requestForEndpointAndArgs(String endpoint, List<Object> args) {
      try {
         Invocation invocation = Invocation.create(method(String.class, "toString"), args);
         return GeneratedHttpRequest.builder().method("POST").endpoint(URI.create(endpoint)).invocation(invocation)
               .build();
      } catch (SecurityException e) {
         throw Throwables.propagate(e);
      }
   }
}
