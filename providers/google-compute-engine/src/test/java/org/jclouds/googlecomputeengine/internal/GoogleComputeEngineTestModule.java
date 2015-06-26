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
package org.jclouds.googlecomputeengine.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.jclouds.crypto.Pems.privateKeySpec;
import static org.jclouds.crypto.Pems.publicKeySpec;
import static org.jclouds.crypto.PemsTest.PRIVATE_KEY;
import static org.jclouds.crypto.PemsTest.PUBLIC_KEY;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.crypto.Crypto;
import org.jclouds.oauth.v2.filters.JWTBearerTokenFlow;
import org.jclouds.ssh.SshKeys;

import com.google.common.base.Supplier;
import com.google.common.io.ByteSource;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

enum GoogleComputeEngineTestModule implements Module {
   INSTANCE;

   private final KeyPair keyPair;
   final String openSshKey;

   GoogleComputeEngineTestModule() {
      try {
         KeyFactory keyfactory = KeyFactory.getInstance("RSA");
         PrivateKey privateKey = keyfactory
               .generatePrivate(privateKeySpec(ByteSource.wrap(PRIVATE_KEY.getBytes(UTF_8))));
         PublicKey publicKey = keyfactory.generatePublic(publicKeySpec(ByteSource.wrap(PUBLIC_KEY.getBytes(UTF_8))));
         keyPair = new KeyPair(publicKey, privateKey);
         openSshKey = SshKeys.encodeAsOpenSSH(RSAPublicKey.class.cast(publicKey));
      } catch (Exception e) {
         throw propagate(e);
      }
   }

   @Override public void configure(Binder binder) {
      // Predictable time
      binder.bind(JWTBearerTokenFlow.class).to(JWTBearerTokenFlow.TestJWTBearerTokenFlow.class);

      // Predictable ssh keys
      Crypto crypto = createMock(Crypto.class);
      KeyPairGenerator rsaKeyPairGenerator = createMock(KeyPairGenerator.class);
      SecureRandom secureRandom = createMock(SecureRandom.class);
      expect(crypto.rsaKeyPairGenerator()).andReturn(rsaKeyPairGenerator).anyTimes();
      rsaKeyPairGenerator.initialize(2048, secureRandom);
      expectLastCall().anyTimes();
      expect(rsaKeyPairGenerator.genKeyPair()).andReturn(keyPair).anyTimes();
      replay(crypto, rsaKeyPairGenerator, secureRandom);
      binder.bind(Crypto.class).toInstance(crypto);
      binder.bind(SecureRandom.class).toInstance(secureRandom);

      //  predictable node names
      final AtomicInteger suffix = new AtomicInteger();
      binder.bind(new TypeLiteral<Supplier<String>>() {
      }).toInstance(new Supplier<String>() {
         @Override
         public String get() {
            return suffix.getAndIncrement() + "";
         }
      });
   }
}
