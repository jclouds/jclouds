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
package org.jclouds.aws.s3.blobstore;

import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.blobstore.S3BlobRequestSigner;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.filters.RequestAuthorizeSignatureV4;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

public class AWSS3BlobRequestSignerV4 extends S3BlobRequestSigner<AWSS3Client> {

   private final RequestAuthorizeSignatureV4 authSigner;

   @Inject
   public AWSS3BlobRequestSignerV4(RestAnnotationProcessor processor, BlobToObject blobToObject,
                                   BlobToHttpGetOptions blob2HttpGetOptions, Class<AWSS3Client> interfaceClass,
                                   RequestAuthorizeSignatureV4 authSigner) throws SecurityException, NoSuchMethodException {
      super(processor, blobToObject, blob2HttpGetOptions, interfaceClass);
      this.authSigner = authSigner;
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      HttpRequest request = processor.apply(Invocation.create(getMethod, ImmutableList.<Object>of(container, name)));
      request = authSigner.signForTemporaryAccess(request, timeInSeconds);
      return cleanRequest(request);
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(blob, "blob");
      HttpRequest request = processor.apply(Invocation.create(createMethod,
         ImmutableList.<Object>of(container, blobToObject.apply(blob))));
      request = authSigner.signForTemporaryAccess(request, timeInSeconds);
      return cleanRequest(request);
   }
}
