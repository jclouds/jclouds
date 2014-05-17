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
package org.jclouds.rest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.crypto.PemsTest;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.config.CredentialStoreModule;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.io.ByteSource;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Test(groups = "unit", singleThreaded = true)
public class CredentialStoreModuleTest {
   Json json = createInjector().getInstance(Json.class);

   @DataProvider(name = "credentials")
   public Object[][] createData() {
      return new Object[][] {
            { "root", PemsTest.PRIVATE_KEY },
            { "identity", "Base64==" },
            { "user@domain", "pa$sw@rd" },
            { "user", "unic₪de" }
      };
   }

   @Test(dataProvider = "credentials")
   public void deleteObject(String identity, String credential) throws InterruptedException, IOException {
      Injector injector = createInjector();
      Map<String, ByteSource> map = getMap(injector);
      check(map, getStore(injector), "i-20312", new Credentials(identity, credential));
   }

   public void testProvidedMapWithValue() throws IOException {
      Map<String, ByteSource> map = new ConcurrentHashMap<String, ByteSource>();

      map.put("test", ByteSource.wrap(json.toJson(new Credentials("user", "pass")).getBytes()));
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", new Credentials("user", "pass"));
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", new Credentials("user", "pass"));
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testProvidedConsistentAcrossRepeatedWrites() throws IOException {
      Map<String, ByteSource> map = new ConcurrentHashMap<String, ByteSource>();

      Injector injector = createInjectorWithProvidedMap(map);
      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Map<String, ByteSource>>() {
      })), map);
      Map<String, Credentials> store = getStore(injector);

      for (int i = 0; i < 10; i++)
         check(map, store, "test" + i, new Credentials("user" + i, "pass" + i));

   }

   public void testProvidedConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, ByteSource> map = new ConcurrentHashMap<String, ByteSource>();

      put(map, getStore(createInjectorWithProvidedMap(map)), "test", new Credentials("user", "pass"));
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", new Credentials("user", "pass"));
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", new Credentials("user", "pass"));
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testDefaultConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, ByteSource> map = getMap(createInjector());

      put(map, getStore(createInjector()), "test", new Credentials("user", "pass"));
      checkConsistent(map, getStore(createInjector()), "test", new Credentials("user", "pass"));
      checkConsistent(map, getStore(createInjector()), "test", new Credentials("user", "pass"));
      remove(map, getStore(createInjector()), "test");

   }

   public void testLoginConsistentAcrossMultipleInjectorsAndLooksNice() throws IOException {
      Map<String, ByteSource> map = getMap(createInjector());
      LoginCredentials creds = LoginCredentials.builder().user("user").password("pass").build();
      put(map, getStore(createInjector()), "test", creds);
      checkConsistent(map, getStore(createInjector()), "test", creds, "{\"user\":\"user\",\"password\":\"pass\"}");
      checkConsistent(map, getStore(createInjector()), "test", creds, "{\"user\":\"user\",\"password\":\"pass\"}");
      remove(map, getStore(createInjector()), "test");
   }

   public void testLoginConsistentAcrossMultipleInjectorsAndLooksNiceWithSudo() throws IOException {
      Map<String, ByteSource> map = getMap(createInjector());
      LoginCredentials creds = LoginCredentials.builder().user("user").password("pass").authenticateSudo(true).build();
      put(map, getStore(createInjector()), "test", creds);
      checkConsistent(map, getStore(createInjector()), "test", creds,
            "{\"user\":\"user\",\"password\":\"pass\",\"authenticateSudo\":true}");
      checkConsistent(map, getStore(createInjector()), "test", creds,
            "{\"user\":\"user\",\"password\":\"pass\",\"authenticateSudo\":true}");
      remove(map, getStore(createInjector()), "test");
   }

   public void testCredentialsToByteSourceConversion() throws Exception {
      Function<Credentials, ByteSource> toBytesFunc = getCredentialsToByteStoreFunction(createInjector());
      Function<ByteSource, Credentials> fromBytesFunc = getByteStoreToCredentialsFunction(createInjector());
      
      LoginCredentials creds = LoginCredentials.builder().user("myuser").password("mypass").authenticateSudo(true).build();
      ByteSource bytes = toBytesFunc.apply(creds);
      LoginCredentials deserializedCreds = (LoginCredentials) fromBytesFunc.apply(bytes);
      
      String json = bytes.asCharSource(Charsets.UTF_8).read();
      assertEquals(json, "{\"user\":\"myuser\",\"password\":\"mypass\",\"authenticateSudo\":true}");
      
      assertEquals(deserializedCreds.identity, creds.identity);
      assertEquals(deserializedCreds.credential, creds.credential);
      assertEquals(deserializedCreds.getUser(), creds.getUser());
      assertEquals(deserializedCreds.getOptionalPassword(), creds.getOptionalPassword());
      assertEquals(deserializedCreds.getOptionalPrivateKey(), creds.getOptionalPrivateKey());
      assertEquals(deserializedCreds.shouldAuthenticateSudo(), creds.shouldAuthenticateSudo());
   }
   
   protected Map<String, Credentials> getStore(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Map<String, Credentials>>() {
      }));
   }

   protected Map<String, ByteSource> getMap(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Map<String, ByteSource>>() {
      }));
   }

   protected Function<ByteSource, Credentials> getByteStoreToCredentialsFunction(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Function<ByteSource, Credentials>>() {
      }));
   }

   protected Function<Credentials, ByteSource> getCredentialsToByteStoreFunction(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Function<Credentials, ByteSource>>() {
      }));
   }
   
   protected Injector createInjectorWithProvidedMap(Map<String, ByteSource> map) {
      return Guice.createInjector(new CredentialStoreModule(map), new GsonModule());
   }

   protected Injector createInjector() {
      return Guice.createInjector(new CredentialStoreModule(), new GsonModule());
   }

   protected void check(Map<String, ByteSource> map, Map<String, Credentials> store, String key, Credentials creds)
         throws IOException {
      put(map, store, key, creds);
      checkConsistent(map, store, key, creds);
      remove(map, store, key);
   }

   protected void remove(Map<String, ByteSource> map, Map<String, Credentials> store, String key) {
      store.remove(key);
      assertEquals(store.size(), 0);
      assertEquals(map.size(), 0);
      assertEquals(store.get(key), null);
      assertEquals(map.get(key), null);
   }

   protected void checkConsistent(Map<String, ByteSource> map, Map<String, Credentials> store, String key,
         Credentials creds) throws IOException {
      checkConsistent(map, store, key, creds, json.toJson(creds));
   }

   protected void checkConsistent(Map<String, ByteSource> map, Map<String, Credentials> store, String key,
         Credentials creds, String expected) throws IOException {
      assertEquals(store.size(), 1);
      assertEquals(map.size(), 1);
      assertTrue(store.containsKey(key));
      //System.out.println("YYYYYY " + store.get(key));
      //System.err.println("YYYYYY " + store.get(key));
      assertTrue(store.containsValue(creds));
      // checkRepeatedRead
      assertEquals(store.get(key), creds);
      assertEquals(store.get(key), creds);
      // checkRepeatedRead
      checkToJson(map, key, expected);
      checkToJson(map, key, expected);
   }

   protected void checkToJson(Map<String, ByteSource> map, String key, String expected) throws IOException {
      assertEquals(map.get(key).asCharSource(Charsets.UTF_8).read(), expected);
   }

   protected void put(Map<String, ByteSource> map, Map<String, Credentials> store, String key, Credentials creds) {
      assertEquals(store.size(), 0);
      assertEquals(map.size(), 0);
      assertFalse(store.containsKey(key));
      assertFalse(store.containsValue(creds));
      store.put(key, creds);
      //System.err.printf("XXXXXXXXXX\n\nStore has %n: %s\n\nXXXXXXXXXX\n", store.size(), Joiner.on(", ").withKeyValueSeparator("=").useForNull("<<EMPTY>>").join(store));
      //System.out.printf("XXXXXXXXXX\n\nStore has %n: %s\n\nXXXXXXXXXX\n", store.size(), Joiner.on(", ").withKeyValueSeparator("=").useForNull("<<EMPTY>>").join(store));
   }
}
