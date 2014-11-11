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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.crypto.Pems.privateKeySpec;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.oauth.v2.JWSAlgorithms;
import org.jclouds.rest.AuthorizationException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Loads {@link PrivateKey} from a pem private key using the KeyFactory obtained vi {@link
 * JWSAlgorithms#keyFactory(String)}. The pem pk algorithm must match the KeyFactory algorithm.
 */
@Singleton // due to cache
public final class PrivateKeySupplier implements Supplier<PrivateKey> {

   private final Supplier<Credentials> creds;
   private final LoadingCache<Credentials, PrivateKey> keyCache;

   @Inject PrivateKeySupplier(@Provider Supplier<Credentials> creds, PrivateKeyForCredentials loader) {
      this.creds = creds;
      // throw out the private key related to old credentials
      this.keyCache = CacheBuilder.newBuilder().maximumSize(2).build(checkNotNull(loader, "loader"));
   }

   /**
    * it is relatively expensive to extract a private key from a PEM. cache the relationship between current
    * credentials
    * so that the private key is only recalculated once.
    */
   @VisibleForTesting
   static final class PrivateKeyForCredentials extends CacheLoader<Credentials, PrivateKey> {
      private final String jwsAlg;

      @Inject PrivateKeyForCredentials(@Named(JWS_ALG) String jwsAlg) {
         this.jwsAlg = jwsAlg;
      }

      @Override public PrivateKey load(Credentials in) {
         try {
            String privateKeyInPemFormat = in.credential;
            KeyFactory keyFactory = JWSAlgorithms.keyFactory(jwsAlg);
            return keyFactory.generatePrivate(privateKeySpec(ByteSource.wrap(privateKeyInPemFormat.getBytes(UTF_8))));
         } catch (IOException e) {
            throw propagate(e);
         } catch (InvalidKeySpecException e) {
            throw new AuthorizationException("security exception loading credentials. " + e.getMessage(), e);
            // catch IAE that is thrown when parsing the pk fails
         } catch (IllegalArgumentException e) {
            throw new AuthorizationException("cannot parse pk. " + e.getMessage(), e);
         }
      }
   }

   @Override public PrivateKey get() {
      try {
         // loader always throws UncheckedExecutionException so no point in using get()
         return keyCache.getUnchecked(checkNotNull(creds.get(), "credential supplier returned null"));
      } catch (UncheckedExecutionException e) {
         AuthorizationException authorizationException = getFirstThrowableOfType(e, AuthorizationException.class);
         if (authorizationException != null) {
            throw authorizationException;
         }
         throw e;
      }
   }
}
