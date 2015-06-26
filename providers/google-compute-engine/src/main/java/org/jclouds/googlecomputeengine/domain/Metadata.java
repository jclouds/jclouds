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
package org.jclouds.googlecomputeengine.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Metadata for an instance or project, with their fingerprint.
 * <p/>
 * This object is mutable and not thread-safe.
 */
@AutoValue
public abstract class Metadata implements Cloneable {

   /** The fingerprint for the items - needed for updating them. */
   @Nullable public abstract String fingerprint();

   /** Adds or replaces a metadata entry. */
   public Metadata put(String key, String value) {
      remove(key);
      items().add(KeyValuePair.create(key, value));
      return this;
   }

   /** Adds or replaces metadata entries. */
   public Metadata putAll(Map<String, String> input) {
      for (Map.Entry<String, String> entry : input.entrySet()) {
         put(entry.getKey(), entry.getValue());
      }
      return this;
   }

   /** Removes any entry with the supplied key. */
   public Metadata remove(String key) {
      for (int i = 0, length = items().size(); i < length; i++) {
         if (items().get(i).key().equals(key)) {
            items().remove(i);
            return this;
         }
      }
      return this;
   }

   /** Copies the metadata into a new mutable map. */
   public Map<String, String> asMap() {
      Map<String, String> result = new LinkedHashMap<String, String>();
      ArrayList<KeyValuePair> items = items();
      for (int i = 0, length = items.size(); i < length; i++) {
         KeyValuePair item = items.get(i);
         result.put(item.key(), item.value());
      }
      return result;
   }

   /** Returns the value with the supplied key, or null. */
   @Nullable public String get(String key) {
      ArrayList<KeyValuePair> items = items();
      for (int i = 0, length = items.size(); i < length; i++) {
         KeyValuePair item = items.get(i);
         if (item.key().equals(key)) {
            return item.value();
         }
      }
      return null;
   }

   public boolean containsKey(String key) {
      return get(key) != null;
   }

   public int size() {
      return items().size();
   }

   /** Mutable list of metadata. */
   abstract ArrayList<KeyValuePair> items();

   public static Metadata create() {
      return Metadata.create(null, null);
   }

   public static Metadata create(String fingerprint) {
      return Metadata.create(fingerprint, null);
   }

   @SerializedNames({ "fingerprint", "items" })
   static Metadata create(String fingerprint, ArrayList<KeyValuePair> items) { // Dictates the type when created from json!
      return new AutoValue_Metadata(fingerprint, items != null ? items : new ArrayList<KeyValuePair>());
   }

   Metadata() {
   }

   @Override public Metadata clone() {
      return Metadata.create(fingerprint(), new ArrayList<KeyValuePair>(items()));
   }
}
