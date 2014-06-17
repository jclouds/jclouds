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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import java.beans.ConstructorProperties;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

/**
 * Metadata for an instance or project, with their fingerprint.
 */
public class Metadata {
   @Nullable
   private final String fingerprint;
   private final Map<String, String> items;

   @ConstructorProperties({"fingerprint", "items"})
   public Metadata(@Nullable String fingerprint, @Nullable Map<String, String> items) {
      this.fingerprint = fingerprint;
      this.items = items == null ? ImmutableMap.<String, String>of() : items;
   }

   /**
    * @return an optional map of metadata key/value pairs for this instance/project
    */
   public Map<String, String> getItems() {
      return items;
   }

   /**
    * Gets the fingerprint for the items - needed for updating them.
    *
    * @return the fingerprint string for the items.
    */
   public String getFingerprint() {
      return fingerprint;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(fingerprint, items);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Metadata that = Metadata.class.cast(obj);
      return equal(this.items, that.items)
              && equal(this.fingerprint, that.fingerprint);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("items", items)
              .add("fingerprint", fingerprint);
   }

   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static final class Builder {

      private ImmutableMap.Builder<String, String> items = ImmutableMap.builder();
      private String fingerprint;

      /**
       * @see Metadata#getItems()
       */
      public Builder addItem(String key, String value) {
         this.items.put(key, value);
         return this;
      }

      /**
       * @see Metadata#getItems()
       */
      public Builder items(Map<String, String> items) {
         this.items.putAll(items);
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Metadata#getFingerprint()
       */
      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      public Metadata build() {
         return new Metadata(this.fingerprint, this.items.build());
      }

      public Builder fromMetadata(Metadata in) {
         return this.fingerprint(in.getFingerprint())
                 .items(in.getItems());
      }
   }
}
