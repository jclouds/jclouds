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
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Map;

import org.jclouds.chef.config.ChefParserModule.KeepLastRepeatedKeyMapTypeAdapterFactory;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit tests for the {@link ChefParserModule} class.
 */
@Test(groups = "unit", testName = "ChefParserModuleTest")
public class ChefParserModuleTest {

   private static class KeyValue {
      private final String key;
      private final String value;

      private KeyValue(String key, String value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(key, value);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         KeyValue that = KeyValue.class.cast(obj);
         return equal(this.key, that.key) && equal(this.value, that.value);
      }
   }

   private Gson map = new GsonBuilder().registerTypeAdapterFactory(new KeepLastRepeatedKeyMapTypeAdapterFactory())
         .create();
   private Type mapType = new TypeToken<Map<String, String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type mapkeyValueType = new TypeToken<Map<String, KeyValue>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testKeepLastRepeatedKeyMapTypeAdapter() {
      Map<String, String> noNulls = map.fromJson("{\"value\":\"a test string!\"}", mapType);
      assertEquals(noNulls, ImmutableMap.of("value", "a test string!"));
      Map<String, String> withNull = map.fromJson("{\"value\":null}", mapType);
      assertEquals(withNull, ImmutableMap.of());
      Map<String, String> withEmpty = map.fromJson("{\"value\":\"\"}", mapType);
      assertEquals(withEmpty, ImmutableMap.of("value", ""));
      Map<String, KeyValue> keyValues = map.fromJson(
            "{\"i-foo\":{\"key\":\"i-foo\",\"value\":\"foo\"},\"i-bar\":{\"key\":\"i-bar\",\"value\":\"bar\"}}",
            mapkeyValueType);
      assertEquals(keyValues,
            ImmutableMap.of("i-foo", new KeyValue("i-foo", "foo"), "i-bar", new KeyValue("i-bar", "bar")));
      Map<String, KeyValue> duplicates = map
            .fromJson(
                  "{\"i-foo\":{\"key\":\"i-foo\",\"value\":\"foo\", \"value\":\"foo2\"},\"i-bar\":{\"key\":\"i-bar\",\"value\":\"bar\",\"value\":\"bar2\"}}",
                  mapkeyValueType);
      assertEquals(duplicates,
            ImmutableMap.of("i-foo", new KeyValue("i-foo", "foo2"), "i-bar", new KeyValue("i-bar", "bar2")));
   }
}
