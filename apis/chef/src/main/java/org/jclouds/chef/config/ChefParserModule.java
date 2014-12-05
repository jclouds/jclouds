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
package org.jclouds.chef.config;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.functions.ParseCookbookDefinitionFromJson;
import org.jclouds.chef.functions.ParseCookbookVersionsV09FromJson;
import org.jclouds.chef.functions.ParseCookbookVersionsV10FromJson;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.chef.suppliers.ChefVersionSupplier;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.MapTypeAdapterFactory;
import org.jclouds.json.internal.NullHackJsonLiteralAdapter;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;
import com.google.inject.Provides;

public class ChefParserModule extends AbstractModule {
   @ImplementedBy(PrivateKeyAdapterImpl.class)
   public interface PrivateKeyAdapter extends JsonDeserializer<PrivateKey> {

   }

   @Singleton
   public static class PrivateKeyAdapterImpl implements PrivateKeyAdapter {
      private final Crypto crypto;

      @Inject
      PrivateKeyAdapterImpl(Crypto crypto) {
         this.crypto = crypto;
      }

      @Override
      public PrivateKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         String keyText = json.getAsString().replaceAll("\\n", "\n");
         try {
            return crypto.rsaKeyFactory().generatePrivate(
                  Pems.privateKeySpec(ByteSource.wrap(keyText.getBytes(Charsets.UTF_8))));
         } catch (UnsupportedEncodingException e) {
            Throwables.propagate(e);
            return null;
         } catch (InvalidKeySpecException e) {
            Throwables.propagate(e);
            return null;
         } catch (IOException e) {
            Throwables.propagate(e);
            return null;
         }
      }
   }

   @ImplementedBy(PublicKeyAdapterImpl.class)
   public interface PublicKeyAdapter extends JsonDeserializer<PublicKey> {

   }

   @Singleton
   public static class PublicKeyAdapterImpl implements PublicKeyAdapter {
      private final Crypto crypto;

      @Inject
      PublicKeyAdapterImpl(Crypto crypto) {
         this.crypto = crypto;
      }

      @Override
      public PublicKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         String keyText = json.getAsString().replaceAll("\\n", "\n");
         try {
            return crypto.rsaKeyFactory().generatePublic(
                  Pems.publicKeySpec(ByteSource.wrap(keyText.getBytes(Charsets.UTF_8))));
         } catch (UnsupportedEncodingException e) {
            Throwables.propagate(e);
            return null;
         } catch (InvalidKeySpecException e) {
            Throwables.propagate(e);
            return null;
         } catch (IOException e) {
            Throwables.propagate(e);
            return null;
         }
      }
   }

   @ImplementedBy(X509CertificateAdapterImpl.class)
   public interface X509CertificateAdapter extends JsonDeserializer<X509Certificate> {

   }

   @Singleton
   public static class X509CertificateAdapterImpl implements X509CertificateAdapter {
      private final Crypto crypto;

      @Inject
      X509CertificateAdapterImpl(Crypto crypto) {
         this.crypto = crypto;
      }

      @Override
      public X509Certificate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         String keyText = json.getAsString().replaceAll("\\n", "\n");
         try {
            return Pems.x509Certificate(ByteSource.wrap(keyText.getBytes(Charsets.UTF_8)),
                  crypto.certFactory());
         } catch (UnsupportedEncodingException e) {
            Throwables.propagate(e);
            return null;
         } catch (IOException e) {
            Throwables.propagate(e);
            return null;
         } catch (CertificateException e) {
            Throwables.propagate(e);
            return null;
         }
      }
   }

   /**
    * writes or reads the literal directly
    */
   @Singleton
   public static class DataBagItemAdapter extends NullHackJsonLiteralAdapter<DatabagItem> {
      final Gson gson = new Gson();

      @Override
      protected DatabagItem createJsonLiteralFromRawJson(String text) {
         IdHolder idHolder = gson.fromJson(text, IdHolder.class);
         checkState(idHolder.id != null,
               "databag item must be a json hash ex. {\"id\":\"item1\",\"my_key\":\"my_data\"}; was %s", text);
         text = text.replaceFirst(String.format("\\{\"id\"[ ]?:\"%s\",", idHolder.id), "{");
         return new DatabagItem(idHolder.id, text);
      }

      @Override
      protected String toString(DatabagItem value) {
         String text = value.toString();

         try {
            IdHolder idHolder = gson.fromJson(text, IdHolder.class);
            if (idHolder.id == null) {
               text = text.replaceFirst("\\{", String.format("{\"id\":\"%s\",", value.getId()));
            } else {
               checkArgument(value.getId().equals(idHolder.id),
                     "incorrect id in databagItem text, should be %s: was %s", value.getId(), idHolder.id);
            }
         } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException(e);
         }

         return text;
      }
   }

   private static class IdHolder {
      private String id;
   }

   // The NullFilteringTypeAdapterFactories.MapTypeAdapter class is final. Do
   // the same logic here
   private static final class KeepLastRepeatedKeyMapTypeAdapter<K, V> extends TypeAdapter<Map<K, V>> {

      protected final TypeAdapter<K> keyAdapter;
      protected final TypeAdapter<V> valueAdapter;

      protected KeepLastRepeatedKeyMapTypeAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
         this.keyAdapter = keyAdapter;
         this.valueAdapter = valueAdapter;
         nullSafe();
      }

      public void write(JsonWriter out, Map<K, V> value) throws IOException {
         if (value == null) {
            out.nullValue();
            return;
         }
         out.beginObject();
         for (Map.Entry<K, V> element : value.entrySet()) {
            out.name(String.valueOf(element.getKey()));
            valueAdapter.write(out, element.getValue());
         }
         out.endObject();
      }

      public Map<K, V> read(JsonReader in) throws IOException {
         Map<K, V> result = Maps.newHashMap();
         in.beginObject();
         while (in.hasNext()) {
            JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
            K name = keyAdapter.read(in);
            V value = valueAdapter.read(in);
            if (value != null) {
               // If there are repeated keys, overwrite them to only keep the last one
               result.put(name, value);
            }
         }
         in.endObject();
         return ImmutableMap.copyOf(result);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(keyAdapter, valueAdapter);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         KeepLastRepeatedKeyMapTypeAdapter<?, ?> that = KeepLastRepeatedKeyMapTypeAdapter.class.cast(obj);
         return equal(this.keyAdapter, that.keyAdapter) && equal(this.valueAdapter, that.valueAdapter);
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("keyAdapter", keyAdapter).add("valueAdapter", valueAdapter).toString();
      }
   }

   public static class KeepLastRepeatedKeyMapTypeAdapterFactory extends MapTypeAdapterFactory {

      public KeepLastRepeatedKeyMapTypeAdapterFactory() {
         super(Map.class);
      }

      @SuppressWarnings("unchecked")
      @Override
      protected <K, V, T> TypeAdapter<T> newAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
         return (TypeAdapter<T>) new KeepLastRepeatedKeyMapTypeAdapter<K, V>(keyAdapter, valueAdapter);
      }
   }
   
   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings(DataBagItemAdapter adapter, PrivateKeyAdapter privateAdapter,
         PublicKeyAdapter publicAdapter, X509CertificateAdapter certAdapter) {
      return ImmutableMap.<Type, Object> of(DatabagItem.class, adapter, PrivateKey.class, privateAdapter,
            PublicKey.class, publicAdapter, X509Certificate.class, certAdapter);
   }

   @Provides
   @Singleton
   @CookbookParser
   public Function<HttpResponse, Set<String>> provideCookbookDefinitionAdapter(ChefVersionSupplier chefVersionSupplier,
         ParseCookbookDefinitionFromJson v10parser, ParseKeySetFromJson v09parser) {
      return chefVersionSupplier.get() >= 10 ? v10parser : v09parser;
   }

   @Provides
   @Singleton
   @CookbookVersionsParser
   public Function<HttpResponse, Set<String>> provideCookbookDefinitionAdapter(ChefVersionSupplier chefVersionSupplier,
         ParseCookbookVersionsV10FromJson v10parser, ParseCookbookVersionsV09FromJson v09parser) {
      return chefVersionSupplier.get() >= 10 ? v10parser : v09parser;
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      bind(MapTypeAdapterFactory.class).to(KeepLastRepeatedKeyMapTypeAdapterFactory.class);
   }
}
