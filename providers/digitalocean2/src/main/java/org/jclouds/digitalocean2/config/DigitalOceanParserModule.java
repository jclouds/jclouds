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
package org.jclouds.digitalocean2.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static com.google.inject.Scopes.SINGLETON;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.digitalocean2.ssh.DSAKeys;
import org.jclouds.digitalocean2.ssh.ECDSAKeys;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.ssh.SshKeys;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Custom parser bindings.
 */
public class DigitalOceanParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class).in(SINGLETON);
   }

   @Singleton
   public static class SshPublicKeyAdapter extends TypeAdapter<PublicKey> {

      private final Function<PublicKey, String> publicKeyToSshKey;
      private final Function<String, PublicKey> sshKeyToPublicKey;

      @Inject
      public SshPublicKeyAdapter(Function<PublicKey, String> publicKeyToSshKey,
            Function<String, PublicKey> sshKeyToPublicKey) {
         this.publicKeyToSshKey = checkNotNull(publicKeyToSshKey, "publicKeyToSshKey cannot be null");
         this.sshKeyToPublicKey = checkNotNull(sshKeyToPublicKey, "sshKeyToPublicKey cannot be null");
      }

      @Override
      public void write(JsonWriter out, PublicKey value) throws IOException {
         out.value(publicKeyToSshKey.apply(value));
      }

      @Override
      public PublicKey read(JsonReader in) throws IOException {
         return sshKeyToPublicKey.apply(in.nextString().trim());
      }
   }

   @Provides
   @Singleton
   public Function<PublicKey, String> publicKeyToSshKey() {
      return new Function<PublicKey, String>() {
         @Override
         public String apply(PublicKey input) {
            if (input instanceof RSAPublicKey) {
               return SshKeys.encodeAsOpenSSH((RSAPublicKey) input);
            } else if (input instanceof DSAPublicKey) {
               return DSAKeys.encodeAsOpenSSH((DSAPublicKey) input);
            } else {
               throw new IllegalArgumentException("Only RSA and DSA keys are supported");
            }
         }
      };
   }

   @Provides
   @Singleton
   public Function<String, PublicKey> sshKeyToPublicKey() {
      return new Function<String, PublicKey>() {
         @Override
         public PublicKey apply(String input) {
            Iterable<String> parts = Splitter.on(' ').split(input);
            checkArgument(size(parts) >= 2, "bad format, should be: [ssh-rsa|ssh-dss] AAAAB3...");
            String type = get(parts, 0);

            try {
               if ("ssh-rsa".equals(type)) {
                  RSAPublicKeySpec spec = SshKeys.publicKeySpecFromOpenSSH(input);
                  return KeyFactory.getInstance("RSA").generatePublic(spec);
               } else if ("ssh-dss".equals(type)) {
                  DSAPublicKeySpec spec = DSAKeys.publicKeySpecFromOpenSSH(input);
                  return KeyFactory.getInstance("DSA").generatePublic(spec);
               } else if (type.startsWith("ecdsa-sha2-")) {
                  ECPublicKeySpec spec = ECDSAKeys.publicKeySpecFromOpenSSH(input);
                  return KeyFactory.getInstance("EC").generatePublic(spec);
               } else {
                  throw new IllegalArgumentException("bad format, jclouds supports ssh-rsa, ssh-dss, ecdsa-sha2-nistp[256|384|521]");
               }
            } catch (InvalidKeySpecException ex) {
               throw propagate(ex);
            } catch (NoSuchAlgorithmException ex) {
               throw propagate(ex);
            }
         }
      };
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings(SshPublicKeyAdapter sshPublicKeyAdapter) {
      return ImmutableMap.<Type, Object> of(PublicKey.class, sshPublicKeyAdapter);
   }

}
