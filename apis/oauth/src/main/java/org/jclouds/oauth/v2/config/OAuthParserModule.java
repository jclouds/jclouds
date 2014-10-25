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
package org.jclouds.oauth.v2.config;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.Token;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/** Configures type adapter factories for {@link Header}, {@link ClaimSet}, and {@link Token}. */
public final class OAuthParserModule extends AbstractModule {
   @Override protected void configure() {
   }

   // TODO: change jclouds core to use collaborative set bindings
   @Provides @Singleton public Set<TypeAdapterFactory> typeAdapterFactories() {
      return ImmutableSet
            .<TypeAdapterFactory>of(new HeaderTypeAdapter(), new ClaimSetTypeAdapter(), new TokenAdapter());
   }

   private static final class HeaderTypeAdapter extends SubtypeAdapterFactory<Header> {
      HeaderTypeAdapter() {
         super(Header.class);
      }

      @Override public void write(JsonWriter out, Header value) throws IOException {
         out.beginObject();
         out.name("alg");
         out.value(value.signerAlgorithm());
         out.name("typ");
         out.value(value.type());
         out.endObject();
      }

      @Override public Header read(JsonReader in) throws IOException {
         in.beginObject();
         in.nextName();
         String signerAlgorithm = in.nextString();
         in.nextName();
         String type = in.nextString();
         in.endObject();
         return Header.create(signerAlgorithm, type);
      }
   }

   private static final class ClaimSetTypeAdapter extends SubtypeAdapterFactory<ClaimSet> {
      ClaimSetTypeAdapter() {
         super(ClaimSet.class);
      }

      @Override public void write(JsonWriter out, ClaimSet value) throws IOException {
         out.beginObject();
         for (Map.Entry<String, String> entry : value.claims().entrySet()) {
            out.name(entry.getKey());
            out.value(entry.getValue());
         }
         out.name("exp");
         out.value(value.expirationTime());
         out.name("iat");
         out.value(value.emissionTime());
         out.endObject();
      }

      @Override public ClaimSet read(JsonReader in) throws IOException {
         Map<String, String> claims = new LinkedHashMap<String, String>();
         in.beginObject();
         while (in.hasNext()) {
            claims.put(in.nextName(), in.nextString());
         }
         in.endObject();
         return ClaimSet.create(0, 0, Collections.unmodifiableMap(claims));
      }
   }

   /** OAuth is used in apis that may not default to snake case. Explicity control case format. */
   private static final class TokenAdapter extends SubtypeAdapterFactory<Token> {
      TokenAdapter() {
         super(Token.class);
      }

      @Override public void write(JsonWriter out, Token value) throws IOException {
         out.beginObject();
         out.name("access_token");
         out.value(value.accessToken());
         out.name("token_type");
         out.value(value.tokenType());
         out.name("expires_in");
         out.value(value.expiresIn());
         out.endObject();
      }

      @Override public Token read(JsonReader in) throws IOException {
         String accessToken = null;
         String tokenType = null;
         int expiresIn = 0;
         in.beginObject();
         while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("access_token")) {
               accessToken = in.nextString();
            } else if (name.equals("token_type")) {
               tokenType = in.nextString();
            } else if (name.equals("expires_in")) {
               expiresIn = in.nextInt();
            } else {
               in.skipValue();
            }
         }
         in.endObject();
         return Token.create(accessToken, tokenType, expiresIn);
      }
   }

   private abstract static class SubtypeAdapterFactory<T> extends TypeAdapter<T> implements TypeAdapterFactory {
      private final Class<T> baseClass;

      private SubtypeAdapterFactory(Class<T> baseClass) {
         this.baseClass = baseClass;
      }

      @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         if (!(baseClass.isAssignableFrom(typeToken.getRawType()))) {
            return null;
         }
         return (TypeAdapter<T>) this;
      }
   }
}
