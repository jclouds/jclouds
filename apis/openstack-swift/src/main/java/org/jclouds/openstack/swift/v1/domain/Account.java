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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents an Account in OpenStack Object Storage.
 * 
 * @see org.jclouds.openstack.swift.v1.features.AccountApi
 */
public class Account {

   private final long containerCount;
   private final long objectCount;
   private final long bytesUsed;
   private final Map<String, String> metadata;
   private final Multimap<String, String> headers;

   // parsed from headers, so ConstructorProperties here would be misleading
   protected Account(long containerCount, long objectCount, long bytesUsed, Map<String, String> metadata,
         Multimap<String, String> headers) {
      this.containerCount = containerCount;
      this.objectCount = objectCount;
      this.bytesUsed = bytesUsed;
      this.metadata = metadata == null ? ImmutableMap.<String, String> of() : metadata;
      this.headers = headers == null ? ImmutableMultimap.<String, String> of() : headers;
   }

   /**
    * @return The count of containers for this account.
    */
   public long getContainerCount() {
      return containerCount;
   }

   /**
    * @return The count of objects for this account.
    */
   public long getObjectCount() {
      return objectCount;
   }

   /**
    * @return The number of bytes used by this account.
    */
   public long getBytesUsed() {
      return bytesUsed;
   }

   /**
    * @return The {@link Optional&lt;String&gt;} temporary URL key for this account.
    */
   public Optional<String> getTemporaryUrlKey() {
      return Optional.fromNullable(metadata.get("temp-url-key"));
   }

   /**
    * <h3>NOTE</h3>
    * In current swift implementations, headers keys are lower-cased. This means
    * characters such as turkish will probably not work out well.
    *
    * @return a {@code Map<String, String>} containing the account metadata.
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    * @return The HTTP headers for this account.
    */
   public Multimap<String, String> getHeaders() {
      return headers;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Account) {
         Account that = Account.class.cast(object);
         return equal(getContainerCount(), that.getContainerCount())
               && equal(getObjectCount(), that.getObjectCount())
               && equal(getBytesUsed(), that.getBytesUsed())
               && equal(getMetadata(), that.getMetadata());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getContainerCount(), getObjectCount(), getBytesUsed(), getMetadata());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper(this)
            .add("containerCount", getContainerCount())
            .add("objectCount", getObjectCount())
            .add("bytesUsed", getBytesUsed())
            .add("metadata", getMetadata());
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromAccount(this);
   }

   public static class Builder {
      protected long containerCount;
      protected long objectCount;
      protected long bytesUsed;
      protected Multimap<String, String> headers = ImmutableMultimap.of();
      protected Map<String, String> metadata = ImmutableMap.of();

      /**
       * @param containerCount  the count of containers for this account.
       *
       * @see Account#getContainerCount()
       */
      public Builder containerCount(long containerCount) {
         this.containerCount = containerCount;
         return this;
      }

      /**
       * @param objectCount  the count of objects for this account.
       *
       * @see Account#getObjectCount()
       */
      public Builder objectCount(long objectCount) {
         this.objectCount = objectCount;
         return this;
      }

      /**
       * @param bytesUsed  the number of bytes used by this account.
       *
       * @see Account#getBytesUsed()
       */
      public Builder bytesUsed(long bytesUsed) {
         this.bytesUsed = bytesUsed;
         return this;
      }

      /**
       * <h3>NOTE</h3>
       * This method will lower-case all metadata keys due to a Swift implementation
       * decision.
       *
       * @param metadata  the metadata for this account. 
       *
       * @see Account#getMetadata()
       */
      public Builder metadata(Map<String, String> metadata) {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder();
         for (Entry<String, String> entry : checkNotNull(metadata, "metadata").entrySet()) {
            builder.put(entry.getKey().toLowerCase(), entry.getValue());
         }
         this.metadata = builder.build();
         return this;
      }

      /**
       * @see Account#getHeaders()
       */
      public Builder headers(Multimap<String, String> headers) {
        this.headers = headers;
        return this;
      }

      public Account build() {
         return new Account(containerCount, objectCount, bytesUsed, metadata, headers);
      }

      public Builder fromAccount(Account from) {
         return containerCount(from.getContainerCount())
               .objectCount(from.getObjectCount())
               .bytesUsed(from.getBytesUsed())
               .metadata(from.getMetadata())
               .headers(from.getHeaders());
      }
   }
}
