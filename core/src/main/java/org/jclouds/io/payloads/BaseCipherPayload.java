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
package org.jclouds.io.payloads;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import org.jclouds.crypto.Crypto;
import org.jclouds.io.Payload;

public abstract class BaseCipherPayload extends DelegatingPayload {

   private final Key key;

   protected final Crypto crypto;

   public BaseCipherPayload(Crypto crypto, Payload delegate, Key key) {
      super(delegate);
      this.crypto = checkNotNull(crypto, "crypto");
      this.key = checkNotNull(key, "key");
   }

   public abstract Cipher initializeCipher(Key key);

   @Override
   public CipherInputStream openStream() throws IOException {
      return new CipherInputStream(super.openStream(), initializeCipher(key));
   }
}
