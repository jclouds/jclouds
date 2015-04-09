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

import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.jclouds.azureblob.options.CreateContainerOptions.Builder.withPublicAccess;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.Fallbacks.TrueOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azureblob.AzureBlobFallbacks.FalseIfContainerAlreadyExists;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.functions.ParseBlobFromHeadersAndHttpContent;
import org.jclouds.azureblob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azureblob.functions.ParsePublicAccessHeader;
import org.jclouds.azureblob.options.CopyBlobOptions;
import org.jclouds.azureblob.options.CreateContainerOptions;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.azureblob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azureblob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.internal.BaseRestAnnotationProcessingTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.reflect.Invokable;

@Test(groups = "unit", testName = "AzureBlobClientTest")
public class AzureBlobClientTest extends BaseRestAnnotationProcessingTest<AzureBlobClient> {

   public void testListContainers() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "listContainers", ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/?comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);

   }

   public void testListContainersOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "listContainers", ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(maxResults(1).marker("marker").prefix("prefix")));

      assertRequestLineEquals(request,
               "GET https://identity.blob.core.windows.net/?comp=list&maxresults=1&marker=marker&prefix=prefix HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "createContainer", String.class,
               CreateContainerOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container"));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "deleteContainer", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container"));

      assertRequestLineEquals(request,
               "DELETE https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);
   }

   public void testCreateContainerOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "createContainer", String.class,
               CreateContainerOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container", withPublicAccess(PublicAccess.BLOB)
               .withMetadata(ImmutableMultimap.of("foo", "bar"))));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "x-ms-blob-public-access: blob\n" +
               "x-ms-meta-foo: bar\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testCreateRootContainer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "createRootContainer", CreateContainerOptions[].class);

      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testDeleteRootContainer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "deleteRootContainer");
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request, "DELETE https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, TrueOnNotFoundOr404.class);
   }

   public void testCreateRootContainerOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "createRootContainer", CreateContainerOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(withPublicAccess(PublicAccess.BLOB).withMetadata(
               ImmutableMultimap.of("foo", "bar"))));

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "x-ms-blob-public-access: blob\n" +
               "x-ms-meta-foo: bar\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "listBlobs", String.class, ListBlobsOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container"));

      assertRequestLineEquals(request,
               "GET https://identity.blob.core.windows.net/container?restype=container&comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ContainerNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);
   }

   public void testListRootBlobs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "listBlobs", ListBlobsOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request,
               "GET https://identity.blob.core.windows.net/$root?restype=container&comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ContainerNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);
   }

   public void testContainerProperties() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "getContainerProperties", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container"));

      assertRequestLineEquals(request,
               "HEAD https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseContainerPropertiesFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnContainerNotFound.class);
   }

   public void testGetPublicAccessForContainer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "getPublicAccessForContainer", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container"));

      assertRequestLineEquals(request,
               "HEAD https://identity.blob.core.windows.net/container?restype=container&comp=acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParsePublicAccessHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnContainerNotFound.class);
   }

   public void testSetPublicAccessForContainer() throws SecurityException, NoSuchMethodException, IOException {
      setAndVerifyPublicAccessForContainer(PublicAccess.CONTAINER,
            "x-ms-blob-public-access: container\n");
      setAndVerifyPublicAccessForContainer(PublicAccess.BLOB,
            "x-ms-blob-public-access: blob\n");
      setAndVerifyPublicAccessForContainer(PublicAccess.PRIVATE,
            "");
   }

   private void setAndVerifyPublicAccessForContainer(PublicAccess access, String expectedHeader) {
      Invokable<?, ?> method = method(AzureBlobClient.class, "setPublicAccessForContainer", String.class, PublicAccess.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container", access));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container?restype=container&comp=acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               expectedHeader +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseETagHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);
   }

   public void testSetResourceMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "setResourceMetadata", String.class, Map.class);
      GeneratedHttpRequest request = processor.createRequest(method,
            ImmutableList.<Object> of("container", ImmutableMap.of("key", "value")));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container?restype=container&comp=metadata HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "x-ms-meta-key: value\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);
   }

   public void testGetBlob() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "getBlob", String.class, String.class, GetOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container", "blob"));

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/container/blob HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseBlobFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnKeyNotFound.class);
   }

   public void testSetBlobMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AzureBlobClient.class, "setBlobMetadata", String.class, String.class, Map.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container", "blob", ImmutableMap.of("key", "value")));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container/blob?comp=metadata HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "x-ms-meta-key: value\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseETagHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);
   }

   public void testCopyBlob() throws Exception {
      Invokable<?, ?> method = method(AzureBlobClient.class, "copyBlob", URI.class, String.class, String.class, CopyBlobOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
               URI.create("https://identity.blob.core.windows.net/fromcontainer/fromblob"), "tocontainer", "toblob", CopyBlobOptions.NONE));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/tocontainer/toblob HTTP/1.1");
      checkFilters(request);
      assertNonPayloadHeadersEqual(request,
               "x-ms-copy-source: https://identity.blob.core.windows.net/fromcontainer/fromblob\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCopyBlobOverwriteUserMetadata() throws Exception {
      CopyBlobOptions options = CopyBlobOptions.builder().overrideUserMetadata(ImmutableMap.of("foo", "bar")).build();
      Invokable<?, ?> method = method(AzureBlobClient.class, "copyBlob", URI.class, String.class, String.class, CopyBlobOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
               URI.create("https://identity.blob.core.windows.net/fromcontainer/fromblob"), "tocontainer", "toblob", options));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/tocontainer/toblob HTTP/1.1");
      checkFilters(request);
      assertNonPayloadHeadersEqual(request,
               "x-ms-copy-source: https://identity.blob.core.windows.net/fromcontainer/fromblob\n" +
               "x-ms-meta-foo: bar\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCopyBlobIfModifiedSince() throws Exception {
      CopyBlobOptions options = CopyBlobOptions.builder().ifModifiedSince(new Date(1000)).build();
      Invokable<?, ?> method = method(AzureBlobClient.class, "copyBlob", URI.class, String.class, String.class, CopyBlobOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
               URI.create("https://identity.blob.core.windows.net/fromcontainer/fromblob"), "tocontainer", "toblob", options));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/tocontainer/toblob HTTP/1.1");
      checkFilters(request);
      assertNonPayloadHeadersEqual(request,
               "x-ms-copy-source: https://identity.blob.core.windows.net/fromcontainer/fromblob\n" +
               "x-ms-source-if-modified-since: Thu, 01 Jan 1970 00:00:01 GMT\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCopyBlobIfUnmodifiedSince() throws Exception {
      CopyBlobOptions options = CopyBlobOptions.builder().ifUnmodifiedSince(new Date(1000)).build();
      Invokable<?, ?> method = method(AzureBlobClient.class, "copyBlob", URI.class, String.class, String.class, CopyBlobOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
               URI.create("https://identity.blob.core.windows.net/fromcontainer/fromblob"), "tocontainer", "toblob", options));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/tocontainer/toblob HTTP/1.1");
      checkFilters(request);
      assertNonPayloadHeadersEqual(request,
               "x-ms-copy-source: https://identity.blob.core.windows.net/fromcontainer/fromblob\n" +
               "x-ms-source-if-unmodified-since: Thu, 01 Jan 1970 00:00:01 GMT\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCopyBlobIfMatch() throws Exception {
      String eTag = "0x8CEB669D794AFE2";
      CopyBlobOptions options = CopyBlobOptions.builder().ifMatch(eTag).build();
      Invokable<?, ?> method = method(AzureBlobClient.class, "copyBlob", URI.class, String.class, String.class, CopyBlobOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
               URI.create("https://identity.blob.core.windows.net/fromcontainer/fromblob"), "tocontainer", "toblob", options));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/tocontainer/toblob HTTP/1.1");
      checkFilters(request);
      assertNonPayloadHeadersEqual(request,
               "x-ms-copy-source: https://identity.blob.core.windows.net/fromcontainer/fromblob\n" +
               "x-ms-source-if-match: " + eTag + "\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCopyBlobIfNoneMatch() throws Exception {
      String eTag = "0x8CEB669D794AFE2";
      CopyBlobOptions options = CopyBlobOptions.builder().ifNoneMatch(eTag).build();
      Invokable<?, ?> method = method(AzureBlobClient.class, "copyBlob", URI.class, String.class, String.class, CopyBlobOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
               URI.create("https://identity.blob.core.windows.net/fromcontainer/fromblob"), "tocontainer", "toblob", options));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/tocontainer/toblob HTTP/1.1");
      checkFilters(request);
      assertNonPayloadHeadersEqual(request,
               "x-ms-copy-source: https://identity.blob.core.windows.net/fromcontainer/fromblob\n" +
               "x-ms-source-if-none-match: " + eTag + "\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   public AzureBlobProviderMetadata createProviderMetadata() {
      return new AzureBlobProviderMetadata();
   }
}
