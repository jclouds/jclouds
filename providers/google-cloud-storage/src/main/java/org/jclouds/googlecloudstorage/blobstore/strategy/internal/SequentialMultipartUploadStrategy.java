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
package org.jclouds.googlecloudstorage.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Provider;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecloudstorage.blobstore.functions.BlobMetadataToObjectTemplate;
import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.googlecloudstorage.domain.templates.ComposeObjectTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public final class SequentialMultipartUploadStrategy extends MultipartUploadStrategy {

   private final GoogleCloudStorageApi api;
   private final Provider<BlobBuilder> blobBuilders;
   private final BlobMetadataToObjectTemplate blob2ObjectTemplate;
   private final MultipartUploadSlicingAlgorithm algorithm;
   private final PayloadSlicer slicer;
   private final MultipartNamingStrategy namingStrategy;

   @Inject SequentialMultipartUploadStrategy(GoogleCloudStorageApi api, Provider<BlobBuilder> blobBuilders,
            BlobMetadataToObjectTemplate blob2ObjectTemplate, MultipartUploadSlicingAlgorithm algorithm,
            PayloadSlicer slicer, MultipartNamingStrategy namingStrategy) {
      this.api = api;
      this.blobBuilders = blobBuilders;
      this.blob2ObjectTemplate = blob2ObjectTemplate;
      this.algorithm = algorithm;
      this.slicer = slicer;
      this.namingStrategy = namingStrategy;
   }

   @Override
   public String execute(String container, Blob blob) {

      ObjectTemplate destination = blob2ObjectTemplate.apply(blob.getMetadata());

      List<GoogleCloudStorageObject> sourceList = Lists.newArrayList();

      String key = blob.getMetadata().getName();
      Payload payload = blob.getPayload();
      Long length = payload.getContentMetadata().getContentLength();
      if (length == null) {
         length = blob.getMetadata().getContentMetadata().getContentLength();
         payload.getContentMetadata().setContentLength(length);
      }
      checkNotNull(length,
               "please invoke payload.getContentMetadata().setContentLength(length) prior to multipart upload");
      long chunkSize = algorithm.calculateChunkSize(length);
      int partCount = algorithm.getParts();
      if (partCount > 0) {
         for (Payload part : slicer.slice(payload, chunkSize)) {
            int partNum = algorithm.getNextPart();
            String partName = namingStrategy.getPartName(key, partNum, partCount);
            long partSize = ((partCount + 1) == partNum) ? algorithm.getRemaining() : algorithm.getChunkSize();
            Blob blobPart = blobBuilders.get().name(partName).payload(part).contentDisposition(partName)
                     .contentLength(partSize).contentType(blob.getMetadata().getContentMetadata().getContentType())
                     .build();
            GoogleCloudStorageObject object = api.getObjectApi().multipartUpload(container,
                     blob2ObjectTemplate.apply(blobPart.getMetadata()), part);
            sourceList.add(object);
         }
         ComposeObjectTemplate template = ComposeObjectTemplate.create(sourceList, destination);
         return api.getObjectApi().composeObjects(container, key, template).etag();
      } else {
         return api.getObjectApi()
                  .multipartUpload(container, blob2ObjectTemplate.apply(blob.getMetadata()), blob.getPayload())
                  .etag();
      }
   }
}
