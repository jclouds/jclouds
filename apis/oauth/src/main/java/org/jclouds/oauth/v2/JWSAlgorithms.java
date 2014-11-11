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
package org.jclouds.oauth.v2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * JSON Web Signature Algorithms
 * <p/>
 * We only support <a href="http://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-36#section-3.1">required
 * or recommended algorithms</a>, with the exception of {@code none}, which is only supported in tests.
 */
public final class JWSAlgorithms {
   /** This is a marker algorithm only supported in tests. */
   public static final String NONE = "none";

   private static final List<String> SUPPORTED_ALGS = ImmutableList.of("ES256", "RS256", "HS256", NONE);

   /**
    * Static mapping between the oauth algorithm name and the Crypto provider signature algorithm name and KeyFactory.
    */
   private static final List<List<String>> ALG_TO_SIGNATURE_ALG_AND_KEY_FACTORY = ImmutableList.<List<String>>of( //
         ImmutableList.of(SUPPORTED_ALGS.get(0), "SHA256withECDSA", "EC"), // ECDSA using P-256 and SHA-256
         ImmutableList.of(SUPPORTED_ALGS.get(1), "SHA256withRSA", "RSA"), // RSASSA-PKCS-v1_5 using SHA-256
         ImmutableList.of(SUPPORTED_ALGS.get(2), "HmacSHA256", "DiffieHellman") // HMAC using SHA-256
   );

   /** Ordered list of supported algorithms by recommendation. */
   public static List<String> supportedAlgs() {
      return SUPPORTED_ALGS;
   }

   public static String macOrSignature(String jwsAlg) {
      return ALG_TO_SIGNATURE_ALG_AND_KEY_FACTORY.get(indexOf(jwsAlg)).get(1);
   }

   public static KeyFactory keyFactory(String jwsAlg) {
      String keyFactoryAlgorithm = ALG_TO_SIGNATURE_ALG_AND_KEY_FACTORY.get(indexOf(jwsAlg)).get(2);
      try {
         return KeyFactory.getInstance(keyFactoryAlgorithm);
      } catch (NoSuchAlgorithmException e) {
         throw new AssertionError("Invalid contents in JWSAlgorithms! " + e.getMessage());
      }
   }

   private static int indexOf(String jwsAlg) {
      int result = SUPPORTED_ALGS.indexOf(checkNotNull(jwsAlg, "jwsAlg"));
      checkArgument(result != -1, "JSON Web Signature alg %s is not in the supported list %s", jwsAlg, SUPPORTED_ALGS);
      return result;
   }

   private JWSAlgorithms() {
   }
}
