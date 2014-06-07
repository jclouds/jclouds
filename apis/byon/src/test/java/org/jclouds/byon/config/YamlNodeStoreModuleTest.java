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
package org.jclouds.byon.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.byon.Node;
import org.jclouds.location.Provider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Charsets;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

@Test(groups = "unit", singleThreaded = true)
public class YamlNodeStoreModuleTest {
   Yaml yaml = createInjector().getInstance(Yaml.class);

   @DataProvider(name = "names")
   public Object[][] createData() {
      return new Object[][] { { "instance1", "bear" }, { "instance2", "apple" }, { "instance2", "francis" },
            { "instance4", "robot" } };
   }

   @Test(dataProvider = "names")
   public void deleteObject(String id, String name) throws InterruptedException, IOException {
      Injector injector = createInjector();
      Map<String, ByteSource> map = getMap(injector);
      check(map, getStore(injector), "i-20312", id, name);
   }

   public void testProvidedMapWithValue() throws IOException {
      Map<String, ByteSource> map =
            new ConcurrentHashMap<String, ByteSource>();

      map.put("test", ByteSource.wrap("id: instance1\nname: instancename\n".getBytes()));
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testProvidedConsistentAcrossRepeatedWrites() throws IOException {
      Map<String, ByteSource> map =
            new ConcurrentHashMap<String, ByteSource>();

      Injector injector = createInjectorWithProvidedMap(map);
      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Map<String, ByteSource>>() {
      }, Names.named("yaml"))), map);
      LoadingCache<String, Node> store = getStore(injector);

      for (int i = 0; i < 10; i++)
         check(map, store, "test" + i, "instance1" + i, "instancename" + i);

   }

   public void testProvidedConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, ByteSource> map =
            new ConcurrentHashMap<String, ByteSource>();

      put(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testDefaultConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, ByteSource> map = getMap(createInjector());

      put(map, getStore(createInjector()), "test", "instance1", "instancename");
      
      checkConsistent(map, getStore(createInjector()), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjector()), "test", "instance1", "instancename");
      remove(map, getStore(createInjector()), "test");

   }

   protected LoadingCache<String, Node> getStore(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<LoadingCache<String, Node>>() {
      }));
   }

   protected Map<String, ByteSource> getMap(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Map<String, ByteSource>>() {
      }, Names.named("yaml")));
   }

   protected Injector createInjectorWithProvidedMap(Map<String, ByteSource> map) {
      return Guice.createInjector(new YamlNodeStoreModule(map), new AbstractModule() {

         @Override
         protected void configure() {
            bind(ByteSource.class).annotatedWith(Provider.class).toProvider(Providers.<ByteSource>of(null));
         }

      });
   }

   protected Injector createInjector() {
      return Guice.createInjector(new YamlNodeStoreModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(ByteSource.class).annotatedWith(Provider.class).toProvider(Providers.<ByteSource>of(null));
         }

      });
   }

   protected void check(Map<String, ByteSource> map, LoadingCache<String, Node> store, String key, String id, String name)
         throws IOException {
      put(map, store, key, id, name);
      checkConsistent(map, store, key, id, name);
      remove(map, store, key);
   }

   protected void remove(Map<String, ByteSource> map, LoadingCache<String, Node> store, String key) {
      store.invalidate(key);
      assertEquals(store.size(), 0);
      map.remove(key);
      assertEquals(map.size(), 0);
      try {
         assertEquals(store.getUnchecked(key), null);
         fail("should not work as null is invalid");
      } catch (UncheckedExecutionException e) {

      }
      assertEquals(map.get(key), null);
   }

   protected void checkConsistent(Map<String, ByteSource> map, LoadingCache<String, Node> store, String key, String id,
         String name) throws IOException {
      assertEquals(map.size(), 1);
      if (store.size() == 0)
         store.getUnchecked(key);
      assertEquals(store.size(), 1);
      // checkRepeatedRead
      assertEquals(store.getUnchecked(key), Node.builder().id(id).name(name).build());
      assertEquals(store.getUnchecked(key), Node.builder().id(id).name(name).build());
      // checkRepeatedRead
      checkToYaml(map, key, id, name);
      checkToYaml(map, key, id, name);
   }

   protected void checkToYaml(Map<String, ByteSource> map, String key, String id, String name) throws IOException {
      assertEquals(map.get(key).asCharSource(Charsets.UTF_8).read(), String.format("id: %s\nname: %s\n", id, name));
   }

   protected void put(Map<String, ByteSource> map, LoadingCache<String, Node> store, String key, String id, String name) {
      assertEquals(store.size(), 0);
      assertEquals(map.size(), 0);
      map.put(key, ByteSource.wrap(String.format("id: %s\nname: %s\n", id, name).getBytes()));
      store.getUnchecked(key);
   }
}
