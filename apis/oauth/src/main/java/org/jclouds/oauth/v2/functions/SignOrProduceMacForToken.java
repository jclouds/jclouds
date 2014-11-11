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
package org.jclouds.oauth.v2.functions;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.oauth.v2.JWSAlgorithms.macOrSignature;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.oauth.v2.config.OAuth;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Function that signs/produces mac's for  OAuth tokens, provided a {@link Signature} or a {@link Mac} algorithm and
 * {@link PrivateKey}
 */
public final class SignOrProduceMacForToken implements Supplier<Function<byte[], byte[]>> {

   private final String macOrSignature;
   private final Supplier<PrivateKey> credentials;

   @Inject SignOrProduceMacForToken(@Named(JWS_ALG) String jwsAlg, @OAuth Supplier<PrivateKey> credentials) {
      this.macOrSignature = macOrSignature(jwsAlg);
      this.credentials = credentials;
   }

   @Override public Function<byte[], byte[]> get() {
      try {
         if (macOrSignature.startsWith("SHA")) {
            return new SignatureGenerator(macOrSignature, credentials.get());
         }
         return new MessageAuthenticationCodeGenerator(macOrSignature, credentials.get());
      } catch (NoSuchAlgorithmException e) {
         throw new AssertionError("Invalid contents in JWSAlgorithms! " + e.getMessage());
      } catch (InvalidKeyException e) {
         throw new AuthorizationException("cannot parse pk. " + e.getMessage(), e);
      }
   }

   private static class MessageAuthenticationCodeGenerator implements Function<byte[], byte[]> {

      private final Mac mac;

      private MessageAuthenticationCodeGenerator(String macAlgorithm, PrivateKey privateKey) throws
              NoSuchAlgorithmException, InvalidKeyException {
         this.mac = Mac.getInstance(macAlgorithm);
         this.mac.init(privateKey);
      }

      @Override public byte[] apply(byte[] input) {
         this.mac.update(input);
         return this.mac.doFinal();
      }
   }

   private static class SignatureGenerator implements Function<byte[], byte[]> {

      private final Signature signature;

      private SignatureGenerator(String signatureAlgorithm, PrivateKey privateKey) throws NoSuchAlgorithmException,
              InvalidKeyException {
         this.signature = Signature.getInstance(signatureAlgorithm);
         this.signature.initSign(privateKey);
      }

      @Override public byte[] apply(byte[] input) {
         try {
            signature.update(input);
            return signature.sign();
         } catch (SignatureException e) {
            throw propagate(e);
         }
      }
   }
}
