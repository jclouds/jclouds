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
package org.jclouds.rest.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.TransformingMap;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ConfiguresCredentialStore;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.io.ByteSource;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

@Beta
@ConfiguresCredentialStore
public class CredentialStoreModule extends AbstractModule {
   private static final Map<String, ByteSource> BACKING = new ConcurrentHashMap<String, ByteSource>();
   private final Map<String, ByteSource> backing;

   public CredentialStoreModule(Map<String, ByteSource> backing) {
      this.backing = backing;
   }

   public CredentialStoreModule() {
      this(null);
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Credentials, ByteSource>>() {
      }).to(CredentialsToJsonByteSource.class);
      bind(new TypeLiteral<Function<ByteSource, Credentials>>() {
      }).to(CredentialsFromJsonByteSource.class);
      if (backing != null) {
         bind(new TypeLiteral<Map<String, ByteSource>>() {
         }).toInstance(backing);
      } else {
         bind(new TypeLiteral<Map<String, ByteSource>>() {
         }).toInstance(BACKING);
      }
   }

   public static class CredentialsToJsonByteSource implements Function<Credentials, ByteSource> {
      private final Json json;

      @Inject CredentialsToJsonByteSource(Json json) {
         this.json = json;
      }

      @Override public ByteSource apply(Credentials from) {
         checkNotNull(from, "inputCredentials");
         return ByteSource.wrap(json.toJson(from).getBytes(Charsets.UTF_8));
      }
   }

   public static class CredentialsFromJsonByteSource implements Function<ByteSource, Credentials> {
      @Resource
      protected Logger logger = Logger.NULL;

      private final Json json;

      @Inject CredentialsFromJsonByteSource(Json json) {
         this.json = json;
      }

      @Override public Credentials apply(ByteSource from) {
         try {
            String creds = (checkNotNull(from)).asCharSource(Charsets.UTF_8).read();
            return json.fromJson(creds, Credentials.class);
         } catch (Exception e) {
            logger.warn(e, "ignoring problem retrieving credentials");
            return null;
         }
      }
   }

   @Provides
   @Singleton
   protected Map<String, Credentials> provideCredentialStore(Map<String, ByteSource> backing,
         Function<Credentials, ByteSource> credentialsSerializer,
         Function<ByteSource, Credentials> credentialsDeserializer) {
      return new TransformingMap<String, ByteSource, Credentials>(backing, credentialsDeserializer,
            credentialsSerializer);
   }
}
