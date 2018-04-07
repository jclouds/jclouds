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
package org.jclouds.googlecloudstorage.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Provider;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.Uris;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger;
import org.jclouds.oauth.v2.config.Authorization;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public final class GoogleCloudStorageBlobRequestSigner implements BlobRequestSigner {
   private static final int DEFAULT_EXPIRY_SECONDS = 15 * 60;
   private static final URI STORAGE_URL = URI.create("http://storage.googleapis.com/");

   private final Supplier<Credentials> creds;
   private final Supplier<PrivateKey> privateKey;
   private final Provider<Long> timestamp;
   private final HttpUtils utils;

   private final BlobToHttpGetOptions toGetOptions = new BlobToHttpGetOptions();

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   protected Logger signatureLog = Logger.NULL;

   @Inject
   protected GoogleCloudStorageBlobRequestSigner(@org.jclouds.location.Provider Supplier<Credentials> creds,
         @Authorization Supplier<PrivateKey> privateKey, @TimeStamp Provider<Long> timestamp, HttpUtils utils) {
      this.creds = creds;
      this.privateKey = privateKey;
      this.timestamp = timestamp;
      this.utils = utils;
   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      return signGetBlob(container, name, DEFAULT_EXPIRY_SECONDS);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      return sign("GET", container, name, GetOptions.NONE, timestamp.get() + timeInSeconds, null);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      return sign("GET", container, name, toGetOptions.apply(options), timestamp.get() + DEFAULT_EXPIRY_SECONDS, null);
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      return signPutBlob(container, blob, DEFAULT_EXPIRY_SECONDS);
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      return sign("PUT", container, blob.getMetadata().getName(), GetOptions.NONE, timestamp.get() + timeInSeconds,
            blob.getMetadata().getContentMetadata().getContentType());
   }

   private HttpRequest sign(String method, String container, String name, GetOptions options, long expires, String contentType) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");

      HttpRequest.Builder request = HttpRequest.builder()
            .method(method)
            .endpoint(Uris.uriBuilder(STORAGE_URL).appendPath(container).appendPath(name).build());
      if (contentType != null) {
         request.replaceHeader(HttpHeaders.CONTENT_TYPE, contentType);
      }

      String stringToSign = createStringToSign(request.build(), expires);
      byte[] rawSignature;
      try {
         Signature signer = Signature.getInstance("SHA256withRSA");
         signer.initSign(privateKey.get());
         signer.update(stringToSign.getBytes(Charsets.UTF_8));
         rawSignature = signer.sign();
      } catch (InvalidKeyException ike) {
         throw new RuntimeException(ike);
      } catch (NoSuchAlgorithmException nsae) {
         throw new RuntimeException(nsae);
      } catch (SignatureException se) {
         throw new RuntimeException(se);
      }
      String signature = BaseEncoding.base64().encode(rawSignature);

      for (Map.Entry<String, String> entry : options.buildRequestHeaders().entries()) {
         request.addHeader(entry.getKey(), entry.getValue());
      }

      return (HttpRequest) request
            .addQueryParam("Expires", String.valueOf(expires))
            .addQueryParam("GoogleAccessId", creds.get().identity)
            .addQueryParam("Signature", signature)
            .build();
   }

   private String createStringToSign(HttpRequest request, long expires) {
      utils.logRequest(signatureLog, request, ">>");
      StringBuilder buffer = new StringBuilder();
      buffer.append(request.getMethod()).append("\n");
      buffer.append(Strings.nullToEmpty(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_MD5))).append("\n");
      buffer.append(Strings.nullToEmpty(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE))).append("\n");
      buffer.append(String.valueOf(expires)).append("\n");
      // TODO: extension headers
      buffer.append(request.getEndpoint().getPath());
      return buffer.toString();
   }
}
