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
package org.jclouds.route53.filters;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.net.HttpHeaders.DATE;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.RequestSigner;

import com.google.common.base.Supplier;

/**
 * Signs the Route53 request.
 * 
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/DeveloperGuide/RESTAuthentication.html#StringToSign"
 *      />
 */
@Singleton
public class RestAuthentication implements HttpRequestFilter, RequestSigner {

   private final Supplier<Credentials> creds;
   private final Provider<String> timeStampProvider;

   @Inject
   public RestAuthentication(@org.jclouds.location.Provider Supplier<Credentials> creds,
         @TimeStamp Provider<String> timeStampProvider) {
      this.creds = creds;
      this.timeStampProvider = timeStampProvider;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      Credentials current = creds.get();
      if (current instanceof SessionCredentials) {
         request = replaceSecurityTokenHeader(request, SessionCredentials.class.cast(current));
      }
      request = replaceDateHeader(request, timeStampProvider.get());
      String signature = sign(createStringToSign(request));
      return replaceAuthorizationHeader(request, signature);
   }

   private HttpRequest replaceSecurityTokenHeader(HttpRequest request, SessionCredentials current) {
      return request.toBuilder().replaceHeader("x-amz-security-token", current.getSessionToken()).build();
   }

   private HttpRequest replaceDateHeader(HttpRequest request, String timestamp) {
      request = request.toBuilder().replaceHeader(DATE, timestamp).build();
      return request;
   }

   @Override
   public String createStringToSign(HttpRequest input) {
      return input.getFirstHeaderOrNull(DATE);
   }

   /**
    * signs {@code toSign} using {@code HmacSHA256} initialized with
    * {@link Credentials#credential}.
    *
    * @param toSign
    *           string to sign in UTF-8 encoding
    *
    * @return base-64 encoded signature.
    */
   @Override
   public String sign(String toSign) {
      try {
         SecretKeySpec keySpec = new SecretKeySpec(creds.get().credential.getBytes(UTF_8), "HmacSHA256");
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(keySpec);
         byte[] result = mac.doFinal(toSign.getBytes(UTF_8));
         return base64().encode(result);
      } catch (InvalidKeyException e) {
         throw propagate(e);
      } catch (NoSuchAlgorithmException e) {
         throw propagate(e);
      }
   }

   private HttpRequest replaceAuthorizationHeader(HttpRequest request, String signature) {
      request = request
            .toBuilder()
            .replaceHeader("X-Amzn-Authorization",
                  "AWS3-HTTPS AWSAccessKeyId=" + creds.get().identity + ",Algorithm=HmacSHA256,Signature=" + signature)
            .build();
      return request;
   }


}
