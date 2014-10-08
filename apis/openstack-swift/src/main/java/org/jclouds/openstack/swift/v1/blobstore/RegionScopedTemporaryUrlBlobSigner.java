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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.Uris;
import org.jclouds.http.options.GetOptions;
import org.jclouds.location.Region;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.TemporaryUrlSigner;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Uses {@link TemporaryUrlSigner} to sign requests for access to blobs. If no
 * interval is supplied, it defaults to a year.
 */
public class RegionScopedTemporaryUrlBlobSigner implements BlobRequestSigner {

   @Inject
   protected RegionScopedTemporaryUrlBlobSigner(@Region Supplier<Map<String, Supplier<URI>>> regionToUris,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, @TimeStamp Provider<Long> timestamp, SwiftApi api,
         @Assisted String regionId) {
      checkNotNull(regionId, "regionId");
      this.timestamp = timestamp;
      this.signer = TemporaryUrlSigner.checkApiEvery(api.getAccountApi(regionId), seconds);
      this.storageUrl = regionToUris.get().get(regionId).get();
   }

   private static final long YEAR = TimeUnit.DAYS.toSeconds(365);
   private final BlobToHttpGetOptions toGetOptions = new BlobToHttpGetOptions();
   private final Provider<Long> timestamp;
   private final TemporaryUrlSigner signer;
   private final URI storageUrl;

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      return signGetBlob(container, name, YEAR);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      return sign("GET", container, name, GetOptions.NONE, timestamp.get() + timeInSeconds);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      return sign("GET", container, name, toGetOptions.apply(options), timestamp.get() + YEAR);
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      return signPutBlob(container, blob, YEAR);
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      return sign("PUT", container, blob.getMetadata().getName(), GetOptions.NONE, timestamp.get() + timeInSeconds);
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      return sign("DELETE", container, name, GetOptions.NONE, timestamp.get() + YEAR);
   }

   private HttpRequest sign(String method, String container, String name, GetOptions options, long expires) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      URI url = Uris.uriBuilder(storageUrl).appendPath(container).appendPath(name).build();
      String signature = signer.sign(method, url.getPath(), expires);
      return HttpRequest.builder()
                        .method(method)
                        .endpoint(url)
                        .addQueryParams(options.buildQueryParameters())
                        .addQueryParam("temp_url_sig", signature)
                        .addQueryParam("temp_url_expires", String.valueOf(expires))
                        .headers(options.buildRequestHeaders()).build();
   }
}
