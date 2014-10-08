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
package org.jclouds.openstack.swift.v1;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base16;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jclouds.openstack.swift.v1.features.AccountApi;

import com.google.common.base.Supplier;

/**
 * Use this utility to create temporary urls.
 */
public class TemporaryUrlSigner {

   public static TemporaryUrlSigner checkApiEvery(final AccountApi api, long seconds) {
      Supplier<String> keySupplier = memoizeWithExpiration(new TemporaryUrlKeyFromAccount(api), seconds, SECONDS);
      return new TemporaryUrlSigner(keySupplier);
   }

   private final Supplier<String> keySupplier;

   TemporaryUrlSigner(Supplier<String> keySupplier) {
      this.keySupplier = keySupplier;
   }

   public String sign(String method, String path, long expirationTimestampSeconds) {
      checkNotNull(method, "method");
      checkNotNull(path, "path");
      checkArgument(expirationTimestampSeconds > 0, "expirationTimestamp must be a unix epoch timestamp");
      String hmacBody = format("%s\n%s\n%s", method, expirationTimestampSeconds, path);
      return base16().lowerCase().encode(hmacSHA1(hmacBody));
   }

   byte[] hmacSHA1(String data) {
      try {
         String key = keySupplier.get();
         checkState(key != null, "%s returned a null temporaryUrlKey!", keySupplier);
         Mac mac = Mac.getInstance("HmacSHA1");
         mac.init(new SecretKeySpec(key.getBytes(UTF_8), "HmacSHA1"));
         return mac.doFinal(data.getBytes(UTF_8));
      } catch (Exception e) {
         throw propagate(e);
      }
   }

   static class TemporaryUrlKeyFromAccount implements Supplier<String> {
      private final AccountApi api;

      private TemporaryUrlKeyFromAccount(AccountApi api) {
         this.api = checkNotNull(api, "accountApi");
      }

      @Override
      public String get() {
         return api.get().getTemporaryUrlKey().orNull();
      }

      @Override
      public String toString() {
         return format("get().getTemporaryUrlKey() using %s", api);
      }
   }
}
