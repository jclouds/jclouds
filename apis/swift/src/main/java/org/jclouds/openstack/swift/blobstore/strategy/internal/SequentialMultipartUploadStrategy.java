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
package org.jclouds.openstack.swift.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.domain.SwiftObject;

import com.google.inject.Inject;

public class SequentialMultipartUploadStrategy implements MultipartUploadStrategy {

   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   private Logger logger = Logger.NULL;

   private final CommonSwiftClient client;
   private final Provider<BlobBuilder> blobBuilders;
   private final BlobToObject blob2Object;
   private final MultipartUploadSlicingAlgorithm algorithm;
   private final PayloadSlicer slicer;
   private final MultipartNamingStrategy namingStrategy;

   @Inject
   public SequentialMultipartUploadStrategy(CommonSwiftClient client, Provider<BlobBuilder> blobBuilders,
         BlobToObject blob2Object, MultipartUploadSlicingAlgorithm algorithm, PayloadSlicer slicer,
         MultipartNamingStrategy namingStrategy) {
      this.client = checkNotNull(client, "client");
      this.blobBuilders = checkNotNull(blobBuilders, "blobBuilders");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.algorithm = checkNotNull(algorithm, "algorithm");
      this.slicer = checkNotNull(slicer, "slicer");
      this.namingStrategy = checkNotNull(namingStrategy, "namingStrategy");
   }

   @Override
   public String execute(String container, Blob blob) {
      String key = blob.getMetadata().getName();
      Payload payload = blob.getPayload();
      Long length = payload.getContentMetadata().getContentLength();
      checkNotNull(length,
            "please invoke payload.getContentMetadata().setContentLength(length) prior to multipart upload");
      long chunkSize = algorithm.calculateChunkSize(length);
      int partCount = algorithm.getParts();
      if (partCount > 0) {
         for (Payload part : slicer.slice(payload, chunkSize)) {
            int partNum = algorithm.getNextPart();
            String partName = namingStrategy.getPartName(key, partNum, partCount);
            Blob blobPart = blobBuilders.get()
                                        .name(partName)
                                        .payload(part)
                                        .contentDisposition(partName)
                                        .build();
            client.putObject(container, blob2Object.apply(blobPart));
         }

         SwiftObject manifest = blob2Object.apply(blob);
         // put empty manifest object retaining existing metadata
         manifest.getPayload().getContentMetadata().setContentLength(0L);
         return client.putObjectManifest(container, manifest);
      } else {
         return client.putObject(container, blob2Object.apply(blob));
      }
   }
}
