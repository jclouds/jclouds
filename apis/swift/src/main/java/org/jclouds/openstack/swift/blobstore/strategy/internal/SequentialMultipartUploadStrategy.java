/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.swift.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.util.Throwables2;

import com.google.inject.Inject;


public class SequentialMultipartUploadStrategy implements MultipartUploadStrategy {
    public static final String PART_SEPARATOR = "/";

    @Resource
    @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
    protected Logger logger = Logger.NULL;

    protected final SwiftBlobStore ablobstore;
    protected final PayloadSlicer slicer;
    
    @Inject
    public SequentialMultipartUploadStrategy(SwiftBlobStore ablobstore, PayloadSlicer slicer) {
        this.ablobstore = checkNotNull(ablobstore, "ablobstore");
        this.slicer = checkNotNull(slicer, "slicer");
    }

    @Override
    public String execute(String container, Blob blob, PutOptions options, BlobToObject blob2Object) {
        String key = blob.getMetadata().getName();
        Payload payload = blob.getPayload();
        MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm();
        algorithm
                .calculateChunkSize(checkNotNull(
                        payload.getContentMetadata().getContentLength(),
                        "contentLength required on all uploads to swift; please invoke payload.getContentMetadata().setContentLength(length) first"));
        int parts = algorithm.getParts();
        long chunkSize = algorithm.getChunkSize();
        if (parts > 0) {
            CommonSwiftClient client = ablobstore.getContext().unwrap(SwiftApiMetadata.CONTEXT_TOKEN).getApi();
            try {
                int part;
                while ((part = algorithm.getNextPart()) <= parts) {
                    Payload chunkedPart = slicer.slice(payload,
                            algorithm.getNextChunkOffset(), chunkSize);
                    Blob blobPart = ablobstore.blobBuilder(blob.getMetadata().getName() + PART_SEPARATOR +
                            String.valueOf(part)).payload(chunkedPart).contentDisposition(
                            blob.getMetadata().getName() + PART_SEPARATOR + String.valueOf(part)).build();
                    client.putObject(container, blob2Object.apply(blobPart));
                }
                long remaining = algorithm.getRemaining();
                if (remaining > 0) {
                    Payload chunkedPart = slicer.slice(payload,
                            algorithm.getNextChunkOffset(), remaining);
                    Blob blobPart = ablobstore.blobBuilder(blob.getMetadata().getName() + PART_SEPARATOR + 
                    String.valueOf(part)).payload(chunkedPart).contentDisposition(
                            blob.getMetadata().getName() + PART_SEPARATOR + String.valueOf(part)).build();
                    client.putObject(container, blob2Object.apply(blobPart));
                }
                return client.putObjectManifest(container, key);
            } catch (Exception ex) {
                RuntimeException rtex = Throwables2.getFirstThrowableOfType(ex, RuntimeException.class);
                if (rtex == null) {
                    rtex = new RuntimeException(ex);
                }
                throw rtex;
            }
        } else {
            return ablobstore.putBlob(container, blob, PutOptions.NONE);
        }
    }
}
