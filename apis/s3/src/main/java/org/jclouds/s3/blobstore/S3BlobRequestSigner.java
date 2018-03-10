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
package org.jclouds.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;
import static org.jclouds.reflect.Reflection2.method;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.GetOptions;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.options.PutObjectOptions;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

@Singleton
public class S3BlobRequestSigner<T extends S3Client> implements BlobRequestSigner {
   /** Matches Amazon default when Expiry parameter not present. */
   private static final int DEFAULT_EXPIRY_SECONDS = 15 * 60;

   private final RequestAuthorizeSignature authSigner;

   protected final RestAnnotationProcessor processor;
   protected final BlobToObject blobToObject;
   protected final BlobToHttpGetOptions blob2HttpGetOptions;

   protected final Invokable<?, ?> getMethod;
   protected final Invokable<?, ?> deleteMethod;
   protected final Invokable<?, ?> createMethod;

   @Inject
   public S3BlobRequestSigner(RestAnnotationProcessor processor, BlobToObject blobToObject,
         BlobToHttpGetOptions blob2HttpGetOptions, Class<T> interfaceClass,
         RequestAuthorizeSignature authSigner)
         throws SecurityException, NoSuchMethodException {
      this.processor = checkNotNull(processor, "processor");
      this.blobToObject = checkNotNull(blobToObject, "blobToObject");
      this.blob2HttpGetOptions = checkNotNull(blob2HttpGetOptions, "blob2HttpGetOptions");
      this.getMethod = method(interfaceClass, "getObject", String.class, String.class, GetOptions[].class);
      this.deleteMethod = method(interfaceClass, "deleteObject", String.class, String.class);
      this.createMethod = method(interfaceClass, "putObject", String.class, S3Object.class, PutObjectOptions[].class);
      this.authSigner = authSigner;
   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      return signGetBlob(container, name, DEFAULT_EXPIRY_SECONDS);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      HttpRequest request = processor.apply(Invocation.create(getMethod, ImmutableList.<Object> of(container, name)));
      return cleanRequest(authSigner.signForTemporaryAccess(request, timeInSeconds));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      return signPutBlob(container, blob, DEFAULT_EXPIRY_SECONDS);
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(blob, "blob");
      HttpRequest request = processor.apply(Invocation.create(createMethod,
         ImmutableList.<Object>of(container, blobToObject.apply(blob))));
      return cleanRequest(authSigner.signForTemporaryAccess(request, timeInSeconds));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      return cleanRequest(processor.apply(Invocation.create(getMethod,
            ImmutableList.of(container, name, blob2HttpGetOptions.apply(checkNotNull(options, "options"))))));
   }
}
