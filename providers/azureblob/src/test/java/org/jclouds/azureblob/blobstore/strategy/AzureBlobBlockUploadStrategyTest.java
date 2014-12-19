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

package org.jclouds.azureblob.blobstore.strategy;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import org.easymock.EasyMock;
import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.azureblob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.testng.annotations.Test;

import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "AzureBlobBlockUploadStrategyTest")
public class AzureBlobBlockUploadStrategyTest {

   public void testExecute() throws Exception {
      String container = "test-container";
      String blobName = "test-blob";
      byte[] blobData = "ABCD".getBytes(Charsets.UTF_8);
      AzureBlobClient client = createMock(AzureBlobClient.class);
      PayloadSlicer slicer = createMock(PayloadSlicer.class);
      BlobToAzureBlob blobToAzureBlob = createMock(BlobToAzureBlob.class);
      MutableBlobMetadata metadata = new MutableBlobMetadataImpl();
      MutableContentMetadata contentMetadata = new BaseMutableContentMetadata();
      contentMetadata.setContentLength((long)blobData.length);
      metadata.setName(blobName);
      metadata.setContentMetadata(contentMetadata);
      Blob blob = new BlobImpl(metadata);
      ByteSource bytes = ByteSource.wrap(blobData);
      Payload payload = Payloads.newByteSourcePayload(bytes);
      payload.setContentMetadata(contentMetadata);
      blob.setPayload(payload);

      List<Payload> payloads = ImmutableList.of(
            createBlockPayload(new byte[]{blobData[0]}),
            createBlockPayload(new byte[]{blobData[1]}),
            createBlockPayload(new byte[]{blobData[2]}),
            createBlockPayload(new byte[]{blobData[3]}));

      expect(slicer.slice(payload, MultipartUploadStrategy.MAX_BLOCK_SIZE)).andReturn(payloads);
      client.putBlock(eq(container), eq(blobName), anyObject(String.class), eq(payloads.get(0)));
      client.putBlock(eq(container), eq(blobName), anyObject(String.class), eq(payloads.get(1)));
      client.putBlock(eq(container), eq(blobName), anyObject(String.class), eq(payloads.get(2)));
      client.putBlock(eq(container), eq(blobName), anyObject(String.class), eq(payloads.get(3)));
      expect(client.putBlockList(eq(container), anyObject(AzureBlob.class), EasyMock.<List<String>>anyObject())).andReturn("Fake ETAG");

      AzureBlobBlockUploadStrategy strat = new AzureBlobBlockUploadStrategy(client, slicer, blobToAzureBlob);
      replay(slicer, client);
      String etag = strat.execute(container, blob);
      assertEquals(etag, "Fake ETAG");

      verify(client);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testExceededContentLengthLimit() throws Exception {
      String container = "test-container";
      String blobName = "test-blob";

      AzureBlobClient client = createNiceMock(AzureBlobClient.class);
      PayloadSlicer slicer = createNiceMock(PayloadSlicer.class);
      BlobToAzureBlob blobToAzureBlob = createMock(BlobToAzureBlob.class);

      MutableBlobMetadata metadata = new MutableBlobMetadataImpl();
      MutableContentMetadata contentMetadata = new BaseMutableContentMetadata();
      contentMetadata.setContentLength(MultipartUploadStrategy.MAX_BLOCK_SIZE * MultipartUploadStrategy.MAX_NUMBER_OF_BLOCKS + 1);
      metadata.setName(blobName);
      metadata.setContentMetadata(contentMetadata);
      Blob blob = new BlobImpl(metadata);
      ByteSource bytes = ByteSource.wrap("ABCD".getBytes(Charsets.UTF_8));
      Payload payload = Payloads.newByteSourcePayload(bytes);
      payload.setContentMetadata(contentMetadata);
      blob.setPayload(payload);

      AzureBlobBlockUploadStrategy strat = new AzureBlobBlockUploadStrategy(client, slicer, blobToAzureBlob);
      strat.execute(container, blob);
   }

   private Payload createBlockPayload(byte[] blockData) {
      ByteSourcePayload payload = Payloads.newByteSourcePayload(ByteSource.wrap(blockData));
      MutableContentMetadata contentMetadata = new BaseMutableContentMetadata();
      contentMetadata.setContentLength((long) blockData.length);
      payload.setContentMetadata(contentMetadata);
      return payload;
   }
}
